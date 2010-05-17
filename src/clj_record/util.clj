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

(defn dashes-to-underscores [s]
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
(defmethod id-query-for :default [db-spec _]
  (throw (Exception. (str "Unrecognized db-spec subprotocol: " db-spec))))

