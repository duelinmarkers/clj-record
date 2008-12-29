(in-ns 'clj-record.core)


(declare all-models-metadata) ; ref def'd in core.clj

(defn- validations-for [model-name] ((@all-models-metadata model-name) :validations))

(defn validate [model-name record]
  (reduce
    (fn [errors [attr message validation-fn]]
      (if (validation-fn (record attr))
        errors
        (merge-with
          (fn [result addl-val] (apply conj result addl-val))
          errors
          {attr [message]})))
    {}
    (validations-for model-name)))

(defn- validates [model-name attribute-name message function]
  (dosync
    (let [metadata (@all-models-metadata model-name)
          validations (or (@metadata :validations) [])]
      (ref-set metadata
        (assoc @metadata :validations (conj validations [(keyword (name attribute-name)) message (eval function)])))))
  nil) ; XXX: Have to return nil since we're not returning def forms.
