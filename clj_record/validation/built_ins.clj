(ns clj-record.validation.built-ins)


(defn match
  "Returns a validation function matching value against pattern."
  [pattern]
  #(re-find pattern (str %)))

(defn non-match
  "Returns a validation function ensuring value doesn't match pattern."
  [pattern]
  (complement (match pattern)))

(def numeric? (non-match #"\D"))
