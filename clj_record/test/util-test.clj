(ns clj-record.test.util-test
  (:require
    [clj-record.util :as util])
  (:use clojure.contrib.test-is))


(deftest pluralizes-simple-words
  (is (= "foos" (util/pluralize "foo"))))

(deftest pluralizes-words-ending-in-y-with-ies
  (are (= _1 (util/pluralize _2))
    "babies" "baby"))
