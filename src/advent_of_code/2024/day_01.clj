(ns advent-of-code.2024.day-01
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2024/day_01"))))

(def test-input ["3   4"
                 "4   3"
                 "2   5"
                 "1   3"
                 "3   9"
                 "3   3"])

(defn parse-input
  [in]
  (map #(read-string (str "[" % "]")) in))

(defn part-1
  []
  (->> input
       parse-input
       u/transpose
       (map sort)
       (apply map -)
       (map abs)
       (reduce +)))

(defn part-2
  []
  (let [[l1 l2] (->> input
                     parse-input
                     u/transpose)
        fs (frequencies l2)]
    (->> l1
         (map (fn [n] ((fnil #(* n %) 0)
                       (get fs n))))
         (reduce +))))

(comment
  (part-1) ;; => 1830467
  (part-2) ;; => 26674158
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1830467 26674158])
