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
