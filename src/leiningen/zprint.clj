(ns leiningen.zprint
  (:require [zprint.core :as zp :exclude [zprint]]
            [zprint.config :as zc]
            [me.raynes.fs :as fs])
  (:import [java.io File]))

(defn lein-zprint-about
  "Return version of this program."
  []
  (str "lein-zprint-1.3.0"))

(defn zprint-about "Return version of zprint library program." [] (zc/about))

(defn vec-str-to-str
  "Take a vector of strings and concatenate them into one string with
  newlines between them."
  [vec-str]
  (apply str (interpose "\n" vec-str)))

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
     " To configure lein zprint, you can:"
     ""
     "   - invoke lein-zprint with a switch:"
     "        lein zprint -d filename1 filename2 ..."
     "        lein zprint -c filename1 filename2 ..."
     "        Supported switches are: -d and -c, see below..."
     "   - create a $HOME/.zprintrc file or a $HOME/.zprint.edn file"
     "     containing a zprint options map"
     "   - define :search-config? as true in the $HOME/.zprintrc or"
     "     $HOME/.zprint.edn file, and zprint will search for a"
     "     .zprintrc or .zprint.edn file in the current directory"
     "     and if it doesn't find one, in the parent of that directory,"
     "     all the way to the root directory, stopping when it finds a"
     "     configuration file."
     "   - add a :zprint {<options-map>} pair to the map in project.clj"
     "   - place a number first in the arguments, which will become the width"
     "   - place an options map (surrounded by double quotes) first in the"
     "     arguments, which will be used to configure zprint (if there is a"
     "     number first, the map can be second in the arguments)"
     ""
     " A small number of command line switches are supported:"
     ""
     "  -c --check    Check to see if the specified files are formatted"
     "                correctly.  If they are, the exit status is 0 for"
     "                success.  If any are not formatted correctly, the"
     "                exit status is 1, for failure."
     ""
     "  -d --default  Ignore external configuration and format based"
     "                only on defaults and ;!zprint directives in the"
     "                file.  The only thing in the :zprint map in the"
     "                project.clj file that is recognized is the :old?"
     "                key, all other keys ignored. $HOME/.zprintrc is"
     "                also ignored."
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

(defn get-switch
  "Given an options, get a switch out of it if there is one to get.  Throw
  an exception for a bad switch. Returns [switch old?], where old is
  the value of :old if options is a map."
  [options]
  (let [map-options? (map? options)
        test? (when map-options? (:test? options))
        options (if map-options? (dissoc options :test?) options)]
    (cond
      (nil? options) nil
      (string? options)
        (if (clojure.string/starts-with? options "-")
          (cond (or (= options "--default") (= options "-d")) [:default nil
                                                               test?]
                (or (= options "--check") (= options "-c")) [:check nil test?]
                #_#_(or (= options "--standard") (= options "-s"))
                  [:standard nil test?]
                :else (throw (Exception.
                               (str "Unknown switch '" options "'")))))
      map-options?
        (if (:command options)
          (let [command (:command options)
                option-keys (keys options)]
            (if (or (= command :default)
                    #_(= command :standard)
                    (= command :check))
              (if (= (count option-keys)
                     (count (filter #{:old? :parallel? :command} option-keys)))
                [command (:old? options) test?]
                (throw (Exception.
                         (str "If key :command appears in an options map"
                                " the only other allowed keys are :old? and"
                              " :parallel?, instead found: " options))))
              (throw (Exception. (str "Unknown switch '" options "'")))))
          [nil (:old? options) test?])
      :else (throw (Exception. (str "Options '"
                                    options
                                    "' must be either a map or a string"))))))

(defn non-nil [x y] (if (nil? x) y x))

(defn process-options-as-switches
  "Take the project-options and line-options, and look for switches.
  If switches are found (and don't conflict with each other), then
  return :default, :standard, or nil.  Throw an exception for a
  problem.  If line-options has switch, ignore project-options if
  it is not a command, but if it is a command, it must match
  line-options.  Pull :old out of project options.  Clean :command
  out of all of the options, and return them too.  Returns [switch
  old? op-options project-options line-options test?]."
  [project-options line-options]
  (let [[line-switch line-old? line-test?] (get-switch line-options)
        #_(println "line-switch:" line-switch)
        #_(println "line-old?:" line-old?)
        #_(println "line-options:" line-options)
        [project-switch project-old? project-test?] (get-switch project-options)
        #_(println "project-switch:" project-switch)
        #_(println "project-old?" project-old?)
        #_(println "project-options:" project-options)
        test? (or line-test? project-test?)
        project-old? (non-nil
                       project-old?
                       (when map? project-options (:old? project-options)))
        clean-project-options (if (map? project-options)
                                (dissoc project-options :command :test?)
                                project-options)
        clean-line-options (if (map? line-options)
                             (dissoc line-options :command :test?)
                             line-options)
        op-options
          (cond (and (map? clean-project-options) (map? clean-line-options))
                  (zc/merge-deep clean-project-options clean-line-options)
                (map? clean-project-options) clean-project-options
                (map? clean-line-options) clean-line-options
                :else {})]
    (cond
      line-options
        (do
          #_(println "lo: project-old?:" project-old? project-options)
          (if line-switch
            ; If project-options were a switch, then we will require
            ; line and project options have the same switch.
            ; If project-options was a options map, then we will ignore it.
            (if project-switch
              (if (= line-switch project-switch)
                [line-switch (non-nil line-old? project-old?) op-options
                 clean-project-options clean-line-options test?]
                (throw
                  (Exception. (str "Command line input '"
                                   line-options
                                   "' conflicted with input from project.clj"
                                   " file '"
                                   project-options
                                   "'!"))))
              [line-switch (non-nil line-old? project-old?) op-options
               clean-project-options clean-line-options test?])
            ; We had line-options, but not a switch.  If we have
            ; a project switch, that is a error.  Project options are fine.
            (if project-switch
              (throw (Exception. (str "Command line input '"
                                      line-options
                                      "' conflicted with input from project.clj"
                                      " file '"
                                      project-options
                                      "'!")))
              [line-switch (non-nil line-old? project-old?) op-options
               clean-project-options clean-line-options test?])))
      ; We just have project-options, no line-options, so this is easy. It
      ; is either a switch or options.!
      project-options (do #_(println "project-options 2:" project-options
                                     "project-old?" project-old?)
                          [project-switch project-old? op-options
                           clean-project-options clean-line-options test?])
      :else nil)))

(defn zprint-one-file
  "Take a file name, possibly including a path, and zprint that one file."
  [op-options project-options line-options switch old? file-spec]
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
                old-file (str file-spec ".old")]
            (println "Processing file:" file-spec)
            (let [; If old? is nil (or, really, not false), then we want to
                  ; default it to true.  False means we explicitly found it
                  ; set to false somewhere, nil means that we didn't see
                  ; anything
                  ; about it one way or the other.
                  old? (if-not (false? old?) true)
                  #_(println "old?" old?)
                  parallel? (get project-options :parallel? true)]
              #_(println "switch" switch
                         "line-options:" line-options
                         "project-options:" project-options)
              (try
                (case switch
                  :default (zp/set-options! {:configured? true,
                                             :color? false,
                                             :parallel? parallel?,
                                             :old? old?})
                  #_#_:standard
                    (zp/set-options! {:configured? true,
                                      :style :standard,
                                      :color? false,
                                      :parallel? parallel?,
                                      :old? old?})
                  (let [line-options (if (= switch :check) {} line-options)]
                    (zp/configure-all! op-options)
                    (zp/set-options! {:parallel? true} "lein-zprint internal")
                    (zp/set-options! project-options
                                     ":zprint map in project.clj")
                    (zp/set-options! line-options "lein-zprint command line")
                    (zp/set-options! {:color? false} "lein-zprint no color")))
                (let [check? (= switch :check)
                      infile (slurp file-spec)
                      formatted-infile (zp/zprint-file-str
                                         infile
                                         (str "file: " (fs/base-name file-spec))
                                         {}
                                         (str "input for file: "
                                              (fs/base-name file-spec)))]
                  (if check?
                    (do (if (= infile formatted-infile)
                          ; It checks correctly
                          :correct
                          (do (binding [*out* *err*]
                                (println "File:"
                                         file-spec
                                         "was incorrectly formatted"))
                              :incorrect)))
                    (do
                      ; If we want old files, create it w/out rename, since
                      ; rename
                      ; is problematic on Windows systems.
                      (when (:old? (zc/get-options))
                        (fs/delete old-file)
                        (spit old-file infile))
                      ; Save the formatted file regardless of the old file flag
                      (spit file-spec formatted-infile)
                      ; Return the old file name if we had :old? true
                      (when (:old? (zc/get-options)) old-file))))
                (catch Exception e
                  (println (str "Unable to process file: "
                                file-spec
                                " because: "
                                e
                                " Leaving it unchanged!"))))))))

(defn ^:no-project-needed zprint
  "Pretty-print all of the arguments that are not a map, replacing the
  existing file with the pretty printed one.  The old one is kept around
  with a .old extension.  If the first arg is a map, it is considered an 
  options map and subsequent files are pretty printed with those options.
  Type: lein zprint :help
  for more complete help!"
  [project & args]
  (let [project-options (:zprint project)
        project-options (zprint.config/sci-load-string (pr-str project-options))
        arg1 (try (zprint.config/sci-load-string (first args))
                  (catch Exception e nil))
        [line-options args]
          (cond
            (map? arg1) [arg1 (next args)]
            (number? arg1)
              (if-let [arg2 (try (zprint.config/sci-load-string (second args))
                                 (catch Exception e nil))]
                (cond (map? arg2) [(merge {:width arg1} arg2) (nnext args)]
                      :else [{:width arg1} (next args)])
                [{:width arg1} (next args)])
            (= :planck-cmd-line arg1)
              (do (println (str ":planck-cmd-line has been removed. "
                                "The last version to support :planck-cmd-line "
                                "was 0.5.0 for both lein-zprint and zprint."))
                  [nil nil])
            (= :lumo-cmd-line arg1)
              (do (println (str ":lumo-cmd-line has been removed. "
                                  "The last version to support :lumo-cmd-line "
                                "was 0.5.0 for both lein-zprint and zprint, "
                                  "but required an older version of lumo."))
                  [nil nil])
            (clojure.string/starts-with? (first args) "-") [(first args)
                                                            (next args)]
            :else [nil args])
        ; Are we doing a test?
        [project-options line-options test?]
          (let [map-project? (map? project-options)
                map-line? (map? line-options)
                test? (or (when map-project? (:test? project-options))
                          (when map-line? (:test? line-options)))]
            [(if map-project? (dissoc project-options :test?) project-options)
             (if map-line? (dissoc line-options :line?) line-options) test?])
        #_(when-not test? (println ">>>>>>>>>>>>>>>>>> test false"))
        [switch old? op-options project-options line-options]
          (try
            (process-options-as-switches project-options line-options)
            (catch Exception e
              (do (println (str "Incorrect command input: "
                                (clojure.string/replace e #".*Exception: " "")))
                  (flush)
                  #_(println "test?" test?)
                  (if test? [:error] (do (shutdown-agents) (System/exit 2))))))
        #_(println "early old?" old?)
        #_(println "early project-options:" project-options)
        ; Having processed options as switches, get rid of :command
        #_(println "switch:" switch)]
    ; All of these options will be reset by zprint-one-file, but we
    ; do them here to see if they work, and for :explain output.
    (when (not (= switch :error))
      (case switch
        :default (zp/set-options! {:configured? true, :parallel? true}
                                  "lein-zprint default switch")
        #_#_:standard
          (zp/set-options!
            {:configured? true, :style :standard, :parallel? true})
        ; Regular, not switch processing
        (do (let [line-options (if (= switch :check) {} line-options)]
              (zp/set-options! {:parallel? true}
                               "lein-zprint: :zprint key in project.clj"
                               op-options)
              (when project-options
                (zp/set-options! project-options ":zprint map in project.clj"))
              (when line-options
                (zp/set-options! line-options "lein zprint command line")))))
      #_(println "late project-options:" project-options)
      (let [check? (= switch :check)
            old-files (mapv #(zprint-one-file op-options
                                              project-options
                                              line-options
                                              switch
                                              old?
                                              %)
                        args)
            old-files (remove nil? old-files)]
        (if check?
          (let [correct-count (count (filter #(= % :correct) old-files))
                incorrect-count (count (filter #(= % :incorrect) old-files))]
            (if (zero? incorrect-count)
              (println "All files formatted correctly.")
              (binding [*out* *err*]
                (println (str incorrect-count
                              " file"
                              (if (> incorrect-count 1) "s" "")
                              " formatted incorrectly"))
                (flush)
                #_(println "test?" test?)
                (when-not test? (shutdown-agents) (System/exit 1)))))
          (when-not (empty? old-files)
            (println "Renamed"
                     (count old-files)
                     (str "original file"
                          (if (> (count old-files) 1) "s" "")
                          " with .old extensions."))
            (println (str "To disable rename, add :zprint {:old? false}"
                          " to your project.clj."))))))
    (flush)
    (when-not test? (shutdown-agents))))
