(ns clj-record.core
  (:require
    [clojure.contrib.sql :as sql]
    [clojure.contrib.str-utils :as str-utils])
  (:use (clj-record util config)))


(defn table-name [model-name]
  (pluralize (if (string? model-name) model-name (name model-name))))

(defn to-conditions [attributes]
  (let [[parameterized-conditions values] (reduce
      (fn [[parameterized-conditions values] [attribute value]]
        (if (nil? value)
          [(conj parameterized-conditions (format "%s IS NULL" (name attribute))) values]
          [(conj parameterized-conditions (format "%s = ?" (name attribute))) (conj values value)]))
      [[] []]
      attributes)]
    (apply vector (str-utils/str-join " AND " parameterized-conditions) values)))

(defn create [model-name attributes]
  (sql/with-connection db
    (let
      [key-vector (keys attributes)
       val-vector (map attributes key-vector)
       id (sql/transaction
            (sql/insert-values (table-name model-name) key-vector val-vector)
            (sql/with-query-results rows ["VALUES IDENTITY_VAL_LOCAL()"] (:1 (first rows))))] ; XXX: db-vendor-specific
      (get-record model-name id))))

(defn get-record [model-name id]
  (sql/with-connection db
    (sql/with-query-results rows [(format "select * from %s where id = ?" (table-name model-name)) id]
      (if (empty? rows) (throw (IllegalArgumentException. "Record does not exist")))
      (merge {} (first rows)))))

(defn find-records [model-name attributes]
  (let [[parameterized-where & values] (to-conditions attributes)
        select-query (format "select * from %s where %s" (table-name model-name) parameterized-where)]
    (sql/with-connection db
      (sql/with-query-results rows (apply vector select-query values)
        (doall (map #(merge {} %) rows))))))

(defn update [model-name attributes]
  (sql/with-connection db
    (sql/update-values (table-name model-name) ["id = ?" (:id attributes)] (dissoc attributes :id))))

(defn destroy-record [model-name record]
  (sql/with-connection db
    (sql/delete-rows (table-name model-name) ["id = ?" (:id record)])))

(defn destroy-records [model-name attributes]
  (sql/with-connection db
    (sql/delete-rows (table-name model-name) (to-conditions attributes))))

(defn- defs-from-option-groups [model-name option-groups]
  (reduce
    (fn [def-forms [option-group-name & options]]
      (let [option-ns (symbol (str "clj-record." (name option-group-name)))
            fn-sym 'handle-option
            handle-option-fn (ns-resolve option-ns fn-sym)]
        (if (nil? handle-option-fn) (throw (RuntimeException. (format "%s/%s not defined" option-ns fn-sym))))
        (reduce
          (fn [def-forms option-form]
            (let [new-defs (apply handle-option-fn model-name option-form)]
              (if new-defs (conj def-forms new-defs) def-forms)))
          def-forms
          options)))
    []
    option-groups))

(def all-models-metadata (ref {}))

(defn- setup-model-metadata [model-name]
  (dosync (commute all-models-metadata assoc model-name (ref {}))))

(defmacro init-model [& options]
  (let [model-name (last (str-utils/re-split #"\." (name (ns-name *ns*))))]
    (setup-model-metadata model-name)
    (let [optional-forms (defs-from-option-groups model-name options)]
    `(do
      (defn ~'table-name [] (table-name ~model-name))
      (defn ~'get-record [id#]
        (get-record ~model-name id#))
      (defn ~'find-records [attributes#]
        (find-records ~model-name attributes#))
      (defn ~'create [attributes#]
        (create ~model-name attributes#))
      (defn ~'update [attributes#]
        (update ~model-name attributes#))
      (defn ~'destroy-record [record#]
        (destroy-record ~model-name record#))
      (defn ~'validate [record#]
        (clj-record.validation/validate ~model-name record#))
      ~@optional-forms))))
