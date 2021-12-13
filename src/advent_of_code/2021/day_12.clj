(ns advent-of-code.2021.day-12
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_12"))))

(def test-input ["start-A"
                 "start-b"
                 "A-c"
                 "A-b"
                 "b-d"
                 "A-end"
                 "b-end"])

(def test-input-2 ["dc-end"
                   "HN-start"
                   "start-kj"
                   "dc-start"
                   "dc-HN"
                   "LN-dc"
                   "HN-end"
                   "kj-sa"
                   "kj-HN"
                   "kj-dc"])

(def test-input-3 ["fs-end"
                   "he-DX"
                   "fs-he"
                   "start-DX"
                   "pj-DX"
                   "end-zg"
                   "zg-sl"
                   "zg-pj"
                   "pj-he"
                   "RW-he"
                   "fs-DX"
                   "pj-RW"
                   "zg-RW"
                   "start-pj"
                   "he-WI"
                   "zg-he"
                   "pj-fs"
                   "start-RW"])

(defn small?
  [c]
  (or (#{:start :end} c)
      (re-matches #"[a-z]+" (name c))))

(defn parse-input
  [in]
  (reduce (fn [g [a b]]
            (-> g
                (update a #(-> %
                               (assoc :visits 0)
                               (update :neighbours conj b)))
                (update b #(-> %
                               (assoc :visits 0)
                               (update :neighbours conj a)))))
          {}
          (map #(map keyword (s/split % #"-")) in)))

(defn relevant?
  [g n]
  (and (small? n)
       (< 0 (:visits (n g)))))

(def dfs
  (memoize
   (fn
     [g goal path visited relevant-fn]
     (let [current (peek path)]
       (let [available (:neighbours (current g))
             new-g (update-in g [current :visits] inc)]
         (if (= current goal)
           [path]
           (->> available
                (remove (set (filter (partial relevant-fn new-g) visited)))
                (mapcat #(dfs new-g
                              goal
                              (conj path %)
                              (set (filter small? (conj visited %)))
                              relevant-fn)))))))))


(defn all-paths
  [g relevant-fn]
  (dfs g :end [:start] #{:start} relevant-fn))

(defn part-1
  []
  (let [in (parse-input input)]
    (count (all-paths in relevant?))))

(defn part-2
  []
  (let [in (parse-input input)
        smalls  (cset/difference (set (filter small? (keys in))) #{:start :end})]
    (count
     (set (mapcat
           (fn [s]
             (letfn [(rel? [g n]
                       (if (= s n)
                         (< 1 (:visits (n g)))
                         (relevant? g n)))]
               (all-paths in rel?)))
           smalls)))))

(comment
  (part-1) ;; => 4659
  (part-2) ;; => 148962
  ,)

;; refactoring check
(= [(part-1) (part-2)] [4659 148962])
