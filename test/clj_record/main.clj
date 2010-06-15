(ns clj-record.main
  (:require [clojure.contrib.sql :as sql]
            [clojure.test :as test-is])
  (:use clj-record.test-helper
        clj-record.test-model.config
        clojure.contrib.str-utils))

(defn load-and-run
  "Resets the test database, then finds, loads, and runs all the tests."
  []
  (let [test-files  (for [f (.listFiles (java.io.File. "clj_record"))
                          :when (re-find #"test.clj$" (.getPath f))]
                      (re-find #"[^.]+" (.getPath f)))
        test-namespaces (map #(symbol (re-gsub #"/|\\" "." (re-gsub #"_" "-" %))) test-files)]
    (doseq [file test-files]
      (load file))
    (apply test-is/run-tests test-namespaces)))

