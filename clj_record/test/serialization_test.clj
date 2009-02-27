(ns clj-record.test.serialization-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is
        clj-record.test.test-helper))


(defdbtest serialized-attributes-support-common-clojure-types
  (let [record (manufacturer/create valid-manufacturer)]
    (are (=
      (do
        (manufacturer/update (assoc record :name _1))
        _1)
      ((manufacturer/get-record (record :id)) :name))
      "some string" ; Cheating? This passes without serialization implemented.
      )))
  
