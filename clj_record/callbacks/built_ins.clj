(ns clj-record.callbacks.built-ins)


(defn transform-value [attribute func]
  (fn [record]
    (assoc record attribute (func (record attribute)))))
