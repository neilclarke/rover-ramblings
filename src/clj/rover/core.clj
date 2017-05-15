(ns rover.core
  [:require [clojure.java.io :as io]
   [rover.drive :as drive]])

(defn parse-grid
  "Builds a structure that holds the X/Y bounds of the grid"
  [grid]
  (->> (clojure.string/split grid #" ")
       (map #(Integer/parseInt %))
       (into [])))

(defn parse-instructions
  "Create a sequence of instruction keywords from a string"
  [instr-str]
  (into []
        (comp (map #(String/valueOf %)) (map keyword))
        (seq instr-str)))

(defn parse-rover-start
  "Builds a structure that holds the starting grid coordinates and heading for the rover"
  [rover-start-str]
  (let [rover-start-seq (clojure.string/split rover-start-str #" ")
        rover-start (->> rover-start-seq
                         (butlast)
                         (map #(Integer/parseInt %))
                         (into []))
        rover-orient (->> rover-start-seq
                          (last)
                          (keyword))]
    [rover-start rover-orient]))

(defn within-bounds
  "Checks if the new position of the rover (px/py) is within the bounds of the grid (gx/gy)"
  [[gx gy] [px py]]
  (and
   (<= 0 px)
   (<= 0 py)
   (<= px gx)
   (<= py gy)))

(defn run-rover
  "And awaaaaay we go!"
  [grid-size lost-zones [rover-start commands]]
  (let [rover (atom rover-start)]
    (for [command commands]
      (let [last-known-pos (first @rover)
            new-state (drive/drive last-known-pos (second @rover) command @lost-zones)]
        (if (not (within-bounds grid-size (first new-state)))
          (swap! lost-zones conj (first @rover)) ;should break here
          (reset! rover new-state))))))

(defn -main
  "Main"
  ([]
   (do
     (println "running with default input file \"resources/input.txt\"")
     (-main "resources/input.txt")))

  ([filename]
   (let [lost-zones (atom #{})]
     (do
       (println "running for file:" filename)
       (let [lost-zones (atom #{})
             filename "resources/input.txt"]
         (with-open [reader (io/reader filename)]
           (let [grid-size (parse-grid (.readLine reader))
                 rover-setups (->> (line-seq reader)
                                   (reduce conj [])
                                   (remove empty?)
                                   (partition 2)
                                   (map (fn [[rover-start rover-instrs]]
                                          [(parse-rover-start rover-start) (parse-instructions rover-instrs)])))]
             (pmap #(run-rover grid-size lost-zones %) rover-setups))))))))
