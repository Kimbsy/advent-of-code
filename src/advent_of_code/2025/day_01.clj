(ns advent-of-code.2025.day-01
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_01"))))

(def test-input ["L68"
                 "L30"
                 "R48"
                 "L5"
                 "R60"
                 "L55"
                 "L1"
                 "L99"
                 "R14"
                 "L82"])

(defn parse-input
  [in]
  (map (fn [s] [(first s) (read-string (subs s 1))])
       in))

(defn part-1
  []
  (->> input
       parse-input
       (reduce (fn [{:keys [current zeros] :as acc} [op n]]
                 (let [new (mod (({\L - \R +} op) current n) 100)]
                   {:current new
                    :zeros (if (zero? new)
                             (inc zeros)
                             zeros)}))
               {:current 50
                :zeros 0})
       :zeros))

(defn rotate
  [start direction amount]
  (reduce 
   (fn [{:keys [current zeros] :as acc} _]
     (let [new ((case direction
                  \R inc
                  \L dec) current)
           modded (mod new 100)]
       {:current modded
        :zeros (if (zero? modded)
                 (inc zeros)
                 zeros)}))
   {:current start
    :zeros 0}
   (range amount)))

(defn part-2
  []
  (->> input
       parse-input
       (reduce (fn [{:keys [current zeros] :as acc} [op n]]
                 (let [adjusted (({\L - \R +} op) current n)
                       new (mod adjusted 100)]
                   {:current new
                    :zeros (+ zeros (:zeros (rotate current op n)))}))
               {:current 50
                :zeros 0})
       :zeros))

(comment
  (part-1) ;; => 984
  (part-2) ;; => 5657
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [984 5657])
