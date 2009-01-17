(ns clj-record.test.model.manufacturer
  (:require [clj-record.core :as cljrec])
  (:require clj-record.validation) ; XXX: Could we load these from someplace else or do we need them in every model?
  (:require clj-record.associations)
  (:use clojure.contrib.test-is))


(cljrec/init-model
  (:associations
    (has-many products))
  (:validation
    (validates name "empty!" #(not (empty? %)))
    (validates name "starts with whitespace!" #(not (re-find #"^\s" %)))
    (validates name "ends with whitespace!" #(not (re-find #"\s$" %)))
    (validates grade "negative!" #(or (nil? %) (>= % 0)))))
