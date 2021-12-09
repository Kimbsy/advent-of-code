(ns advent-of-code.2021.day-09
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_09"))))

(def test-input ["2199943210" "3987894921" "9856789892" "8767896789" "9899965678"])

(defn parse-input
  [in]
  (mapv #(mapv u/char->int %) in))

(defn adj-vals
  [m pos]
  (map (partial u/grid-val m)
       (u/adjacent-positions m pos)))

(defn low?
  [m pos]
  (let [v (get-in m pos)]
    (every? #(< v %) (adj-vals m pos))))

(defn part-1
  []
  (let [in (parse-input input)]
    (->> (u/grid-positions in)
         (filter #(not= 9 (u/grid-val in %)))
         (filter #(low? in %))
         (map (partial u/grid-val in))
         (map inc)
         (reduce +))))

(defn non-9-cardinal-adjacent-positions
  [m pos]
  (remove #(= 9 (u/grid-val m %))
          (u/cardinal-adjacent-positions m pos)))

(defn expand
  [m poss]
  (let [new (->> poss
                 (mapcat (partial non-9-cardinal-adjacent-positions m))
                 (concat poss)
                 (into #{}))]
    (if (= poss new)
      new
      (recur m new))))

(defn part-2
  []
  (let [in (parse-input input)]
    (->> (u/grid-positions in)
         (filter #(not= 9 (u/grid-val in %)))
         (filter #(low? in %))
         (map #(expand in #{%}))
         (into #{})
         (map count)
         sort
         reverse
         (take 3)
         (reduce *))))

(comment
  (part-1) ;; => 502
  (part-2) ;; => 1330560
  ,)

;; refactoring check
(= (part-1) 502)
(= [(part-1) (part-2)] [502 1330560])
