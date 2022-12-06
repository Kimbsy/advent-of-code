(ns advent-of-code.2022.day-06
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (slurp (io/reader (io/resource "2022/day_06"))))

(def test-input "mjqjpqmgbljsphdztnvjfqwrcgsmlb")

(defn solve
  [in n]
  (->> in
       (partition n 1)
       (map-indexed vector)
       (filter (fn [[i xs]] (= n (count (set xs)))))
       ffirst
       (+ n)))

(defn part-1
  []
  (solve input 4))

(defn part-2
  []
  (solve input 14))

(comment
  (part-1) ;; => 1343
  (part-2) ;; => 2193
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1343 2193])
