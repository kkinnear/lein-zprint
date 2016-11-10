(defproject lein-zprint "0.1.9"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License"
	    :url "https://opensource.org/licenses/MIT"
	    :key "mit"
	    :year 2015}
  :plugins [[lein-expectations "0.0.8"]
            [lein-zprint "0.1.8"]]
  :zprint {:old? false}
  :eval-in-leiningen true
  :profiles {:dev {:dependencies [[expectations "2.0.16"]]}}
  :dependencies [[zprint "0.2.9"]
		 [trptcolin/versioneer "0.1.0"]
                 [me.raynes/fs "1.4.6"]])
