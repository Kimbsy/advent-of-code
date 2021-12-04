(ns advent-of-code.2021.day-04
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_04"))))

(def test-input ["7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1" "" "22 13 17 11  0" " 8  2 23  4 24" "21  9 14 16  7" " 6 10  3 18  5" " 1 12 20 15 19" "" " 3 15  0  2 22" " 9 18 13 17  5" "19  8  7 25 23" "20 11 10 24  4" "14 21 16 12  6" "" "14 21 17 24  4" "10 16 15  9 19" "18  8 23 26 20" "22 11 13  6  5" " 2  0 12  3  7"])

(defn numbers
  [input]
  (map read-string (s/split (first input) #",")))

(defn cell
  [s]
  {:num (read-string s)
   :called false})

(defn board
  [input-data]
  (->> input-data
       (map #(s/split % #" "))
       (map #(remove empty? %))
       (map (fn [row] (map cell row)))))

(defn boards
  [input]
  (->> input
       (drop 2)
       (partition-by empty?)
       (remove #(empty? (first %)))
       (map board)))

(defn update-cell
  [c called]
  (if (= called (:num c))
    (assoc c :called true)
    c))

(defn update-board
  [board called]
  (map (fn [row]
         (map #(update-cell % called) row))
       board))

(defn update-boards
  [boards called]
  (map #(update-board % called) boards))

(defn line-win?
  [line]
  (every? :called line))

(defn board-win?
  [board]
  (some line-win? (concat board (u/transpose board))))

(defn uncalled-total
  [board]
  (transduce (comp (remove :called)
                   (map :num))
             +
             (apply concat board)))

(defn part-1
  []
  (let [res (reduce (fn [{:keys [boards prev] :as acc} n]
                      (let [updated (update-boards boards n)]
                        (if (some board-win? boards)
                          (reduced {:board (first (filter board-win? boards))
                                    :n prev})
                          (assoc acc :boards updated :prev n))))
                    {:prev nil :boards (boards input)}
                    (numbers input))]
    (* (uncalled-total (:board res))
       (:n res))))

(defn part-2
  []
  (let [res (reduce (fn [{:keys [boards prev] :as acc} n]
                      (let [updated (update-boards boards n)]
                        (if (every? board-win? updated)
                          (reduced {:board (first updated)
                                    :n n})
                          (assoc acc :boards (remove board-win? updated) :prev n))))
                    {:prev nil :boards (boards input)}
                    (numbers input))]
    (* (uncalled-total (:board res))
       (:n res))))

(comment
  (part-1) ;; => 89001
  (part-2) ;; => 7296
  ,)

;; refactoring check
(= [(part-1) (part-2)] [89001 7296])
