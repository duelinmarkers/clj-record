(ns clj-record.serialization-test
  (:require
    [clj-record.serialization :as serialization]
    [clj-record.test-model.manufacturer :as manufacturer])
  (:use clojure.test
        clj-record.test-helper))


(defdbtest serialized-attributes-support-common-clojure-types
  (restoring-ref (manufacturer/model-metadata)
    (serialization/serialize-attribute "manufacturer" :name)
    (let [record (manufacturer/create valid-manufacturer)]
      (are [value] (=
        (do (manufacturer/update (assoc record :name value)) value)
        ((manufacturer/get-record (record :id)) :name))
        "some string"
        23
        [1 2 3]))))

(deftest expand-init-option-creates-a-call-to-serialize-attribute
  (is (=
    '(clj-record.serialization/serialize-attribute "foo" :bar)
    (serialization/expand-init-option "foo" :bar))))
