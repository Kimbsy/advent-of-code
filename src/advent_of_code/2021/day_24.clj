(ns advent-of-code.2021.day-24
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_24"))))

(def test-input-1
  ["inp x"
   "mul x -1"])

(def test-input-2
  ["inp z"
   "inp x"
   "mul z 3"
   "eql z x"])

(def test-input-3
  ["inp w"
   "add z w"
   "mod z 2"
   "div w 2"
   "add y w"
   "mod y 2"
   "div w 2"
   "add x w"
   "mod x 2"
   "div w 2"
   "mod w 2"])

(defn parse-input
  [in]
  (map #(s/split % #" ") in))

(def initial-state
  {"w" 0
   "x" 0
   "y" 0
   "z" 0})

(def r? #{"w" "x" "y" "z"})

(defn inp*
  [s k v]
  (assoc s k v))

(defn add*
  [s a b]
  (let [bv (if (r? b) (get s b) (read-string b))]
    (update s a + bv)))

(defn mul*
  [s a b]
  (let [bv (if (r? b) (get s b) (read-string b))]
    (update s a * bv)))

(defn div*
  [s a b]
  (let [bv (if (r? b) (get s b) (read-string b))]
    (update s a quot bv)))

(defn mod*
  [s a b]
  (let [bv (if (r? b) (get s b) (read-string b))]
    (update s a mod bv)))

(defn eql*
  [s a b]
  (let [av (get s a)
        bv (if (r? b) (get s b) (read-string b))]
    (assoc s a (if (= av bv) 1 0))))

(defn run-program
  [program digits]
  (reduce (fn [{state :state [d & ds] :ds :as acc} [i & [a b]]]
            (cond
              (= "inp" i) (-> acc
                              (assoc :state (inp* state a d))
                              (update :ds rest))
              (= "add" i) (-> acc (assoc :state (add* state a b)))
              (= "mul" i) (-> acc (assoc :state (mul* state a b)))
              (= "div" i) (-> acc (assoc :state (div* state a b)))
              (= "mod" i) (-> acc (assoc :state (mod* state a b)))
              (= "eql" i) (-> acc (assoc :state (eql* state a b)))))
          {:state initial-state
           :ds digits}
          program))

(defn part-1
  []
  (let [in (parse-input test-input-1)]
    ))

(defn part-2
  []
  (let [in test-input-1]
    ))

(comment
  (part-1) ;; =>
  (part-2) ;; =>
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [])
