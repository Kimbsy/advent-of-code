(ns advent-of-code.2021.day-18
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_18"))))

(def test-input (s/split-lines ""))

(defn part-1
  []
  (let [in test-input]
    ))

(defn part-2
  []
  (let [in test-input]
    ))

(comment
  (part-1) ;; =>
  (part-2) ;; =>
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [])
