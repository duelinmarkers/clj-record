(ns clj-record.callbacks
  (:use clj-record.meta)
  (:require [clojure.contrib.seq-utils :as seq-utils]))


(defn expand-init-option [model-name hook func]
  `(add-callback ~model-name ~hook ~func))

(defn add-callback [model-name hook func]
  (dosync
    (let [callbacks (or (model-metadata-for model-name :callbacks) {})]
      (set-model-metadata-for model-name :callbacks
        (merge-with concat callbacks {hook [func]})))))

(defn run-callbacks [record model-name & hooks]
  (let [callback-map (or (model-metadata-for model-name :callbacks) {})]
    (reduce #(%2 %1) record (filter identity (seq-utils/flatten (map callback-map hooks))))))