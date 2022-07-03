(ns leiningen.zprint-test
  (:require [expectations :refer :all]
            [leiningen.zprint :refer :all]
            [me.raynes.fs :as fs]))

(expect [:a :b :c] [:a :b :c])


;;
;; Note: At present this depends on $HOME/.zprintrc being essentially
;; empty.  At some point, we could use environ in zprint to let us
;; have our own $HOME and therefore our own .zprintrc.  But for now,
;; let's just get the basic tests working.
;;

(fs/copy "test/leiningen/basic" "test/leiningen/basic.in")
(fs/delete "test/leiningen/basic.in.old")

; This will replace basic.in with a reformatted basic.in and leave basic.in.old
; around as well.

(leiningen.zprint/zprint {:zprint {:old? true, :parallel? false :test? true}}
                         "test/leiningen/basic.in")

(expect (slurp "test/leiningen/basic.out") (slurp "test/leiningen/basic.in"))

(expect (slurp "test/leiningen/basic") (slurp "test/leiningen/basic.in.old"))

; If you put the delete here, it happens before the expect executes, so we
; delete the .old file before we start to ensure that it actually shows up.


;--------------------------

(fs/copy "test/leiningen/narrow" "test/leiningen/narrow.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "50"
                         "test/leiningen/narrow.in")

(expect (slurp "test/leiningen/narrow.out") (slurp "test/leiningen/narrow.in"))

;--------------------------

(fs/copy "test/leiningen/noindent" "test/leiningen/noindent.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "50"
                         "{:list {:indent 0}}"
                         "test/leiningen/noindent.in")

(expect (slurp "test/leiningen/noindent.out")
        (slurp "test/leiningen/noindent.in"))


;--------------------------

(fs/copy "test/leiningen/off" "test/leiningen/off.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/off.in")

(expect (slurp "test/leiningen/off.out") (slurp "test/leiningen/off.in"))


;--------------------------

(fs/copy "test/leiningen/on" "test/leiningen/on.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/on.in")

(expect (slurp "test/leiningen/on.out") (slurp "test/leiningen/on.in"))


;--------------------------

(fs/copy "test/leiningen/next" "test/leiningen/next.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/next.in")

(expect (slurp "test/leiningen/next.out") (slurp "test/leiningen/next.in"))


;--------------------------

(fs/copy "test/leiningen/skip" "test/leiningen/skip.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/skip.in")

(expect (slurp "test/leiningen/skip.out") (slurp "test/leiningen/skip.in"))

;--------------------------

(fs/copy "test/leiningen/comment" "test/leiningen/comment.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/comment.in")

(expect (slurp "test/leiningen/comment.out")
        (slurp "test/leiningen/comment.in"))

;--------------------------

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/dropspace.in")

(expect (slurp "test/leiningen/dropspace.out")
        (slurp "test/leiningen/dropspace.in"))

;--------------------------

(fs/copy "test/leiningen/keepspace" "test/leiningen/keepspace.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "test/leiningen/keepspace.in")

(expect (slurp "test/leiningen/keepspace.out")
        (slurp "test/leiningen/keepspace.in"))


;--------------------------

(expect
  ":planck-cmd-line has been removed. The last version to support :planck-cmd-line was 0.5.0 for both lein-zprint and zprint.\n"
  (with-out-str (zprint {:zprint {:test? true}} ":planck-cmd-line" "sr")))

(expect
  ":planck-cmd-line has been removed. The last version to support :planck-cmd-line was 0.5.0 for both lein-zprint and zprint.\n"
  (with-out-str (zprint {:zprint {:test? true}} ":planck-cmd-line")))


;----------------------

;;
;; # Command tests
;;
;; Note: not all of these are documented
;;

(expect
  "Incorrect command input: Command line input '{:width 20}' conflicted with input from project.clj file '{:command :default, :old? false}'!\n"
  (with-out-str (leiningen.zprint/zprint
                  {:zprint {:command :default, :old? false, :test? true}}
                  "{:width 20}"
                  "test/leiningen/keepspace.in")))


(expect
  "Incorrect command input: If key :command appears in an options map the only other allowed keys are :old? and :parallel?, instead found: {:old? false, :command :default, :width 20}\n"
  (with-out-str
    (leiningen.zprint/zprint
      {:zprint {:old? false, :command :default, :width 20, :test? true}}
      "test/leiningen/keepspace.in")))


(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace2.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "-d"
                         "test/leiningen/dropspace2.in")

(expect (slurp "test/leiningen/dropspace2.out")
        (slurp "test/leiningen/dropspace2.in"))

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace3.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false :test? true}}
                         "--default"
                         "test/leiningen/dropspace3.in")

(expect (slurp "test/leiningen/dropspace3.out")
        (slurp "test/leiningen/dropspace3.in"))

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace4.in")

(expect "Incorrect command input: Unknown switch '--stuff'\n"
        (with-out-str (leiningen.zprint/zprint
                        {:zprint {:old? false, :parallel? false, :test? true}}
                        "--stuff"
                        "test/leiningen/dropspace4.in")))

;--------------------------
; Can we handle a complex function definition on the command line?

(fs/copy "test/leiningen/sci" "test/leiningen/sci.in")

(leiningen.zprint/zprint
  {:zprint {:old? false, :parallel? false :test? true}}
  "{:style [:community :rod] :style-map {:rod {:fn-map {\"defn\" [:fn {:list {:option-fn (partial rodguide {:multi-arity-nl? true})}}]}}}}"
  "test/leiningen/sci.in")

(expect (slurp "test/leiningen/sci.out")
        (slurp "test/leiningen/sci.in"))

;--------------------------
; Can we handle a complex function definition in the :zprint key of the
; project.clj?

(fs/copy "test/leiningen/sci2" "test/leiningen/sci2.in")

(leiningen.zprint/zprint
  (clojure.tools.reader.edn/read-string
    "{:zprint {:old? false, :parallel? false :test? true
      :style [:community :rod],
      :style-map {:rod {:fn-map {\"defn\" [:fn
                                      {:list {:option-fn (partial
                                                           rodguide
                                                           {:multi-arity-nl?
                                                              true})}}]}}}}}")
  "test/leiningen/sci2.in")

(expect (slurp "test/leiningen/sci2.out")
        (slurp "test/leiningen/sci2.in"))

;---------------------------------------------------------
; Can we check a file that is not formatted correctly?
;

(fs/copy "test/leiningen/basiccheck" "test/leiningen/basiccheck.in")

; This will check basic.in and NOT reformat it

(expect
"Processing file: test/leiningen/basiccheck.in\nFile: test/leiningen/basiccheck.in was incorrectly formatted\n1 file formatted incorrectly\n"
(with-out-str
(binding [*err* *out*]

(leiningen.zprint/zprint {:zprint {:old? true, :parallel? false :test? true}}
			 "--check"
                         "test/leiningen/basiccheck.in"))))

(expect (slurp "test/leiningen/basiccheck") (slurp "test/leiningen/basiccheck.in"))

; If you put the delete here, it happens before the expect executes, so we
; delete the .old file before we start to ensure that it actually shows up.


;---------------------------------------------------------
; Can we check a file that is formatted correctly?
;

(fs/copy "test/leiningen/correctcheck" "test/leiningen/correctcheck.in")

; This will check basic.in and NOT reformat it

(expect
"Processing file: test/leiningen/correctcheck.in\nAll files formatted correctly.\n"
(with-out-str
(binding [*err* *out*]

(leiningen.zprint/zprint {:zprint {:old? true, :parallel? false :test? true}}
			 "--check"
                         "test/leiningen/correctcheck.in"))))

(expect (slurp "test/leiningen/correctcheck") (slurp "test/leiningen/correctcheck.in"))

; If you put the delete here, it happens before the expect executes, so we
; delete the .old file before we start to ensure that it actually shows up.

;---------------------------------------------------------
; Can we check a file that is formatted correctly?
;

(fs/copy "test/leiningen/correctcheck" "test/leiningen/correctcheck.in")

; This will check basic.in and NOT reformat it

(expect
"Processing file: test/leiningen/correctcheck.in\nAll files formatted correctly.\n"
(with-out-str
(binding [*err* *out*]

(leiningen.zprint/zprint {:zprint {:old? true, :parallel? false :test? true}}
			 "-c"
                         "test/leiningen/correctcheck.in"))))

(expect (slurp "test/leiningen/correctcheck") (slurp "test/leiningen/correctcheck.in"))

; If you put the delete here, it happens before the expect executes, so we
; delete the .old file before we start to ensure that it actually shows up.

;---------------------------------------------------------
; Can we check a file that is formatted correctly?
;

(fs/copy "test/leiningen/correctcheck" "test/leiningen/correctcheck.in")

; This will check basic.in and NOT reformat it

(expect
"Processing file: test/leiningen/correctcheck.in\nAll files formatted correctly.\n"
(with-out-str
(binding [*err* *out*]

(leiningen.zprint/zprint {:zprint {:old? true, :parallel? false :test? true :command :check}}
                         "test/leiningen/correctcheck.in"))))

(expect (slurp "test/leiningen/correctcheck") (slurp "test/leiningen/correctcheck.in"))

; If you put the delete here, it happens before the expect executes, so we
; delete the .old file before we start to ensure that it actually shows up.




 



