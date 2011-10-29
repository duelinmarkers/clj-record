(ns clj-record.test-model.manufacturer
  (:require [clojure.java.jdbc :as sql]
            clj-record.boot
            [clj-record.validation.built-ins :as valid]
            [clj-record.callbacks.built-ins :as cb])
  (:use clj-record.test-model.config))


; def'd here to illustrate that validation messages don't have to live inline in the init-model form.
(def my-grade-validation-message "negative!")

(defn infer-full-year [year]
  (if (-> year str count (= 2))
    (str "19" year)
    year))

(defn print-record [record]
  ;(print "record: " record)
  record)

(clj-record.core/init-model
  (:associations
    (has-many products))
  (:validation
    (:name "empty!" #(not (empty? %)))
    (:name "starts with whitespace!" (valid/non-match #"^\s"))
    (:name "ends with whitespace!" (valid/non-match #"\s$"))
    (:founded "must be numeric" #(or (nil? %) (valid/numeric? %)))
    (:grade my-grade-validation-message #(or (nil? %) (>= % 0))))
  (:callbacks
    (:before-save (cb/transform-value :founded infer-full-year))
    (:after-load print-record)))

(defn first-record []
  (clj-record.core/model-transaction
    (sql/with-query-results rows [(format "select * from %s where id = ?" (table-name)) 1]
      (doall (after-load rows)))))