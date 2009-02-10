(ns clj-record.test.util-test
  (:require
    [clj-record.util :as util])
  (:use clojure.contrib.test-is))


(deftest singularize (are (= _1 (util/singularize _2))
    "foo" "foos"
    "baby" "babies"))

(deftest pluralize (are (= _1 (util/pluralize _2))
    "foos" "foo"
    "babies" "baby"))

(deftest provides-id-query-for-derby
  (are (= _1 (util/id-query-for _2 "table_name"))
    "VALUES IDENTITY_VAL_LOCAL()" {:classname "org.apache.derby.jdbc.EmbeddedDriver" :subprotocol "derby"}))

(deftest provides-id-query-for-postgresql
  (are (= _1 (util/id-query-for _2 "table_name"))
    "SELECT last_value FROM table_name_id_seq" {:classname "org.postgresql.Driver" :subprotocol "postgresql"}))

(deftest throws-if-asked-for-id-query-of-unrecognized-db-spec
  (is (thrown? Exception
    (util/id-query-for {:classname "com.example.MadeUpDriver" :subprotocol "madeup"}))))
