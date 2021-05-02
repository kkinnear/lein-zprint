(defproject lein-zprint "1.2.0"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License",
            :url "https://opensource.org/licenses/MIT",
            :key "mit",
            :year 2015}
  :plugins [[lein-expectations "0.0.8"] [lein-zprint "1.0.2"]]
  :zprint {:old? false}
  :eval-in-leiningen true
  :jvm-opts ^:replace ["-server" "-Xss500m"]
  :profiles {:dev {:dependencies [[expectations "2.2.0-rc3"]]}}
  :dependencies [[zprint "1.2.0"]
                 #_[clojure-future-spec "1.9.0-alpha17"]
                 [me.raynes/fs "1.4.6"]])
