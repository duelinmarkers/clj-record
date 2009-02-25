(ns clj-record.query
  (:require [clojure.contrib.str-utils :as str-utils]))


(defmacro generate-where-clauses
  "Run thru the arguments (collection of vectors), creating each query function
  using the first item of each vector as the fucntion name, the second item as the
  operator format for the clause, the third (optional) as the function's argument 
  vector (if not supplied, the generated query accepts one argument), and the fourth to
  join the operator parameters if the query accepts more than one argument."
  [& operators]
  `(do ~@(map 
          (fn [operator]
            (let [operator-name (symbol (name (first operator)))
                  operator-format (second operator)
                  args (nth operator 2 '[value])
                  params (if (= "&" (name (first args))) (symbol (name (second args))) args)
                  join-with (nth operator 3 nil)]
              `(defn ~operator-name ~args
                 (fn [attribute#]
                   (let [clause-params-vector#
                         (reduce
                          (fn [operator-params# value#]
                           (conj operator-params# (if (nil? value#) (throw (Exception. "A query argument must not be nil.")) "?"))) [] ~params)
                         clause-params#
                         (if (nil? ~join-with)
                          (apply str clause-params-vector#)
                          (str-utils/str-join ~join-with clause-params-vector#))]
                     [(format (str "%s " ~operator-format) (name attribute#) clause-params#) (filter (complement nil?) ~params)])))))
          operators)))

(generate-where-clauses
 [:equal "= %s"]
 [:not-equal "<> %s"]
 [:greater-than "> %s"]
 [:greater-than-or-equal ">= %s"]
 [:less-than "< %s"]
 [:less-than-or-equal "<= %s"]
 [:like "LIKE %s" [pattern]]
 [:not-like "NOT LIKE %s" [pattern]]
 [:between "BETWEEN %s" [value1 value2] " AND "]
 [:not-between "NOT BETWEEN %s" [value1 value2] " AND "]
 [:in "IN (%s)" [& values] ", "]
 [:not-in "NOT IN (%s)" [& values] ", "])