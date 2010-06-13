(ns clj-record.test-model.person
  (:require clj-record.boot)
  (:use clj-record.test-model.config))

(clj-record.core/init-model
 :table-name "person"
  (:associations
    (belongs-to mother :model person)                        ; default fk name mother_id is correct
    (belongs-to father :fk father_person_id :model person)   ; override default fk name
    (has-many things :fk owner_person_id :model thing-one)))
