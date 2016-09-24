(defproject lein-zprint "0.1.3"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License"
	    :url "https://opensource.org/licenses/MIT"
	    :key "mit"
	    :year 2015}
  :plugins [[lein-zprint "0.1.3"]]
  :zprint {:old? false}
  :eval-in-leiningen true
  :dependencies [[zprint "0.2.2"]
		 [trptcolin/versioneer "0.1.0"]
                 [me.raynes/fs "1.4.6"]])
