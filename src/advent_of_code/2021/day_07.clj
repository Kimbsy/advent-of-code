(ns advent-of-code.2021.day-07
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (map read-string (s/split (first (line-seq (io/reader (io/resource "2021/day_07")))) #",")))

(def test-input [16 1 2 0 4 2 7 1 2 14])

(defn abs
  [n]
  (max n (- n)))

(defn part-1
  []
  (let [low (apply min input)
        high (apply max input)]
    (first (sort (map (fn [t]
                        (reduce + (map #(abs (- % t))
                                       input)))
                      (range low (inc high)))))))

(def cost
  (memoize
   (fn [total v]
     (if (zero? v)
       total
       (recur (+ total v) (dec v))))))

(defn part-2
  []
  (let [low (apply min input)
        high (apply max input)]
    (first (sort (map (fn [t]
                        (reduce + (map
                                   (fn [c]
                                     (cost 0 (abs (- c t))))
                                   input)))
                      (range low (inc high))))))  )

(comment
  (part-1) ;; => 344605
  (part-2) ;; => 93699985
  ,)


;; refactoring check
(= [(part-1) (part-2)] [344605 93699985])
