(ns rover.drive)

(defn rotate
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
  [heading position]
  (let [[x y] position]
    (case heading
      :N [x (inc y)]
      :E [(inc x) y]
      :S [x (dec y)]
      :W [(dec x) y]
      position)))
