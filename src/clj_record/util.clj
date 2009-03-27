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

(defn id-query-for [{:keys [subprotocol] :as db-spec} table-name]
  (cond
    (= subprotocol "derby")
      "VALUES IDENTITY_VAL_LOCAL()"
    (= subprotocol "postgresql")
      (str "SELECT currval(pg_get_serial_sequence('" table-name "','id'))")
    (= subprotocol "mysql")
      "SELECT LAST_INSERT_ID()"
    :else
      (throw (Exception. (str "Unrecognized db-spec: " db-spec)))))
