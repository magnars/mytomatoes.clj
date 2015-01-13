(ns mytomatoes.account
  (:require [pandect.core :refer [sha256]]))

(def static-salt
  "*k*Pn9OR, ab5ec025e85ab1ab0de4bcab4b70068b4b3642fe, 062b1030-e63c-4f16-96bc-fd38dee78ae6OwBDZefhqlbYZ-wiIm+/N81l)V_(q-a5xD0IL4fzAFiRaxv9M39e87N_O*tog9+u, de5e6b220220759326851bc49cde941e576e9114, 18287807-d501-4c4c-9a13-cbcff7b75ee8(seRpitb!P=eSOCvd7@gbfH!c6oROD#OqRb7**EnBtlZn24fhzQp(U*(")

(defn hash-password [password random-salt]
  (sha256 (str password "+" static-salt "+" random-salt)))

(defn get-random-salt []
  (str (System/currentTimeMillis)))
