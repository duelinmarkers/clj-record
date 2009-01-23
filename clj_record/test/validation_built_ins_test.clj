(ns clj-record.test.validation-built-ins-test
  (:use clojure.contrib.test-is)
  (:require [clj-record.validation.built-ins :as vfn]))


(deftest match-creates-a-matching-fn-for-the-given-pattern
  (is ((vfn/match #"foo") "yes foo can"))
  (is (not ((vfn/match #"bar") "no foo can't"))))

(deftest non-match-creates-an-anti-matching-fn-for-the-given-pattern
  (is (not ((vfn/non-match #"foo") "yes foo can")))
  (is ((vfn/non-match #"bar") "no foo can't")))
