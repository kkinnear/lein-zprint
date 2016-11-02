(ns leiningen.zprint-test
    (:require
     [expectations :refer :all]
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

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/basic.in")

(expect (slurp "test/leiningen/basic.out") (slurp "test/leiningen/basic.in"))

;--------------------------

(fs/copy "test/leiningen/narrow" "test/leiningen/narrow.in")

(leiningen.zprint/zprint {:zprint {:old? false}}
                         "50"
                         "test/leiningen/narrow.in")

(expect (slurp "test/leiningen/narrow.out") (slurp "test/leiningen/narrow.in"))

;--------------------------

(fs/copy "test/leiningen/noindent" "test/leiningen/noindent.in")

(leiningen.zprint/zprint {:zprint {:old? false}}
                         "50"
                         "{:list {:indent 0}}"
                         "test/leiningen/noindent.in")

(expect (slurp "test/leiningen/noindent.out")
        (slurp "test/leiningen/noindent.in"))


;--------------------------

(fs/copy "test/leiningen/off" "test/leiningen/off.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/off.in")

(expect (slurp "test/leiningen/off.out") (slurp "test/leiningen/off.in"))


;--------------------------

(fs/copy "test/leiningen/on" "test/leiningen/on.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/on.in")

(expect (slurp "test/leiningen/on.out") (slurp "test/leiningen/on.in"))


;--------------------------

(fs/copy "test/leiningen/next" "test/leiningen/next.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/next.in")

(expect (slurp "test/leiningen/next.out") (slurp "test/leiningen/next.in"))


;--------------------------

(fs/copy "test/leiningen/skip" "test/leiningen/skip.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/skip.in")

(expect (slurp "test/leiningen/skip.out") (slurp "test/leiningen/skip.in"))

;--------------------------

(fs/copy "test/leiningen/comment" "test/leiningen/comment.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/comment.in")

(expect (slurp "test/leiningen/comment.out")
        (slurp "test/leiningen/comment.in"))

;--------------------------

(fs/copy "test/leiningen/dropspace" "test/leiningen/dropspace.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/dropspace.in")

(expect (slurp "test/leiningen/dropspace.out")
        (slurp "test/leiningen/dropspace.in"))

;--------------------------

(fs/copy "test/leiningen/keepspace" "test/leiningen/keepspace.in")

(leiningen.zprint/zprint {:zprint {:old? false}} "test/leiningen/keepspace.in")

(expect (slurp "test/leiningen/keepspace.out")
        (slurp "test/leiningen/keepspace.in"))


