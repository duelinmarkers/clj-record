(ns clj-record.test.util-test
  (:require
    [clj-record.util :as util])
  (:use clojure.contrib.test-is))


(deftest singularize (are (= _1 (util/singularize _2))
    "foo" "foos"
    "baby" "babies"
    "beach" "beaches"
    "box" "boxes"
    "bush" "bushes"
    "bus" "buses"))

(deftest pluralize (are (= _1 (util/pluralize _2))
    "foos" "foo"
    "beaches" "beach"
    "babies" "baby"
    "boxes" "box"
    "bushes" "bush"
    "buses" "bus"))

(deftest provides-id-query-for-derby
  (are (= _1 (util/id-query-for _2 "table_name"))
    "VALUES IDENTITY_VAL_LOCAL()" {:classname "org.apache.derby.jdbc.EmbeddedDriver" :subprotocol "derby"}))

(deftest provides-id-query-for-postgresql
  (are (= _1 (util/id-query-for _2 "table_name"))
    "SELECT currval(pg_get_serial_sequence('table_name','id'))" {:classname "org.postgresql.Driver" :subprotocol "postgresql"}))

(deftest provides-id-query-for-mysql
  (are (= _1 (util/id-query-for _2 "table_name"))
    "SELECT LAST_INSERT_ID()" {:classname "com.mysql.jdbc.Driver" :subprotocol "mysql"}))

(deftest throws-if-asked-for-id-query-of-unrecognized-db-spec
  (is (thrown? Exception
    (util/id-query-for {:classname "com.example.MadeUpDriver" :subprotocol "madeup"}))))
