(ns advent-of-code.2024.day-02
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2024/day_02"))))

(def test-input ["7 6 4 2 1"
                 "1 2 7 8 9"
                 "9 7 6 2 1"
                 "1 3 2 4 5"
                 "8 6 4 4 1"
                 "1 3 6 7 9"])

(defn parse-input
  [in]
  (map #(read-string (str "[" % "]"))
       in))

(defn safe?
  [report]
  (and (or (apply < report)
           (apply > report))
       (let [pairs (partition 2 1 report)]
         (every? (fn [[a b]]
                   (<= 1 (abs (- a b)) 3))
                 pairs))))

(defn part-1
  []
  (let [reports (parse-input input)]
    (count (filter safe? reports))))

(defn get-options
  [report]
  (for [i (range (count report))]
    (concat (take i report)
            (drop (inc i) report))))

(defn damped-safe?
  [report]
  (let [options (get-options report)]
    (some safe? options)))

(defn part-2
  []
  (let [reports (parse-input input)]
    (count (filter damped-safe? reports))))

(comment
  (part-1) ;; => 369
  (part-2) ;; => 428
  ,)

;; refactoring check
(= [(part-1) (part-2)] [369 428])
