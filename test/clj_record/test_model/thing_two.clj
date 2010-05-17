(ns clj-record.test-model.thing-two
  (:require clj-record.boot)
  (:use clj-record.test-model.config))

(clj-record.core/init-model
 :table-name "thing_two"
  (:associations
    (belongs-to thing-one)))
