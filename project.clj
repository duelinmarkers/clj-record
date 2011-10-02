(defproject clj-record "1.0-SNAPSHOT"
  :description "A pseudo-port of ActiveRecord to the Clojure programming language"
  :url "http://github.com/duelinmarkers/clj-record"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure/swank-clojure "1.2.1"]
                     [mysql/mysql-connector-java "5.1.17"]])


(ns leiningen.reset-db
  (:require leiningen.compile))

(defn reset-db [project]
  (leiningen.compile/eval-in-project project
    '(do
      (clj-record.test-helper/reset-db))
    nil nil (require 'clj-record.test-helper)))
