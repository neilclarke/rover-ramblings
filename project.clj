(defproject rover "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]]

  :source-paths ["src/clj"]

 :profiles {:dev {:source-paths ["dev"]
                  :main rover.core}
            :uberjar {:aot [rover.core]
                      :main rover.core}})
