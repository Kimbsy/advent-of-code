(ns advent-of-code.2022.day-08
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_08"))))

(def test-input ["30373"
                 "25512"
                 "65332"
                 "33549"
                 "35390"])

(defn parse-input
  [in]
  (map (fn [s] (map #(Character/digit % 10) s)) in))

(defn enrich-positions
  [in]
  (for [i (range (count in))]
    (for [j (range (count (first in)))]
      [[i j] (nth (nth in i) j)])))

(defn check-visible
  [previously-visible in]
  (reduce (fn [visible row]
            (:vts (reduce (fn [{:keys [vts h] :as acc} [tpos th]]
                            (if (< h th)
                              (-> acc
                                  (update :vts conj tpos)
                                  (assoc :h th))
                              acc))
                          {:vts visible
                           :h -1}
                          row)))
          previously-visible
          in))

(defn part-1
  []
  (let [in (-> input
               parse-input
               enrich-positions)
        visible-trees #{}]
    (count (reduce check-visible
                   visible-trees
                   [in
                    (map reverse in)
                    (u/transpose in)
                    (map reverse (u/transpose in))]))))

(def above [0 -1])
(def below [0 1])
(def left [-1 0])
(def right [1 0])

(defn all-in-direction
  [pos direction max-i max-j]
  (rest (take-while (fn [[i j]] (and (<= 0 j max-j)
                                     (<= 0 i max-i)))
                    (iterate #(map - % direction) pos))))

(defn sight-lines
  [pos max-i max-j]
  (map #(all-in-direction pos % max-i max-j)
       [above
        below
        left
        right]))

(defn height
  [in [i j]]
  (nth (nth in i) j))

(defn scenic-score
  [in [i j]]
  (let [th (nth (nth in i) j)]
    (reduce (fn [acc line]
              (->> line
                   (map (partial height in))
                   (u/take-until (fn [h] (<= th h)))
                   count
                   (* acc)))
            1
            (sight-lines [i j] (dec (count in)) (dec (count (first in)))))))

(defn part-2
  []
  (let [in (parse-input input)]
    (apply max (for [i (range (count in))
                     j (range (count (first in)))]
                 (scenic-score in [i j])))))

(comment
  (part-1) ;; => 1814
  (part-2) ;; => 330786
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1814 330786])
