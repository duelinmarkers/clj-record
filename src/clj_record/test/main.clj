(ns clj-record.test.main
  (:gen-class)
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.test-is :as test-is]
            [clojure.contrib.str-utils :as str-utils]
            clj-record.test.model.config))


(defmulti get-id-key-spec :subprotocol)
(defmethod get-id-key-spec "derby" [db-spec name]
  [:id :int (str "GENERATED ALWAYS AS IDENTITY CONSTRAINT " name " PRIMARY KEY")])
(defmethod get-id-key-spec :default [db-spec name]
  [:id "SERIAL UNIQUE PRIMARY KEY"])

(defn drop-tables []
  (try
    (sql/drop-table :manufacturers)
    (sql/drop-table :productos)
    (catch Exception e)))

(defn create-tables []
  (sql/create-table :manufacturers
    (get-id-key-spec clj-record.test.model.config/db "manufacturer_pk")
    [:name    "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade   :int])
  (sql/create-table :productos
    (get-id-key-spec clj-record.test.model.config/db "product_pk")
    [:name            "VARCHAR(32)" "NOT NULL"]
    [:price           :int]
    [:manufacturer_id :int "NOT NULL"]))

(defn -main [test-dirname]
  (sql/with-connection clj-record.test.model.config/db
    (sql/transaction
      (drop-tables)
      (create-tables)))
  (println (str (clj-record.test.model.config/db :subprotocol) " database setup complete."))
  (let [test-files  (for [f (-> test-dirname java.io.File. .listFiles)
                          :when (re-find #"test.clj$" (.getPath f))]
                      f)
        test-namespaces (map #(symbol (str "clj-record.test." (str-utils/re-gsub #"_" "-" (re-find #"[^.]+" (.getName %))))) test-files)]
    (doseq [file test-files]
      (load (str-utils/re-sub #"src/" "/" (re-find #"[^.]+" (.getPath file)))))
    (apply test-is/run-tests test-namespaces)))

