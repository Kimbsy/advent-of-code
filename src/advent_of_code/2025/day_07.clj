(ns advent-of-code.2025.day-07
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2025/day_07"))))

(def test-input
  [".......S......."
   "..............."
   ".......^......."
   "..............."
   "......^.^......"
   "..............."
   ".....^.^.^....."
   "..............."
   "....^.^...^...."
   "..............."
   "...^.^...^.^..."
   "..............."
   "..^...^.....^.."
   "..............."
   ".^.^.^.^.^...^."
   "..............."])

(defn parse-input
  [in]
  in)

(defn indices-of
  [s c]
  (keep-indexed (fn [i sc]
                  (when (= sc c)
                    i))
                s))

(defn simulate
  [in]
  (reduce
   (fn [acc [i r]]
     (let [start-pos (s/index-of r \S)
           carets (indices-of r \^)
           prev-pipes (when (seq acc)
                        (indices-of (nth acc (max 0 (dec i))) \|))]
       (cond
         start-pos [(apply str (assoc (vec (nth in i)) start-pos \|))]
         (seq carets) (conj acc
                            (let [splitted (apply str
                                                  (reduce (fn [r-acc c]
                                                            (if (contains? (set prev-pipes)
                                                                           c)
                                                              (-> r-acc
                                                                  (assoc (dec c) \|)
                                                                  (assoc (inc c) \|))
                                                              r-acc))
                                                          (vec r)
                                                          carets))]
                              (apply str
                                     (reduce (fn [r-acc p]
                                               (if (not (contains? (set carets) p))
                                                 (assoc r-acc p \|)
                                                 r-acc))
                                             (vec splitted)
                                             prev-pipes))))
         :else (conj acc (apply str
                                (reduce (fn [r-acc p]
                                          (assoc r-acc p \|))
                                        (vec r)
                                        prev-pipes))))))
   []
   (map list (range) in)))

(defn part-1
  []
  (let [in (parse-input input)
        finished (simulate in)]
    (count (filter (fn [[y x :as p]]
                     (and (= \^ (u/grid-val finished p))
                          (= \| (u/grid-val finished [(dec y) x]))))
                   (u/grid-positions finished)))))

(defn part-2
  []
  (let [in (parse-input input)
        finished (simulate in)
        compact (->> finished
                     reverse
                     (map list (range))
                     (filter (comp odd? first))
                     (map second)
                     (map list (range)))]
    (apply max
           (last
            (reduce (fn [acc [i r]]
                      (if (zero? i)
                        (conj acc (mapv {\| 1 \. 0 \^ 2} r))
                        (let [prev-r (nth acc (dec i))
                              carets (indices-of r \^)]
                          (conj acc
                                (mapv (fn [[i c]]
                                        (case c
                                          \. 0
                                          \| (nth prev-r i)
                                          \^ (+ (nth prev-r (dec i))
                                                (nth prev-r (inc i)))))
                                      (map list (range) r))))))
                    []
                    compact)))))

(comment
  (part-1) ;; => 1507
  (part-2) ;; => 1537373473728
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [1507 1537373473728])
