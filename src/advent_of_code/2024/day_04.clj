(ns advent-of-code.2024.day-04
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (vec (line-seq (io/reader (io/resource "2024/day_04")))))

(def test-input ["MMMSXXMASM"
                 "MSAMXMSMSA"
                 "AMXSXMAAMM"
                 "MSAMASMSMX"
                 "XMASAMXAMM"
                 "XXAMMXXAMA"
                 "SMSMSASXSS"
                 "SAXAMASAAA"
                 "MAMMMXMMMM"
                 "MXMXAXMASX"])

(defn parse-input
  [in]
  in)

(defn transpose-str
  [grid]
  (mapv (partial apply str)
        (u/transpose grid)))

(defn tl-br-diags
  [grid]
  (let [nr (count grid)
        nc (count (first grid))]
    (map (partial apply str)
         (concat
          (for [start-r (range nr)]
            (for [d (range nc)]
              (u/grid-val grid
                          [(+ start-r d)
                           d])))
          (for [start-c (rest (range nc))]
            (for [d (range nr)]
              (u/grid-val grid
                          [d
                           (+ start-c d)])))))))

(defn tr-bl-diags
  [grid]
  (let [nr (count grid)
        nc (count (first grid))]
    (map (partial apply str)
         (concat
          (for [start-r (range nr)]
            (for [d (range nc)]
              (u/grid-val grid
                          [(+ start-r d)
                           (- nc (inc d))])))
          (for [start-c (rest (range nc))]
            (for [d (range nr)]
              (u/grid-val grid
                          [d
                           (- nc (+ start-c (inc d)))])))))))

(defn diags
  [grid]
  (concat (tl-br-diags grid)
          (tr-bl-diags grid)))

(defn rev-str
  [s]
  (apply str (reverse s)))

(defn options
  [grid]
  (let [rows grid
        cols (transpose-str grid)
        diags (diags grid)]
    (concat grid
            (map rev-str rows)
            cols
            (map rev-str cols)
            diags
            (map rev-str diags))))


(defn part-1
  []
  (let [strs (options input)]
    (->> strs
         (map #(re-seq #"XMAS" %))
         (map count)
         (reduce +))))

(defn x-mas?
  [grid a-pos]
  (let [tl (u/grid-val grid (map + a-pos [-1 -1]))
        tr (u/grid-val grid (map + a-pos [1 -1]))
        bl (u/grid-val grid (map + a-pos [-1 1]))
        br (u/grid-val grid (map + a-pos [1 1]))]
    (or (and (= \M tl tr)
             (= \S bl br))
        (and (= \M bl br)
             (= \S tl tr))
        (and (= \M tl bl)
             (= \S tr br))
        (and (= \M tr br)
             (= \S tl bl)))))

(defn part-2
  []
  (let [in input
        positions (u/grid-positions in)]
    (->> positions
         (filter #(= \A (u/grid-val in %)))
         (filter (partial x-mas? in))
         count)))

(comment
  (part-1) ;; => 
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [])
