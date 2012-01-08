(ns clj-record.serialization
  (:require [clj-record.callbacks :as callbacks]
            [clj-record.callbacks.built-ins :as callb]))


(defn serialize-attribute [model-name attribute]
  (callbacks/add-callback model-name :before-save (callb/transform-value attribute pr-str))
  (callbacks/add-callback model-name :after-load (callb/transform-value attribute read-string)))

(defn expand-init-option
  "init-model macro-expansion delegate that generates a call to add-validation."
  [model-name attribute-name & ignored-options]
  `(serialize-attribute ~model-name ~attribute-name))
