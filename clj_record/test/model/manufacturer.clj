(ns clj-record.test.model.manufacturer
  (:require [clj-record.core :as cljrec])
  (:require clj-record.validation) ; XXX: Could we load these from someplace else or do we need them in every model?
  (:require clj-record.associations)
  (:use clojure.contrib.test-is))


(def my-grade-validation-message "negative!")
(defn my-grade-validation-fn [grade] (or (nil? grade) (>= grade 0)))

(cljrec/init-model
  (:associations
    (has-many products))
  (:validation
    (name "empty!" #(not (empty? %)))
    (name "starts with whitespace!" #(not (re-find #"^\s" %)))
    (name "ends with whitespace!" #(not (re-find #"\s$" %)))
    (founded "must be numeric" #(or (nil? %) (not (re-find #"\D" %))))
    (grade my-grade-validation-message my-grade-validation-fn)))
