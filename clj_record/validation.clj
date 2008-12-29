(in-ns 'clj-record.core)


(declare all-models-metadata) ; ref def'd in core.clj

(defn validate [model-name record]
  (for [[attribute-name message function] ((@all-models-metadata model-name) :validations)
        :when (not (function (record (keyword attribute-name))))]
    [attribute-name message]))

(defn- validates [model-name attribute-name message function]
  (dosync
    (let [metadata (@all-models-metadata model-name)
          validations (or (@metadata :validations) [])]
      (ref-set metadata
        (assoc @metadata :validations (conj validations [(name attribute-name) message (eval function)])))))
  nil)
