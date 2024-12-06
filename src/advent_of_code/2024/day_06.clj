(ns advent-of-code.2024.day-06
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2024/day_06"))))

(defn print-grid
  [grid]
  (print (apply str (interpose "\n" grid))))

(def test-input ["....#....."
                 ".........#"
                 ".........."
                 "..#......."
                 ".......#.."
                 ".........."
                 ".#..^....."
                 "........#."
                 "#........."
                 "......#..."])

(defn parse-input
  [in]
  (mapv vec in))

(defn get-start
  [grid]
  (second
   (first 
    (filter
     #(= \^ (first %))
     (for [i (range (count grid))
           j (range (count (first grid)))]
       [(u/grid-val grid [i j]) [i j]])))))

(def all-directions
  ;; reverse because [i j] => [j i]
  (cycle (map reverse [[0 -1]
                       [1 0]
                       [0 1]
                       [-1 0]])))

(defn part-1
  []
  (let [in (parse-input input)
        start (get-start in)]
    (count
     (loop [grid in
            pos start
            directions all-directions
            visited #{}]
       (let [next-pos (map + pos (first directions))]
         (if-let [next-val (u/grid-val grid next-pos)]
           (if (= \# next-val)
             (recur grid pos (rest directions) visited)
             (recur grid next-pos directions (conj visited next-pos)))
           visited))))))

(defn loops?
  [g new-obstacle-pos]
  (let [start (get-start g)]
    ;; don't put one at the start
    (when (not= new-obstacle-pos start)
      (let [modified (assoc-in g new-obstacle-pos \#)]
        (loop [grid modified
               pos start
               directions all-directions
               visited #{}]
          (let [next-pos (map + pos (first directions))]
            (when-let [next-val (u/grid-val grid next-pos)]
              (if (= \# next-val)
                (recur grid pos (rest directions) visited)
                (if (visited [next-pos (first directions)])
                  true
                  (recur grid next-pos directions (conj visited [next-pos (first directions)])))))))))))

(defn part-2
  []
  (let [in (parse-input input)
        options (u/grid-positions in)
        results (pmap #(loops? in %) options)]
    (frequencies results)))

(comment
  (part-1) ;; => 5329
  (part-2) ;; => 2162
  ,)

;; leave this off, part-2 takes ages
#_(= [(part-1) (part-2)] [5329 2162])
