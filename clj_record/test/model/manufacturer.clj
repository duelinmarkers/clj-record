(ns clj-record.test.model.manufacturer
  (:require clj-record.boot))


; The following defs are here to illustrate validation messages and functions
; don't have to live inline in the init-model form.
(def my-grade-validation-message "negative!")
(defn my-grade-validation-fn [grade] (or (nil? grade) (>= grade 0)))

(clj-record.core/init-model
  (:associations
    (has-many products))
  (:validation
    (:name "empty!" #(not (empty? %)))
    (:name "starts with whitespace!" #(not (re-find #"^\s" %)))
    (:name "ends with whitespace!" #(not (re-find #"\s$" %)))
    (:founded "must be numeric" #(or (nil? %) (not (re-find #"\D" %))))
    (:grade my-grade-validation-message my-grade-validation-fn)))
