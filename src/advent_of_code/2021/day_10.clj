(ns advent-of-code.2021.day-10
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_10"))))

(def test-input ["[({(<(())[]>[[{[]{<()<>>"
                 "[(()[<>])]({[<{<<[]>>("
                 "{([(<{}[<>[]}>{[]{[(<()>"
                 "(((({<>}<{<{<>}{[]{[]{}"
                 "[[<[([]))<([[{}[[()]]]"
                 "[{[{({}]{}}([{[{{{}}([]"
                 "{<[[]]>}<{[{[{[]{()[[[]"
                 "[<(<(<(<{}))><([]([]()"
                 "<{([([[(<>()){}]>(<<{{"
                 "<{([{{}}[<[[[<>{}]]]>[]]"])

(defn parse-input
  [in]
  (map #(s/split % #"") in))

(def pairs
  {"(" ")"
   "[" "]"
   "{" "}"
   "<" ">"})

(defn matches?
  [closer opener]
  (= closer (get pairs opener)))

(def opener? #{"(" "[" "{" "<"})
(def closer? #{")" "]" "}" ">"})

(defn check
  [stack cs]
  (let [expected (get match (peek stack))
        c (first cs)]
    (cond
      (empty? cs) {:corrupt? false :stack stack}
      (closer? c) (if (matches? c (peek stack))
                    (recur (pop stack) (rest cs))
                    {:corrupt? true :last c})
      (opener? c) (recur (conj stack c) (rest cs))
      :default {:corrupt? true :last c})))

(defn convert-last
  [c]
  (get {")" 3
        "]" 57
        "}" 1197
        ">" 25137}
       c))

(defn part-1
  []
  (let [in input]
    (->> (parse-input in)
         (map (partial check ()))
         (filter :corrupt?)
         (map :last)
         (map convert-last)
         (reduce +))))

(defn cost
  [c]
  (get {")" 1
        "]" 2
        "}" 3
        ">" 4}
       c))

(defn total-cost
  [stack]
  (reduce (fn [acc c]
            (+ (* acc 5)
               (cost c)))
          0
          stack))

(defn part-2
  []
  (let [in input]
    (->> (parse-input in)
         (map (partial check ()))
         (remove :corrupt?)
         (map :stack)
         (map (fn [stack] (map #(get pairs %) stack)))
         (map total-cost)
         sort
         u/median)))

(comment
  (part-1) ;; => 364389
  (part-2) ;; => 2870201088
  ,)

;; refactoring check
(= [(part-1) (part-2)] [364389 2870201088])
