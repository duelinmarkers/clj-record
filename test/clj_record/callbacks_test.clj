(ns clj-record.callbacks-test
  (:require
    [clojure.set :as clj-set]
    [clj-record.test-model.manufacturer :as manufacturer]
    [clj-record.callbacks :as callbacks])
  (:use clojure.test
        clj-record.test-helper))


(def test-ref (ref {}))

(defn callback-called [callback-name]
  (dosync 
    (ref-set test-ref 
      (merge-with clj-set/union @test-ref { :called #{ callback-name } }))))

(defn callback-called? [callback-name]
  (contains? (:called @test-ref) callback-name))

(defmacro assure-called [model callback-name & body]
  `(restoring-ref (manufacturer/model-metadata)
    (restoring-ref test-ref
      (callbacks/add-callback ~model ~callback-name (fn [input#]  (callback-called ~callback-name) input#))
      ~@body
      (is (callback-called? ~callback-name)))))

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

(defdbtest destroy-record-calls-after-destroy?
  (let [m (manufacturer/create valid-manufacturer)]
    (assure-called "manufacturer" :after-destroy 
      (manufacturer/destroy-record m))))

(defdbtest destroy-records-calls-after-destroy?
  (let [m (manufacturer/create valid-manufacturer)]
    (assure-called "manufacturer" :after-destroy 
      (manufacturer/destroy-records m))))

(defdbtest insert-calls-after-insert?
  (assure-called "manufacturer" :after-insert 
    (manufacturer/insert valid-manufacturer)))

(defdbtest insert-calls-after-save?
  (assure-called "manufacturer" :after-save 
    (manufacturer/insert valid-manufacturer)))

(defdbtest update-calls-after-update?
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (assure-called "manufacturer" :after-update 
      (manufacturer/update { :id (:id m), :founded "2008" }))))

(defdbtest update-calls-after-save?
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (assure-called "manufacturer" :after-save 
      (manufacturer/update { :id (:id m), :founded "2008" }))))

(defdbtest validate-calls-after-validation?
  (assure-called "manufacturer" :after-validation 
    (manufacturer/validate valid-manufacturer)))

(defdbtest destroy-record-calls-before-destroy?
  (let [m (manufacturer/create valid-manufacturer)]
    (assure-called "manufacturer" :before-destroy 
      (manufacturer/destroy-record m))))

(defdbtest destroy-records-calls-before-destroy?
  (let [m (manufacturer/create valid-manufacturer)]
    (assure-called "manufacturer" :before-destroy 
      (manufacturer/destroy-records m))))

(defdbtest insert-calls-before-insert?
  (assure-called "manufacturer" :before-insert 
    (manufacturer/insert valid-manufacturer)))

(defdbtest insert-calls-before-save?
  (assure-called "manufacturer" :before-save 
    (manufacturer/insert valid-manufacturer)))

(defdbtest update-calls-before-update?
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (assure-called "manufacturer" :before-update 
      (manufacturer/update { :id (:id m), :founded "2008" }))))

(defdbtest update-calls-before-save?
  (let [m (manufacturer/create (valid-manufacturer-with {:founded "2008"}))]
    (assure-called "manufacturer" :before-save 
      (manufacturer/update { :id (:id m), :founded "2008" }))))

(defdbtest validate-calls-before-validation?
  (assure-called "manufacturer" :before-validation
    (manufacturer/validate valid-manufacturer)))

(defdbtest delete-records-does-not-call-before-destroy
  (restoring-ref (manufacturer/model-metadata)
    (restoring-ref test-ref
      (callbacks/add-callback "manufacturer" :before-destroy (fn [r]  (callback-called :before-destroy) r))
      (manufacturer/create valid-manufacturer)
      (manufacturer/delete-records valid-manufacturer)
      (is (not (callback-called? :before-destroy))))))
