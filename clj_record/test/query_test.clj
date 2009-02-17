(ns clj-record.test.query-test
  (:require
    [clj-record.core :as core]
    [clj-record.query :as query]
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is
        clj-record.test.test-helper))


(defdbtest find-records-using-between
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors" :grade 90}))
	foyoto (manufacturer/create (valid-manufacturer-with {:name "Foyoto Auto" :grade 78}))
	gmb (manufacturer/create (valid-manufacturer-with {:name "GMB Motors" :grade 89}))
	ghysler (manufacturer/create (valid-manufacturer-with {:name "Ghysler Inc" :grade 59}))
	merledas (manufacturer/create (valid-manufacturer-with {:name "Merledas Automotive" :grade 39}))]
    (is (= #{ghysler foyoto} (set (manufacturer/find-records {:grade (query/between 40 80)}))))))

(defdbtest find-records-using-in
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))
	foyoto (manufacturer/create (valid-manufacturer-with {:name "Foyoto Auto"}))
	gmb (manufacturer/create (valid-manufacturer-with {:name "GMB Motors"}))
	ghysler (manufacturer/create (valid-manufacturer-with {:name "Ghysler Inc"}))
	merledas (manufacturer/create (valid-manufacturer-with {:name "Merledas Automotive"}))]
    (is (= #{gmb merledas} (set (manufacturer/find-records {:name (query/in "GMB Motors" "Merledas Automotive")}))))))

(defdbtest find-records-using-like
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))
	foyoto (manufacturer/create (valid-manufacturer-with {:name "Foyoto Auto"}))
	gmb (manufacturer/create (valid-manufacturer-with {:name "GMB Motors"}))
	ghysler (manufacturer/create (valid-manufacturer-with {:name "Ghysler Inc"}))
	merledas (manufacturer/create (valid-manufacturer-with {:name "Merledas Automotive"}))]
    (is (= #{gmb ghysler} (set (manufacturer/find-records {:name (query/like "G%")}))))))
