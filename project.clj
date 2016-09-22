(defproject lein-zprint "0.1.1"
  :description "Run zprint to pretty print source files in your project."
  :url "https://github.com/kkinnear/lein-zprint"
  :license {:name "MIT License"
	    :url "https://opensource.org/licenses/MIT"
	    :key "mit"
	    :year 2015}
  :eval-in-leiningen true
  :dependencies [[zprint "0.2.0"]
		 [trptcolin/versioneer "0.1.0"]
                 [me.raynes/fs "1.4.6"]])
