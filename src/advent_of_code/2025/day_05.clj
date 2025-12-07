(ns advent-of-code.2025.day-05
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
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

(defn no-overlap?
  [rs [cl ch]]
  (every? (fn [[l h]]
            (and (not (<= l cl h))
                 (not (<= l ch h))
                 (not (<= cl l h ch))))
          rs))

(defn remove-wrapped
  [rs [cl ch]]
  (remove (fn [[l h]] (<= cl l h ch))
          rs))

(defn extend-ranges
  [rs [cl ch]]
  (map (fn [[l h]]
         (cond
           (<= l cl h ch) [l ch]
           (<= cl l ch h) [cl h]
           :else [l h]))
       rs))

(defn combine-range
  [rs r]
  (let [new (remove-wrapped rs r)]
    (cond
      (completely-within? new r) new
      (no-overlap? new r) (conj new r)
      :else (extend-ranges new r))))

(defn part-2
  []
  (let [[range-strings _] (parse-input input)
        ranges (parse-ranges range-strings)

        combined-ranges (reduce combine-range
                                (take 1 ranges)
                                (rest ranges))]

    ;; not quite working
    ;; (combine-range [[5 10] [20 30]] [9 21])
    ;; => ([5 21] [9 30])
    ;; so we can't just subtract, we need to combine overlapping ones
    ;; maybe sort-by l and then pair them till no change?
    
    ))

(comment
  (part-1) ;; => 
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [])
