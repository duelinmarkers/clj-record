(ns model.manufacturer
  (:require [clj_record.core :as cljrec]))


(defn find-record [id]
  (cljrec/find-record :manufacturer id))

(defn create [attributes]
  (cljrec/create :manufacturer attributes))
