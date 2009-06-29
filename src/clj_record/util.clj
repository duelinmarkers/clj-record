(ns clj-record.util
  (:use (clojure.contrib str-utils)))

(defn singularize [plural]
  (let [lc (.toLowerCase plural)]
    (condp re-find lc
      #"ies$" (re-sub #"ies$" "y" lc)
      #"(s|z|ch|sh|x)es$" (re-sub #"(s|z|ch|sh|x)es$" "$1" lc)
      #"s$" (re-sub #"s$" "" lc)
      lc)))

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
