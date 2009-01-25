(ns clj-record.core
  (:require [clojure.contrib.sql        :as sql]
            [clojure.contrib.str-utils  :as str-utils])
  (:use (clj-record meta util config callbacks)))


(defn table-name [model-name]
  (pluralize (if (string? model-name) model-name (name model-name))))

(defn to-conditions
  "Converts the given attribute map into a clojure.contrib.sql style 'where-params,'
  a vector containing a parameterized conditions string followed by ordered values for the parameters.
  Conditions will be ANDed together.
  Nil attributes will be turned into 'attr_name IS NULL' with no value in the vector."
  [attributes]
  ; XXX: Surely there's a better way.
  (let [[parameterized-conditions values] (reduce
      (fn [[parameterized-conditions values] [attribute value]]
        (if (nil? value)
          [(conj parameterized-conditions (format "%s IS NULL" (name attribute))) values]
          [(conj parameterized-conditions (format "%s = ?" (name attribute))) (conj values value)]))
      [[] []]
      attributes)]
    (apply vector (str-utils/str-join " AND " parameterized-conditions) values)))

(defmacro connected
  "Ensures that the body is run with a single DB connection.
  Doesn't create a new connection if there already is one.
  You're probably more interested in the 'transaction' macro."
  [& body]
  `(let [func# (fn [] ~@body)]
    (if (sql/find-connection)
      (func#)
      (sql/with-connection db (func#)))))

(defmacro transaction
  "Runs body in a single DB transaction, first ensuring there's a connection."
  [& body]
  `(connected
    (sql/transaction
      ~@body)))

(defn insert
  "Inserts a record populated with attributes and returns the generated id."
  [model-name attributes]
  (transaction
    (let [attributes (run-callbacks attributes model-name :before-save)]
      (sql/insert-values (table-name model-name) (keys attributes) (vals attributes)))
    (sql/with-query-results rows [(id-query-for db)] (:1 (first rows)))))

(defn get-record
  "Retrieves record by id, throwing if not found."
  [model-name id]
  (connected
    (sql/with-query-results rows [(format "select * from %s where id = ?" (table-name model-name)) id]
      (if (empty? rows) (throw (IllegalArgumentException. "Record does not exist")))
      (merge {} (first rows)))))

(defn create
  "Inserts a record populated with attributes and returns it."
  [model-name attributes]
  (let [id (insert model-name attributes)]
    (connected
      (get-record model-name id))))

(defn find-records
  "Returns a vector of matching records.
  Given a where-params vector, uses it as-is. (See clojure.contrib.sql/with-query-results.)
  Given a map of attribute-value pairs, uses to-conditions to convert to where-params."
  [model-name attributes-or-where-params]
  (let [[parameterized-where & values]
          (if (map? attributes-or-where-params)
            (to-conditions attributes-or-where-params)
            attributes-or-where-params)
        select-query (format "select * from %s where %s" (table-name model-name) parameterized-where)]
    (connected
      (sql/with-query-results rows (apply vector select-query values)
        (doall (map #(merge {} %) rows))))))

(defn update
  "Updates by (partial-record :id), updating only those columns included in partial-record."
  [model-name partial-record]
  (connected
    (let [id (partial-record :id)
          partial-record (-> partial-record (run-callbacks model-name :before-save :before-update) (dissoc :id))]
      (sql/update-values (table-name model-name) ["id = ?" id] partial-record)
      (assoc partial-record :id id))))

(defn destroy-record
  "Deletes by (record :id)."
  [model-name record]
  (connected
    (sql/delete-rows (table-name model-name) ["id = ?" (:id record)])))

(defn destroy-records
  "Deletes all records matching (-> attributes to-conditions)."
  [model-name attributes]
  (connected
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

(defmacro init-model
  "Macro to turn a namespace into a 'model.'
  The segment of the namespace name following the last dot is used as the model-name.
  Model-specific versions of most public functions in clj-record.core are defined 
  in the model namespace (where the model-name as first argument can be omitted).
  Optional forms for associations and validation are specified here.
  See clj_record/test/model/manufacturer.clj for an example."
  [& option-groups]
  (let [model-name (last (str-utils/re-split #"\." (name (ns-name *ns*))))
        optional-defs (defs-from-option-groups model-name option-groups)]
      `(do
        (init-model-metadata ~model-name)
        (defn ~'model-metadata [& args#]
          (apply model-metadata-for ~model-name args#))
        (defn ~'table-name [] (table-name ~model-name))
        (defn ~'get-record [id#]
          (get-record ~model-name id#))
        (defn ~'find-records [attributes#]
          (find-records ~model-name attributes#))
        (defn ~'create [attributes#]
          (create ~model-name attributes#))
        (defn ~'insert [attributes#]
          (insert ~model-name attributes#))
        (defn ~'update [attributes#]
          (update ~model-name attributes#))
        (defn ~'destroy-record [record#]
          (destroy-record ~model-name record#))
        (defn ~'validate [record#]
          (clj-record.validation/validate ~model-name record#))
        ~@optional-defs)))
