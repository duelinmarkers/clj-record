(ns clj-record.validation.built-ins)


(defn match [pattern]
  #(and % (re-find pattern %)))

(defn non-match [pattern]
  (complement (match pattern)))
