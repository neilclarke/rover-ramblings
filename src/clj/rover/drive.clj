(ns rover.drive)

(defn rotate
  "Returns the new orientation for the rover based on the rotation applied to the
  current orientation"
  [heading direction]
  (let [directions [:N :E :S :W]
        dir-count (count directions)
        start (case heading
                :N 0
                :E 1
                :S 2
                :W 3
                0)
         new-heading (case direction
                      :R (mod (inc start) dir-count)
                      :L (mod (+ (dec dir-count) start) dir-count)
                      start)]
      (nth directions new-heading)))

(defn advance
  "Advances the rover in the grid, based on its current orientation"
  [heading position]
  (let [[x y] position]
    (case heading
      :N [x (inc y)]
      :E [(inc x) y]
      :S [x (dec y)]
      :W [(dec x) y]
      position)))

(defn drive
  "Advance or rotate, ignoring commands to enter lost zones"
  [position heading command lost-zones]
  (cond

    (= command :F)
    (let [new-pos (advance heading position)]
      (if (contains? lost-zones new-pos) [position heading] [new-pos heading]))

    (or (= :L command) (= :R command)) [position (rotate heading command)]

    :else [position heading]))
