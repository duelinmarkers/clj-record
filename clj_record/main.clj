(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj_record.test.db"
         :create true})

(ns clj_record.main
  (:require [clojure.contrib.sql :as sql]
            [model.manufacturer :as manufacturer]))


(defn drop-manufacturer []
  (try
    (sql/drop-table :manufacturer)
    (catch Exception e)))

(defn create-manufacturer []
  (sql/create-table :manufacturer
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT manufacturer_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade :real]))

(defn insert-manufacturers []
  (sql/insert-values :manufacturer
    [:name :founded :grade]
    ["Ford"      "1904" 60]
    ["Chevrolet" "1912" 88.6]
    ["Honda"     "1972" 92.2]
    ["Acura"     "1988" 90.0]))

(sql/with-connection db
  (sql/transaction
    (drop-manufacturer)
    (create-manufacturer)
    (insert-manufacturers)))

(sql/with-connection db
  (sql/with-results res
   "select * from manufacturer"
    (doseq [rec res]
      (println rec))))

(println "Setup complete!")

(println (str "(manufacturer/table-name) is " (manufacturer/table-name)))
(println (str "(manufacturer/find-record 1) returned " (manufacturer/find-record 1)))
(println (str "(manufacturer/create ...) returned " (manufacturer/create {:name "GM" :grade 45})))
