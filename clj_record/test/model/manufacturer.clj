(ns clj-record.test.model.manufacturer
  (:require [clj-record.core :as cljrec])
  (:use clojure.contrib.test-is))


(cljrec/init-model
  (has-many products)
  (validates name "empty!" #(not (empty? %)))
  (validates name "starts with whitespace!" #(not (re-find #"^\s" %)))
  (validates name "ends with whitespace!" #(not (re-find #"\s$" %)))
  (validates grade "negative!" #(or (nil? %) (>= % 0))))
