(ns advent-of-code.2021.day-09
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (mapv #(mapv u/char->int %) (line-seq (io/reader (io/resource "2021/day_09")))))

(def test-input (mapv #(mapv u/char->int % ) ["2199943210" "3987894921" "9856789892" "8767896789" "9899965678"]))

(defn adj
  [m [x y]]
  (let [xs (filter #(<= 0 % (dec (count m))) [(dec x) x (inc x)])
        ys (filter #(<= 0 % (dec (count (first m)))) [(dec y) y (inc y)])]
    (remove nil?    (map #(get-in m %)
                         (for [i xs
                               j ys
                               :when (not (and (= x i) (= y j)))]
                           [i j])))))

(defn card-adj-poss
  [m [x y]]
  (let [xs (filter #(<= 0 % (dec (count m))) [(dec x) x (inc x)])
        ys (filter #(<= 0 % (dec (count (first m)))) [(dec y) y (inc y)])]
    (for [i xs
          j ys
          :when (and (or (= x i) (= y j))
                     (not (and (= x i) (= y j))))]
      [i j])))

(defn low?
  [m pos]
  (let [v (get-in m pos)]
    (every? #(< v %) (adj m pos))))

(defn part-1
  []
  (let [in input
        poss (for [i (range (count in))
                   j (range (count (first in)))]
               [i j])]

    (reduce + (map inc (map #(get-in in %) (filter #(low? in %) poss))))))

(defn pos-val
  [m pos]
  (get-in m pos))

(defn expand
  [m poss]
  (let [new (into #{} (concat (mapcat (fn [pos]
                                        (remove #(= 9 (pos-val m %))
                                                (card-adj-poss m pos)))
                                      poss)
                              poss))]
    (if (= poss new)
      new
      (recur m new))))

(defn part-2
  []
  (let [in input
        poss (for [i (range (count in))
                   j (range (count (first in)))
                   :when (not= 9 (pos-val in [i j]))]
               [i j])]
    (reduce * (take 3 (reverse (sort (map count (into #{} (map #(expand in #{%}) poss)))))))) )

(comment
  (part-1) ;; => 502
  (part-2) ;; => 1330560
  ,)

;; refactoring check
(= [(part-1) (part-2)] [502 1330560])
