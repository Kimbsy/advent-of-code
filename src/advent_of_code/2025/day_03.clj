(ns advent-of-code.2025.day-03
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_03"))))

(def test-input ["987654321111111"
                 "811111111111119"
                 "234234234234278"
                 "818181911112111"])

(defn parse-input
  [in]
  (mapv (partial mapv #(Character/digit % 10)) in))

(defn max-joltage
  [js]
  (let [[i max-l] (reduce (fn [[_ current :as acc] [i j]]
                            (if (= 9 j)
                              (reduced [i j])
                              (if (< current j)
                                [i j]
                                acc)))
                          [-1 -1]
                          (map list (range) (butlast js)))
        remaining (drop (inc i) js)
        max-r (apply max remaining)]
;;    [[max-l i] max-r (+ (* max-l 10) max-r)]
    (+ (* max-l 10) max-r)))

(defn part-1
  []
  (->> input
       parse-input
       (map max-joltage)
       (apply +)))

(defn max-joltage-n
  [js chosen n]
  (if (zero? n)
    (read-string (apply str chosen))
    (let [[i max-l] (reduce (fn [[_ current :as acc] [i j]]
                              (if (= 9 j)
                                (reduced [i j])
                                (if (< current j)
                                  [i j]
                                  acc)))
                            [-1 -1]
                            (map list (range) (drop-last (dec n) js)))
          remaining (drop (inc i) js)]
      (max-joltage-n remaining (conj chosen max-l) (dec n)))))

(defn part-2
  []
  (->> input
       parse-input
       (map #(max-joltage-n % [] 12))
       (apply +)))

(comment
  (part-1) ;; => 
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [])
