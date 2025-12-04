(ns advent-of-code.2024.day-07
  (:require [clojure.data :as d]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2024/day_07"))))

(def test-input (s/split-lines "190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20"))

(defn parse-input
  [in]
  (->> in
       (map #(s/split % #":"))
       (map (fn [[target numbers]]
              [(read-string target)
               (read-string (str "[" numbers "]"))]))))

(defn options
  [numbers]
  (if (= 2 (count numbers))
    [(apply list '+ numbers)
     (apply list '* numbers)]
    (concat (map #(apply list '+ (first numbers) (list %)) (options (rest numbers)))
            (map #(apply list '* (first numbers) (list %)) (options (rest numbers))))))

(defn part-1
  []
  (let [in (parse-input input)]
    (reduce +
            (filter
             some?
             (pmap (fn [[target numbers]]
                     (let [os (options (reverse numbers))] ; reverse becasue left-to-right ooo
                       (when (some (fn [o] (= target (eval o))) os)
                         target)))
                   in)))))

(defn int-cc
  [& numbers]
  (read-string (apply str (reverse numbers))))

;; @TODO: silly, concat should be done while evaluating, not beforehand

(defn options-2
  [numbers]
  (when (seq numbers)
    (case (count numbers)
      1
      [(first numbers)]
      
      2
      [(apply list '+ numbers)
       (apply list '* numbers)
       (apply list 'int-cc numbers)]
      
      (concat (map #(apply list '+ (first numbers) (list %)) (options-2 (rest numbers)))
              (map #(apply list '* (first numbers) (list %)) (options-2 (rest numbers)))
              (map #(apply list 'int-cc (first numbers) (list %)) (options-2 (rest numbers)))))))

(defn part-2
  []
  (let [in (parse-input input)]
    (reduce +
            (filter
             some?
             (pmap (fn [[target numbers]]                     
                     (let [os (options-2 (reverse numbers))] ; reverse becasue left-to-right ooo
                       (when (some (fn [o] (= target (eval o))) os)
                         target)))
                   in)))))

(comment
  (part-1) ;; => 10741443549536
  (part-2) ;; => 
  ,)

;; refactoring check
#_(= [(part-1) (part-2)] [10741443549536 ])
