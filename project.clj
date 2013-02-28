(defproject sqloader "0.1.0-SNAPSHOT"
  :description "A library and client to load csv files to sql databases"
  :url "http://github.com/mihi-tr/sqloader"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
    [org.clojure/java.jdbc "0.2.3"]
    [postgresql "9.1-901.jdbc4"]
    [csv-map "0.1.0-SNAPSHOT"]])
