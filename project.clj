(defproject dhall-clj "0.1.0"
  :description "Dhall compiler to and from Clojure"
  :url "https://github.com/f-f/dhall-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git" :url "https://github.com/f-f/dhall-clj"}
  :dependencies [[org.clojure/clojure "1.10.0-alpha4"]
                 [org.clojure/spec.alpha "0.2.176"]
                 [org.clojure/tools.reader "1.3.0"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [me.raynes/fs "1.4.6"]
                 [digest "1.4.8"]
                 [lambdaisland/uri "1.1.0"]
                 [clj-http "3.9.1"]
                 [cc.qbits/ex "0.1.3"]
                 [com.gfredericks/catch-data "0.2.0"]
                 [mvxcvi/clj-cbor "0.6.0"]
                 [medley "1.0.0"]
                 ;; We require glow here only so that we can exclude instaparse
                 ;; during Cloverage execution. See cloverage/cloverage/issues/109
                 ;; and technomancy/leiningen/issues/2131
                 [venantius/glow "0.1.5" :exclusions [instaparse]]
                 [f-f/instaparse "1.4.9-patch-alt"]]
  :plugins [[lein-shell "0.5.0"]
            [lein-cloverage "1.0.13" :exclusions [instaparse]]]
  :prep-tasks [["shell" "cp" "dhall-lang/standard/dhall.abnf" "resources/"] "javac" "compile"]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.22.0"]]}
             :uberjar {:aot :all}}
  :test-paths ["test"]
  :test-refresh {:quiet true
                 :changes-only true})
