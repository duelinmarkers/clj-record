(ns clj-record.callbacks
  (:use clj-record.meta))


(defn expand-init-option [model-name hook func & ignored-options]
  `(add-callback ~model-name ~hook ~func))

(defn add-callback [model-name hook func]
  (dosync
    (let [callbacks (or (model-metadata-for model-name :callbacks) {})]
      (set-model-metadata-for model-name :callbacks
        (merge-with concat callbacks {hook [func]})))))

(defn run-callbacks [record model-name & hooks]
  (let [callback-map (or (model-metadata-for model-name :callbacks) {})]
    (reduce #(%2 %1) record (filter identity (flatten (map callback-map hooks))))))

(defn after-destroy
  "Runs the after destroy call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :after-destroy))

(defn after-insert
  "Runs the after insert call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :after-insert))

(defn after-load
  "Runs the after load call back on all of the given rows."
  [model-name rows]
  (map #(run-callbacks (merge {} %) model-name :after-load) rows))

(defn after-save
  "Runs the after save call back on all of the given rows."
  [model-name attributes]
  (run-callbacks attributes model-name :after-save))

(defn after-update
  "Runs the after update call back on all of the given rows."
  [model-name attributes]
  (run-callbacks attributes model-name :after-update))

(defn after-validation
  "Runs the after validation call back on all of the given rows."
  [model-name attributes]
  (run-callbacks attributes model-name :after-validation))

(defn before-destroy
  "Runs the before destroy call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :before-destroy))

(defn before-insert
  "Runs the before insert call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :before-insert))

(defn before-save
  "Runs the before save call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :before-save))

(defn before-update
  "Runs the before update call back on the given attributes."
  [model-name attributes]
  (run-callbacks attributes model-name :before-update))

(defn before-validation
  "Runs the before validation call back on all of the given rows."
  [model-name attributes]
  (run-callbacks attributes model-name :before-validation))