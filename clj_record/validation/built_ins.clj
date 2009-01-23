(ns clj-record.validation.built-ins)


(defn match [pattern]
  #(re-find pattern (str %)))

(defn non-match [pattern]
  (complement (match pattern)))
