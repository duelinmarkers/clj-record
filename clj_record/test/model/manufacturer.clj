(ns clj-record.test.model.manufacturer
  (:require [clj-record.core :as cljrec])
  (:use clojure.contrib.test-is))


(cljrec/init-model
  (has-many products)
  (validates name "Name cannot be empty." #(not (empty? %)))
  (validates name "Name can't start with whitespace." #(not (re-find #"^\s" %)))
  (validates name "Name can't end with whitespace." #(not (re-find #"\s$" %))))
