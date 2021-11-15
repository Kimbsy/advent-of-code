(ns advent-of-code.2020.day-01
  (:require [clojure.java.io :as io]))

(def input
  (line-seq (io/reader (io/resource "2020/day_01"))))

(def int-inputs (map read-string input))

(defn part-1
  []
  (for [i int-inputs
        j int-inputs
        :when (not= i j)
        :when (< i j)
        :when (= 2020 (+ i j))]
    (* i j)))

(defn part-2
  []
  (for [i int-inputs
        j int-inputs
        k int-inputs
        :when (and (not= i j) (not= i k) (not= j k))
        :when (< i j k)
        :when (= 2020 (+ i j k))]
    (* i j k)))

(comment
  (part-1) ;; => (100419)
  (part-2) ;; => (265253940)
  ,)
