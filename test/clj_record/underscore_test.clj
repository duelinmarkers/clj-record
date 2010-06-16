(ns clj-record.underscore-test
  (:require
    [clj-record.test-model.thing-one :as thing-one]
    [clj-record.test-model.thing-two :as thing-two])
  (:use clojure.test
        clj-record.test-helper))


(defdbtest nothing-blows-up
  (let [t1-rec1 (thing-one/create {:name "foo"})
        t2-rec1 (thing-two/create {:thing_one_id (:id t1-rec1)})
        t2-rec2 (thing-two/create {:thing_one_id (:id t1-rec1)})]
    (is (= 1 (:count (first (thing-one/find-by-sql ["select count(*) as count from thing_one"])))))
    (is (= 2 (:count (first (thing-two/find-by-sql ["select count(*) as count from thing_two"])))))))

(defdbtest belongs-to-works
  (let [t1-rec1 (thing-one/create {:name "foo"})
        t2-rec1 (thing-two/create {:thing_one_id (:id t1-rec1)})
        t2-rec2 (thing-two/create {:thing_one_id (:id t1-rec1)})]
    (is (= t1-rec1 (thing-two/find-thing-one t2-rec1)))
    (is (= t1-rec1 (thing-two/find-thing-one t2-rec2)))))

(defdbtest has-many-works
  (let [t1-rec1 (thing-one/create {:name "foo"})
        t2-rec1 (thing-two/create {:thing_one_id (:id t1-rec1)})
        t2-rec2 (thing-two/create {:thing_one_id (:id t1-rec1)})]
    (is (= [t2-rec1 t2-rec2] (thing-one/find-thing-twos t1-rec1)))))
