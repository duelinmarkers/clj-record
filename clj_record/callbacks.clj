(ns clj-record.callbacks
  (:use clj-record.meta))


(defn expand-init-option [model-name hook func]
  `(add-callback ~model-name ~hook ~func))

(defn add-callback [model-name hook func]
  (dosync
    (let [callbacks (or (model-metadata-for model-name :callbacks) {})]
      (set-model-metadata-for model-name :callbacks
        (merge-with concat callbacks {hook [func]})))))

(defn run-callbacks [record model-name hook]
  (loop [r record
         funcs ((or (model-metadata-for model-name :callbacks) {}) hook)]
    (if (empty? funcs) r
      (recur ((first funcs) r) (rest funcs)))))
