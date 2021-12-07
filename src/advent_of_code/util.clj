(ns advent-of-code.util)

(defn divisors
  [n]
  (->> (range n)
       (map inc)
       (filter #(zero? (rem n %)))))

(defn prime?
  [n]
  (= [1 n] (divisors n)))

(defn transpose
  [m]
  (apply map list m))

(defn char->int
  [c]
  (Character/digit c 10))

(defn abs
  [n]
  (max n (- n)))
