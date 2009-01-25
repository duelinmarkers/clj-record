(ns clj-record.test.model.product
  (:require clj-record.boot))


(clj-record.core/init-model
  :table-name "productos"
  (:associations
    (belongs-to manufacturer)))
