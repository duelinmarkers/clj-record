(ns clj-record.boot
  "Requiring this one namespace will require everything needed to use clj-record."
  (:require (clj-record
    core
    callbacks
    associations
    validation
    serialization)))
