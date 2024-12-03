(ns advent-of-code.2024.day-03
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (slurp (io/reader (io/resource "2024/day_03"))))

(def test-input "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))")

(defn parse-input
  [in]
  in)

(defn part-1
  []
  (->> input
       (re-seq #"mul\((\d{1,3}),(\d{1,3})\)")
       (map (partial drop 1))
       (map #(map read-string %))
       (map (partial apply *))
       (reduce +)))

(def test-input-2 "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")

(defn part-2
  []
  (let [in (s/replace input #"(?s)don\'t\(\).*?do\(\)" "")]
    (->> in
         (re-seq #"mul\((\d{1,3}),(\d{1,3})\)")
         (map (partial drop 1))
         (map #(map read-string %))
         (map (partial apply *))
         (reduce +)))

  
  )

(comment
  (part-1) ;; => 155955228
  (part-2) ;; => 100189366
  ,)

;; refactoring check
(= [(part-1) (part-2)] [155955228 100189366])
