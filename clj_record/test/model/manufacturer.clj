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
    (validates name "empty!" #(not (empty? %)))
    (validates name "starts with whitespace!" #(not (re-find #"^\s" %)))
    (validates name "ends with whitespace!" #(not (re-find #"\s$" %)))
    (validates founded "must be numeric" #(or (nil? %) (not (re-find #"\D" %))))
    (validates grade my-grade-validation-message my-grade-validation-fn)))
