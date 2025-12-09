(ns advent-of-code.2025.day-05
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_05"))))

(def test-input
  ["3-5"
   "10-14"
   "16-20"
   "12-18"
   ""
   "1"
   "5"
   "8"
   "11"
   "17"
   "32"])

(defn parse-ranges
  [range-strings]
  (map #(map read-string (s/split % #"-"))
       range-strings))

(defn parse-input
  [in]
  (->> in
       (partition-by #{""})
       (remove #{[""]})))

(defn part-1
  []
  (let [[range-strings str-ids] (parse-input input)
        ids (map read-string str-ids)
        ranges (parse-ranges range-strings)]
    (->> ids
         (filter (fn [id]
                   (some (fn [[l h]]
                           (<= l id h))
                         ranges)))
         count)))

(defn completely-within?
  [rs [cl ch]]
  (every? (fn [[l h]]
            (< l cl ch h))
          rs))

(defn overlapping?
  [[l1 h1] [l2 h2]]
  (or (<= l1 l2 h2 h1)
      (<= l2 l1 h1 h2)
      (<= l1 l2 h1 h2)
      (<= l2 l1 h2 h1)))

(defn no-overlap?
  [rs current]
  (every? (fn [r]
            (not (overlapping? current r)))
          rs))

(defn remove-wrapped
  [rs [cl ch]]
  (remove (fn [[l h]] (<= cl l h ch))
          rs))

(defn extend-range
  [[l1 h1] [l2 h2]]
  (cond
    (<= l1 l2 h2 h1) [l1 h1]
    (<= l2 l1 h1 h2) [l2 h2]
    (<= l1 l2 h1 h2) [l1 h2]
    (<= l2 l1 h2 h1) [l2 h1]))

;; @NOTE largest range is 8,662,419,183,882
(defn part-2
  []
  (let [[range-strings _] (parse-input input)
        ranges (distinct (parse-ranges range-strings))
        combined (loop [rs ranges]
                   (let [pairs (remove (partial apply =) (combo/combinations rs 2))]
                     (if (every? (partial apply (complement overlapping?)) pairs)
                       rs
                       (let [[r1 r2] (first (filter (partial apply overlapping?) pairs))
                             fine (remove #{r1 r2} rs)]
                         (recur (conj fine (extend-range r1 r2)))))))]

    (->> combined
         (map (fn [[l h]] (- (inc h) l)))
         (apply +))

    ;; ((3 5) (10 14) (16 20) (12 18))
    ;; ([10 18] (3 5) (10 14) (16 20) (12 18))
    ;; ([10 18] [10 18] (3 5) (10 14) (16 20) (12 18))
    ;; ([10 18] [10 18] [10 18] (3 5) (10 14) (16 20) (12 18))
    ;; ([10 18] [10 18] [10 18] [10 18] (3 5) (10 14) (16 20) (12 18))

    

    ;; not quite working
    ;; (combine-range [[5 10] [20 30]] [9 21])
    ;; => ([5 21] [9 30])
    ;; so we can't just subtract, we need to combine overlapping ones
    ;; maybe sort-by l and then pair them till no change?
    
    ))

(comment
  (part-1) ;; => 679
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [679 nil])
