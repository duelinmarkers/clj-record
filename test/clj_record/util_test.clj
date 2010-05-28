(ns clj-record.util-test
  (:require
    [clj-record.util :as util])
  (:use clojure.test))


(deftest singularize
  (are [singular plural] (= singular (util/singularize plural))
    "foo" "foos"
    "baby" "babies"
    "beach" "beaches"
    "box" "boxes"
    "bush" "bushes"
    "bus" "buses"
    "visit" "visits"
    "type" "types"))

(deftest pluralize
  (are [plural singular] (= plural (util/pluralize singular))
    "foos" "foo"
    "beaches" "beach"
    "babies" "baby"
    "boxes" "box"
    "bushes" "bush"
    "buses" "bus"
    "types" "type"))

(deftest provides-id-queries-for-various-drivers
  (are [id-query db-spec] (= id-query (util/id-query-for db-spec "table_name"))
    "VALUES IDENTITY_VAL_LOCAL()" {:classname "org.apache.derby.jdbc.EmbeddedDriver" :subprotocol "derby"}
    "SELECT currval(pg_get_serial_sequence('table_name','id'))" {:classname "org.postgresql.Driver" :subprotocol "postgresql"}
    "SELECT LAST_INSERT_ID()"     {:classname "com.mysql.jdbc.Driver" :subprotocol "mysql"}
    "CALL IDENTITY()"             {:subprotocol "h2"}))

(deftest throws-if-asked-for-id-query-of-unrecognized-db-spec
  (is (thrown? Exception
    (util/id-query-for {:classname "com.example.MadeUpDriver" :subprotocol "madeup"}))))

(defmethod util/id-query-for "fake subprotocol" [db-spec table-name]
  (str "made up query"))

(deftest provides-id-query-for-whatever-dbms-you-like-if-you-defmethod-for-it
  (is (= "made up query" (util/id-query-for {:subprotocol "fake subprotocol"} "table_name"))))


