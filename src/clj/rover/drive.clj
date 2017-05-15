(ns rover.drive)

(defn- rotate
  "Returns the new orientation for the rover based on the rotation applied to the
  current orientation"
  [heading direction]
  (let [directions ["N" "E" "S" "W"]
        dir-count (count directions)
        start (case heading
                "N" 0
                "E" 1
                "S" 2
                "W" 3
                0)
         new-heading (case direction
                      "R" (mod (inc start) dir-count)
                      "L" (mod (+ (dec dir-count) start) dir-count)
                      start)]
      (nth directions new-heading)))

(defn- advance-fn
  "How to alter coordinates when advancing in a certain direction"
  [heading]
  ({ "N" (fn [[x y]] [x (inc y)])
     "E" (fn [[x y]] [(inc x) y])
     "S" (fn [[x y]] [x (dec y)])
     "W" (fn [[x y]] [(dec x) y])} heading))

(defn- advance
  "Advances the rover in the grid, based on its current orientation"
  [heading position]
  ((advance-fn heading) position))

(defn drive
  "Advance or rotate, ignoring commands to enter lost zones"
  [position heading command lost-zones]
  (cond
    (= command "F")
    (let [new-pos (advance heading position)]
      (if (contains? lost-zones [position heading]) [position heading] [new-pos heading]))

    (or (= "L" command) (= "R" command)) [position (rotate heading command)]

    :else [position heading]))

(defn within-bounds
  "Checks if the new position of the rover (px/py) is within the bounds of the grid (gx/gy)"
  [[gx gy] [px py]]
  (and
   (<= 0 px)
   (<= 0 py)
   (<= px gx)
   (<= py gy)))
