(ns advent-of-code.2021.day-01
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_01"))))

(def test-input [199 200 208 210 200 207 240 269 260 263])

(defn part-1
  []
  (->> input
       (map read-string)
       (partition 2 1)
       (filter (partial apply <))
       count))

(defn part-2
  []
  (->> input
       (map read-string)
       (partition 3 1)
       (map (partial reduce +))
       (partition 2 1)
       (filter (partial apply <))
       count))

(comment
  (part-1) ;; => 1233
  (part-2) ;; => 1275
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1233 1275])
