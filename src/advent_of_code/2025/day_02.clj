(ns advent-of-code.2025.day-02
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (slurp (io/reader (io/resource "2025/day_02"))))

(def test-input "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659, 824824821-824824827,2121212118-2121212124")

(defn parse-input
  [in]
  (->> (clojure.string/split in #",")
       (map #(clojure.string/split % #"-"))
       (map (partial map read-string))))

(defn halve
  [n]
  (let [s (str n)]
    [(take (/ (count s) 2) s)
     (drop (/ (count s) 2) s)]))

(defn part-1
  []
  (->> input
       parse-input
       (mapcat (fn [[l h]] (range l (inc h))))
       (filter (fn [n]
                 (and (even? (count (str n)))
                      (apply = (halve n)))))
       (apply +)))

(defn part-2
  []
  (->> input
       parse-input
       (mapcat (fn [[l h]] (range l (inc h))))
       (filter (fn [n]
                 (let [s (str n)
                       l (count s)]
                   (and (< 1 l)
                        (some (fn [i]
                                (apply = (partition-all i s)))
                              (map inc (range (/ l 2))))))))
       (apply +)))

(comment
  (part-1) ;; => 13919717792
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [13919717792 ])
