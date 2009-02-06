(ns clj-record.test.model.manufacturer
  (:require clj-record.boot
            [clj-record.validation.built-ins :as v]
            [clj-record.callbacks.built-ins :as cb])
  (:use clj-record.test.model.config))


; def'd here to illustrate that validation messages don't have to live inline in the init-model form.
(def my-grade-validation-message "negative!")

(defn infer-full-year [year]
  (if (-> year str count (= 2))
    (str "19" year)
    year))

(clj-record.core/init-model
  (:associations
    (has-many products))
  (:validation
    (:name "empty!" #(not (empty? %)))
    (:name "starts with whitespace!" (v/non-match #"^\s"))
    (:name "ends with whitespace!" (v/non-match #"\s$"))
    (:founded "must be numeric" #(or (nil? %) (v/numeric? %)))
    (:grade my-grade-validation-message #(or (nil? %) (>= % 0))))
  (:callbacks
    (:before-save (cb/transform-value :founded infer-full-year))))
