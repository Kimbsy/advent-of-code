(ns advent-of-code.2021.day-13
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_13"))))

(def test-input ["6,10"
                 "0,14"
                 "9,10"
                 "0,3"
                 "10,4"
                 "4,11"
                 "6,0"
                 "6,12"
                 "4,1"
                 "0,13"
                 "10,12"
                 "3,4"
                 "3,0"
                 "8,4"
                 "1,10"
                 "2,14"
                 "8,10"
                 "9,0"
                 ""
                 "fold along y=7"
                 "fold along x=5"])

(defn parse-input
  [in]
  (let [[dots folds] (remove #(empty? (first %)) (partition-by empty? in))]
    (let [positions (map #(map read-string (s/split % #",")) dots)]
      {:dots (map reverse positions)
       :folds (map (fn [f] (let [[v n] (s/split (subs f 11) #"=")] [(keyword v) (read-string n)])) folds)
       :max-pos [(apply max (map first positions))
                 (apply max (map second positions))]})))

(defn make-horiz-fold
  [g n]
  (let [l (map (partial take n) g)
        r (map (partial drop n) g)]
    (map (partial map +) l (map reverse r)))  )

(defn make-vert-fold
  [g n]
  (let [t (take n g)
        b (drop n g)]
    (map (partial map +) t (reverse b)))
)

(defn make-fold
  [g [axis n]]
  (if (= :x axis)
    (make-horiz-fold g n)
    (make-vert-fold g n)))

(defn part-1
  []
  (let [{:keys [dots folds max-pos]} (parse-input input)
        grid (apply u/grid-of 0 (map inc max-pos))
        dotted (reduce (fn [g p]
                         (assoc-in g p 1))
                       grid
                       dots)]
    (->> (reduce make-fold
                 dotted
                 (take 1 folds))
         (apply concat)
         (filter (partial < 0))
         count)))

(defn part-2
  []
  (let [in test-input]
    (let [{:keys [dots folds max-pos]} (parse-input input)
          grid (apply u/grid-of 0 (map inc max-pos))
          dotted (reduce (fn [g p]
                           (assoc-in g p 1))
                         grid
                         dots)]
      (map (fn [r] (map (fn [d] (if (< 0 d) 1 0)) r))(reduce make-fold
                                                                 dotted
                                                                 folds)))))

(comment
  (part-1) ;; => 647
  (part-2) ;; => HEJHJRCJ
  ,)

(def expected
  '((1 0 0 1 0 1 1 1 1 0 0 0 1 1 0 1 0 0 1 0 0 0 1 1 0 1 1 1 0 0 0 1 1 0 0 0 0 1 1 0)
    (1 0 0 1 0 1 0 0 0 0 0 0 0 1 0 1 0 0 1 0 0 0 0 1 0 1 0 0 1 0 1 0 0 1 0 0 0 0 1 0)
    (1 1 1 1 0 1 1 1 0 0 0 0 0 1 0 1 1 1 1 0 0 0 0 1 0 1 0 0 1 0 1 0 0 0 0 0 0 0 1 0)
    (1 0 0 1 0 1 0 0 0 0 0 0 0 1 0 1 0 0 1 0 0 0 0 1 0 1 1 1 0 0 1 0 0 0 0 0 0 0 1 0)
    (1 0 0 1 0 1 0 0 0 0 1 0 0 1 0 1 0 0 1 0 1 0 0 1 0 1 0 1 0 0 1 0 0 1 0 1 0 0 1 0)
    (1 0 0 1 0 1 1 1 1 0 0 1 1 0 0 1 0 0 1 0 0 1 1 0 0 1 0 0 1 0 0 1 1 0 0 0 1 1 0 0)))

;; refactoring check
(= [(part-1) (part-2)] [647 expected])
