(ns clj-record.core
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as string])
  (:use (clj-record meta util callbacks)))


(defn table-name
  "Retrieves table-name from model-metadata."
  [model-name]
  (model-metadata-for model-name :table-name))

(defn set-table-name
  "Puts table-name into model metadata."
  [model-name tbl-name]
  (dosync (set-model-metadata-for model-name :table-name tbl-name)))

(defn set-db-spec [model-name db-spec]
  (dosync (set-model-metadata-for model-name :db-spec db-spec)))

(defn to-conditions
  "Converts the given attribute map into a clojure.java.jdbc style 'where-params,'
  a vector containing a parameterized conditions string followed by ordered values for the parameters.
  Conditions will be ANDed together.
  Nil attributes will be turned into 'attr_name IS NULL' with no value in the vector."
  [attributes]
  ; XXX: Surely there's a better way.
  (let [[parameterized-conditions values] (reduce
      (fn [[parameterized-conditions values] [attribute value]]
       (cond
         (nil? value)
         [(conj parameterized-conditions (format "%s IS NULL" (name attribute))) values]
         (fn? value)
         (let [[new-condition new-values] (value attribute)]
           [(conj parameterized-conditions new-condition) (apply conj values new-values)])
         :else
         [(conj parameterized-conditions (format "%s = ?" (name attribute))) (conj values value)]))
      [[] []]
      attributes)]
    (apply vector (string/join " AND " parameterized-conditions) values)))

(defmacro connected
  "Ensures that the body is run with a single DB connection.
  Doesn't create a new connection if there already is one.
  You're probably more interested in the 'transaction' macro."
  [db-spec & body]
  `(let [func# (fn [] ~@body)]
    (if (sql/find-connection)
      (func#)
      (sql/with-connection ~db-spec (func#)))))

(defmacro transaction
  "Runs body in a single DB transaction, first ensuring there's a connection."
  ([db-spec & body]
  `(connected ~db-spec
    (sql/transaction
      ~@body))))

(defmacro model-transaction
  "Runs body in a single DB transaction, first ensuring there's a connection. Note: body is a single list in this
instance."
  [& body]
  `(transaction (db-spec-for ~'model-name) ~@body))

(defn find-by-sql
  "Returns a vector of matching records.
  select-query-and-values should be something like
    [\"SELECT id, name FROM manufacturers WHERE id = ?\" 23]
  This allows the caller total control over the SELECT and FROM clauses, but note that callbacks are still run,
  so if you omit columns your callbacks will have to be written to tolerate incomplete records."
  [model-name select-query-and-values]
    (connected (db-spec-for model-name)
      (sql/with-query-results rows select-query-and-values
        (doall (after-load model-name rows)))))

(defn find-records
  "Returns a vector of matching records.
  Given a where-params vector, uses it as-is. (See clojure.java.jdbc/with-query-results.)
  Given a map of attribute-value pairs, uses to-conditions to convert to where-params."
  [model-name attributes-or-where-params]
  (let [[parameterized-where & values]
          (if (map? attributes-or-where-params)
            (to-conditions attributes-or-where-params)
            attributes-or-where-params)
          select-query (format "select * from %s where %s" (table-name model-name) parameterized-where)]
    (find-by-sql model-name (apply vector select-query values))))

(defn find-record
  "Returns the first matching record. This is just (first (find-records ...)).
  Note that at the moment there's no optimization to prevent the entire result set being read and converted into records."
  [model-name attributes-or-where-params]
  (first (find-records model-name attributes-or-where-params)))

(defn record-count
  "Returns the number of records that match, or the total number of records
  in the table." 
  ([model-name] (record-count model-name ["1=1"]))
  ([model-name attributes-or-where-params]
     (let [[parameterized-where & values]
           (if (map? attributes-or-where-params)
             (to-conditions attributes-or-where-params)
             attributes-or-where-params)
           select-query (format "select count(*) as count from %s where %s" (table-name model-name) parameterized-where)]
       (:count (first (find-by-sql model-name (apply vector select-query values)))))))

(defn get-record
  "Retrieves record by id, throwing if not found."
  [model-name id]
  (or (find-record model-name {:id id})
      (throw (IllegalArgumentException. "Record does not exist"))))

(defn insert
  "Inserts a record populated with attributes and returns the generated id."
  [model-name attributes]
  (transaction (db-spec-for model-name)
    (let [attributes (before-insert model-name (before-save model-name attributes))]
      (sql/insert-values (table-name model-name) (keys attributes) (vals attributes)))
    (sql/with-query-results rows [(id-query-for (db-spec-for model-name) (table-name model-name))]
      (let [id (val (first (first rows)))]
        (after-save model-name (after-insert model-name (assoc attributes :id id)))
        id))))

(defn create
  "Inserts a record populated with attributes and returns it."
  [model-name attributes]
  (let [id (insert model-name attributes)]
    (connected (db-spec-for model-name)
      (get-record model-name id))))

(defn update
  "Updates by (partial-record :id), updating only those columns included in partial-record."
  [model-name partial-record]
  (connected (db-spec-for model-name)
    (let [id (partial-record :id)
          partial-record (-> partial-record (run-callbacks model-name :before-save :before-update) (dissoc :id))]
      (sql/update-values (table-name model-name) ["id = ?" id] partial-record)
      (let [output-record (assoc partial-record :id id)]
        (after-save model-name (after-update model-name output-record))
        output-record))))

(defn destroy-record
  "Deletes by (record :id)."
  [model-name record]
  (connected (db-spec-for model-name)
    (before-destroy model-name record)
    (sql/delete-rows (table-name model-name) ["id = ?" (:id record)])
    (after-destroy model-name record)))

(defn destroy-records
  "Deletes all records matching (-> attributes to-conditions), 
  running before- and after-destroy callbacks on each matching record."
  [model-name attributes]
  (let [conditions (to-conditions attributes)
        model-table-name (table-name model-name)
        [parameterized-where & values] conditions
        select-query (format "select * from %s where %s" model-table-name parameterized-where)]
    (connected (db-spec-for model-name)
      (sql/with-query-results rows-to-delete (apply vector select-query values)
        (doseq [record rows-to-delete]
          (before-destroy model-name record))
        (sql/delete-rows model-table-name conditions)
        (doall
          (map #(after-destroy model-name %) rows-to-delete))))))

(defn delete-records
  "Deletes all records matching (-> attributes to-conditions) without running callbacks."
  [model-name attributes]
  (connected (db-spec-for model-name)
    (sql/delete-rows (table-name model-name) (to-conditions attributes))))

(defn- defs-from-option-groups [model-name option-groups]
  (reduce
    (fn [def-forms [option-group-name & options]]
      (let [option-ns (symbol (str "clj-record." (name option-group-name)))
            expand-init-option-fn (ns-resolve option-ns 'expand-init-option)]
        (if (nil? expand-init-option-fn)
          (throw (IllegalArgumentException. (format "%s/expand-init-option not defined" option-ns))))
        (into def-forms (map #(apply expand-init-option-fn model-name %) options))))
    []
    option-groups))

(defn- split-out-init-options [init-options]
  (loop [top-level-options {}
         remaining-options init-options]
    (if (keyword? (first remaining-options))
      (recur
        (assoc top-level-options (first remaining-options) (fnext remaining-options))
        (nnext remaining-options))
      [top-level-options remaining-options])))

(defmacro init-model
  "Macro to create a model out of a clojure namespace.
  The segment of the namespace name following the last dot is used as the model-name.
  Model-specific versions of most public functions in clj-record.core are defined 
  in the model namespace (minus the model-name as first argument).
  Optional forms for associations and validation are specified here.
  
  See clj_record/test/model/manufacturer.clj for an example."
  [& init-options]
  (let [model-name (last (string/split (name (ns-name *ns*)) #"\."))
        [top-level-options option-groups] (split-out-init-options init-options)
        tbl-name (or (top-level-options :table-name) (dashes-to-underscores (pluralize model-name)))
        optional-defs (defs-from-option-groups model-name option-groups)]
    `(do
      (init-model-metadata ~model-name)
      (set-db-spec ~model-name ~'db)
      (set-table-name ~model-name ~tbl-name)
      (def ~'model-name ~model-name)
      (def ~'table-name (table-name ~model-name))
      (defn ~'model-metadata [& args#]
        (apply model-metadata-for ~model-name args#))
      (defn ~'table-name [] (table-name ~model-name))
      (defn ~'record-count 
        ([] (record-count ~model-name))
        ([attributes#] (record-count ~model-name attributes#)))
      (defn ~'get-record [id#]
        (get-record ~model-name id#))
      (defn ~'find-records [attributes#]
        (find-records ~model-name attributes#))
      (defn ~'find-record [attributes#]
        (find-record ~model-name attributes#))
      (defn ~'find-by-sql [select-query-and-values#]
        (find-by-sql ~model-name select-query-and-values#))
      (defn ~'create [attributes#]
        (create ~model-name attributes#))
      (defn ~'insert [attributes#]
        (insert ~model-name attributes#))
      (defn ~'update [attributes#]
        (update ~model-name attributes#))
      (defn ~'destroy-record [record#]
        (destroy-record ~model-name record#))
      (defn ~'destroy-records [attributes#]
        (destroy-records ~model-name attributes#))
      (defn ~'delete-records [attributes#]
        (delete-records ~model-name attributes#))
      (defn ~'validate [record#]
        (~'clj-record.validation/validate ~model-name record#))
      (defn ~'after-destroy [attributes#]
        (after-destroy ~model-name attributes#))
      (defn ~'after-insert [attributes#]
        (after-insert ~model-name attributes#))
      (defn ~'after-load [rows#]
        (after-load ~model-name rows#))
      (defn ~'after-save [attributes#]
        (after-save ~model-name attributes#))
      (defn ~'after-update [attributes#]
        (after-update ~model-name attributes#))
      (defn ~'after-validation [attributes#]
        (after-validation ~model-name attributes#))
      (defn ~'before-destroy [attributes#]
        (before-destroy ~model-name attributes#))
      (defn ~'before-insert [attributes#]
        (before-insert ~model-name attributes#))
      (defn ~'before-save [attributes#]
        (before-save ~model-name attributes#))
      (defn ~'before-update [attributes#]
        (before-update ~model-name attributes#))
      (defn ~'before-validation [attributes#]
        (before-validation ~model-name attributes#))
      ~@optional-defs)))
