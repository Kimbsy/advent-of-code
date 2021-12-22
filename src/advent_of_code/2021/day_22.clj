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

(def test-input-3
  ["on x=-5..47,y=-31..22,z=-19..33"
 "on x=-44..5,y=-27..21,z=-14..35"
 "on x=-49..-1,y=-11..42,z=-10..38"
 "on x=-20..34,y=-40..6,z=-44..1"
 "off x=26..39,y=40..50,z=-2..11"
 "on x=-41..5,y=-41..6,z=-36..8"
 "off x=-43..-33,y=-45..-28,z=7..25"
 "on x=-33..15,y=-32..19,z=-34..11"
 "off x=35..47,y=-46..-34,z=-11..5"
 "on x=-14..36,y=-6..44,z=-16..29"
 "on x=-57795..-6158,y=29564..72030,z=20435..90618"
 "on x=36731..105352,y=-21140..28532,z=16094..90401"
 "on x=30999..107136,y=-53464..15513,z=8553..71215"
 "on x=13528..83982,y=-99403..-27377,z=-24141..23996"
 "on x=-72682..-12347,y=18159..111354,z=7391..80950"
 "on x=-1060..80757,y=-65301..-20884,z=-103788..-16709"
 "on x=-83015..-9461,y=-72160..-8347,z=-81239..-26856"
 "on x=-52752..22273,y=-49450..9096,z=54442..119054"
 "on x=-29982..40483,y=-108474..-28371,z=-24328..38471"
 "on x=-4958..62750,y=40422..118853,z=-7672..65583"
 "on x=55694..108686,y=-43367..46958,z=-26781..48729"
 "on x=-98497..-18186,y=-63569..3412,z=1232..88485"
 "on x=-726..56291,y=-62629..13224,z=18033..85226"
 "on x=-110886..-34664,y=-81338..-8658,z=8914..63723"
 "on x=-55829..24974,y=-16897..54165,z=-121762..-28058"
 "on x=-65152..-11147,y=22489..91432,z=-58782..1780"
 "on x=-120100..-32970,y=-46592..27473,z=-11695..61039"
 "on x=-18631..37533,y=-124565..-50804,z=-35667..28308"
 "on x=-57817..18248,y=49321..117703,z=5745..55881"
 "on x=14781..98692,y=-1341..70827,z=15753..70151"
 "on x=-34419..55919,y=-19626..40991,z=39015..114138"
 "on x=-60785..11593,y=-56135..2999,z=-95368..-26915"
 "on x=-32178..58085,y=17647..101866,z=-91405..-8878"
 "on x=-53655..12091,y=50097..105568,z=-75335..-4862"
 "on x=-111166..-40997,y=-71714..2688,z=5609..50954"
 "on x=-16602..70118,y=-98693..-44401,z=5197..76897"
 "on x=16383..101554,y=4615..83635,z=-44907..18747"
 "off x=-95822..-15171,y=-19987..48940,z=10804..104439"
 "on x=-89813..-14614,y=16069..88491,z=-3297..45228"
 "on x=41075..99376,y=-20427..49978,z=-52012..13762"
 "on x=-21330..50085,y=-17944..62733,z=-112280..-30197"
 "on x=-16478..35915,y=36008..118594,z=-7885..47086"
 "off x=-98156..-27851,y=-49952..43171,z=-99005..-8456"
 "off x=2032..69770,y=-71013..4824,z=7471..94418"
 "on x=43670..120875,y=-42068..12382,z=-24787..38892"
 "off x=37514..111226,y=-45862..25743,z=-16714..54663"
 "off x=25699..97951,y=-30668..59918,z=-15349..69697"
 "off x=-44271..17935,y=-9516..60759,z=49131..112598"
 "on x=-61695..-5813,y=40978..94975,z=8655..80240"
 "off x=-101086..-9439,y=-7088..67543,z=33935..83858"
 "off x=18020..114017,y=-48931..32606,z=21474..89843"
 "off x=-77139..10506,y=-89994..-18797,z=-80..59318"
 "off x=8476..79288,y=-75520..11602,z=-96624..-24783"
 "on x=-47488..-1262,y=24338..100707,z=16292..72967"
 "off x=-84341..13987,y=2429..92914,z=-90671..-1318"
 "off x=-37810..49457,y=-71013..-7894,z=-105357..-13188"
 "off x=-27365..46395,y=31009..98017,z=15428..76570"
 "off x=-70369..-16548,y=22648..78696,z=-1892..86821"
 "on x=-53470..21291,y=-120233..-33476,z=-44150..38147"
 "off x=-93533..-4276,y=-16170..68771,z=-104985..-24507"])

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
  (or (outside? x1s x2s)
      (outside? y1s y2s)
      (outside? z1s z2s)))

;; encloses face?
(defn encloses-face-top?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))
(defn encloses-face-bottom?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))
(defn encloses-face-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))
(defn encloses-face-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))
(defn encloses-face-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (encloses? z1s z2s)))
(defn encloses-face-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (encloses? y1s y2s)
       (encloses? z1s z2s)))

;; intersects with face?
(defn intersects-face-top?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (inside? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-face-bottom?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (inside? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-face-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-low? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-face-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-high? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-face-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (inside? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-face-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (inside? y1s y2s)
       (inside? z1s z2s)))

;; encloses edge?
(defn encloses-edge-top-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-high? z1s z2s)))

(defn encloses-edge-top-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-high? z1s z2s)))

(defn encloses-edge-top-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))

(defn encloses-edge-top-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (encloses? y1s y2s)
       (intersects-high? z1s z2s)))

(defn encloses-edge-bottom-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-low? z1s z2s)))

(defn encloses-edge-bottom-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (encloses? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-low? z1s z2s)))

(defn encloses-edge-bottom-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))

(defn encloses-edge-bottom-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (encloses? y1s y2s)
       (intersects-low? z1s z2s)))

(defn encloses-edge-front-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))

(defn encloses-edge-front-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-low? y1s y2s)
       (encloses? z1s z2s)))

(defn encloses-edge-back-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))

(defn encloses-edge-back-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-high? y1s y2s)
       (encloses? z1s z2s)))

;; intersects with edge?
(defn intersects-edge-top-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (inside? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-top-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (inside? y1s y2s)
       (intersects-high? z1s z2s)))

(defn intersects-edge-bottom-front?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-low? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-back?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (inside? x1s x2s)
       (intersects-high? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (inside? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-bottom-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (inside? y1s y2s)
       (intersects-low? z1s z2s)))

(defn intersects-edge-front-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-low? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-edge-front-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-low? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-edge-back-left?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-low? x1s x2s)
       (intersects-high? y1s y2s)
       (inside? z1s z2s)))

(defn intersects-edge-back-right?
  [[x1s y1s z1s]
   [x2s y2s z2s]]
  (and (intersects-high? x1s x2s)
       (intersects-high? y1s y2s)
       (inside? z1s z2s)))

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

;; encloses face?
(defn cleave-face-top
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   ])

(defn cleave-face-bottom
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   ])

(defn cleave-face-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back
   ])

(defn cleave-face-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front
   ])

(defn cleave-face-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   ])

(defn cleave-face-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   ])

;; intersects with face?
(defn subtract-face-top
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 xh1] [yl1 (dec yl2)] [zl2 zh1]] ; top-front
   [[xl1 xh1] [(inc yh2) yh1] [zl2 zh1]] ; top-back
   [[xl1 (dec xl2)] [yl2 yh2] [zl2 zh1]] ; top-left
   [[(inc xh2) xh1] [yl2 yh2] [zl2 zh1]] ; top-right
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
   [[xl1 xh1] [yl2 yh1] [(inc zh2) zh1]] ; back-top
   [[xl1 xh1] [yl2 yh1] [zl1 (dec zl2)]] ; back-bottom
   [[xl1 (dec xl2)] [yl2 yh1] [zl2 zh2]] ; back-left
   [[(inc xh2) xh1] [yl2 yh1] [zl2 zh2]] ; back-right
   ])

(defn subtract-face-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [yl1 yh1] [(inc zh2) zh1]] ; left-top
   [[xl1 xh2] [yl1 yh1] [zl1 (dec zl2)]] ; left-bottom
   [[xl1 xh2] [yl1 (dec yl2)] [zl2 zh2]] ; left-front
   [[xl1 xh2] [(inc yh2) yh1] [zl2 zh2]] ; left-back
   ])

(defn subtract-face-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [yl1 yh1] [(inc zh2) zh1]] ; right-top
   [[xl2 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; right-bottom
   [[xl2 xh1] [yl1 (dec yl2)] [zl2 zh2]] ; right-front
   [[xl2 xh1] [(inc yh2) yh1] [zl2 zh2]] ; right-back
   ])

;; encloses edge?
(defn cleave-edge-top-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 xh1] [(inc yh2) yh1] [zl2 zh1]] ; top-back
   ])

(defn cleave-edge-top-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 xh1] [yl1 (dec yl2)] [zl2 zh1]] ; top-front
   ])

(defn cleave-edge-top-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[(inc xh2) xh1] [yl1 yh1] [zl2 zh1]] ; top-right
   ])

(defn cleave-edge-top-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 (dec xl2)] [yl1 yh1] [zl2 zh1]] ; top-left
   ])

(defn cleave-edge-bottom-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 xh1] [(inc yh2) yh1] [zl1 zh2]] ; bottom-back
   ])

(defn cleave-edge-bottom-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 xh1] [yl1 (dec yl2)] [zl1 zh2]] ; bottom-front
   ])

(defn cleave-edge-bottom-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[(inc xh2) xh1] [yl1 yh1] [zl1 zh2]] ; bottom-right
   ])

(defn cleave-edge-bottom-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 (dec xl2)] [yl1 yh1] [zl1 zh2]] ; bottom-left
   ])

(defn cleave-edge-front-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [(inc yh2) yh1] [zl1 zh1]] ; back-left
   ])

(defn cleave-edge-front-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back-right
   ])

(defn cleave-edge-back-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [yl1 (dec yl2)] [zl1 zh1]] ; front-left
   ])

(defn cleave-edge-back-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front-right
   ])

;; intersects with edge?
(defn subtract-edge-top-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 xh1] [(inc yh2) yh1] [zl2 zh1]] ; top-back
   [[xl1 (dec xl2)] [yl1 yh2] [zl2 zh1]] ; top-front-left
   [[(inc xh2) xh1] [yl1 yh2] [zl2 zh1]] ; top-front-right
   ])

(defn subtract-edge-top-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 xh1] [yl1 (dec yl2)] [zl2 zh1]] ; top-front
   [[xl1 (dec xl2)] [yl2 yh1] [zl2 zh1]] ; top-back-left
   [[(inc xh2) xh1] [yl2 yh1] [zl2 zh1]] ; top-back-right
   ])

(defn subtract-edge-top-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[(inc xh2) xh1] [yl1 yh1] [zl2 zh1]] ; top-right
   [[xl1 xh2] [yl1 (dec yl2)] [zl2 zh1]] ; top-front-left
   [[xl1 xh2] [(inc yh2) yh1] [zl2 zh1]] ; top-back-left
   ])

(defn subtract-edge-top-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [zl1 (dec zl2)]] ; bottom
   [[xl1 (dec xl2)] [yl1 yh1] [zl2 zh1]] ; top-left
   [[xl2 xh1] [yl1 (dec yl2)] [zl2 zh1]] ; top-front-right
   [[xl2 xh1] [(inc yh2) yh1] [zl2 zh1]] ; top-back-right
   ])

(defn subtract-edge-bottom-front
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 xh1] [(inc yh2) yh1] [zl1 zh2]] ; bottom-back
   [[xl1 (dec xl2)] [yl1 yh2] [zl1 zh2]] ; bottom-front-left
   [[(inc xh2) xh1] [yl1 yh2] [zl1 zh2]] ; bottom-front-right
   ])

(defn subtract-edge-bottom-back
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 xh1] [yl1 (dec yl2)] [zl1 zh2]] ; bottom-front
   [[xl1 (dec xl2)] [yl2 yh1] [zl1 zh2]] ; bottom-back-left
   [[(inc xh2) xh1] [yl2 yh1] [zl1 zh2]] ; bottom-back-right
   ])

(defn subtract-edge-bottom-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[(inc xh2) xh1] [yl1 yh1] [zl1 zh2]] ; bottom-right
   [[xl1 xh2] [yl1 (dec yl2)] [zl1 zh2]] ; bottom-front-left
   [[xl1 xh2] [(inc yh2) yh1] [zl1 zh2]] ; bottom-back-left
   ])

(defn subtract-edge-bottom-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 xh1] [yl1 yh1] [(inc zh2) zh1]] ; top
   [[xl1 (dec xl2)] [yl1 yh1] [zl1 zh2]] ; bottom-left
   [[xl2 xh1] [yl1 (dec yl2)] [zl1 zh2]] ; bottom-front-right
   [[xl2 xh1] [(inc yh2) yh1] [zl1 zh2]] ; bottom-back-right
   ])

(defn subtract-edge-front-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [(inc yh2) yh1] [zl1 zh1]] ; back-left
   [[xl1 xh2] [yl1 yh2] [(inc zh2) zh1]] ; top-front-left
   [[xl1 xh2] [yl1 yh2] [zl1 (dec zl2)]] ; bottom-front-left
   ])

(defn subtract-edge-front-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back-right
   [[xl2 xh1] [yl1 yh2] [(inc zh2) zh1]] ; top-front-right
   [[xl2 xh1] [yl1 yh2] [zl1 (dec zl2)]] ; bottom-front-right
   ])

(defn subtract-edge-back-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [yl1 (dec yl2)] [zl1 zh1]] ; front-left
   [[xl1 xh2] [yl2 yh1] [(inc zh2) zh1]] ; top-back-left
   [[xl1 xh2] [yl2 yh1] [zl1 (dec zl2)]] ; bottom-back-left
   ])

(defn subtract-edge-back-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front-right
   [[xl2 xh1] [yl2 yh1] [(inc zh2) zh1]] ; top-back-right
   [[xl2 xh1] [yl2 yh1] [zl1 (dec zl2)]] ; bottom-back-right
   ])

;; intersects with corner?
(defn subtract-corner-top-front-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [(inc yh2) yh1] [zl1 zh1]] ; back-left
   [[xl1 xh2] [yl1 yh2] [zl1 (dec zl2)]] ; bottom-front-left
   ])

(defn subtract-corner-top-front-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back-right
   [[xl2 xh1] [yl1 yh2] [zl1 (dec zl2)]] ; bottom-front-right
   ])

(defn subtract-corner-top-back-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [yl1 (dec yl2)] [zl1 zh1]] ; front-left
   [[xl1 xh2] [yl2 yh1] [zl1 (dec zl2)]] ; bottom-back-left
   ])

(defn subtract-corner-top-back-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front-right
   [[xl2 xh1] [yl2 yh1] [zl1 (dec zl2)]] ; bottom-back-right
   ])

(defn subtract-corner-bottom-front-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [(inc yh2) yh1] [zl1 zh1]] ; back-left
   [[xl1 xh2] [yl1 yh2] [(inc zh2) zh1]] ; top-front-left
   ])

(defn subtract-corner-bottom-front-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [(inc yh2) yh1] [zl1 zh1]] ; back-right
   [[xl2 xh1] [yl1 yh2] [(inc zh2) zh1]] ; top-front-right
   ])

(defn subtract-corner-bottom-back-left
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[(inc xh2) xh1] [yl1 yh1] [zl1 zh1]] ; right
   [[xl1 xh2] [yl1 (dec yl2)] [zl1 zh1]] ; front-left
   [[xl1 xh2] [yl2 yh1] [(inc zh2) zh1]] ; top-back-left
   ])

(defn subtract-corner-bottom-back-right
  [[[xl1 xh1] [yl1 yh1] [zl1 zh1]]
   [[xl2 xh2] [yl2 yh2] [zl2 zh2]]]
  [[[xl1 (dec xl2)] [yl1 yh1] [zl1 zh1]] ; left
   [[xl2 xh1] [yl1 (dec yl2)] [zl1 zh1]] ; front-right
   [[xl2 xh1] [yl2 yh1] [(inc zh2) zh1]] ; top-back-right
   ])

(def test-c [[1 4] [1 4] [1 4]])

(comment
  (do
    (subtract-cube-inside test-c [[2 3] [2 3] [2 3]])
    (subtract-cube-encloses test-c [[0 5] [0 5] [0 5]])
    (subtract-cube-outside test-c [[6 7] [6 7] [6 7]])

    (subtract-face-top test-c [[2 3] [2 3] [4 5]])
    (subtract-face-bottom test-c [[2 3] [2 3] [0 1]])
    (subtract-face-front test-c [[2 3] [0 1] [2 3]])
    (subtract-face-back test-c [[2 3] [4 5] [2 3]])
    (subtract-face-left test-c [[0 1] [2 3] [2 3]])
    (subtract-face-right test-c [[4 5] [2 3] [2 3]])

    (subtract-edge-top-front test-c [[2 3] [0 1] [4 5]])
    (subtract-edge-top-back test-c [[2 3] [4 5] [4 5]])
    (subtract-edge-top-left test-c [[0 1] [2 3] [4 5]])
    (subtract-edge-top-right test-c [[4 5] [2 3] [4 5]])
    (subtract-edge-bottom-front test-c [[2 3] [0 1] [0 1]])
    (subtract-edge-bottom-back test-c [[2 3] [4 5] [0 1]])
    (subtract-edge-bottom-left test-c [[0 1] [2 3] [0 1]])
    (subtract-edge-bottom-right test-c [[4 5] [2 3] [0 1]])
    (subtract-edge-front-left test-c [[0 1] [0 1] [2 3]])
    (subtract-edge-front-right test-c [[4 5] [0 1] [2 3]])
    (subtract-edge-back-left test-c [[0 1] [4 5] [2 3]])
    (subtract-edge-back-right test-c [[4 5] [4 5] [2 3]])

    (subtract-corner-top-front-left test-c [[0 1] [0 1] [4 5]])
    (subtract-corner-top-front-right test-c [[4 5] [0 1] [4 5]])
    (subtract-corner-top-back-left test-c [[0 1] [4 5] [4 5]])
    (subtract-corner-top-back-right test-c [[4 5] [4 5] [4 5]])
    (subtract-corner-bottom-front-left test-c [[0 1] [0 1] [0 1]])
    (subtract-corner-bottom-front-right test-c [[4 5] [0 1] [0 1]])
    (subtract-corner-bottom-back-left test-c [[0 1] [4 5] [0 1]])
    (subtract-corner-bottom-back-right test-c [[4 5] [4 5] [0 1]])))

(defn subtract-cube
  [c1 c2]
  (cond
    (cube-inside? c1 c2) (subtract-cube-inside c1 c2)
    (cube-encloses? c1 c2) (subtract-cube-encloses c1 c2)
    (cube-outside? c1 c2) (subtract-cube-outside c1 c2)

    (encloses-face-top? c1 c2) (cleave-face-top c1 c2)
    (encloses-face-bottom? c1 c2) (cleave-face-bottom c1 c2)
    (encloses-face-front? c1 c2) (cleave-face-front c1 c2)
    (encloses-face-back? c1 c2) (cleave-face-back c1 c2)
    (encloses-face-left? c1 c2) (cleave-face-left c1 c2)
    (encloses-face-right? c1 c2) (cleave-face-right c1 c2)

    (intersects-face-top? c1 c2) (subtract-face-top c1 c2)
    (intersects-face-bottom? c1 c2) (subtract-face-bottom c1 c2)
    (intersects-face-front? c1 c2) (subtract-face-front c1 c2)
    (intersects-face-back? c1 c2) (subtract-face-back c1 c2)
    (intersects-face-left? c1 c2) (subtract-face-left c1 c2)
    (intersects-face-right? c1 c2) (subtract-face-right c1 c2)

    (encloses-edge-top-front? c1 c2) (cleave-edge-top-front c1 c2)
    (encloses-edge-top-back? c1 c2) (cleave-edge-top-back c1 c2)
    (encloses-edge-top-left? c1 c2) (cleave-edge-top-left c1 c2)
    (encloses-edge-top-right? c1 c2) (cleave-edge-top-right c1 c2)
    (encloses-edge-bottom-front? c1 c2) (cleave-edge-bottom-front c1 c2)
    (encloses-edge-bottom-back? c1 c2) (cleave-edge-bottom-back c1 c2)
    (encloses-edge-bottom-left? c1 c2) (cleave-edge-bottom-left c1 c2)
    (encloses-edge-bottom-right? c1 c2) (cleave-edge-bottom-right c1 c2)
    (encloses-edge-front-left? c1 c2) (cleave-edge-front-left c1 c2)
    (encloses-edge-front-right? c1 c2) (cleave-edge-front-right c1 c2)
    (encloses-edge-back-left? c1 c2) (cleave-edge-back-left c1 c2)
    (encloses-edge-back-right? c1 c2) (cleave-edge-back-right c1 c2)

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
    (intersects-corner-bottom-back-right? c1 c2) (subtract-corner-bottom-back-right c1 c2)
    ))

(defn turn-off
  [cubes c]
  (mapcat (fn [existing-cube]
            (subtract-cube existing-cube c))
          cubes))

(defn turn-on
  [cubes c]
  (conj (turn-off cubes c) c))

(defn volume
  [[[xl xh] [yl yh] [zl zh]]]
  (* (- (inc xh) xl)
     (- (inc yh) yl)
     (- (inc zh) zl)))

(defn part-2
  []
  (let [in (parse-input test-input-3)]
    (let [cubes (reduce (fn [acc [state & c]]
                          (if state
                            (turn-on acc c)
                            (turn-off acc c)))
                        []
                        in)]
      (->> cubes
           (map volume)
           (reduce +)))))

(comment
  (part-1) ;; => 644257
  (part-2) ;; =>
  ,)

;; refactoring check
;; (= [(part-1) (part-2)] [644257 ])
