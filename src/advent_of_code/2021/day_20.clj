(ns advent-of-code.2021.day-20
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_20"))))

(def test-input
  [(apply str
          ["..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##"
           "#..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###"
           ".######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#."
           ".#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#....."
           ".#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.."
           "...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#....."
           "..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#"])
   ""
   "#..#."
   "#...."
   "##..#"
   "..#.."
   "..###"])

(defn parse-input
  [in]
  (let [[alg & image-data] (remove empty? in)]
    [alg (vec (mapv #(s/split % #"") image-data))]))

(defn pad-grid
  [g]
  (let [x (count (first g))
        y (count g)]
    (mapv vec
          (concat [(repeat (+ 2 x) ".")]
                  (map (fn [row] (concat ["."] row ["."])) g)
                  [(repeat (+ 2 x) ".")]))))

(defn reduce-grid
  [g]
  (->> g
       (drop 1)
       butlast
       (map (partial drop 1))
       (map butlast)))

(defn strs->bin
  [strs]
  (->> strs
       (map {"." 0 "#" 1})
       (apply str "2r")
       read-string))

(defn enhanced-val
  [alg strs]
  (str (nth alg (strs->bin strs))))

(defn get-group
  [g p]
  (let [[a b c
         d   f
         g h i] (u/adjacent-positions g p)]
    [a b c d p f g h i]))

(defn enhance
  [alg image]
  (print ".")
  (let [image+1 (pad-grid image)
        image+2 (pad-grid image+1)
        ps (u/grid-positions image+1)
        shifted-ps (map (partial mapv inc) ps)
        groups (map (juxt identity (partial get-group image+2)) shifted-ps)
        new-vals (->> groups
                      (map (fn [[p adj-ps]]
                             (->> adj-ps
                                  (map (partial u/grid-val image+2))
                                  (enhanced-val alg)
                                  (conj [p])))))]
    (reduce-grid(reduce-grid(reduce (fn [acc [pos v]]
                                      (assoc-in acc (map dec pos) v))
                                    image+1
                                    new-vals)))))

(defn part-1
  []
  (let [[alg in] (parse-input input)]
    (->> (enhance alg (enhance alg (pad-grid (pad-grid (pad-grid in)))))
         reduce-grid
         reduce-grid
         reduce-grid
         (map frequencies)
         (map #(or (get % "#") 0))
         (reduce +))))

(defn part-2
  []
  (let [[alg in] (parse-input input)]
    (->> (nth (iterate (partial enhance alg) (nth (iterate pad-grid in) 120)) 50)
         (map frequencies)
         (map #(or (get % "#") 0))
         (reduce +))))

(comment
  (part-1) ;; => 5231
  (part-2) ;; => 14279
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [5231 14279])
