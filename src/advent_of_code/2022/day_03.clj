(ns advent-of-code.2022.day-03
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_03"))))

(def test-input ["vJrwpWtwJgWrhcsFMMfFFhFp"
                 "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL"
                 "PmmdzqPrVvPwwTWBwg"
                 "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn"
                 "ttgJtRGJQctTZtZT"
                 "CrZsJsPPZsGzwwsLwLmpwMDw"])

(def priorities (into (zipmap (range 65 91) (range 27 53))
                      (zipmap (range 97 123) (range 1 27))))

(defn split-half
  [s]
  (let [half (/ (count s) 2)]
    [(take half s) (drop half s)]))

(defn part-1
  []
  (transduce
   (comp (map split-half)
         (map (partial map set))
         (map (partial apply cset/intersection))
         (map first)
         (map int)
         (map priorities))
   +
   input))

(defn part-2
  []
  (transduce
   (comp (map (partial map set))
         (map (partial apply cset/intersection))
         (map first)
         (map int)
         (map priorities))
   +
   (partition 3 input)))

(comment
  (part-1) ;; => 7872
  (part-2) ;; => 2497
  ,)

;; refactoring check
(= [(part-1) (part-2)] [7872 2497])
