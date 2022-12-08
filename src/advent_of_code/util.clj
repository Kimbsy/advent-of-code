(ns advent-of-code.util)

;; (take-until zero? [2 3 0 5 4]) => (2 3 0)
(defn take-until
  [pred col]
  (lazy-seq
   (when-let [s (seq col)]
     (if (pred (first s))
       (cons (first s) nil)
       (cons (first s) (take-until pred (rest s)))))))

(defn divisors
  [n]
  (->> (range n)
       (map inc)
       (filter #(zero? (rem n %)))))

(defn prime?
  [n]
  (= [1 n] (divisors n)))

(defn char->int
  [c]
  (Character/digit c 10))

(defn abs
  [n]
  (max n (- n)))

(defn median
  "The middle value of a sorted sequence"
  [coll]
  (nth coll (int (/ (count coll) 2))))

;;; Multidimensional grid stuff

;; Note: x and y will be backwards, but it should be fine if we always
;; ignore it. When printing we can use `transpose` to make it look
;; right.

(defn transpose
  [m]
  (apply map list m))

(defn grid-of
  "Create a grid of `v` whose dimensions are specified by dims"
  [v & dims]
  (reduce (fn [acc d]
            (vec (repeat d acc)))
          (vec (repeat (first dims) v))
          (rest dims)))

(defn grid-positions
  "The list of position vectors in a 2D grid"
  [grid]
  (for [i (range (count grid))
        j (range (count (first grid)))]
    [i j]))

(defn grid-val
  "Get the value at a position in a multidimensional grid"
  [grid pos]
  (get-in grid pos))

(defn bounded-adjacent-xys
  "Get the adjacent row/column indices for a position on a 2D grid"
  [grid [x y]]
  [(filter #(<= 0 % (dec (count grid))) [(dec x) x (inc x)])
   (filter #(<= 0 % (dec (count (first grid)))) [(dec y) y (inc y)])])

(defn adjacent-positions
  "Get the adjacent positions for a position on a 2D grid

  This will return 3 positions for a corner position, 5 for an edge
  position, or all 8 for a middle position"
  [grid [x y :as pos]]
  (let [[xs ys] (bounded-adjacent-xys grid pos)]
    (for [i xs
          j ys
          :when (not (and (= x i) (= y j)))]
      [i j])))

(defn cardinal-adjacent-positions
  "Get the cardinally adjacent positions for a position on a 2D grid

  This will return 2 positions for a corner position, 3 for an edge
  position, or all 4 for a middle position"
  [grid [x y :as pos]]
  (let [[xs ys] (bounded-adjacent-xys grid pos)]
    (for [i xs
          j ys
          :when (and (or (= x i) (= y j))
                     (not (and (= x i) (= y j))))]
      [i j])))
