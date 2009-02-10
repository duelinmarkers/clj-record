(ns clj-record.util
  (:use clojure.contrib.test-is)
  (:use clojure.contrib.str-utils))


(defn singularize [plural]
  (if (.endsWith plural "ies")
    (re-sub #"ies$" "y" plural)
    (re-sub #"s$" "" plural)))

(defn pluralize [word]
  (if (.endsWith word "y")
    (re-sub #"y$" "ies" word)
    (str word "s")))

(defn postgresql-id-sequence-name [table-name]
  (str table-name "_id_seq"))

(defn id-query-for [{:keys [subprotocol] :as db-spec} table-name]
  (cond
    (= subprotocol "derby")
      "VALUES IDENTITY_VAL_LOCAL()"
    (= subprotocol "postgresql")
      (str "SELECT last_value FROM " (postgresql-id-sequence-name table-name))
    (= subprotocol "mysql")
      "SELECT LAST_INSERT_ID()"
    :else
      (throw (Exception. (str "Unrecognized db-spec: " db-spec)))))

