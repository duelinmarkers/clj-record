(ns clj-record.config)


(def db {:classname "org.apache.derby.jdbc.EmbeddedDriver"
         :subprotocol "derby"
         :subname "/tmp/clj-record.test.db"
         :create true})
