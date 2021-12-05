(ns advent-of-code.2021.day-05
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_05"))))

(def test-input ["0,9 -> 5,9" "8,0 -> 0,8" "9,4 -> 3,4" "2,2 -> 2,1" "7,0 -> 7,4" "6,4 -> 2,0" "0,9 -> 2,9" "3,4 -> 1,4" "0,0 -> 8,8" "5,5 -> 8,2"])

(defn parse-line
  [string]
  (let [[x1 y1 x2 y2] (map read-string (s/split string #"( -> |,)"))]
    [[x1 y1] [x2 y2]]))

(defn cardinal?
  [[[x1 y1] [x2 y2]]]
  (or (= x1 x2)
      (= y1 y2)))

(defn limits
  [lines]
  (let [xs (mapcat (fn [l]
                  (map first l))
                   lines)
        ys (mapcat (fn [l]
                     (map first l))
                   lines)]
    [(inc (apply max xs)) (inc (apply max ys))]))

(defn create-grid
  [in]
  (let [lines (map parse-line in)
        [max-x max-y] (limits lines)
        grid (vec (repeat max-y (vec (repeat max-x 0))))]
    grid))

(defn cardinal-positions
  [[[x1 y1] [x2 y2]]]
  (let [[rx1 rx2] (sort [x1 x2])
        [ry1 ry2] (sort [y1 y2])]
    (for [i (if (= x1 x2) [x1] (range rx1 (inc rx2)))
          j (if (= y1 y2) [y1] (range ry1 (inc ry2)))]
      [j i])))

(defn diagonal-positions
  [[[x1 y1] [x2 y2]]]
  (let [[rx1 rx2] (sort [x1 x2])
        [ry1 ry2] (sort [y1 y2])
        x-range (range rx1 (inc rx2))
        y-range (range ry1 (inc ry2))]
    (map list
         (if (< y1 y2) y-range (reverse y-range))
         (if (< x1 x2) x-range (reverse x-range)))))

(defn add-line
  [grid l]
  (reduce (fn [acc pos]
            (update-in acc pos inc))
          grid
          (if (cardinal? l)
            (cardinal-positions l)
            (diagonal-positions l))))

(defn add-lines
  [grid lines]
  (reduce (fn [acc l]
            (add-line acc l))
          grid
          lines))

(defn count-overlaps
  [grid]
  (count (mapcat #(filter (fn [n] (< 1 n)) %) grid)))

(defn part-1
  []
  (let [in input
        res (add-lines (create-grid in) (filter cardinal? (lines in)))]
    (count-overlaps res)))

(defn part-2
  []
  (let [in input
        res (add-lines (create-grid in) (lines in))]
    (count-overlaps res)))

(comment
  (part-1) ;; => 5124
  (part-2) ;; => 19771
  ,)

;; refactoring check
(= [(part-1) (part-2)] [5124 19771])
