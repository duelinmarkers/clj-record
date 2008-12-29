(ns clj-record.util
  (:use clojure.contrib.test-is)
  (:use clojure.contrib.str-utils))


(defn singularize [plural]
  (if (.endsWith plural "ies")
    (re-sub #"ies$" "y" plural)
    (re-sub #"s$" "" plural)))

(defn pluralize [word]
  (if (.endsWith word "y")
    (re-sub #"y$" "ies" word)
    (str word "s")))
