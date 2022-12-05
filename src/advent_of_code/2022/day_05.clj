(ns advent-of-code.2022.day-05
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (slurp (io/reader (io/resource "2022/day_05"))))

(def test-input
  "    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2")

(defn pre-parse
  [in]
  (map s/split-lines (s/split in #"\n\n")))

(defn parse-stacks
  [stack-lines]
  (let [stack-data (butlast stack-lines)
        col-count (read-string (str (last (re-seq #"\d+" (last stack-lines)))))
        backward (reduce (fn [cols data-line]
                           (let [indices (map #(inc (* % 4)) (range col-count))
                                 indexed-crates (keep (fn [i]
                                                        (when (and (< i (count data-line))
                                                                   (not= \space (nth data-line i)))
                                                          [(/ (dec i) 4) (nth data-line i)]))
                                                      indices)]
                             (reduce (fn [cs [i c]]
                                       (update cs i conj c))
                                     cols
                                     indexed-crates)))
                         (vec (repeat col-count []))
                         stack-data)]
    (->> backward
         (map reverse)
         (mapv vec))))

(defn parse-moves
  [move-lines]
  (->> move-lines
       (map #(re-seq #"\d+" %))
       (map #(map read-string %))
       (map (fn [[n f t]] [n (dec f) (dec t)]))))

(defn move-applier
  [reverse?]
  (fn [stacks [n from to]]
    (let [crates (take-last n (nth stacks from))]
      (update stacks from #(vec (drop-last n %)))
      (-> stacks
          (update from #(vec (drop-last n %)))
          (update to #(vec (concat % (if reverse?
                                       (reverse crates)
                                       crates))))))))

(defn skim-top
  [stacks]
  (apply str (map last stacks)))

(defn part-1
  []
  (let [[stack-lines move-lines] (pre-parse input)
        stacks (parse-stacks stack-lines)
        moves (parse-moves move-lines)
        move-fn (move-applier true)
        result (reduce move-fn stacks moves)]
    (skim-top result)))

(defn part-2
  []
  (let [[stack-lines move-lines] (pre-parse input)
        stacks (parse-stacks stack-lines)
        moves (parse-moves move-lines)
        move-fn (move-applier false)
        result (reduce move-fn stacks moves)]
    (skim-top result)))

(comment
  (part-1) ;; => "VJSFHWGFT"
  (part-2) ;; => "LCTQFBVZV"
  ,)

;; refactoring check
(= [(part-1) (part-2)] ["VJSFHWGFT" "LCTQFBVZV"])
