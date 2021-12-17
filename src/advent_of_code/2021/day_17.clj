(ns advent-of-code.2021.day-17
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input [[143 177] [-106 -71]])

(def test-input [[20 30] [-10 -5]])

(defn pos-in-rect?
  [[xs ys :as rect] [px py :as pos]]
  (let [[rx1 rx2] (sort xs)
        [ry1 ry2] (sort ys)]
    (and (<= rx1 px rx2)
         (<= ry1 py ry2))))

(defn update-state
  [[[vx vy :as v] p]]
  (let [new-p (map + p v)
        new-vx (cond (zero? vx) 0
                     (pos? vx) (dec vx)
                     (neg? vx) (inc vx))
        new-vy (dec vy)
        new-v [new-vx new-vy]]
    [new-v new-p]))

(defn valid?
  [start-v [xs ys :as target]]
  (let [ps (->> (iterate update-state [start-v [0 0]])
                  (take-while (fn [[v [px py :as p]]]
                                (<= (apply min ys) py)))
                  (map second))
        hits (filter (partial pos-in-rect? target) ps)]
    (when (seq hits)
      [start-v ps hits (last (sort-by second ps))])))

(defn part-1
  []
  (let [[xs ys :as in] input]
    (->> (for [vx (range (inc (apply max xs)))
               vy (range 200)]
           (valid? [vx vy] in))
         (remove nil?)
         (sort-by (fn [[_ _ _ [_ max-y]]] max-y))
         last
         last
         last)))

(defn part-2
  []
  (let [[xs ys :as in] input]
    (->> (for [vx (range 1 178)
               vy (range -150 150)]
           (valid? [vx vy] in))
         (remove nil?)
         count
         )))

(comment
  (part-1) ;; => 5565
  (part-2) ;; => 2118
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [])
