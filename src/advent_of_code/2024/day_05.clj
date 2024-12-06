(ns advent-of-code.2024.day-05
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (slurp (io/reader (io/resource "2024/day_05"))))

(def test-input "47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47")

(defn parse-input
  [in]
  (map s/split-lines (s/split in #"\n\n")))

(defn parse-raw-rules
  [rule-data]
  (map #(map read-string (s/split % #"\|")) rule-data))

(defn create-rules
  [rule-data]
  (reduce (fn [acc [before after]]
            (update acc before #(set (conj % after))))
          {}
          rule-data))

(defn parse-update-pages
  [update-data]
  (map #(read-string (str "[" % "]")) update-data))

(defn valid?
  [rules pages]
  (loop [[page1 page2 :as remaining] pages]
    (if (< 1 (count remaining))
      (if ((get rules page1 #{}) page2)
        (recur (rest remaining))
        false)
      true)))

(defn middle-page
  [pages]
  (nth pages (int (/ (count pages) 2))))

(defn part-1
  []
  (let [[rule-raw-data update-data] (parse-input input)
        rule-data                   (parse-raw-rules rule-raw-data)
        manual-updates              (parse-update-pages update-data)
        rules                       (create-rules rule-data)]
    (->> manual-updates
         (filter (partial valid? rules))
         (map middle-page)
         (reduce +))))

(defn split-valid
  [rules pages]
  (loop [[page1 page2 :as remaining] pages
         valid []]
    (if page2
      (if ((get rules page1 #{}) page2)
        (recur (rest remaining) (conj valid page1))
        [valid remaining])
      [pages []])))

(defn shift
  [xs]
  (let [n (count xs)]
    (take n (drop 1 (cycle xs)))))

(defn fix
  [rules pages]
  (let [[valid invalid] (split-valid rules pages)]
    (if (seq invalid)
      (fix rules (concat valid (shift invalid)))
      pages)))

(defn part-2
  []
  (let [[rule-raw-data update-data] (parse-input input)
        rule-data                   (parse-raw-rules rule-raw-data)
        manual-updates              (parse-update-pages update-data)
        rules                       (create-rules rule-data)
        invalid                     (remove (partial valid? rules) manual-updates)]
    (->> invalid
         (map (partial fix rules))
         (map middle-page)
         (reduce +))))

(comment
  (part-1) ;; => 5639
  (part-2) ;; => 5273
  ,)

;; refactoring check
(= [(part-1) (part-2)] [5639 5273])
