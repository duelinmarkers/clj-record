(ns clj-record.validation
  (:use clj-record.util)
  (:use clj-record.core))


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

(defn handle-option [model-name attribute-name message function]
  (dosync
    (let [metadata (@all-models-metadata model-name)
          validations (or (@metadata :validations) [])]
      (ref-set metadata
        (assoc @metadata :validations (conj validations
          [ (keyword (name attribute-name))
            (eval message)
            (eval function)])))))
  nil)
