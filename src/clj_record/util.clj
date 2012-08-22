(ns clj-record.util
  "Assorted utilities for internal use."
  (:require [clojure.string :as string]))

(defn singularize [^String plural]
  (let [lc (.toLowerCase plural)]
    (condp re-find lc
      #"ies$" (string/replace lc #"ies$" "y")
      #"(s|z|ch|sh|x)es$" (string/replace lc #"(s|z|ch|sh|x)es$" "$1")
      #"s$" (string/replace lc #"s$" "")
      lc)))

(defn pluralize [^String word]
  (let [lc (.toLowerCase word)]
    (cond
      (.endsWith lc "y") (string/replace lc #"y$" "ies")
      (some #(.endsWith lc %) ["s" "z" "ch" "sh" "x"]) (str lc "es")
      :else (str lc "s"))))

(defn dashes-to-underscores [^String s]
  (.replaceAll s "-" "_"))

(defmulti  id-query-for :subprotocol)
(defmethod id-query-for "derby" [_ _]
  "VALUES IDENTITY_VAL_LOCAL()")
(defmethod id-query-for "postgresql" [_ table-name]
  (str "SELECT currval(pg_get_serial_sequence('" table-name "','id'))"))
(defmethod id-query-for "mysql" [_ _]
  "SELECT LAST_INSERT_ID()")
(defmethod id-query-for "h2" [_ _]
  "CALL IDENTITY()")
(defmethod id-query-for "sqlserver" [_ table-name]
 (str  "SELECT IDENT_CURRENT('" table-name "')"))
(defmethod id-query-for "db2" [_ _]
  "VALUES IDENTITY_VAL_LOCAL()")
(defmethod id-query-for :default [db-spec _]
  (throw (Exception. (str "Unrecognized db-spec subprotocol: " db-spec))))

