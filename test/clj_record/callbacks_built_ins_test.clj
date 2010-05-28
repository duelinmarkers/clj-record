(ns clj-record.callbacks-built-ins-test
  (:use clojure.test)
  (:require [clj-record.callbacks.built-ins :as cbfunc]))


(deftest transform-value
  (let [reverse-a-transformer (cbfunc/transform-value :a reverse)]
    (is (= {:a [2 1]} (reverse-a-transformer {:a [1 2]})))))
