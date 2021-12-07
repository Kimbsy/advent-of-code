(ns advent-of-code.results
  (:require [cheshire.core :as json]
            [simple-time.core :as time]))

(def results (json/parse-string (slurp "resources/2021/results")))

(defn readable-time
  [seconds]
  (let [t (time/seconds->timespan seconds)]
    (str (time/timespan->hours t) "h " (time/timespan->minutes t) "m " (time/timespan->seconds t) "s")))

(defn order
  [day star]
  (->> (get results "members")
       (map second)
       (filter (fn [m]
                 (get-in m ["completion_day_level" (str day) (str star)])))
       (map (juxt #(get %"name")
                  (fn [m]
                    (get-in m ["completion_day_level" (str day) (str star) "get_star_ts"]))))
       (sort-by second)))

(defn relative-order
  [day star]
  (let [order (order day star)
        best (second (first order))]
    (map (fn [[name time]]
           [name (readable-time (- time best))])
         order)))
