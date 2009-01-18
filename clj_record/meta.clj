(ns clj-record.meta)


(def all-models-metadata (ref {}))

(defn init-model-metadata [model-name]
  (dosync (commute all-models-metadata assoc model-name (ref {}))))

(defn model-metadata-for [model-name category]
  (@(@all-models-metadata model-name) category))

(defn set-model-metadata-for [model-name category value]
  (let [model-ref (@all-models-metadata model-name)]
    (ref-set model-ref (assoc @model-ref category value))))
