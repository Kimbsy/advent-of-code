(ns advent-of-code.2022.day-02
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_02"))))

(def test-input ["A Y"
                 "B X"
                 "C Z"])

(def points {"A" 1
             "B" 2
             "C" 3
             :win 6
             :lose 0
             :draw 3})

(def same {"X" "A"
           "Y" "B"
           "Z" "C"})

(def loses-to {"A" "C"
               "B" "A"
               "C" "B"})

(defn pre-parse-input
  [in]
  (map #(s/split % #" ") in))

(defn parse-1
  [in]
  (map (fn [[y m]]
         [y (same m)])
       in))

(defn wld
  [[y m]]
  (if (= y m)
    :draw
    (if (= y (loses-to m))
      :win
      :lose)))

(defn calc-points
  [[y m :as game]]
  (+ (points m)
     (points (wld game))))

(defn part-1
  []
  (->> input
       pre-parse-input
       parse-1
       (map calc-points)
       (reduce +))
  )

(def goals {"X" loses-to
            "Y" identity
            "Z" (cset/map-invert loses-to)})

(defn parse-2
  [in]
  (map (fn [[y g]]
         [y ((goals g) y)])
       in))

(defn part-2
  []
  (->> input
       pre-parse-input
       parse-2
       (map calc-points)
       (reduce +)))

(comment
  (part-1) ;; => 13268
  (part-2) ;; => 15508
  ,)

;; refactoring check
(= [(part-1) (part-2)] [13268 15508])
