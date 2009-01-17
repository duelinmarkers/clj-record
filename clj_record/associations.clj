(ns clj-record.associations
  (:use clj-record.util))


(defn has-many [model-name association-name]
  (let [associated-model-name (singularize (name association-name))
        foreign-key-attribute (keyword (str model-name "_id"))
        find-fn-name (symbol (str "find-" association-name))
        destroy-fn-name (symbol (str "destroy-" association-name))]
    `(do
      (defn ~find-fn-name [record#]
        (clj-record.core/find-records ~associated-model-name {~foreign-key-attribute (record# :id)}))
      (defn ~destroy-fn-name [record#]
        (clj-record.core/destroy-records ~associated-model-name {~foreign-key-attribute (record# :id)})))))

(defn belongs-to [model-name association-name]
  (let [associated-model-name (str association-name)
        find-fn-name (symbol (str "find-" associated-model-name))
        foreign-key-attribute (keyword (str associated-model-name "_id"))]
    `(defn ~find-fn-name [record#]
      (clj-record.core/find-record ~associated-model-name (~foreign-key-attribute record#)))))
