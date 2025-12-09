(ns advent-of-code.2025.day-09
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_09"))))

(def test-input
  ["7,1"
   "11,1"
   "11,7"
   "9,7"
   "9,5"
   "2,5"
   "2,3"
   "7,3"])

(defn parse-input
  [in]
  (->> in
       (map #(s/split % #","))
       (map (partial map read-string))))

(defn area
  [[x1 y1] [x2 y2]]
  (let [w (inc (- (max x1 x2)
                  (min x1 x2)))
        h (inc (- (max y1 y2)
                  (min y1 y2)))]
    (* w h)))

(defn part-1
  []
  (let [in (parse-input input)]
    (for [p1 in
          p2 in
          :when (not= p1 p2)]
      [p1 p2 (area p1 p2)])))

(defn part-2
  []
  )

(comment
  (part-1) ;; => 
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [])
