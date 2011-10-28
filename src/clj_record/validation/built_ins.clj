(ns clj-record.validation.built-ins
  (:require [clojure.string :as string]))


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

(defn email?
  "Validation function that sees whether value looks like a valid email address."
  [email]
  (if (not (re-find #"^\S+@\S+\.\S+$" email))
    false
    (let [[local-part domain] (string/split email #"@")
          dot-atom? (fn [s]
            (let [atexts (string/split s #"\.")
                  atext-run-pattern #"^[\p{Alnum}_\-]+$"]
              (and
                (not (empty? atexts))
                (every? #(re-find atext-run-pattern %) atexts))))]
      (and
        (dot-atom? local-part)
        (dot-atom? domain)))))
