(ns advent-of-code.2020.day-04
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]))

(def input
  (line-seq (io/reader (io/resource "2020/day_04"))))

(def test-input-1
  ["ecl:gry pid:860033327 eyr:2020 hcl:#fffffd"
   "byr:1937 iyr:2017 cid:147 hgt:183cm"
   ""
   "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884"
   "hcl:#cfa07d byr:1929"
   ""
   "hcl:#ae17e1 iyr:2013"
   "eyr:2024"
   "ecl:brn pid:760753108 byr:1931"
   "hgt:179cm"
   ""
   "hcl:#cfa07d eyr:2025 pid:166559648"
   "iyr:2011 ecl:brn hgt:59in"])

(defn clean
  [in]
  (->> in
       (partition-by empty?)
       (remove (fn [[x]] (empty? x)))
       (map (partial s/join " "))))

(def required-fields #{"byr" "iyr" "eyr" "hgt" "hcl" "ecl" "pid"}) ;; cid not required

(defn split-field
  [raw]
  (s/split raw #":"))

(defn passport
  [input]
  (->> (s/split input #" ")
       (map split-field)
       (into {})))

(defn valid?
  [passport]
  (-> passport
      keys
      set
      (cset/intersection required-fields)
      (= required-fields)))

(defn part-1
  []
  (->> input
       clean
       (map passport)
       (filter valid?)
       count))

(def valid-eye-colours #{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"})

(defn strictly-valid?
  [{:strs [byr iyr eyr hgt hcl ecl pid] :as passport}]
  (and (valid? passport)
       (re-matches #"\d{4}" byr)
       (<= 1920 (read-string byr) 2002)
       (re-matches #"\d{4}" iyr)
       (<= 2010 (read-string iyr) 2020)
       (re-matches #"\d{4}" eyr)
       (<= 2020 (read-string eyr) 2030)
       (or (and (re-matches #"\d{3}cm" hgt)
                (<= 150 (read-string (apply str (drop-last 2 hgt))) 193))
           (and (re-matches #"\d{2,3}in" hgt)
                (<= 59 (read-string (apply str (drop-last 2 hgt))) 76)))
       (re-matches #"\#[0-9a-f]{6}" hcl)
       (valid-eye-colours ecl)
       (re-matches #"\d{9}" pid)))

(defn part-2
  []
  (->> input
       clean
       (map passport)
       (filter strictly-valid?)
       count))

(comment
  (part-1) ;; => 208
  (part-2) ;; => 167
  ,)
