(ns clj-record.callbacks
  (:use clj-record.meta))


(defn expand-init-option [model-name hook func]
  `(add-callback ~model-name ~hook ~func))

(defn add-callback [model-name hook func]
  (dosync
    (let [callbacks (or (model-metadata-for model-name :callbacks) {})]
      (set-model-metadata-for model-name :callbacks
        (merge-with concat callbacks {hook [func]})))))

(defn callback-functions [model-name hooks]
  (let [callbacks (model-metadata-for model-name :callbacks)]
    (reduce #(concat %1 (%2 callbacks)) [] hooks)))

(defn run-callbacks [record model-name & hooks]
  (loop [r record
         funcs (callback-functions model-name hooks)]
    (if (empty? funcs) r
      (recur ((first funcs) r) (rest funcs)))))
