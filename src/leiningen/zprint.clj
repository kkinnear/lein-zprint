(ns leiningen.zprint
    (:require
     [zprint.core :as zp :exclude [zprint]]
     [zprint.config]
     [trptcolin.versioneer.core :as version]
     [me.raynes.fs :as fs])
    (:import [java.io File]))

(defn lein-zprint-about
  "Return version of this program."
  []
  (str "lein zprint " (version/get-version "lein-zprint" "lein-zprint")))

(defn zprint-about
  "Return version of zprint library program."
  []
  (str "zprint-" (version/get-version "zprint" "zprint")))

(defn vec-str-to-str
  "Take a vector of strings and concatenate them into one string with
  newlines between them."
  [vec-str]
  (apply str (interpose "\n" vec-str)))

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
     " To reformat one file in myproject:" 
     "" 
     "   lein zprint src/myproject/core.clj" 
     ""
     " To reformat all of the Clojure source files in myproject:" 
     ""
     "   lein zprint src/myproject/*.clj" 
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
     " You can type: lein zprint :help to get this text." 
     ""]))

(defn zprint-one-file
  "Take a file name, possibly including a path, and zprint that one file."
  [file-name]
  (cond
    (= file-name ":explain") (do (println (lein-zprint-about))
                                 (println (zprint-about))
                                 (zp/czprint nil :explain))
    (= file-name ":about") (println (lein-zprint-about))
    (= file-name ":help") (println help-str)
    :else
      (let [parent-path (fs/parent file-name)
            tmp-name (fs/temp-name "zprint")
            tmp-file (str parent-path File/separator tmp-name)
            old-file (str file-name ".old")]
        (println "Processing file:" file-name)
        (try
          (zp/zprint-file file-name tmp-file)
          (fs/delete old-file)
          (fs/rename file-name old-file)
          (fs/rename tmp-file file-name)
          (catch
            Exception
            e
            (println
              (str "Unable to process file: "
                   file-name
                   " because: "
                   e
                   " Leaving it unchanged!")))))))

(defn ^:no-project-needed zprint
  "Pretty-print all of the arguments that are not a map, replacing the
  existing file with the pretty printed one.  The old one is kept around
  with a .old extension.  If the arg is a map, it is considered an options
  map and subsequent files are pretty printed with those options."
  [project & args]
  (when-let [project-options (:zprint project)]
    (zprint.config/set-options! project-options ":zprint map in project.clj"))
  (let [arg1 (try (read-string (first args)) (catch Exception e nil))
        args
          (cond
            (map? arg1)
              (do (zprint.config/set-options! arg1 "first arg to lein zprint")
                  (next args))
            (number? arg1)
              (do
                (zprint.config/set-options! {:width arg1})
                (if-let [arg2 (try (read-string (second args))
                                   (catch Exception e nil))]
                  (cond (map? arg2) (do (zprint.config/set-options!
                                          arg2
                                          "second arg to lein zprint")
                                        (nnext args))
                        :else (next args))
                  (next args)))
            :else args)]
    (doseq [file-name args] (zprint-one-file file-name))
    (flush)))
       