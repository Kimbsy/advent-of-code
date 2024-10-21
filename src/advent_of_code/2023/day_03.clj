(ns advent-of-code.2023.day-03
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2023/day_03"))))

(def mini-input
  [".23."
   ".^.."])

(def test-input
  ["467..114.."
   "...*......"
   "..35..633."
   "......#..."
   "617*......"
   ".....+.58."
   "..592....."
   "......755."
   "...$.*...."
   ".664.598.."])

(defn parse-input
  [in]
  in)

(defn buffer-input
  [in]
  (let [w (count (first in))
        buffer (apply str (repeat (+ w 2) \.))]
    (mapv vec
          (concat [buffer]
                  (map (fn [line]
                         (apply str
                                (concat [\.]
                                        line
                                        [\.])))
                       in)
                  [buffer]))))

(def digit? #{\1 \2 \3 \4 \5 \6 \7 \8 \9 \0})

(def special-char? (complement (cset/union digit? #{\.})))

(defn get-active-numbers
  [grid active-nums pos remaining]
  (if (nil? pos)
    active-nums
    (let [rows (partition-by first remaining)
          current-row (first rows)]
      (if (digit? (u/grid-val grid pos))
        (let [digits (cons {:pos pos
                            :val (u/grid-val grid pos)}
                           (map (fn [pos]
                                  {:pos pos
                                   :val (u/grid-val grid pos)})
                                (take-while #(digit? (u/grid-val grid %))
                                            current-row)))
              number (parse-long (apply str (map :val digits)))
              adjacent-positions (into #{}
                                       (mapcat #(u/adjacent-positions grid %)
                                               (map :pos digits)))
              adjacent-values (map #(u/grid-val grid %) adjacent-positions)
              active? (some special-char? adjacent-values)]
          (if active?
            (recur grid
                   (conj active-nums number)
                   (first (drop (count digits) remaining))
                   (rest (drop (count digits) remaining)))
            (recur grid
                   active-nums
                   (first (drop (count digits) remaining))
                   (rest (drop (count digits) remaining)))))
        (recur grid
               active-nums
               (first remaining)
               (rest remaining))))))

(defn part-1
  []
  (let [in input
        grid (buffer-input in)
        positions (map #(map + [1 1] %)
                       (u/grid-positions in))
        active (get-active-numbers grid [] (first positions) (rest positions))]
    (reduce + active)))

(defn part-2
  []
  )

(comment
  (part-1) ;; => 527144
  (part-2) ;; =>
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [527144 0])
