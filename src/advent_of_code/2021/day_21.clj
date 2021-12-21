(ns advent-of-code.2021.day-21
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_21"))))

(def test-input ["Player 1 starting pos: 4"
                 "Player 2 starting pos: 8"])


(defn parse-input
  [in]
  [(read-string (str (last (first in))))
   (read-string (str (last (last in))))])

(def die-rolls (cycle (map inc (range 100))))

(defn initial-state
  [[p1-start p2-start]]
  {:turn 0
   :p1-pos p1-start
   :p2-pos p2-start
   :p1-score 0
   :p2-score 0
   :remaining-rolls die-rolls})

(def positions (cycle (map inc (range 10))))

(defn add-pos
  [old d]
  (let [new-positions (drop old positions)]
    (first (drop (dec d) new-positions))))

(defn update-state
  [{:keys [turn
           remaining-rolls
           p1-pos
           p1-score
           p2-pos
           p2-score]
    :as state}]
  (let [ds (take 3 remaining-rolls)
        d (reduce + ds)]
    (if (even? turn)
      ;; p1 turn
      (let [new-pos (add-pos p1-pos d)
            new-score (+ p1-score new-pos)]
        (-> state
            (assoc :p1-pos new-pos)
            (assoc :p1-score new-score)
            (update :turn inc)
            (update :remaining-rolls #(drop 3 %))))
      ;;p2 turn
      (let [new-pos (add-pos p2-pos d)
            new-score (+ p2-score new-pos)]
        (-> state
            (assoc :p2-pos new-pos)
            (assoc :p2-score new-score)
            (update :turn inc)
            (update :remaining-rolls #(drop 3 %)))))))

(defn play
  [{:keys [p1-score p2-score] :as state}]
  (if (or (<= 1000 p1-score)
          (<= 1000 p2-score))
    (dissoc state :remaining-rolls)
    (recur (update-state state))))

(defn part-1
  []
  (let [in (parse-input input)]
    (let [{:keys [turn p1-score p2-score]} (play (initial-state in))]
      (* (* turn 3)
         (min p1-score p2-score)))))

(def possible-dirac-rolls
  (map (partial reduce +) (combo/selections [1 2 3] 3)))

(def winning-score 21)

(def split-wins
  (memoize
   (fn [turn [[pos-1 score-1]
              [pos-2 score-2]]]
     (let [possible-rolls (frequencies possible-dirac-rolls)
           outcomes (group-by (fn [[[[p1 s1] [p2 s2]] n]]
                                (if (even? turn)
                                  (<= winning-score s1)
                                  (<= winning-score s2)))
                              (pmap (fn [[roll n]]
                                      (let [new-pos-1 (add-pos pos-1 roll)
                                            new-pos-2 (add-pos pos-2 roll)]
                                        (if (even? turn)
                                          [[[new-pos-1 (+ score-1 new-pos-1)]
                                            [pos-2 score-2]] n]
                                          [[[pos-1 score-1]
                                            [new-pos-2 (+ score-2 new-pos-2)]] n])))
                                   possible-rolls))
           win-count (reduce + (map second (get outcomes true)))
           non-wins (get outcomes false)]
       {:win-count win-count
        :non-wins (into {} non-wins)}))))

(defn dirac-initial-state
  [[p1-start p2-start]]
  {:turn 0
   :p1-wins 0N
   :p2-wins 0N
   :states {[[p1-start 0]
             [p2-start 0]] 1N}})

(def get-results
  (memoize
   (fn [turn [pos-score n]]
     (let [{wc :win-count nw :non-wins} (split-wins turn pos-score)]
       {:win-count (* n wc)
        :non-wins (pmap (fn [[s v]] [s (* v n)]) nw)}))))

(defn dirac-update
  [{:keys [turn
           states]
    :as state}]

  (if (even? turn)
    ;; p1 turn
    (let [results (pmap (partial get-results turn) states)
          win-count (reduce + (map :win-count results))
          non-wins (reduce (fn [acc nw]
                             (reduce (fn [a [s n]]
                                       (update a s (fnil + 0N) n))
                                     acc
                                     nw))
                           {}
                           (map :non-wins results))]
      (-> state
          (update :turn inc)
          (update :p1-wins + win-count)
          (assoc :states non-wins)))
    ;;p2 turn
    (let [results (pmap (partial get-results turn) states)
          win-count (reduce + (map :win-count results))
          non-wins (reduce (fn [acc nw]
                             (reduce (fn [a [s n]]
                                       (update a s (fnil + 0N) n))
                                     acc
                                     nw))
                           {}
                           (map :non-wins results))]
      (-> state
          (update :turn inc)
          (update :p2-wins + win-count)
          (assoc :states non-wins)))))

(defn dirac-play
  [{:keys [states] :as state}]
  (if (empty? states)
    state
    (recur (dirac-update state))))

(defn part-2
  []
  (let [in (parse-input input)]
    (long (apply max ((juxt :p1-wins :p2-wins) (dirac-play (dirac-initial-state in)))))))

(comment
  (part-1) ;; => 598416
  (part-2) ;; => 27674034218179
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [598416 27674034218179])
