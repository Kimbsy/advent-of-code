(ns advent-of-code.2021.day-22
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as cset]
            [advent-of-code.util :as u]))

(def input
  (line-seq (io/reader (io/resource "2021/day_22"))))

(def test-input
  ["on x=10..12,y=10..12,z=10..12"
   "on x=11..13,y=11..13,z=11..13"
   "off x=9..11,y=9..11,z=9..11"
   "on x=10..10,y=10..10,z=10..10"])

(def test-input-2
  ["on x=-20..26,y=-36..17,z=-47..7"
   "on x=-20..33,y=-21..23,z=-26..28"
   "on x=-22..28,y=-29..23,z=-38..16"
   "on x=-46..7,y=-6..46,z=-50..-1"
   "on x=-49..1,y=-3..46,z=-24..28"
   "on x=2..47,y=-22..22,z=-23..27"
   "on x=-27..23,y=-28..26,z=-21..29"
   "on x=-39..5,y=-6..47,z=-3..44"
   "on x=-30..21,y=-8..43,z=-13..34"
   "on x=-22..26,y=-27..20,z=-29..19"
   "off x=-48..-32,y=26..41,z=-47..-37"
   "on x=-12..35,y=6..50,z=-50..-2"
   "off x=-48..-32,y=-32..-16,z=-15..-5"
   "on x=-18..26,y=-33..15,z=-7..46"
   "off x=-40..-22,y=-38..-28,z=23..41"
   "on x=-16..35,y=-41..10,z=-47..6"
   "off x=-32..-23,y=11..30,z=-14..3"
   "on x=-49..-5,y=-3..45,z=-29..18"
   "off x=18..30,y=-20..-8,z=-3..13"
   "on x=-41..9,y=-7..43,z=-33..15"
   "on x=-54112..-39298,y=-85059..-49293,z=-27449..7877"
   "on x=967..23432,y=45373..81175,z=27513..53682"])

(defn parse-input
  [in]
  (let [instructions (map #(s/split % #"(,| )") in)]
    (map (fn [[state x y z]]
           (let [[x-range y-range z-range] (map #(map read-string
                                                      (s/split (subs % 2) #"\.\."))
                                                [x y z])]
             [(= "on" state) x-range y-range z-range]))
         instructions)))

(defn bounded-inclusive-range
  [low high]
  (range (max -50 low) (min 50 (inc high))))

(defn bounded-cubes
  [[xl xh] [yl yh] [zl zh]]
  (for [x (bounded-inclusive-range xl xh)
        y (bounded-inclusive-range yl yh)
        z (bounded-inclusive-range zl zh)]
    [x y z]))

(defn inclusive-range
  [low high]
  (range low (inc high)))

(defn cubes
  [[xl xh] [yl yh] [zl zh]]
  (for [x (inclusive-range xl xh)
        y (inclusive-range yl yh)
        z (inclusive-range zl zh)]
    [x y z]))

(defn in-range?
  [[nl nh]]
  (or (<= nl -50  nh)
      (<= nl 50 nh)
      (<= -50 nl nh 50)))

(defn part-1
  []
  (let [in (parse-input input)]
    (get (->> (reduce (fn [acc [state xs ys zs]]
                        (prn (count acc))
                        (if (and (in-range? xs)
                                 (in-range? ys)
                                 (in-range? zs))
                          (reduce (fn [a cube]
                                    (assoc a cube state))
                                  acc
                                  (bounded-cubes xs ys zs))
                          acc))
                      {}
                      in)
              vals
              frequencies)
         true)))

;; @TODO: these subtract and add functions are for one dimension. In
;; oder to do 3 dimensions we need to think of all the ways (corners,
;; edges, etc) that two cuboids can intersect and then return the
;; resultant list of cuboids.

;; @TODO: when adding, you need to subtract first and then add so we
;; don't end up counting coordinates twice.

;; 1D predicates
(defn intersects-low?
  [[nl1 nh1] [nl2 nh2]]
  (<= nl2 nl1 nh2 nh1))
(defn intersects-high?
  [[nl1 nh1] [nl2 nh2]]
  (<= nl1 nl2 nh1 nh2))
(defn inside?
  [[nl1 nh1] [nl2 nh2]]
  (<= nl1 nl2 nh2 nh1))
(defn encloses?
  [[nl1 nh1] [nl2 nh2]]
  (<= nl2 nl1 nh1 nh2))
(defn outside?
  [[nl1 nh1] [nl2 nh2]]
  (or (< nh1 nl2)
      (< nh2 nl1)))

;; inside?
(defn cube-inside?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (inside? y1s y2s)
       (inside? z1s z2s)))

;; encloses?
(defn cube-encloses?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (encloses? y1s y2s)
       (encloses? z1s z2s)))

;; outside?
(defn cube-outside?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (outside? x1s x2s)
       (outside? y1s y2s)
       (outside? z1s z2s)))

;; intersects with face?
(defn intersects-face-top?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-face-bottom?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-face-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-face-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-face-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-face-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (encloses? z1s z2s)))

;; intersects with edge?
(defn intersects-edge-top-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-bottom-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-front-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-edge-front-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-edge-back-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))

(defn intersects-edge-back-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))

;; intersects with corner?
(defn intersects-corner-top-front-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-corner-top-front-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-corner-top-back-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-corner-top-back-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-corner-bottom-front-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-corner-bottom-front-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-corner-bottom-back-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-corner-bottom-back-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-low? z1s z2s)))
















;; inside?
(defn subtract-cube-inside
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom

   [[xl1 xh1] [yl1 (dec yl2)] [zl2 zh2]] ; front
   [[xl1 xh1] [(inc yh2) yh1] [zl2 zh2]] ; back

   [[xl1 (dec xl2)] [yl2 yh2] [zl2 zh2]] ; left
   [[(inc xh2) xh1] [yl2 yh2] [zl2 zh2]] ; right
   ])

;; encloses?
(defn subtract-cube-encloses
  [c1 c2]
  [])

;; outside?
(defn subtract-cube-outside
  [c1 c2]
  [c1])

;; intersects with face?
(defn subtract-face-top
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom

   [[xl1 xh1] [yl1 (dec yl2)] [zl2 zh1]] ; top-front
   [[xl1 xh1] [(inc yh2) yh1] [zl2 zh1]] ; top-back

   [[xl1 (dec xl2)] [yl2 yh1] [zl2 zh1]] ; top-left
   [[(inc xh2) xh1] [yl2 yh1] [zl2 zh1]] ; top-right
   ])

(defn subtract-face-bottom
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top

   [[xl1 xh1] [yl1 (dec yl2)] [zl1 zh2]] ; bottom-front
   [[xl1 xh1] [(inc yh2) yh1] [zl1 zh2]] ; bottom-back

   [[xl1 (dec xl2)] [yl2 yh2] [zl1 zh2]] ; bottom-left
   [[(inc xh2) xh1] [yl2 yh2] [zl1 zh2]] ; bottom-right
   ])

(defn subtract-face-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back

   [[xl1 xh1] [yl1 yh2] [(inc zh2) zh1]] ; front-top
   [[xl1 xh1] [yl1 yh2] [zl1 (dec zl2)]] ; front-bottom

   [[xl1 (dec xl2)] [yl1 yh2] [zl2 zh2]] ; front-left
   [[(inc xh2) xh1] [yl1 yh2] [zl2 zh2]] ; front-right
   ])

(defn subtract-face-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front

   [[xl1 xh1] [yh2 yh1] [(inc zh2) zh1]] ; back-top
   [[xl1 xh1] [yh2 yh1] [zl1 (dec zl2)]] ; back-bottom

   [[xl1 (dec xl2)] [yh2 yh1] [zl2 zh2]] ; back-left
   [[(inc xh2) xh1] [yh2 yh1] [zl2 zh2]] ; back-right
   ])

;; (defn subtract-face-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-face-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; ;; intersects with edge?
;; (defn subtract-edge-top-front
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-top-back
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-top-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-top-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-bottom-front
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-bottom-back
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-bottom-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-bottom-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-front-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-front-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-back-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-edge-back-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; ;; intersects with corner?
;; (defn subtract-corner-top-front-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-top-front-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-top-back-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-top-back-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-bottom-front-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-bottom-front-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-bottom-back-left
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

;; (defn subtract-corner-bottom-back-right
;;   [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
;;    [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
;;   )

(defn subtract-cube
  [c1 c2]
  (cond
    (cube-inside? c1 c2) (subtract-cube-inside c1 c2)
    (cube-encloses? c1 c2) (subtract-cube-encloses c1 c2)
    (cube-outside? c1 c2) (subtract-cube-outside c1 c2)
    (intersects-face-top? c1 c2) (subtract-face-top c1 c2)
    (intersects-face-bottom? c1 c2) (subtract-face-bottom c1 c2)
    (intersects-face-front? c1 c2) (subtract-face-front c1 c2)
    (intersects-face-back? c1 c2) (subtract-face-back c1 c2)
    (intersects-face-left? c1 c2) (subtract-face-left c1 c2)
    (intersects-face-right? c1 c2) (subtract-face-right c1 c2)
    (intersects-edge-top-front? c1 c2) (subtract-edge-top-front c1 c2)
    (intersects-edge-top-back? c1 c2) (subtract-edge-top-back c1 c2)
    (intersects-edge-top-left? c1 c2) (subtract-edge-top-left c1 c2)
    (intersects-edge-top-right? c1 c2) (subtract-edge-top-right c1 c2)
    (intersects-edge-bottom-front? c1 c2) (subtract-edge-bottom-front c1 c2)
    (intersects-edge-bottom-back? c1 c2) (subtract-edge-bottom-back c1 c2)
    (intersects-edge-bottom-left? c1 c2) (subtract-edge-bottom-left c1 c2)
    (intersects-edge-bottom-right? c1 c2) (subtract-edge-bottom-right c1 c2)
    (intersects-edge-front-left? c1 c2) (subtract-edge-front-left c1 c2)
    (intersects-edge-front-right? c1 c2) (subtract-edge-front-right c1 c2)
    (intersects-edge-back-left? c1 c2) (subtract-edge-back-left c1 c2)
    (intersects-edge-back-right? c1 c2) (subtract-edge-back-right c1 c2)
    (intersects-corner-top-front-left? c1 c2) (subtract-corner-top-front-left c1 c2)
    (intersects-corner-top-front-right? c1 c2) (subtract-corner-top-front-right c1 c2)
    (intersects-corner-top-back-left? c1 c2) (subtract-corner-top-back-left c1 c2)
    (intersects-corner-top-back-right? c1 c2) (subtract-corner-top-back-right c1 c2)
    (intersects-corner-bottom-front-left? c1 c2) (subtract-corner-bottom-front-left c1 c2)
    (intersects-corner-bottom-front-right? c1 c2) (subtract-corner-bottom-front-right c1 c2)
    (intersects-corner-bottom-back-left? c1 c2) (subtract-corner-bottom-back-left c1 c2)
    (intersects-corner-bottom-back-right? c1 c2) (subtract-corner-bottom-back-right c1 c2)))

(defn turn-off
  [cubes c1]
  (reduce (fn [resulting-cubes c2]
            (concat resulting-cubes
                    (subtract-cube c1 c2)))
          []
          cubes))

(defn turn-on
  [cubes c]
  (conj (turn-off cubes c) c))


(defn part-2
  []
  (let [in (parse-input test-input)]
    (let [cubes (reduce (fn [acc [state & c]]
                          (if (= "on" state)
                            (turn-on acc c)
                            (turn-off acc c)))
                        []
                        in)]
      (->> cubes
           (map (fn [c]
                  (reduce *
                          (map (fn [[l h]]
                                 (- (inc  h) l))
                               c))))
           (reduce +)))))

(comment
  (part-1) ;; => 644257
  (part-2) ;; =>
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [644257 ])
