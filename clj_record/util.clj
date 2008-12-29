(ns clj-record.util
  (:use clojure.contrib.test-is)
  (:use clojure.contrib.str-utils))


(defn singularize 
  ([plural] (re-sub #"s$" "" plural))
  {:test (fn []
    (are (= _1 (singularize _2))
      "foo" "foos"))})

(defn pluralize [word]
  (if (.endsWith word "y")
    (re-sub #"y$" "ies" word)
    (str word "s")))
