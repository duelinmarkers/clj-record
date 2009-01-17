(ns clj-record.test.model.product
  (:require clj-record.boot))


(clj-record.core/init-model
  (:associations
    (belongs-to manufacturer)))
