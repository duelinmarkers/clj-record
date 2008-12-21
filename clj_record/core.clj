(ns clj_record.core
  (:require [clojure.contrib.sql :as sql])
  (:use clojure.contrib.str-utils)
  (:use clojure.contrib.test-is))

(defn singularize 
  ([plural] (re-sub #"s$" "" plural))
  {:test (fn []
    (are (= _1 (singularize _2))
      "foo" "foos"))})

(defn table-name
  ([model-name]
    (if (string? model-name) model-name (.getName model-name)))
  {:test (fn []
    (are (= (table-name _1) _2)
      "foo" "foo"
      'foo "foo"
      :foo "foo"))})

(defn find-record [model-name id]
  (sql/with-connection db
    (sql/with-results rows (format "select * from %s where id = %s" (table-name model-name) id)
      (merge {} (first rows)))))

(defn find-records [model-name attributes]
  (let [to-conditions (fn [attrs]
          (str-join " AND " (map #(str (.getName (first %)) " = " (frest %)) attrs)))]
    (sql/with-connection db
      (sql/with-results rows 
        (format "select * from %s where %s" (table-name model-name) (to-conditions attributes))
        (merge {} (first rows))))))

(defn create [model-name attributes]
  (sql/with-connection db
    (let
      [key-vector (keys attributes)
       val-vector (map attributes key-vector)
       id (sql/transaction
            (sql/insert-values (table-name model-name) key-vector val-vector)
            (sql/with-results rows "VALUES IDENTITY_VAL_LOCAL()" (:1 (first rows))))]
      (find-record model-name id))))

(defn- has-many [model-name association-name]
  (println (format "has-many given %s and %s" model-name association-name))
  (let [associated-model-name (singularize (.getName association-name))
        foreign-key-attribute (keyword (str model-name "_id"))
        find-fn-name (symbol (str "find-" association-name))
        clear-fn-name (symbol (str "clear-" association-name))]
    `(do
      (defn ~find-fn-name [record#]
        (find-records ~associated-model-name {~foreign-key-attribute (record# :id)}))
      (defn ~clear-fn-name [record#]
        (println "Can't delete things yet!")))))

(defn- belongs-to [& args] nil)

(defn- defs-from-options [model-name options]
  (for [option-form options]
    (apply (ns-resolve 'clj_record.core (first option-form)) model-name (rest option-form))))

(defmacro init-model [model-name & options]
  (let [optional-forms (defs-from-options model-name options)
        model-name (.getName model-name)]
    `(do
      (defn ~'table-name [] (table-name ~model-name))
      (defn ~'find-record [id#]
        (find-record ~model-name id#))
      (defn ~'create [attributes#]
        (create ~model-name attributes#))
      ~@optional-forms)))


