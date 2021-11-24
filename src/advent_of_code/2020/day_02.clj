(ns advent-of-code.2020.day-02
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(def input
  (line-seq (io/reader (io/resource "2020/day_02"))))

(def test-input-1 ["1-3 a: abcde"
                   "1-3 b: cdefg"
                   "2-9 c: ccccccccc"])

(defn split-pass
  [s]
  (s/split s #"(-|: | )"))

(defn part-1
  []
  (->> input
       (filter (fn [in]
                 (let [[minimum maximum [c] pass] (split-pass in)
                       fs (frequencies pass)]
                   (<= (read-string minimum)
                       (or (get fs c) 0)
                       (read-string maximum)))))
       count))

(def test-input-2 ["1-3 a: abcde"
                   "1-3 b: cdefg"
                   "2-9 c: ccccccccc"
                   "13-16 j: jjjvjmjjkjjjjjjj"])

(defn part-2
  []
  (->> input
       (filter (fn [in]
                 (let [[i1 i2 [c] pass] (split-pass in)
                       good-1 (= c (get pass (dec (read-string i1))))
                       good-2 (= c (get pass (dec (read-string i2))))]
                   (or (and good-1 (not good-2))
                       (and (not good-1) good-2)))))
       count))



(comment
  (part-1) ;; => 542
  (part-2) ;; => 360
  ,)
