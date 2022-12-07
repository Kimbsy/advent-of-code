(ns advent-of-code.2022.day-07
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2022/day_07"))))

(def test-input
  ["$ cd /"
   "$ ls"
   "dir a"
   "14848514 b.txt"
   "8504156 c.dat"
   "dir d"
   "$ cd a"
   "$ ls"
   "dir e"
   "29116 f"
   "2557 g"
   "62596 h.lst"
   "$ cd e"
   "$ ls"
   "584 i"
   "$ cd .."
   "$ cd .."
   "$ cd d"
   "$ ls"
   "4060174 j"
   "8033020 d.log"
   "5626152 d.ext"
   "7214296 k"])

(defn command? [s] (= \$ (first s)))
(def loc? (complement command?))
(defn d? [s] (= "dir " (apply str (take 4 s))))
(defn f? [s] (and (loc? s) (not (d? s))))
(defn cd? [s] (= "$ cd" (apply str (take 4 s))))
(defn ls? [s] (= "$ ls" s))

(defn parse-input
  ([in]
   (parse-input [] in))
  ([out [hl & rls :as lines]]
   (if (empty? lines)
     out
     (cond
       (cd? hl) (parse-input (conj out hl) rls)
       (ls? hl) (parse-input (conj out (concat [hl] (take-while loc? rls)))
                             (drop-while loc? rls))))))

(defn cd-arg [s] (apply str (drop 5 s)))
(defn dir-name [s] (apply str (drop 4 s)))
(defn file-name [s] (second (s/split s #" ")))
(defn file-size [s] (read-string (first (s/split s #" "))))

(defn partition-down
  [col]
  (for [i (range 1 (inc (count col)))]
    (take i col)))

(defn gather-dir-sizes
  [in]
  (reduce (fn [{:keys [fs pwd sizes] :as state} command]
            (if (cd? command)
              (let [d (cd-arg command)]
                (assoc state
                       :pwd
                       (if (= ".." d)
                         (vec (butlast pwd))
                         (conj pwd d))))
              (assoc state
                     :sizes
                     (reduce (fn [acc loc]
                               (if (f? loc)
                                 (reduce (fn [acc2 p]
                                           (update acc2
                                                   p
                                                   (fnil #(+ % (file-size loc)) 0)))
                                         acc
                                         (partition-down pwd))
                                 acc))
                             sizes
                             (rest command)))))
          {:fs {}
           :pwd []
           :sizes {}}
          in))

(defn part-1
  []
  (->> input
       parse-input
       gather-dir-sizes
       :sizes
       (map second)
       (filter #(<= % 100000))
       (reduce +)))

(def total-used (-> input
                    parse-input
                    gather-dir-sizes
                    :sizes
                    (get ["/"])))

(def total-size 70000000)
(def required-size 30000000)
(def current-available (- total-size total-used))
(def additional-required (- required-size current-available))

(defn part-2
  []
  (->> input
       parse-input
       gather-dir-sizes
       :sizes
       (filter (fn [[pwd size]] (>= size additional-required)))
       (sort-by second)
       first
       second))

(comment
  (part-1) ;; => 1325919
  (part-2) ;; => 2050735
  ,)

;; refactoring check
(= [(part-1) (part-2)] [1325919 2050735])
