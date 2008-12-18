(ns model.make
  (:require [clojure.contrib.sql :as sql]))

(defn create [attributes]
  (let [key-vector (keys attributes) 
        val-vector (map attributes key-vector)]
    (sql/with-connection db
      (sql/transaction
        (sql/insert-values :makes key-vector val-vector)
        (sql/with-results rows "select * from makes where id = IDENTITY_VAL_LOCAL()" 
          (merge {} (first rows)))))))

