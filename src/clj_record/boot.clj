(ns
  #^{:doc "Requiring this one namespace will require everything needed to use clj-record."}
  clj-record.boot
  (:require (clj-record
    core
    callbacks
    associations
    validation
    serialization)))
