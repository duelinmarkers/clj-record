(require 'clj-record.config)
(require 'clojure.contrib.sql)


(println "manufacturers")
(clojure.contrib.sql/with-connection clj-record.config/db
  (clojure.contrib.sql/with-query-results rows ["select * from manufacturers"]
    (doseq [row rows] (println row))))

(println)

(println "products")
(clojure.contrib.sql/with-connection clj-record.config/db
  (clojure.contrib.sql/with-query-results rows ["select * from products"]
    (doseq [row rows] (println row))))
