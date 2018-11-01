(ns leiningen.zprint
  (:require [zprint.core :as zp :exclude [zprint]]
            [zprint.config :as zc]
            [me.raynes.fs :as fs])
  (:import [java.io File]))

(defn lein-zprint-about
  "Return version of this program."
  []
  (str "lein-zprint-0.3.12"))

(defn zprint-about "Return version of zprint library program." [] (zc/about))

(defn vec-str-to-str
  "Take a vector of strings and concatenate them into one string with
  newlines between them."
  [vec-str]
  (apply str (interpose "\n" vec-str)))


(defn common-cmd-str
  "Figure out a planck command string if possible."
  [cache-dir]
  (let [cp (seq (.getURLs (clojure.lang.RT/baseLoader)))
        paths (map #(.getPath %) cp)
        zprint-cp (first (filter #(re-find #"/zprint.*jar" %) paths))
        rewrite-cljs-cp (first (filter #(re-find #"rewrite-cljs.*jar" %)
                                 paths))]
    (cond (and (nil? zprint-cp) (nil? rewrite-cljs-cp))
            "Cannot find classpath for both zprint and rewrite-cljs .jar files!"
          (nil? zprint-cp) "Unable to determine classpath for zprint .jar file!"
          (nil? rewrite-cljs-cp)
            "Unable to determine classpath for rewrite-cljs .jar file!"
          :else
            (str "-k " cache-dir " -c " zprint-cp ":" rewrite-cljs-cp " -m"))))

;!zprint {:format :next :vector {:wrap? false}}
(def help-str
  (vec-str-to-str
    [(lein-zprint-about)
     (zprint-about)
     ""
     " lein zprint reformats Clojure source files from scratch using the"
     " zprint library, ignoring any line breaks or whitespace in the files."
     " It replaces the files by new ones that are completely reformatted."
     " The existing files are renamed by appending .old to the previous name."
     ""
     "     If you put this line in your project.clj, it won't keep"
     "     the original files around:"
     ""
     "         :zprint {:old? false}"
     ""
     " Using lein zprint:"
     " ------------------"
     ""
     " To reformat one file in myproject:"
     ""
     "   lein zprint src/myproject/core.clj"
     ""
     " To reformat all of the Clojure source files in myproject:"
     ""
     "   lein zprint src/myproject/*.clj"
     ""
     " Make sure you run lein zprint with the current directory set to"
     " the directory containing your project.clj file."
     ""
     " To have lein-zprint output a string which you can use as a "
     " zprint pretty print filter:"
     ""
     "    lein zprint :planck-cmd-line <cache-dir>"
     "    lein zprint :lumo-cmd-line <cache-dir>"
     ""
     " where <cache-dir> is a directory that exists that can hold the"
     " Javascript files that planck or lumo will generate. See the readme"
     " for additional details."
     ""
     " To configure lein zprint, you can:"
     ""
     "   - create a $HOME/.zprintrc file containing a zprint options map"
     "   - define environment variables as described in the zprint doc"
     "   - add a :zprint {<options-map>} to project.clj"
     "   - place a number first in the arguments, which will become the width"
     "   - place an options map (surrounded by double quotes) first in the"
     "     arguments, which will be used to configure zprint (if there is a"
     "     number first, the map can be second in the arguments)"
     ""
     " You can place the token :explain anywhere you can place a file name"
     " and the current options will be output to standard out."
     ""
     " You can also place the token :support anywhere you can put a file"
     " name, and you should put :support at the end of a line that isn't"
     " doing what you want, and submit that with your issue."
     ""
     " Within a file, you can control the function of the zprint formatter"
     " with lines that start with ;!zprint, and contain an options map."
     ""
     " ;!zprint <options>       perform a (set-options! <options>)"
     "                          Will use these options for the rest of the"
     "                          file (or until they are changed again)"
     ""
     " ;!zprint {:format :off}  turn off formatting"
     ""
     " ;!zprint {:format :on}   turn on formattting"
     ""
     " ;!zprint {:format :skip} do not format the next element that is"
     "                          not a comment and not whitespace"
     ""
     " ;!zprint {:format :next <options>}"
     "                          Format only the next non-comment non-whitespace"
     "                          element with the specified <options>"
     ""
     " You can type: lein zprint :help to get this text."
     ""]))

(defn zprint-one-file
  "Take a file name, possibly including a path, and zprint that one file."
  [project-options line-options file-spec]
  (cond (= file-spec ":explain") (do (println (lein-zprint-about))
                                     (println (zprint-about))
                                     (zp/czprint nil :explain))
        (= file-spec ":support") (do (println (lein-zprint-about))
                                     (println (zprint-about))
                                     (zp/czprint nil :support))
        (= file-spec ":about") (println (lein-zprint-about))
        (= file-spec ":help") (println help-str)
        :else
          (let [parent-path (fs/parent file-spec)
                tmp-name (fs/temp-name "zprint")
                tmp-file (str parent-path File/separator tmp-name)
                old-file (str file-spec ".old")]
            (println "Processing file:" file-spec)
            (try (zp/configure-all!)
                 (zp/set-options! {:parallel? true} "lein-zprint internal")
                 (zp/set-options! project-options ":zprint map in project.clj")
                 (zp/set-options! line-options "lein zprint command line")
                 (zp/zprint-file file-spec (fs/base-name file-spec) tmp-file)
                 (when (:old? (zc/get-options))
                   (fs/delete old-file)
                   (fs/rename file-spec old-file))
                 (fs/rename tmp-file file-spec)
                 (when (:old? (zc/get-options)) old-file)
                 (catch Exception e
                   (println (str "Unable to process file: "
                                 file-spec
                                 " because: "
                                 e
                                 " Leaving it unchanged!")))))))

(defn ^:no-project-needed zprint
  "Pretty-print all of the arguments that are not a map, replacing the
  existing file with the pretty printed one.  The old one is kept around
  with a .old extension.  If the arg is a map, it is considered an options
  map and subsequent files are pretty printed with those options."
  [project & args]
  (let [project-options (:zprint project)
        ; All of these options will be reset by zprint-one-file, but we
        ; do them here to see if they work, and for :explain output.
        _ (zp/set-options! {:parallel? true} "lein-zprint internal")
        _ (when project-options
            (zp/set-options! project-options ":zprint map in project.clj"))
        arg1 (try (read-string (first args)) (catch Exception e nil))
        [line-options args]
          (cond
            (map? arg1) [arg1 (next args)]
            (number? arg1) (if-let [arg2 (try (read-string (second args))
                                              (catch Exception e nil))]
                             (cond (map? arg2) [(merge {:width arg1} arg2)
                                                (nnext args)]
                                   :else [{:width arg1} (next args)])
                             [{:width arg1} (next args)])
            (= :planck-cmd-line arg1)
              (do
                (let [cache-dir (first (next args))
                      full-cache-dir (str (fs/file cache-dir))]
                  (if cache-dir
                    (if (fs/exists? full-cache-dir)
                      (println (str "planck -s "
                                    (common-cmd-str full-cache-dir)
                                    " zprint.planck"))
                      (println (str ":planck-cmd-line cache directory '"
                                    cache-dir
                                    "' must already exist, and it does not!")))
                    (println ":planck-cmd-line requires a cache-dir!")))
                [nil nil])
            (= :lumo-cmd-line arg1)
              (do
                (let [cache-dir (first (next args))
                      full-cache-dir (str (fs/file cache-dir))]
                  (if cache-dir
                    (if (fs/exists? full-cache-dir)
                      (println (str "lumo "
                                    (common-cmd-str full-cache-dir)
                                    " zprint.lumo"))
                      (println (str ":lumo-cmd-line cache directory '"
                                    cache-dir
                                    "' must already exist, and it does not!")))
                    (println ":lumo-cmd-line requires a cache-dir!")))
                [nil nil])
            :else [{} args])]
    (when line-options
      (zp/set-options! line-options "lein zprint command line"))
    (let [old-files (mapv #(zprint-one-file project-options line-options %)
                      args)
          old-files (remove nil? old-files)]
      (when-not (empty? old-files)
        (println "Renamed original"
                 (count old-files)
                 "files with .old extensions.")
        (println
          "To disable rename, add :zprint {:old? false} to your project.clj.")))
    (flush)
    (shutdown-agents)))
