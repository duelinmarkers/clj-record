(ns clj-record.test.util-test
  (:require
    [clj-record.util :as util])
  (:use clojure.contrib.test-is))


(deftest singularize (are (= _1 (util/singularize _2))
    "foo" "foos"
    "baby" "babies"))

(deftest pluralize (are (= _1 (util/pluralize _2))
    "foos" "foo"
    "babies" "baby"))
