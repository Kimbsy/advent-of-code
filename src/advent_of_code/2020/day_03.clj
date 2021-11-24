(ns advent-of-code.2020.day-03
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]))

(def input
  (line-seq (io/reader (io/resource "2020/day_03"))))

(def test-input
  ["..##......."
   "#...#...#.."
   ".#....#..#."
   "..#.#...#.#"
   ".#...##..#."
   "..#.##....."
   ".#.#.#....#"
   ".#........#"
   "#.##...#..."
   "#...##....#"
   ".#..#...#.#"])

(defn infinite-trees
  [input]
  (map cycle input))

(defn count-trees
  [dx dy in]
  (-> (reduce
       (fn [{:keys [y x] :as acc} row]
         (if (zero? (mod y dy))
           (if (= \# (nth row x))
             (-> acc
                 (update :total inc)
                 (update :x + dx)
                 (update :y inc))
             (-> acc
                 (update :x + dx)
                 (update :y inc)))
           (-> acc
               (update :y inc))))
       {:x 0
        :y 0
        :total 0}
       (infinite-trees in))
      :total))

(defn part-1
  []
  (count-trees 3 1 input))

(defn part-2
  []
  (let [slopes [[1 1]
                [3 1]
                [5 1]
                [7 1]
                [1 2]]]
    (transduce
     (map (fn [[dx dy]]
            (count-trees dx dy input)))
     *
     slopes)))

(comment
  (part-1) ;; => 184
  (part-2) ;; => 2431272960
  ,)
