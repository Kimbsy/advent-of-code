(ns advent-of-code.results
  (:require [advent-of-code.util :as u]
            [cheshire.core :as json]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [simple-time.core :as time]))

(def results (json/parse-string (slurp "resources/2021/results")))

(defn readable-diff
  [seconds]
  (let [t (time/seconds->timespan seconds)]
    (str (time/timespan->hours t) "h " (time/timespan->minutes t) "m " (time/timespan->seconds t) "s")))

(defn readable-ts
  [ts]
  (time/format (time/datetime (* 1000 ts)) "HH:mm:ss"))

(defn order
  [day star]
  (->> (get results "members")
       (map second)
       (filter (fn [m]
                 (get-in m ["completion_day_level" (str day) (str star)])))
       (map (juxt #(get % "name")
                  (fn [m]
                    (get-in m ["completion_day_level" (str day) (str star) "get_star_ts"]))))
       (map (fn [[name ts]]
              [name ts (readable-ts ts)]))
       (sort-by second)))

(defn relative-order
  [day star]
  (let [order (order day star)
        best (second (first order))]
    (map (fn [[name ts readable-ts]]
           [name ts (readable-diff (- ts best))])
         order)))

(defn add-score
  [acc [day star]]
  (->> (order day star)
       (map first)
       (map list (reverse (map inc (range (count acc)))))
       (reduce (fn [acc [score player]]
                 (update acc player + score))
               acc)))

(defn initial-leaderboard
  []
  (->> (get results "members")
       (map second)
       (map (fn [player-data]
              (or (get player-data "name")
                  (str "(anonymous user #" (get player-data "id") ")"))))
       (map (fn [player] [player 0]))
       (into {})))

(def day-stars (for [d (map inc (range 25))
                     s (map inc (range 2))]
                 [d s]))

(defn relevant-day-stars
  [[day star]]
  (take-while (fn [[d s]]
                (or (< d day)
                    (and (= d day)
                         (<= s star))))
              day-stars))

(defn score-at
  [day-star]
  (->> (relevant-day-stars day-star)
       (reduce add-score
               (initial-leaderboard))
       (sort-by second)
       reverse))

(defn progression
  [day-star]
  (->> (relevant-day-stars day-star)
       (reduce (fn [acc ds]
                 (conj acc (score-at ds)))
               [])))

(defn flourish-data
  []
  (let [progression-data (progression [25 2])
        sorted-data (map #(sort-by first %) progression-data)
        names (map list (map first (first sorted-data)))
        numbers (map #(map second %) sorted-data)
        table (map concat names (repeat ["" "" ""]) (u/transpose numbers))
        header (concat ["names" "" "" ""] (map (fn [[d s]] (str d "-" s)) day-stars))]
    (concat [header] table)))

(defn write-data!
  [data]
  (with-open [w (io/writer "resources/2021/flourish.csv")]
    (csv/write-csv w data)))
