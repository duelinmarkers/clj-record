(ns clj-record.test.test-helper
  (:require [clj-record.core :as core])
  (:use clojure.contrib.test-is))


(defmacro defdbtest [name & body]
  `(deftest ~name
    (rolling-back ~@body)))

(defmacro rolling-back [& body]
  `(core/transaction
    (try
      ~@body
      (finally
        (clojure.contrib.sql/set-rollback-only)))))

(defmacro restoring-ref [ref & body]
  `(let [old-value# (deref ~ref)]
    (try
      ~@body
      (finally
        (dosync (ref-set ~ref old-value#))))))

(def valid-manufacturer {:name "Valid Name" :founded "1999" :grade 99})

(defn valid-manufacturer-with [attributes] (merge valid-manufacturer attributes))
