(ns clj-record.test.model.manufacturer
  (:require clj-record.boot
            [clj-record.validation.built-ins :as vfunc]))


; def'd here to illustrate that validation messages don't have to live inline in the init-model form.
(def my-grade-validation-message "negative!")

(defn infer-full-year [year]
  (if (= 2 (count year)) (str "19" year) year))

(clj-record.core/init-model
  (:associations
    (has-many products))
  (:validation
    (:name "empty!" #(not (empty? %)))
    (:name "starts with whitespace!" (vfunc/non-match #"^\s"))
    (:name "ends with whitespace!" (vfunc/non-match #"\s$"))
    (:founded "must be numeric" #(or (nil? %) (vfunc/numeric? %)))
    (:grade my-grade-validation-message #(or (nil? %) (>= % 0))))
  (:callbacks
    (:before-save #(let [year (infer-full-year (% :founded))] (if year (assoc % :founded year) %)))))
