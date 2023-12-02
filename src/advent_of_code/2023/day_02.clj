(ns advent-of-code.2022.day-02
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2023/day_02"))))

(def test-input
  ["Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
   "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"
   "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"
   "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"
   "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"])

(defn parse-game
  [s]
  (let [colors-strings (s/split s #", ")]
    (into {}
          (map (fn [color-string]
                 (let [[n color] (s/split color-string #" ")]
                   [(keyword color) (read-string n)]))
               colors-strings))))

(defn parse-input
  [in]
  (map (fn [line]
         (let [[intro games-string] (s/split line #": ")
               id (read-string (re-find #"\d+" intro))
               games (map parse-game (s/split games-string #"; "))]
           [id games]))
       in))

(defn part-1
  []
  (let [in (parse-input input)
        maxes (map (fn [[id games]]
                     [id (apply merge-with max games)])
                   in)
        valid (filter (fn [[id {:keys [red green blue]}]]
                        (and (<= red 12)
                             (<= green 13)
                             (<= blue 14)))
                      maxes)]
    (reduce + (map first valid))))

(defn part-2
  []
  (let [in (parse-input input)
        maxes (map (fn [[id games]]
                     [id (apply merge-with max games)])
                   in)
        powers (map (fn [[_id {:keys [red green blue]
                               :or {red 0 green 0 blue 0}}]]
                      (* red green blue))
                    maxes)]
    (reduce + powers)))

(comment
  (part-1) ;; => 2476
  (part-2) ;; => 54911
  ,)

;; refactoring check
(= [(part-1) (part-2)] [2476 54911])
