(ns advent-of-code.2025.day-06
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_06"))))

(def test-input ["123 328  51 64 "
                 " 45 64  387 23 "
                 "  6 98  215 314"
                 "*   +   *   +  "])

(defn parse-input
  [in]
  (->> in
       (map s/trim)
       (map #(s/split % #"\s+"))
       (map (partial map read-string))
       (apply map (comp reverse list))))

(defn part-1
  []
  (let [in input]
    (->> in
         parse-input
         (map eval)
         (apply +))))

(defn part-2
  []
  (let [in input]
    (->> in
         drop-last
         (apply map list)
         (map #(remove #{\space} %))
         (partition-by #{[]})
         (remove #{[[]]})
         (map #(map (partial apply str) %))
         (map #(map read-string %))
         (map (fn [op args]
                (apply op args))
              (map (comp eval read-string) (s/split (last in) #"\s+")))
         (apply +))))

(comment
  (part-1) ;; => 5060053676136
  (part-2) ;; => 9695042567249
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [5060053676136 9695042567249])
