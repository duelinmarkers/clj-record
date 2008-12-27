(ns clj_record.test.model.product
  (:require [clj_record.core :as cljrec]))


(cljrec/init-model
  (belongs-to manufacturer))
