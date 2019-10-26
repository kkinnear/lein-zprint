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

; This will replace basic.in with a reformatted basic.in

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/basic.in")

(expect (slurp "test/leiningen/basic.out") (slurp "test/leiningen/basic.in"))

;--------------------------

(fs/copy "test/leiningen/narrow" "test/leiningen/narrow.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "50"
                         "test/leiningen/narrow.in")

(expect (slurp "test/leiningen/narrow.out") (slurp "test/leiningen/narrow.in"))

;--------------------------

(fs/copy "test/leiningen/noindent" "test/leiningen/noindent.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "50"
                         "{:list {:indent 0}}"
                         "test/leiningen/noindent.in")

(expect (slurp "test/leiningen/noindent.out")
        (slurp "test/leiningen/noindent.in"))


;--------------------------

(fs/copy "test/leiningen/off" "test/leiningen/off.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/off.in")

(expect (slurp "test/leiningen/off.out") (slurp "test/leiningen/off.in"))


;--------------------------

(fs/copy "test/leiningen/on" "test/leiningen/on.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/on.in")

(expect (slurp "test/leiningen/on.out") (slurp "test/leiningen/on.in"))


;--------------------------

(fs/copy "test/leiningen/next" "test/leiningen/next.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/next.in")

(expect (slurp "test/leiningen/next.out") (slurp "test/leiningen/next.in"))


;--------------------------

(fs/copy "test/leiningen/skip" "test/leiningen/skip.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/skip.in")

(expect (slurp "test/leiningen/skip.out") (slurp "test/leiningen/skip.in"))

;--------------------------

(fs/copy "test/leiningen/comment" "test/leiningen/comment.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/comment.in")

(expect (slurp "test/leiningen/comment.out")
        (slurp "test/leiningen/comment.in"))

;--------------------------

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/dropspace.in")

(expect (slurp "test/leiningen/dropspace.out")
        (slurp "test/leiningen/dropspace.in"))

;--------------------------

(fs/copy "test/leiningen/keepspace" "test/leiningen/keepspace.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                         "test/leiningen/keepspace.in")

(expect (slurp "test/leiningen/keepspace.out")
        (slurp "test/leiningen/keepspace.in"))


;--------------------------

(expect
  ":planck-cmd-line has been removed. The last version to support :planck-cmd-line was 0.5.0 for both lein-zprint and zprint.\n"
  (with-out-str (zprint {} ":planck-cmd-line" "sr")))

(expect
  ":planck-cmd-line has been removed. The last version to support :planck-cmd-line was 0.5.0 for both lein-zprint and zprint.\n"
  (with-out-str (zprint {} ":planck-cmd-line")))


;----------------------

;;
;; # Command tests
;;
;; Note: not all of these are documented
;;

(expect
  "java.lang.Exception: Command line input '{:width 20}' conflicted with input from project.clj file '{:command :default, :old? false}'!"
  (try (leiningen.zprint/zprint {:zprint {:command :default, :old? false}}
                                "{:width 20}"
                                "test/leiningen/keepspace.in")
       (catch Exception e (str e))))


(expect
"java.lang.Exception: If key :command appears in an options map the only other allowed keys are :old? and :parallel?, instead found: {:old? false, :command :default, :width 20}"
  (try (leiningen.zprint/zprint {:zprint
                                   {:old? false, :command :default, :width 20}}
                                "test/leiningen/keepspace.in")
       (catch Exception e (str e))))


(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace2.in")

(leiningen.zprint/zprint {:zprint {:old? false :parallel? false}}
			 "-d"
                         "test/leiningen/dropspace2.in")

(expect (slurp "test/leiningen/dropspace2.out")
        (slurp "test/leiningen/dropspace2.in"))

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace3.in")

(leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
			 "--default"
                         "test/leiningen/dropspace3.in")

(expect (slurp "test/leiningen/dropspace3.out")
        (slurp "test/leiningen/dropspace3.in"))

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace4.in")

(expect "java.lang.Exception: Unknown switch '--stuff'"
        (try (leiningen.zprint/zprint {:zprint {:old? false, :parallel? false}}
                                      "--stuff"
                                      "test/leiningen/dropspace4.in")
             (catch Exception e (str e))))

