(ns advent-of-code.2025.day-08
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_08"))))

(def test-input
  ["162,817,812"
   "57,618,57"
   "906,360,560"
   "592,479,940"
   "352,342,300"
   "466,668,158"
   "542,29,236"
   "431,825,988"
   "739,650,466"
   "52,470,668"
   "216,146,977"
   "819,987,18"
   "117,168,530"
   "805,96,715"
   "346,949,466"
   "970,615,88"
   "941,993,340"
   "862,61,35"
   "984,92,344"
   "425,690,689"])

(defn parse-input
  [in]
  in)

(defn part-1
  []
  )

(defn part-2
  []
  )

(comment
  (part-1) ;; => 
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [])
