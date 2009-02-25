(ns clj-record.query
  (:require [clojure.contrib.str-utils :as str-utils]))


(defmacro query-operator
  ([operator-format]               (query-operator operator-format '[value]))
  ([operator-format args]          (query-operator operator-format args nil))
  ([operator-format args join-with]
    (let [params (if (= "&" (name (first args))) (symbol (name (second args))) args)]
      `(fn ~args
        (fn [attribute#]
          (let [clause-params-vector#
                  (reduce
                    (fn [operator-params# value#]
                      (conj operator-params# (if (nil? value#) (throw (Exception. "A query argument must not be nil.")) "?"))) [] ~params)
                clause-params#
                  (if (nil? ~join-with)
                    (apply str clause-params-vector#)
                    (str-utils/str-join ~join-with clause-params-vector#))]
            [(format (str "%s " ~operator-format) (name attribute#) clause-params#) (filter (complement nil?) ~params)]))))))

(def equal (query-operator "= %s"))
(def not-equal (query-operator "<> %s"))
(def greater-than (query-operator "> %s"))
(def greater-than-or-equal (query-operator ">= %s"))
(def less-than (query-operator "< %s"))
(def less-than-or-equal (query-operator "<= %s"))
(def like (query-operator "LIKE %s" [pattern]))
(def not-like (query-operator "NOT LIKE %s" [pattern]))
(def between (query-operator "BETWEEN %s" [value1 value2] " AND "))
(def not-between (query-operator "NOT BETWEEN %s" [value1 value2] " AND "))
(def in (query-operator "IN (%s)" [& values] ", "))
(def not-in (query-operator "NOT IN (%s)" [& values] ", "))
