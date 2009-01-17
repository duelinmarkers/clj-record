(ns clj-record.associations
  (:use clj-record.util))


(defn handle-option [model-name association-type-sym association-name]
  (let [assoc-fn (ns-resolve 'clj-record.associations association-type-sym)]
    (assoc-fn model-name association-name)))

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
      (clj-record.core/get-record ~associated-model-name (~foreign-key-attribute record#)))))
