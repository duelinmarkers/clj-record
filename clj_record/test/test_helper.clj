(ns clj-record.test.test-helper
  (:use clojure.contrib.test-is))


(defmacro defdbtest [name & body]
  `(deftest ~name
    (io! "DB test running. No STM allowed."
      (clojure.contrib.sql/with-connection clj-record.config/db
        (clojure.contrib.sql/transaction
          (try
            ~@body
            (finally
              (clojure.contrib.sql/set-rollback-only))))))))
