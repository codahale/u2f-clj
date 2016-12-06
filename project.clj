(defproject com.codahale/u2f-clj "0.1.0-SNAPSHOT"
  :description "A library for implementing FIDO U2F."
  :url "https://github.com/codahale/u2f-clj"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.yubico/u2flib-server-core "0.16.0"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :jvm-opts ["-Djava.awt.headless=true"]
  :profiles {:dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             :profiles/dev  {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :profiles/test {}
             :project/dev   {:source-paths ["dev"]
                             :repl-options {:init-ns user}}
             :project/test  {:dependencies []}})
