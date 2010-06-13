(ns clj-record.associations-test
  (:require
    [clj-record.test-model.manufacturer :as manufacturer]
    [clj-record.test-model.product :as product])
  (:use clojure.test
        clj-record.test-helper))

(use-fixtures :once (reset-db-fixture [:manufacturers :productos]))

(defdbtest belongs-to-creates-find-function
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Automotive"}))
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})]
    (is (= humedai (product/find-manufacturer s3000xi)))))

(defdbtest has-many-creates-a-find-function
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Automotive"}))
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})
        s3000xl (product/create {:name "S-3000xl" :manufacturer_id (:id humedai)})]
    (is (= [s3000xi s3000xl] (manufacturer/find-products humedai)))))

(defdbtest has-many-creates-a-destroy-function
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Automotive"}))
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})
        s3000xl (product/create {:name "S-3000xl" :manufacturer_id (:id humedai)})]
    (manufacturer/destroy-products humedai)
    (is (empty? (manufacturer/find-products humedai)))))

(comment
(defdbtest find-records-can-do-eager-fetching-of-has-many-association
  (let [manu1 (manufacturer/create (valid-manufacturer-with {:name "manu1" :grade 99}))
        prod1 (product/create {:name "prod1" :manufacturer_id (:id manu1)})
        prod2 (product/create {:name "prod2" :manufacturer_id (:id manu1)})
        manu2 (manufacturer/create (valid-manufacturer-with {:name "manu2" :grade 99}))
        prod3 (product/create {:name "prod3" :manufacturer_id (:id manu2)})
        prod4 (product/create {:name "prod4" :manufacturer_id (:id manu2)})]
    (let [[eager-manu1 eager-manu2] (manufacturer/find-records {:grade 99} {:include [:products]})]
      (is (= "manu1" (:name eager-manu1)))
      (is (= "manu2" (:name eager-manu2)))
      (is (= [prod1 prod2] (:products eager-manu1)))
      (is (= [prod3 prod4] (:products eager-manu2))))))
)