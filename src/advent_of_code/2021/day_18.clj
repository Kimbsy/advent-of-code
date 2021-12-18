(ns advent-of-code.2021.day-18
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [clojure.walk :as w]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_18"))))

(def test-input ["[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"
                 "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"
                 "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"
                 "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"
                 "[7,[5,[[3,8],[1,4]]]]"
                 "[[2,[2,2]],[8,[8,1]]]"
                 "[2,9]"
                 "[1,[[[9,3],9],[[9,0],[0,7]]]]"
                 "[[[5,[7,4]],7],1]"
                 "[[[[4,2],2],6],[8,7]]"])

(defn parse-input
  [in]
  (map read-string in))

(defn magnitude
  ([a]
   (if (sequential? a) (reduce magnitude a) a))
  ([a b]
   (+ (* (if (sequential? a) (magnitude a) a) 3)
      (* (if (sequential? b) (magnitude b) b) 2))))

(defn depth
  [a]
  (if (sequential? a)
    (inc (apply max (map depth a)))
    0))

(defn deep?
  [a]
  (< 4 (depth a)))

(defn large?
  [a]
  (<= 10 (apply max (flatten a))))

(defn leftmost-path
  [path a]
  (if (int? a)
    path
    (if (int? (first a))
      (conj path 0)
      (leftmost-path (conj path 0) (first a)))))

(defn rightmost-path
  [path a]
  (if (int? a)
    path
    (if (int? (second a))
      (conj path 1)
      (rightmost-path (conj path 1) (second a)))))

(defn lefter-than
  [p1 p2]
  (and (seq p1)
       (seq p2)
       (not (= p1 p2))
       (or (< (first p1) (first p2))
           (lefter-than (rest p1) (rest p2)))))

(defn find-paths
  [d path l-path r-path a]
  (if (and (sequential? a)
           (every? int? a)
           (<= 4 d))
    [path l-path r-path a]
    (when (sequential? a)
      (let [updated-l (if (int? (first a))
                        (conj path 0)
                        (rightmost-path (conj path 0) (first a)))
            updated-r (if (int? (second a))
                        (conj path 1)
                        (leftmost-path (conj path 1) (second a)))]
        (or (find-paths (inc d) (conj path 0) l-path updated-r (first a))
            (find-paths (inc d) (conj path 1) updated-l r-path (second a)))))))

(defn explode
  [a]
  (let [[path l-path r-path [bl br]] (find-paths 0 [] [] [] a)]
   (cond-> a
      (seq l-path) (update-in l-path + bl)
      (seq r-path) (update-in r-path + br)
      :always (assoc-in path 0))))

(comment
  (do
    (do (prn (= (explode [[[[[9,8],1],2],3],4])
                [[[[0,9],2],3],4]))
        (prn (= (explode [7,[6,[5,[4,[3,2]]]]])
                [7,[6,[5,[7,0]]]]))
        (prn (= (explode [[6,[5,[4,[3,2]]]],1])
                [[6,[5,[7,0]]],3]))
        (prn (= (explode [[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]])
                [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]))
        (prn (= (explode [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]])
                [[3,[2,[8,0]]],[9,[5,[7,0]]]])))

    (do (prn (= (split* 10) [5 5]))
        (prn (= (split* 11) [5 6])))

    (do (prn (= (take 6 (iterate (fn [a]
                                   (if (deep? a)
                                     (explode a)
                                     (if (large? a)
                                       (split a)
                                       a)))
                                 [[[[[4,3],4],4],[7,[[8,4],9]]] [1,1]]))
                [[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]
                 [[[[0,7],4],[7,[[8,4],9]]],[1,1]]
                 [[[[0,7],4],[15,[0,13]]],[1,1]]
                 [[[[0,7],4],[[7,8],[0,13]]],[1,1]]
                 [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]
                 [[[[0,7],4],[[7,8],[6,0]]],[8,1]]])))

    (do (prn (= (add [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                     [7,[[[3,7],[4,3]],[[6,3],[8,8]]]])
                [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]))
        (prn (= (add [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                     [7,[[[3,7],[4,3]],[[6,3],[8,8]]]])
                [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]))
        (prn (= (add [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
                     [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]])
                [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]))
        (prn (= (add [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]
                     [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]])
                [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]))
        (prn (= (add [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]
                     [7,[5,[[3,8],[1,4]]]])
                [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]))
        (prn (= (add [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
                     [[2,[2,2]],[8,[8,1]]])
                [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]))
        (prn (= (add [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
                     [2,9])
                 [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]))
        (prn (= (add [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]
                     [1,[[[9,3],9],[[9,0],[0,7]]]])
                [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]))
        (prn (= (add [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]
                     [[[5,[7,4]],7],1])
                [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]))
        (prn (= (add [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
                     [[[[4,2],2],6],[8,7]])
                [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]])))
    ,)
  ,)

(defn split*
  [n]
  [(int (Math/floor (/ n 2)))
   (int (Math/ceil (/ n 2)))])

(defn split
  [a]
  (let [modified? (atom false)]
    (w/prewalk (fn [a]
                 (if (and (not @modified?)
                          (int? a)
                          (<= 10 a))
                   (do (reset! modified? true)
                       (split* a))
                   a))
                a)))

(defn sn-reduce
  [a]
  (if (deep? a)
    (do #_(prn "exploding" a)
       (recur (explode a)))
    (if (large? a)
      (do #_(prn "splitting" a)
          (recur (split a)))
      a)))

(defn add
  [a b]
  (sn-reduce [a b]))

(defn part-1
  []
  (let [in (parse-input input)]
    (magnitude (reduce add in))))

(defn part-2
  []
  (let [in (parse-input input)]
    (apply max
           (for [x in
                 y in]
             (magnitude (add x y))))))

(comment
  (part-1) ;; => 3884
  (part-2) ;; => 4595
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [3884 4595])
