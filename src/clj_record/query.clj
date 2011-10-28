(ns clj-record.query
  (:require [clojure.string :as string]))


(defn- operator-fn
  ([operator-format values] (operator-fn operator-format values nil))
  ([operator-format values join-with]
    (if (some nil? values) (throw (IllegalArgumentException. "A query argument must not be nil."))) ; Do we really want this?
    (fn [attribute]
      (let [clause-params-vector
              (reduce (fn [operator-params value] (conj operator-params "?")) [] values)
            clause-params
              (string/join join-with clause-params-vector)]
        [(format (str "%s " operator-format) (name attribute) clause-params) (filter (complement nil?) values)]))))

(defn equal [value] (operator-fn "= %s" [value]))
(defn not-equal [value] (operator-fn "<> %s" [value]))
(defn greater-than  [value] (operator-fn "> %s" [value]))
(defn greater-than-or-equal [value] (operator-fn ">= %s" [value]))
(defn less-than [value] (operator-fn "< %s" [value]))
(defn less-than-or-equal [value] (operator-fn "<= %s" [value]))
(defn like [pattern] (operator-fn "LIKE %s" [pattern]))
(defn not-like [pattern] (operator-fn "NOT LIKE %s" [pattern]))
(defn between [value1 value2] (operator-fn "BETWEEN %s" [value1 value2] " AND "))
(defn not-between [value1 value2] (operator-fn "NOT BETWEEN %s" [value1 value2] " AND "))
(defn in [& values] (operator-fn "IN (%s)" values ", "))
(defn not-in [& values] (operator-fn "NOT IN (%s)" values ", "))
