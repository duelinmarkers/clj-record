(ns clj-record.callbacks.built-ins)


(defn transform-value
  "Given an attribute name and a function, creates a callback function that
  uses the given function to transform the named attribute in a record.
  
  Assuming clj-record.callbacks.built-ins has been aliased to cb,
  you might do this in an init-model form:
  
    ...
      (:callbacks
        (:before-save (cb/transform-value :save-count inc)))
    ..."
  [attribute func]
  (fn [record]
    (assoc record attribute (func (get record attribute)))))
