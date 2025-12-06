(ns advent-of-code.2025.day-04
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_04"))))

(def test-input
  ["..@@.@@@@."
   "@@@.@.@.@@"
   "@@@@@.@.@@"
   "@.@@@@..@."
   "@@.@@@@.@@"
   ".@@@@@@@.@"
   ".@.@.@.@@@"
   "@.@@@.@@@@"
   ".@@@@@@@@."
   "@.@.@@@.@."])

(defn parse-input
  [in]
  (mapv vec in))

(defn available
  [in]
  (->> in
       u/grid-positions
       ;;(take 3)
       (filter #(#{\@} (u/grid-val in %)))
       (map (fn [p] [p (u/adjacent-positions in p)]))
       (map (fn [[p ps]]
              [p (->> ps
                      (map (partial u/grid-val in))
                      (filter #{\@})
                      count)]))
       (filter #(< (second %) 4))))

(defn part-1
  []
  (let [in (parse-input input)]
    (->> in
         available
         count)))

(defn all-available
  [in total]
  (let [to-remove (available in)]
    (if (empty? to-remove)
      total
      (let [updated (reduce (fn [acc [[x y] _]]
                              (assoc-in acc [x y] \.))
                            in
                            to-remove)]
        (all-available updated (+ total (count to-remove)))))))

(defn part-2
  []
  (let [in (parse-input input)]
    (all-available in 0)))

(comment
  (part-1) ;; => 1516
  (part-2) ;; => 
  ,)

;; refactoring check
(= [(part-1) #_(part-2)] [1516])
