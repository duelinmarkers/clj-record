(ns clj-record.validation
  (:use (clj-record core meta util))
  (:require [clj-record.callbacks :as callbacks]))


(defn- validations-for [model-name] ((@all-models-metadata model-name) :validations))

(def valid? empty?)

(def messages-for get)

(defn- collect-errors [record errors [attr message validation-fn]]
  (if (validation-fn (record attr))
    errors
    (merge-with
      #(apply conj %1 %2)
      errors
      {attr [message]})))

(defn validate [model-name record]
  (callbacks/before-validation model-name record)
  (let [errors (reduce #(collect-errors record %1 %2) {} (validations-for model-name))]
    (callbacks/after-validation model-name record)
    errors))

(defn add-validation
  "Adds a validation to the named model.
  Called behind the scenes by the expanded form of
  (init-model ... (:validation (attribute-name message function) ...))."
  [model-name attribute-name message function]
  (dosync
    (let [validations (or (model-metadata-for model-name :validations) [])]
      (set-model-metadata-for model-name :validations
        (conj validations [attribute-name message function])))))

(defn expand-init-option
  "init-model macro-expansion delegate that generates a call to add-validation."
  [model-name attribute-name message function & ignored-options]
  `(add-validation ~model-name ~attribute-name ~message ~function))
