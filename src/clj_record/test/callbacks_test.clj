(ns clj-record.test.callbacks-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.callbacks :as callbacks])
  (:use clojure.contrib.test-is
        clj-record.test.test-helper))


(defdbtest before-save-can-transform-the-record-before-create
  (let [m (manufacturer/create {:name "A" :founded "68"})]
    (is (= "1968" (m :founded)))))

(defdbtest before-save-can-transform-the-record-before-update
  (let [m (manufacturer/create {:name "A" :founded "1968"})
        id (m :id)]
    (manufacturer/update {:id id :founded "68"})
    (is (= "1968" ((manufacturer/get-record id) :founded)))))

(deftest before-update-can-transform-record
  (restoring-ref (manufacturer/model-metadata)
    (callbacks/add-callback "manufacturer" :before-update #(assoc % :founded "0000"))
    (rolling-back
      (let [m (manufacturer/create {:name "A" :founded "2008"})]
        (is (= "2008" (:founded m)))
        (is (= "0000" (:founded (manufacturer/update (select-keys m [:id :founded])))))))))

(defdbtest after-load-can-transform-record-after-a-find
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (restoring-ref (manufacturer/model-metadata)
      (callbacks/add-callback "manufacturer" :after-load #(assoc % :founded "0000"))
      (is (= "0000" ((first (manufacturer/find-records {:founded "2008"})) :founded))))))

(defdbtest after-load-can-transform-record-after-a-get
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (restoring-ref (manufacturer/model-metadata)
      (callbacks/add-callback "manufacturer" :after-load #(assoc % :founded "0000"))
      (is (= "0000" ((manufacturer/get-record (m :id)) :founded))))))
