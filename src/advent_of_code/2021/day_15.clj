(ns advent-of-code.2021.day-15
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_15"))))

(def test-input ["1163751742"
                 "1381373672"
                 "2136511328"
                 "3694931569"
                 "7463417111"
                 "1319128137"
                 "1359912421"
                 "3125421639"
                 "1293138521"
                 "2311944581"])

(def ^:dynamic *timer* (atom 0))

(defn parse-input
  [in]
  (mapv #(mapv u/char->int %) in))

(defn update-costs
  "Returns costs updated with any shorter paths found to curr's unvisisted
  neighbors by using curr's shortest path"
  [g costs unvisited curr]
  (let [curr-cost (get costs curr)]
    (reduce-kv
      (fn [c nbr nbr-cost]
        (if (unvisited nbr)
          (update-in c [nbr] min (+ curr-cost nbr-cost))
          c))
      costs
      (get g curr))))

(defn dijkstra
  "Returns a map of nodes to minimum cost from src using Dijkstra algorithm.
  Graph is a map of nodes to map of neighboring nodes and associated cost.
  Optionally, specify destination node to return once cost is known"
  ([g src]
    (dijkstra g src nil))
  ([g src dst]
    (loop [costs (assoc (zipmap (keys g) (repeat ##Inf)) src 0)
           curr src
           unvisited (disj (set (keys g)) src)]
      (when (< 1000 (- (System/currentTimeMillis) @*timer*))
        (reset! *timer* (System/currentTimeMillis))
        (newline)
        (pr (count unvisited)))
      (cond
       (= curr dst)
       (select-keys costs [dst])
       :else
       (let [next-costs (update-costs g costs unvisited curr)
             next-node (apply min-key next-costs unvisited)]
         (recur next-costs next-node (disj unvisited next-node)))))))

(defn graph
  [g]
  (let [positions (u/grid-positions g)]
    (reduce (fn [acc [px py :as p]]
              (assoc acc p (into {}
                                 (map (fn [cp]
                                        [cp (u/grid-val g cp)])
                                      (u/cardinal-adjacent-positions g p)))))
            {}
            positions)))

(defn part-1
  []
  (reset! *timer* (System/currentTimeMillis))
  (let [in (parse-input input)
        g (graph in)
        ps (u/grid-positions in)]
    (get (dijkstra g (first ps) (last ps)) (last ps))))

(defn incr
  [n]
  (if (= 9 n) 1 (inc n)))

(defn part-2
  []
  (prn "starting")
  (reset! *timer* (System/currentTimeMillis))
  (let [in (parse-input input)
        wide-in (map (fn [row]
                       (vec (apply concat (take 5 (iterate #(mapv incr %) row)))))
                     in)
        big-in (vec (apply concat (take 5 (iterate (fn [gr] (mapv #(mapv incr %) gr)) wide-in))))
        g (graph big-in)
        ps (u/grid-positions big-in)]
    (prn "graph created")
    (prn (get (dijkstra g (first ps) (last ps)) (last ps)))))

(comment
  (part-1) ;; => 621
  (part-2) ;; => 2904   this took 4.5 hours to run
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [621 2904])
