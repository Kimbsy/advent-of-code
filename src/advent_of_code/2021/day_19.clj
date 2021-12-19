(ns advent-of-code.2021.day-19
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_19"))))

(def test-input
  (line-seq (io/reader (io/resource "2021/day_19_b"))))

(defn parse-input
  [in]
  (->> in
       (partition-by empty?)
       (remove (comp empty? first))
       (map rest)
       (map (fn [i beacons]
              {:id i
               :beacons (into #{} (map #(read-string (str "[" % "]")) beacons))})
            (range))
       (into clojure.lang.PersistentQueue/EMPTY)))

(def variation-transforms
  [(fn [[x y z]] [x y z])
   (fn [[x y z]] [x (- z) y])
   (fn [[x y z]] [x (- y) (- z)])
   (fn [[x y z]] [x z (- y)])

   (fn [[x y z]] [(- x) y (- z)])
   (fn [[x y z]] [(- x) (- z) (- y)])
   (fn [[x y z]] [(- x) (- y) z])
   (fn [[x y z]] [(- x) z y])

   (fn [[x y z]] [(- y) x z])
   (fn [[x y z]] [z x y])
   (fn [[x y z]] [y x (- z)])
   (fn [[x y z]] [(- z) x (- y)])

   (fn [[x y z]] [z (- x) (- y)])
   (fn [[x y z]] [y (- x) z])
   (fn [[x y z]] [(- z) (- x) y])
   (fn [[x y z]] [(- y) (- x) (- z)])

   (fn [[x y z]] [z (- y) x])
   (fn [[x y z]] [y z x])
   (fn [[x y z]] [(- z) y x])
   (fn [[x y z]] [(- y) (- z)x])

   (fn [[x y z]] [(- y) z (- x)])
   (fn [[x y z]] [z y (- x)])
   (fn [[x y z]] [y (- z )(- x)])
   (fn [[x y z]] [(- z) (- y) (- x)])])

(defn variations
  "return the list of 24 variants of a scanner"
  [scanner]
  (map (fn [v]
         (map v scanner))
       variation-transforms))

(def ^:dynamic *offsets* (atom #{}))

(defn new-beacons
  [existing-beacons original-scanner-b]
  (first
   (into #{}
         (remove empty?
                 (mapcat (fn [scanner-b]
                           (remove empty?
                                   (mapcat (fn [anchor-beacon]
                                             (remove nil?
                                                     (pmap (fn [test-beacon]
                                                             (let [offset (map - test-beacon anchor-beacon)
                                                                   shifted-scanner-b (into #{} (map (fn [b]
                                                                                                      (map - b offset))
                                                                                                    scanner-b))]
                                                               (when (<= 12 (count (cset/intersection existing-beacons
                                                                                                      shifted-scanner-b)))
                                                                 (swap! *offsets* conj offset)
                                                                 shifted-scanner-b)))
                                                           scanner-b)))
                                           existing-beacons)))
                         (variations (:beacons original-scanner-b)))))))

(defn recursive-solve
  [existing-beacons remaining-scanners]
  (prn (count existing-beacons) (map :id remaining-scanners))
  (if (empty? remaining-scanners)
    existing-beacons
    (let [current-scanner (peek remaining-scanners)
          new-existing (cset/union existing-beacons (new-beacons existing-beacons current-scanner))]
      (if (= (count existing-beacons)
             (count new-existing))
        (recur existing-beacons (conj (pop remaining-scanners) current-scanner))
        (recur new-existing (pop remaining-scanners))))))


(defn part-1
  []
  (let [scanners (parse-input input)
        start (peek scanners)
        others (pop scanners)]
    (recursive-solve (:beacons start) others)))

(defn manhattan-distance
  [p1 p2]
  (reduce + (map - p1 p2)))

(defn part-2
  []
  (reset! *offsets* #{})
  (let [scanners (parse-input input)
        start (peek scanners)
        others (pop scanners)]
    (recursive-solve (:beacons start) others))
  (apply max (map u/abs (map (partial apply manhattan-distance) (combo/combinations (map (partial map u/abs) (conj @*offsets* [0 0 0])) 2)))))

(comment
  (part-1) ;; => 342
  (part-2) ;; => 9668
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [342 9668])
