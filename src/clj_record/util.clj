(ns clj-record.util
  (:use clojure.contrib.str-utils))

(defn singularize [plural]
  (let [lc (.toLowerCase plural)]
    (cond
      (.endsWith lc "ies") (re-sub #"ies$" "y" lc)
      (.endsWith lc "es") (re-sub #"es$" "" lc)
      :else (re-sub #"s" "" lc))))

(defn pluralize [word]
  (let [lc (.toLowerCase word)]
    (cond
      (.endsWith lc "y") (re-sub #"y$" "ies" lc)
      (some #(.endsWith lc %) ["s" "z" "ch" "sh" "x"]) (str lc "es")
      :else (str lc "s"))))

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
