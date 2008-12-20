(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj_record.test.db"
         :create true})

(ns clj_record.main
  (:require [clojure.contrib.sql :as sql]
            [model.manufacturer :as manufacturer]
            [model.product :as product]))


(defn drop-tables []
  (try
    (sql/drop-table :manufacturer)
    (sql/drop-table :product)
    (catch Exception e)))

(defn create-tables []
  (sql/create-table :manufacturer
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT manufacturer_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade :real])
  (sql/create-table :product
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT product_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:price "INT"]
    [:manufacturer_id "INT" "NOT NULL"]))

(defn insert-manufacturers []
  (sql/insert-values :manufacturer
    [:name :founded :grade]
    ["Ford"      "1904" 60]
    ["Chevrolet" "1912" 88.6]
    ["Honda"     "1972" 92.2]
    ["Acura"     "1988" 90.0]))

(sql/with-connection db
  (sql/transaction
    (drop-tables)
    (create-tables)
    (insert-manufacturers)))

(sql/with-connection db
  (sql/with-results res
   "select * from manufacturer"
    (doseq [rec res]
      (println rec))))

(println "Setup complete!")

(println (str "(manufacturer/table-name) is " (manufacturer/table-name)))
(println (str "(manufacturer/find-record 1) returned " (manufacturer/find-record 1)))
(let [gm (manufacturer/create {:name "GM" :grade 45})]
  (println (str "(manufacturer/create ...) returned " gm))
  (println (product/create {:name "K Car" :price 4000 :manufacturer_id (gm :id)}))
  (println (manufacturer/find-products gm)))


(println "Finished happy!")


