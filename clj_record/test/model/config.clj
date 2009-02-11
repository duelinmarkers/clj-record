(ns clj-record.test.model.config)

(def db {:classname "com.mysql.jdbc.Driver" 
         :subprotocol "mysql" 
         :user "root" 
         :password "root" 
         :subname "//localhost/test"})

(comment
(def db {:classname "org.postgresql.Driver" 
         :subprotocol "postgresql" 
         :subname "//localhost:5433/coffeemug"
         :user "enter-user-name-here"
         :password ""})

(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj-record.test.db"
         :create true})

)
