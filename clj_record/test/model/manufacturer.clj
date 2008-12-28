(ns clj-record.test.model.manufacturer
  (:require [clj-record.core :as cljrec])
  (:use clojure.contrib.test-is))


(cljrec/init-model
  (has-many products)
  (validates name #(not (empty? %))))
