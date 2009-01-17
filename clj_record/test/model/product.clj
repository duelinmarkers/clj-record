(ns clj-record.test.model.product
  (:require [clj-record.core :as cljrec]))


(cljrec/init-model
  (:associations
    (belongs-to manufacturer)))
