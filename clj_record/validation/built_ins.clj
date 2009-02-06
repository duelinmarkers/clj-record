(ns clj-record.validation.built-ins)


(defn match
  "Returns a validation function matching value against pattern."
  [pattern]
  (fn [value] (re-find pattern (str value))))

(defn non-match
  "Returns a validation function ensuring value doesn't match pattern."
  [pattern]
  (complement (match pattern)))

(def
  #^{:doc "Validation function that makes sure a value is numeric (though not necessarily a number)."}
  numeric? (non-match #"\D"))
