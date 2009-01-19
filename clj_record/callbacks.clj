(ns clj-record.callbacks
  (:use clj-record.meta))


(defn expand-init-option [model-name hook func]
  `(add-callback ~model-name ~hook ~func))

(defn add-callback [model-name hook func]
  (dosync
    (let [callbacks (or (model-metadata-for model-name :callbacks) {})]
      (set-model-metadata-for model-name :callbacks
        (merge-with concat callbacks {hook [func]})))))

(defn run-callbacks [record model-name & hooks]
  (let [callbacks (model-metadata-for model-name :callbacks)]
    (loop [r record
           funcs (reduce #(concat %1 (%2 callbacks)) [] hooks)]
      (if (empty? funcs) r
        (recur ((first funcs) r) (rest funcs))))))
