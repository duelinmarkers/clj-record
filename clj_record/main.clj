(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj_record.test.db"
         :create true})

(ns clj_record.main
  (:require [clojure.contrib.sql :as sql]
            [model.make :as make]))


(defn drop-makes []
  (try
    (sql/drop-table :makes)
    (catch Exception e)))

(defn create-makes []
  (sql/create-table :makes
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT makes_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade :real]))

(defn insert-makes []
  (sql/insert-values :makes
    [:name :founded :grade]
    ["Ford"      "1904" 60]
    ["Chevrolet" "1912" 88.6]
    ["Honda"     "1972" 92.2]
    ["Acura"     "1988" 90.0]))

(sql/with-connection db
  (sql/transaction
    (drop-makes)
    (println "dropped makes")
    (create-makes)
    (println "created makes")
    (insert-makes)
    (println "inserted 4 makes")))

(sql/with-connection db
  (sql/with-results res
   "select * from makes"
    (doseq [rec res]
      (println rec))))

(println "Setup complete. Woo hoo!")

(println (str "make/create returned " (make/create {:name "GM" :grade 45})))
