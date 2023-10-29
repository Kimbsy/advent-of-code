(ns advent-of-code.2022.day-09
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_09"))))

(def test-input ["R 4"
                 "U 4"
                 "L 3"
                 "D 1"
                 "R 4"
                 "D 1"
                 "L 5"
                 "R 2"])

(defn parse-input
  [in]
  (->> in
       (map #(s/split % #" "))
       (mapcat (fn [[d n]]
                 (repeat (read-string n) d)))))

(def directions
  {"U" [0 -1]
   "D" [0 1]
   "L" [-1 0]
   "R" [1 0]})

(defn update-t
  [[tx ty :as t] [hx hy :as h]]
  (let [t-adj (u/unbounded-adjacent-positions t)
        adj? (contains? (set t-adj) h)]
    (prn (map - h t))
    (cond
      (or (= h t) adj?) t
      (< 1 (- tx hx)) (-> t (update 0 dec) (assoc 1 hy))
      (> -1 (- tx hx)) (-> t (update 0 inc) (assoc 1 hy))
      (< 1 (- ty hy)) (-> t (update 1 dec) (assoc 0 hx))
      (> -1 (- ty hy)) (-> t (update 1 inc) (assoc 0 hx)))))

(defn update-state
  [{:keys [h t remaining-moves visited]
    :as state}]
  (let [move (get directions (first remaining-moves))
        new-h (map + h move)
        new-t (update-t t new-h)]
    (-> state
        (assoc :h new-h)
        (assoc :t new-t)
        (update :visited conj new-t)
        (update :remaining-moves rest))))

(defn continue?
  [state]
  (seq (:remaining-moves state)))

(defn initial-state
  [moves]
  {:h [0 0]
   :t [0 0]
   :remaining-moves moves
   :visited #{[0 0]}})

(defn part-1
  []
  (let [moves (parse-input test-input)
        updates (iterate update-state (initial-state moves))]
    (-> (take-while continue? updates)
        last
        :visited
        count)))

(defn part-2
  []
  )

(comment
  (part-1) ;; =>
  (part-2) ;; =>
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [0 0])
