(defproject clj-record "1.0-SNAPSHOT"
  :description "A pseudo-port of ActiveRecord to the Clojure programming language"
  :url "http://github.com/duelinmarkers/clj-record"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]]
  :dev-dependencies [[swank-clojure/swank-clojure "1.2.1"]
                     [org.apache.derby/derby "10.5.3.0_1"]])


(ns leiningen.reset-db
  (:require leiningen.compile))

(defn reset-db [project]
  (leiningen.compile/eval-in-project project
    '(do
      (require 'clj-record.test-helper)
      (clj-record.test-helper/reset-db))))
