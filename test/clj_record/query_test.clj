(ns clj-record.query-test
  (:require
    [clj-record.core :as core]
    [clj-record.query :as query]
    [clj-record.test-model.manufacturer :as manufacturer]
    [clj-record.test-model.product :as product])
  (:use clojure.test
        clj-record.test-helper))


(defn manufacturers [] 
  [ (manufacturer/create (valid-manufacturer-with {:name "Sozooke Motors" :grade 90}))
    (manufacturer/create (valid-manufacturer-with {:name "Foyoto Auto" :grade 78}))
    (manufacturer/create (valid-manufacturer-with {:name "GMB Motors" :grade 89}))
    (manufacturer/create (valid-manufacturer-with {:name "Ghysler Inc" :grade 60}))
    (manufacturer/create (valid-manufacturer-with {:name "Merledas Automotive" :grade 39}))])

(defdbtest find-records-using-equal
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke} (set (manufacturer/find-records {:grade (query/equal 90)}))))))

(defdbtest find-records-using-not-equal
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke foyoto ghysler merledas} (set (manufacturer/find-records {:name (query/not-equal "GMB Motors")}))))))

(defdbtest find-records-using-greater-than
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke gmb} (set (manufacturer/find-records {:grade (query/greater-than 80)}))))))

(defdbtest find-records-using-greater-than-or-equal
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke foyoto gmb ghysler} (set (manufacturer/find-records {:grade (query/greater-than-or-equal 60)}))))))

(defdbtest find-records-using-less-than
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{foyoto ghysler merledas} (set (manufacturer/find-records {:grade (query/less-than 80)}))))))

(defdbtest find-records-using-less-than-or-equal
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{ghysler merledas} (set (manufacturer/find-records {:grade (query/less-than-or-equal 60)}))))))

(defdbtest find-records-using-like
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{gmb ghysler} (set (manufacturer/find-records {:name (query/like "G%")}))))))

(defdbtest find-records-using-not-like
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{foyoto ghysler merledas} (set (manufacturer/find-records {:name (query/not-like "%Motors")}))))))

(defdbtest find-records-using-between
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{foyoto ghysler} (set (manufacturer/find-records {:grade (query/between 40 80)}))))))

(defdbtest find-records-using-not-between
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke gmb merledas} (set (manufacturer/find-records {:grade (query/not-between 40 80)}))))))

(defdbtest find-records-using-in
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{gmb merledas} (set (manufacturer/find-records {:name (query/in "GMB Motors" "Merledas Automotive")}))))))

(defdbtest find-records-using-not-in
  (let [[sozooke foyoto gmb ghysler merledas] (manufacturers)]
    (is (= #{sozooke foyoto ghysler} (set (manufacturer/find-records {:name (query/not-in "GMB Motors" "Merledas Automotive")}))))))

(deftest attempt-to-supply-nil-value
  (are [bad-call] (thrown? IllegalArgumentException bad-call)
    (query/equal nil)
    (query/not-equal nil)
    (query/greater-than nil)
    (query/between 3 nil)
    (query/between nil 3)))
