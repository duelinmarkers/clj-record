(ns clj_record.util
  (:use clojure.contrib.test-is)
  (:use clojure.contrib.str-utils))

(defn singularize 
  ([plural] (re-sub #"s$" "" plural))
  {:test (fn []
    (are (= _1 (singularize _2))
      "foo" "foos"))})
