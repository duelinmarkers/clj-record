(ns clj_record.test.model.manufacturer
  (:require [clj_record.core :as cljrec])
  (:use clojure.contrib.test-is))


(cljrec/init-model
  (has-many products))

(defn good? 
  ([manufacturer] 
    (and (:grade manufacturer) (>= (:grade manufacturer) 90)))
  {:test (fn []
    (are (good? {:grade _1})
      90 90.0 91)
    (are (not (good? {:grade _1}))
      89 80 nil))})
