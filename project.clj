(defproject advent-of-code "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/data.codec "0.1.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [org.clojure/math.combinatorics "0.1.6"]
                 [criterium "0.4.6"]
                 [cheshire "5.10.0"]
                 [simple-time "0.2.0"]
                 [ubergraph "0.8.2"]]
  :main ^:skip-aot advent-of-code.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
