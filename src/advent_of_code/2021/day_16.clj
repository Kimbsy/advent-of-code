(ns advent-of-code.2021.day-16
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (first (line-seq (io/reader (io/resource "2021/day_16")))))

(def test-input-1 "D2FE28")

(def test-input-2 "38006F45291200")

(def test-input-3 "EE00D40C823060")

(def hex->bin
  {\0 "0000"
   \1 "0001"
   \2 "0010"
   \3 "0011"
   \4 "0100"
   \5 "0101"
   \6 "0110"
   \7 "0111"
   \8 "1000"
   \9 "1001"
   \A "1010"
   \B "1011"
   \C "1100"
   \D "1101"
   \E "1110"
   \F "1111"})

(def ^:dynamic *total* (atom 0))

(declare parse-packets)

(defn bin->dec
  [bs]
  (when seq bs
        (read-string (str "2r" bs))))

(defn parse-literal
  [sections]
  (read-string (str "2r" (apply str (mapcat rest sections)))))

(defn partition-literal
  [bs]
  (let [sections (partition 5 5 nil bs)
        beginning (take-while #(= \1 (first %)) sections)
        end (take 1 (drop-while #(= \1 (first %)) sections))
        literal-sections (concat beginning end)]
    [(parse-literal literal-sections) (apply concat (drop 1 (drop-while #(= \1 (first %)) sections)))]))

(defn partition-operator
  [bs]
  (let [length-type-id (first bs)
        rem-bs (rest bs)]
     (if (= \0 length-type-id)
       (let [length (bin->dec (apply str (take 15 rem-bs)))
             subs-bs (take length (drop 15 rem-bs))
             remaining (drop length (drop 15 rem-bs))]
         [(parse-packets [] subs-bs) remaining])
       (let [sub-count (bin->dec (apply str (take 11 rem-bs)))]
         (let [res (parse-packets [] (drop 11 rem-bs) :n sub-count)]
           [(take sub-count res) (drop sub-count res)])))))

(defn less-than
  [a b]
  (if (< a b) 1 0))

(defn greater-than
  [a b]
  (if (> a b) 1 0))

(defn equal-to
  [a b]
  (if (= a b) 1 0))

(defn handle-operator
  [f other n]
  (let [[subs remaining] (partition-operator other)]
    (concat (list (concat [f] subs))
            (when-not (every? #{\0} remaining)
              (parse-packets [] remaining :n (dec n))))))

(defn parse-packets
  [acc bs & {:keys [n] :or {n ##Inf}}]
  (if (zero? n)
    bs
    (when (seq bs)
        (let [v (bin->dec (apply str (take 3 bs)))
              t (bin->dec (apply str (take 3 (drop 3 bs))))
              other (drop 6 bs)]
          (swap! *total* (partial + v))
          (cond
            (= t 4) (let [[literal remaining] (partition-literal other)]
                      (concat [literal]
                              (when-not (every? #{\0} remaining)
                                (parse-packets [] remaining :n (dec n)))))
            (= t 0) (handle-operator + other n)
            (= t 1) (handle-operator * other n)
            (= t 2) (handle-operator min other n)
            (= t 3) (handle-operator max other n)
            (= t 5) (handle-operator greater-than other n)
            (= t 6) (handle-operator less-than other n)
            (= t 7) (handle-operator equal-to other n))))))

(defn parse-input
  [in]
  (let [bin (apply str (map hex->bin in))]
    (parse-packets [] bin)))

(defn part-1
  []
  (reset! *total* 0)
  (let [in (parse-input input)]
    @*total*))

(defn examples
  []
  (let [results (map (fn [[i o]]
                       (= o (eval (first (parse-input i)))))
                     [["C200B40A82" 3]
                      ["04005AC3389 " 54]
                      ["880086C3E88112" 7]
                      ["CE00C43D881120" 9]
                      ["D8005AC2A8F0" 1]
                      ["F600BC2D8F" 0]
                      ["9C005AC2F8F0" 0]
                      ["9C0141080250320F1802104A08" 1]])]
    [(every? true? results) results]))

(defn part-2
  []
  (let [in (parse-input input)]
    (eval (first in))))

(comment
  (part-1) ;; => 917
  (part-2) ;; => 2536453523344
  ,)

;; refactoring check
(= [(part-1) (part-2)] [917 2536453523344])
