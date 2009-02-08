(ns clj-record.test.model.config)

(comment
(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj-record.test.db"
         :create true})
)

(def db {:classname "org.postgresql.Driver" 
         :subprotocol "postgresql" 
         :subname "//localhost:5433/coffeemug"
         :user "enter-user-name-here"
         :password ""})