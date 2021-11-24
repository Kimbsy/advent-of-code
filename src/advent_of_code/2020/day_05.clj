(ns advent-of-code.2020.day-05
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]))

(def input
  (line-seq (io/reader (io/resource "2020/day_05"))))

(defn translate
  [code]
  (-> code
      (s/replace #"B" "1")
      (s/replace #"F" "0")
      (s/replace #"R" "1")
      (s/replace #"L" "0")))

(defn parse-code
  [code]
  (let [[r c] (split-at 7 (translate code))
        row (read-string (apply str "2r" r))
        col (read-string (apply str "2r" c))]
    (+ (* row 8) col)))

(defn part-1
  []
  (->> input
       (map parse-code)
       (apply max)))

(defn part-2
  []
  (let [ids (->> input
                 (map parse-code))]
    (cset/difference (set (range (apply min ids) (inc (apply max ids)))) (set ids))))

(comment
  (part-1) ;; => 911
  (part-2) ;; => #{629}
  ,)
