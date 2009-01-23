(ns clj-record.validation.built-ins)


(defn match [pattern]
  #(re-find pattern %))

(defn non-match [pattern]
  (complement (match pattern)))
