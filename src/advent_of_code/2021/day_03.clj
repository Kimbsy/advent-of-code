(ns advent-of-code.2021.day-03
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_03"))))

(def test-input ["00100" "11110" "10110" "10111" "10101" "01111" "00111" "11100" "10000" "11001" "00010" "01010"])

(defn zero-count
  [bin-str]
  (->> bin-str
       (map u/char->int)
       (filter zero?)
       count))

(defn most-common
  [col]
  (if (<= (zero-count col) (/ (count col) 2))
    1
    0))

(defn least-common
  [col]
  (if (<= (zero-count col) (/ (count col) 2))
    0
    1))

(defn bin->int
  [bin-str]
  (read-string (apply str "2r" bin-str)))

(defn part-1
  []
  (* (bin->int (map most-common (u/transpose input)))
     (bin->int (map least-common (u/transpose input)))))

(defn filter-by-frequency
  [cols common-fn]
  (-> (loop [numbers cols
             digits (map common-fn (u/transpose cols))
             i 0]
        (if (= 1 (count numbers))
          (first numbers)
          (let [remainder (filter #(= (first digits) (u/char->int (nth % i))) numbers)]
            (recur remainder
                   (drop (inc i) (map common-fn (u/transpose remainder)))
                   (inc i)))))
      bin->int))

(defn part-2
  []
  (let [oxy (filter-by-frequency input most-common)
        co2 (filter-by-frequency input least-common)]
    (* oxy co2)))

(comment
  (part-1) ;; => 2648450
  (part-2) ;; => 2845944
  ,)

;; refactoring check
(= [(part-1) (part-2)] [2648450 2845944])
