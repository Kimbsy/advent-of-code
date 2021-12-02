(ns advent-of-code.2021.day-02
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(defn parse
  [input]
  (->> input
       (map #(s/split % #" "))
       (map #(update % 1 read-string))))

(def input
  (parse (line-seq (io/reader (io/resource "2021/day_02")))))

(def test-input (parse ["forward 5" "down 5" "forward 8" "up 3" "down 8" "forward 2"]))

(defn part-1
  []
  (let [r (fn [acc [d n]]
            (case d
              "forward" (update acc :h + n)
              "down" (update acc :d + n)
              "up" (update acc :d - n)))
        {:keys [h d]} (reduce r {:h 0 :d 0} input)]
    (* h d)))

(defn part-2
  []
  (let [r (fn [{aim :a :as acc} [d n]]
            (case d
              "forward" (-> acc
                            (update :h + n)
                            (update :d + (* aim n)))
              "down" (update acc :a + n)
              "up" (update acc :a - n)))
        {:keys [h d]} (reduce r {:h 0 :d 0 :a 0} input)]
    (* h d)))

(comment
  (part-1) ;; => 1728414
  (part-2) ;; => 1765720035
  ,)
