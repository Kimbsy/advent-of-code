(ns advent-of-code.2022.day-01
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2023/day_01"))))

(def test-input ["1abc2"
                 "pqr3stu8vwx"
                 "a1b2c3d4e5f"
                 "treb7uchet"])

(defn parse-input
  [in]
  in)

(defn part-1
  []
  (->> input
       (map #(re-seq #"\d" %))
       (map (juxt first last))
       (map (partial apply str))
       (map read-string)
       (reduce +)))

(def test-input-2 ["two1nine"
                   "eightwothree"
                   "abcone2threexyz"
                   "xtwone3four"
                   "4nineeightseven2"
                   "zoneight234"
                   "7pqrstsixteen"])

(def lookup
  {"one"   1
   "two"   2
   "three" 3
   "four"  4
   "five"  5
   "six"   6
   "seven" 7
   "eight" 8
   "nine"  9
   "1"     1
   "2"     2
   "3"     3
   "4"     4
   "5"     5
   "6"     6
   "7"     7
   "8"     8
   "9"     9})

(defn replace-words
  [s]
  (reduce (fn [acc [k v]]
            (s/replace acc k v))
          s
          replacements))

;; @TODO: somehow broken :'(
(defn part-2
  []
  (->> input
       (map #(re-seq #"\d|one|two|three|four|five|six|seven|eight|nine" %))
       (map (fn [digits] (map lookup digits)))
       (map (juxt first last))
       (map (partial apply str))
       (map read-string)
       (reduce +)
       ))

(comment
  (part-1) ;; => 57346
  (part-2) ;; => 57325 - wrong
  ,)

;; refactoring check
(= [(part-1) (part-2)] [57346 0])
