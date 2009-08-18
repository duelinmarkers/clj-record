(ns clj-record.test-model.product
  (:require clj-record.boot)
  (:use clj-record.test-model.config))


(clj-record.core/init-model
  :table-name "productos"
  (:associations
    (belongs-to manufacturer)))
