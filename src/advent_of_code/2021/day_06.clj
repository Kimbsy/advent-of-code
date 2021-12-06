(ns advent-of-code.2021.day-06
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (first (line-seq (io/reader (io/resource "2021/day_06")))))

(def test-input "3,4,3,1,2")

(defn parse
  [in]
  (map read-string (map str (s/split in #","))))

(def offspring
  (memoize
   (fn
     [start days]
     (let [m (inc (quot (- days start 1) 7))
           r (- days start 3)]
       (if (<= days start)
         1N
         (+ 1N (reduce + (map #(offspring 6 (- r (* 7 %)))
                              (range m)))))))))

(defn part-1
  []
  (long (reduce + (pmap #(offspring % 80) (parse input)))))

(defn part-2
  []
  (long (reduce + (pmap #(offspring % 256) (parse input)))))

(comment
  (part-1) ;; => 363101
  (part-2) ;; => 1644286074024
  ,)

;; refactoring check
(= [(part-1) (part-2)] [363101 1644286074024])
