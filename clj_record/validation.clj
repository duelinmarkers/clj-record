(ns clj-record.validation
  (:use clj-record.util)
  (:use clj-record.meta)
  (:use clj-record.core))


(defn- validations-for [model-name] ((@all-models-metadata model-name) :validations))

(def valid? empty?)

(def messages-for get)

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

(defn add-validation
  "Adds a validation to the named model.
  Called behind the scenes by the expanded form of
  (init-model ... (:validation (attribute-name message function) ...))."
  [model-name attribute-name message function]
  (dosync
    (let [validations (or (model-metadata-for model-name :validations) [])]
      (set-model-metadata-for model-name :validations
        (conj validations
          [attribute-name message function])))))

(defn expand-init-option
  "init-model macro-expansion delegate that generates a call to add-validation."
  [model-name attribute-name message function]
  `(add-validation ~model-name ~attribute-name ~message ~function))
