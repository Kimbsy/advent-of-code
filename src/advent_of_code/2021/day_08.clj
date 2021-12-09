(ns advent-of-code.2021.day-08
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_08"))))

(def test-input
  ["be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe"
   "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc"
   "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg"
   "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb"
   "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea"
   "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb"
   "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe"
   "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef"
   "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb"
   "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce"])

(defn parse-line
  [l]
  (let [[left right] (s/split l #" \| ")]
    {:left (s/split left #" ")
     :right (s/split right #" ")}))

(defn part-1
  []
  (let [rights (map :right (map parse-line input))]
    (apply + (map (fn [rs] (count (filter (fn [r] (#{2 3 4 7} (count r))) rs))) rights))))

(def test-input-2 ["bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef"])

(defn get-single
  [pred coll]
  (set (first (filter pred coll))))

(defn get-many
  [pred coll]
  (set (map set (filter pred coll))))

(defn has-length
  [n]
  #(= n (count %)))

;; this is nasty
(defn analyse
  [nums]
  (let [one (get-single (has-length 2) nums)
        seven (get-single (has-length 3) nums)
        four (get-single (has-length 4) nums)
        eight (get-single (has-length 7) nums)
        three (get-single (fn [n] (and (= 5 (count n)) (= 2 (count (cset/difference (set n) seven))))) nums)
        mm+bm (cset/difference three seven)
        bm (cset/difference mm+bm four)
        mm (cset/difference mm+bm bm)
        zero (get-single (fn [n] (and (= 6 (count n)) (= #{} (cset/intersection mm (set n))))) nums)
        six+nine (get-many (fn [n] (= 1 (count (cset/difference zero (set n))))) nums)
        nine (get-single (fn [n] (= seven (cset/intersection (set n) seven))) six+nine)
        six (first (cset/difference six+nine #{nine}))
        five+two (cset/difference (get-many (fn [n] (= 2 (count (cset/difference zero (set n))))) nums) #{three})
        two (get-single (fn [n] (= 1 (count (cset/difference n six)))) five+two)
        five (first (cset/difference five+two #{two}))]
    (zipmap (range) [zero one two three four five six seven eight nine])))

(defn get-digit
  [mapping encoding]
  (ffirst (filter (fn [[i n]]
                    (= (set encoding) n))
                  mapping)))

(defn part-2
  []
  (reduce + (map (fn [{:keys [left right]}]
                   (let [results (analyse left)]
                     (Integer/parseInt (apply str (map #(get-digit results %) right)))))
                 (map parse-line input))))

(comment
  (part-1) ;; => 383
  (part-2) ;; => 998900
  ,)

;; refactoring check
(= [(part-1) (part-2)] [383 998900])
