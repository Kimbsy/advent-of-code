(ns advent-of-code.2021.day-14
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_14"))))

(def test-input ["NNCB"
                 ""
                 "CH -> B"
                 "HH -> N"
                 "CB -> H"
                 "NH -> C"
                 "HB -> C"
                 "HC -> B"
                 "HN -> C"
                 "NN -> C"
                 "BH -> H"
                 "NC -> B"
                 "NB -> B"
                 "BN -> B"
                 "BB -> N"
                 "BC -> B"
                 "CC -> N"
                 "CN -> C"])

(defn parse-input
  [in]
  [(first in) (into {} (map #(s/split % #" -> ") (drop 2 in)))])


(def insert
  (memoize
   (fn [[a b :as pair] rules]
     (str (get rules (apply str pair)) b))))

(defn part-1
  []
  (let [[start rules] (parse-input input)
        freqs (frequencies
               (nth (iterate
                     (fn [poly]
                       (let [strs (map #(insert % rules) (partition 2 1 poly))]
                         (reduce str (first poly) strs)))
                     start)
                    10))
        sorted (sort-by second freqs)]
    (- (second (last sorted))
       (second (first sorted)))))

(defn extract-result
  [result]
  (reduce (fn [acc [k v]]
            (update acc k (fn [val] (if val (+ v val) v))))
          {}
          (map (fn [[k v]]
                 [(first k) v])
               result)))

(defn part-2
  []
  (let [[start rule-input] (parse-input input)
        rules (into {} (concat (map (fn [[k v]]
                                      [(seq k) [[(first k) (first v)][(first v) (second k)]]])
                                    rule-input)
                               (map (fn [c] [[c nil] [[c nil]]]) (set (map first (keys rule-input))))))
        empty-state (zipmap (map seq (keys rules))
                            (repeat 0))
        starting-state (reduce (fn [s p]
                                 (update s p inc))
                               empty-state
                               (conj (partition 2 1 start)
                                     [(last start) nil]))]
    (let [freqs (extract-result
                 (nth (iterate #(reduce (fn [s [p c]]
                                          (reduce (fn [ss rp]
                                                    (update ss rp + c))
                                                  (update s p - c)
                                                  (get rules p)))
                                        %
                                        (filter (comp (partial < 0) second) %))
                               starting-state)
                      40))
          sorted (sort-by second freqs)]
      (- (second (last sorted))
         (second (first sorted))))))

(comment
  (part-1) ;; => 2345
  (part-2) ;; => 2432786807053
  ,)

;; refactoring check
(= [(part-1) (part-2)] [2345 2432786807053])
