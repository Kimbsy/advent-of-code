(ns advent-of-code.2022.day-01
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_01"))))

(def test-input ["1000"
                 "2000"
                 "3000"
                 ""
                 "4000"
                 ""
                 "5000"
                 "6000"
                 ""
                 "7000"
                 "8000"
                 "9000"
                 ""
                 "10000"])

(defn parse-input
  [in]
  (->> in
      (partition-by s/blank?)
      (remove #(= [""] %))))

(defn part-1
  []
  (let [in (parse-input input)]
     (->> in
          (map #(map read-string %))
          (map #(reduce + %))
          (apply max))))

(defn part-2
  []
  (let [in (parse-input input)]
     (->> in
          (map #(map read-string %))
          (map #(reduce + %))
          sort
          (take-last 3)
          (reduce +))))

(comment
  (part-1) ;; => 72718
  (part-2) ;; => 213089
  ,)

;; refactoring check
(= [(part-1) (part-2)] [72718 213089])
