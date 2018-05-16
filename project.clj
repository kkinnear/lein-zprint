(defproject lein-zprint "0.3.10"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License"
	    :url "https://opensource.org/licenses/MIT"
	    :key "mit"
	    :year 2015}
  :plugins [[lein-expectations "0.0.8"]
            [lein-zprint "0.3.9"]]
  :zprint {:old? false}
  :eval-in-leiningen true
  :jvm-opts ^:replace ["-server" "-Xss500m"]
  :profiles {:dev {:dependencies [#_[expectations "2.0.16"] [expectations "2.2.0-rc3"]]}}
  :dependencies [[zprint "0.4.10"]
                 [clojure-future-spec "1.9.0-alpha17"]
                 [cprop "0.1.6"]
                 [me.raynes/fs "1.4.6"]])
