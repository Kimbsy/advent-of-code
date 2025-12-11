(ns advent-of-code.2025.day-10
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_10"))))

(def test-input
  ["[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}"
   "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}"
   "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"])

(defn parse-lights
  [s]
  (->> s
       (filter #{\. \#})
       (map {\. 0 \# 1})
       (apply str "2r")
       (read-string)))

(defn parse-buttons
  [n-bits bs]
  (mapv (fn [s]
          (let [wired-bits (read-string s)
                bits (vec (repeat n-bits 0))]
            (->> wired-bits
                 (reduce (fn [acc b]
                           (assoc acc b 1))
                         bits)
                 (apply str "2r")
                 read-string)))
        bs))

(defn parse-input
  [in]
  (map (fn [r]
         (let [target-lights (re-find #"\[.*\]" r)
               n-bits (- (count target-lights) 2)
               buttons (re-seq #"\(.*?\)" r)
               joltages (re-find #"\{.*\}" r)]
           [(parse-lights target-lights)
            (parse-buttons n-bits buttons)
            joltages]))
       in))

(defn valid-combos
  "button presses are commutative (they're XOR ops) so we should always
  have only 0 or 1 presses of each button"
  [bs]
  (apply concat 
         (for [i (map inc (range (count bs)))]
           (combo/combinations bs i))))

(defn min-presses
  [target-lights buttons]
  (let [press-combos (valid-combos buttons)]
    (->> press-combos
         (filter (fn [presses]
                   (= target-lights
                      (reduce (fn [acc p]
                                (bit-xor acc p))
                              0
                              presses))))
         first
         count)))

(defn part-1
  []
  (let [in (parse-input input)]
    (apply + (map (fn [[target-lights buttons _joltages]]
                    (min-presses target-lights buttons))
                  in))))

(defn part-2
  []
  )

(comment
  (part-1) ;; => 527
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [527 nil])
