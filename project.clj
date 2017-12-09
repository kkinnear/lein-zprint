(defproject lein-zprint "0.3.6"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License"
	    :url "https://opensource.org/licenses/MIT"
	    :key "mit"
	    :year 2015}
  :plugins [[lein-expectations "0.0.8"]
            [lein-zprint "0.3.4"]]
  :zprint {:old? false}
  :eval-in-leiningen true
  :profiles {:dev {:dependencies [[expectations "2.0.16"]]}}
  :dependencies [[zprint "0.4.5"]
                 [clojure-future-spec "1.9.0-alpha17"]
                 [cprop "0.1.6"]
                 [me.raynes/fs "1.4.6"]])
