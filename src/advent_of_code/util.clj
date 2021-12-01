(ns advent-of-code.util)

(defn divisors
  [n]
  (->> (range n)
       (map inc)
       (filter #(zero? (rem n %)))))

(defn prime?
  [n]
  (= [1 n] (divisors n)))
