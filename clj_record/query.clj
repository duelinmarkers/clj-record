(ns clj-record.query
  (:require [clojure.contrib.str-utils :as str-utils]))


(defn- generate-predicate-fn [predicate join-predicate-params-with values]
  (fn [attribute]
     (let [[clause params] 
	   [(str-utils/str-join join-predicate-params-with
             (reduce
               (fn [predicate-params value]
                 (conj predicate-params (if (nil? value) "NULL" "?"))) [] values))
            (filter (complement nil?) values)]]
       [(format (str "%s " predicate) (name attribute) clause) params]))) 

(defn between [value1 value2]
  (generate-predicate-fn "BETWEEN %s" " AND " [value1 value2]))

(defn in [& values]
  (generate-predicate-fn "IN (%s)" ", " values))

(defn like [value]
  (generate-predicate-fn "LIKE %s" "" [value]))