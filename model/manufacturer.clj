(ns model.manufacturer
  (:require [clj_record.core :as cljrec]))

(cljrec/init-model 'manufacturer
  (has-many products))
