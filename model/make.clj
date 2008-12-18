(ns model.make
  (:require [clojure.contrib.sql :as sql]))


(defn find-record [id]
  (sql/with-connection db
    (sql/with-results rows (str "select * from make where id =" id)
      (merge {} (first rows)))))

(defn create [attributes]
  (sql/with-connection db
    (let
      [key-vector (keys attributes)
       val-vector (map attributes key-vector)
       id (sql/transaction
            (sql/insert-values :make key-vector val-vector)
            (sql/with-results rows "VALUES IDENTITY_VAL_LOCAL()" (:1 (first rows))))]
      (find-record id))))
