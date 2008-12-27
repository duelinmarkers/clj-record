(ns clj_record.core
  (:require [clojure.contrib.sql :as sql])
  (:use clj_record.util)
  (:use clojure.contrib.str-utils)
  (:use clojure.contrib.test-is)
  (:load "associations"))


(defn table-name
  ([model-name]
    (if (string? model-name) model-name (name model-name)))
  {:test (fn []
    (are (= _1 (table-name _2))
      "foo" "foo"
      "foo" 'foo
      "foo" :foo))})

(defn find-record [model-name id]
  (sql/with-connection db
    (sql/with-results rows (format "select * from %s where id = %s" (table-name model-name) id)
      (merge {} (first rows)))))

(defn- to-condition [[attribute value]]
  (str
    (name attribute)
    (if (nil? value)
      " IS NULL"
      (str " = " (if (string? value) (format "'%s'" value) value)))))

(defn to-conditions [attributes]
  (str-join " AND " (map to-condition attributes)))

(defn find-records [model-name attributes]
  (let [select-query (format "select * from %s where %s" (table-name model-name) (to-conditions attributes))]
    (sql/with-connection db
      (sql/with-results rows select-query
        (doall (map #(merge {} %) rows))))))

(defn create [model-name attributes]
  (sql/with-connection db
    (let
      [key-vector (keys attributes)
       val-vector (map attributes key-vector)
       id (sql/transaction
            (sql/insert-values (table-name model-name) key-vector val-vector)
            (sql/with-results rows "VALUES IDENTITY_VAL_LOCAL()" (:1 (first rows))))]
      (find-record model-name id))))

(defn destroy-record [model-name record]
  (sql/with-connection db
    (sql/do-prepared
      (format "delete from %s where id = ?" (table-name model-name))
      [(:id record)])))

(defn destroy-records [model-name attributes]
  (sql/with-connection db
    (sql/do-prepared
      (format "delete from %s where %s" (table-name model-name) (to-conditions attributes)) [])))

(defn- defs-from-options [model-name options]
  (for [option-form options]
    (apply (ns-resolve 'clj_record.core (first option-form)) model-name (rest option-form))))

(defmacro init-model [& options]
  (let [model-name (first (reverse (re-split #"\." (name (ns-name *ns*)))))
        optional-forms (defs-from-options model-name options)]
    `(do
      (defn ~'table-name [] (table-name ~model-name))
      (defn ~'find-record [id#]
        (find-record ~model-name id#))
      (defn ~'find-records [attributes#]
        (find-records ~model-name attributes#))
      (defn ~'create [attributes#]
        (create ~model-name attributes#))
      (defn ~'destroy-record [record#]
        (destroy-record ~model-name record#))
      ~@optional-forms)))
