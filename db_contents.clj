(require 'clj-record.test-model.config)
(require 'clojure.contrib.sql)


(println "manufacturers")
(clojure.contrib.sql/with-connection clj-record.test-model.config/db
  (clojure.contrib.sql/with-query-results rows ["select * from manufacturers"]
    (doseq [row rows] (println row))))

(println)

(println "products")
(clojure.contrib.sql/with-connection clj-record.test-model.config/db
  (clojure.contrib.sql/with-query-results rows ["select * from productos"]
    (doseq [row rows] (println row))))
