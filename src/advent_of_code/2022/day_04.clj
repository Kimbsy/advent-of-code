(ns advent-of-code.2022.day-04
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_04"))))

(def test-input ["2-4,6-8"
                 "2-3,4-5"
                 "5-7,7-9"
                 "2-8,3-7"
                 "6-6,4-6"
                 "2-6,4-8"])

(defn parse-input
  [in]
  (map (fn [s]
         (let [[f1 t1 f2 t2] (map read-string (s/split s #"[-,]"))]
           [(set (range f1 (inc t1)))
            (set (range f2 (inc t2)))]))
       in))

(defn part-1
  []
  (->> input
       parse-input
       (map (partial apply d/diff))
       (map (partial take 2))
       (filter (partial some nil?))
       count))

(defn part-2
  []
  (->> input
       parse-input
       (map (partial apply d/diff))
       (map last)
       (remove nil?)
       count))

(comment
  (part-1) ;; => 515
  (part-2) ;; => 883
  ,)

;; refactoring check
(= [(part-1) (part-2)] [515 883])
