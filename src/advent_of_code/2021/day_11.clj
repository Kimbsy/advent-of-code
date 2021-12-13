(ns advent-of-code.2021.day-11
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_11"))))

(def test-input ["5483143223"
                 "2745854711"
                 "5264556173"
                 "6141336146"
                 "6357385478"
                 "4167524645"
                 "2176841721"
                 "6882881134"
                 "4846848554"
                 "5283751526"])

;; Ewwwwwwwwww
(def ^:dynamic *counter* (atom 0))

(defn prn-grid
  [grid]
  (clojure.pprint/pprint (mapv #(mapv :energy %) grid)))

(defn parse-input
  [in]
  (->> in
       (map #(s/split % #""))
       (mapv #(mapv (fn [o] {:energy (read-string o)
                             :flashed? false})
                    %))))

(defn inc-all
  [grid]
  (reduce (fn [grid pos]
            (update-in grid (conj pos :energy) inc))
          grid
          (u/grid-positions grid)))

(defn flash
  [grid]
  (let [flashed (set (->> (u/grid-positions grid)
                          (filter (fn [pos] (let [o (u/grid-val grid pos)]
                                              (and (not (:flashed? o))
                                                   (< 9 (:energy o))))))))
        flash-effected (mapcat (partial u/adjacent-positions grid)
                               flashed)
        tag-flashed (reduce (fn [g pos]
                              (assoc-in g (conj pos :flashed?) true))
                            grid
                            flashed)
        update-effected (reduce (fn [g pos]
                                  (update-in g (conj pos :energy) inc))
                                tag-flashed
                                flash-effected)]
    (swap! *counter* (partial + (count flashed)))
    (if (= update-effected grid)
      (reduce (fn [g pos]
                (update-in g pos (fn [o]
                                   (if (:flashed? o)
                                     (-> o
                                         (assoc :energy 0)
                                         (assoc :flashed? false))
                                     o))))
              update-effected
              (u/grid-positions update-effected))
      ;; keep going till no new ones
      (flash update-effected)
      )))

(defn update-grid
  [grid]
  (->> grid
       inc-all
       flash))

(defn part-1
  []
  (reset! *counter* 0)
  (let [in input]
    (nth (iterate update-grid (parse-input in)) 100)
    @*counter*))

(defn part-2
  []
  (let [in input]
    (reset! *counter* 0)
    (->> (map list
              (range)
              (iterate update-grid (parse-input in)))
         (filter (fn [[i g]]
                   (every? zero? (map :energy (apply concat g)))))
         ffirst)))

(comment
  (part-1) ;; => 1679
  (part-2) ;; => 519
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1679 519])
