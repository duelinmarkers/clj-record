(ns clj-record.meta
  "Functions for managing all the metadata specified in (init-model ...) forms,
  which are stored in this namespace's all-models-metadata ref. Nothing in this
  namespace is likely to be used by code that uses clj-record.

  Note that clj-record's model-metadata has nothing to do with Clojure metadata.
  Suggestions of a reasonable name that's not overloaded are appreciated.")


(def all-models-metadata (ref {}))

(defn init-model-metadata [model-name]
  (dosync (commute all-models-metadata assoc model-name (ref {}))))

(defn model-metadata-for
  "Given only model-name, returns the ref to that model's metadata map.
  Given a model-name and category, derefs and extracts just that category's value from the model's metadata map."
  ([model-name]
    (@all-models-metadata model-name))
  ([model-name category]
    (@(model-metadata-for model-name) category)))

(defn set-model-metadata-for [model-name category value]
  (let [model-ref (@all-models-metadata model-name)]
    (ref-set model-ref (assoc @model-ref category value))))

(defn db-spec-for [model-name]
  (let [db-spec (model-metadata-for model-name :db-spec)]
    (if (fn? db-spec)
      (db-spec)
      db-spec)))
