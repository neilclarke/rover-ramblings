(ns rover.core
  (:require [clojure.java.io :as io]
            [rover.drive :as drive])
  (:gen-class))

(defn parse-grid
  "Builds a structure that holds the X/Y bounds of the grid (max grid size 50x50, positive numbers)"
  [grid]
  (let [parsed-grid (->> (clojure.string/split grid #" ")
                         (map #(Integer/parseInt %))
                         (into []))
        [gx gy] parsed-grid]
    [(max 0 (min 50 gx)) (max 0 (min 50 gy))]))

(defn parse-instructions
  "Create a sequence of instruction letters from a string"
  [instr-str]
  (into []
        (map #(String/valueOf %))
        (take 100 (seq instr-str))))

(defn parse-rover-start
  "Builds a structure that holds the starting grid coordinates and heading for the rover"
  [rover-start-str]
  (let [rover-start-seq (clojure.string/split rover-start-str #" ")
        rover-start (->> rover-start-seq
                         (butlast)
                         (map #(Integer/parseInt %))
                         (into []))
        rover-heading (->> rover-start-seq
                           (last))]
    [rover-start rover-heading]))

(defn run-rover
  "And awaaaaay we go!"
  [grid-size lost-zones [rover-start commands]]
  (loop [commands commands
         rover-pos rover-start]
    (if-let [command (first commands)]
      (let [[current-pos current-heading] rover-pos
            new-state (drive/drive current-pos current-heading command @lost-zones)]
        (if (not (drive/within-bounds grid-size (first new-state)))
          (do
            (swap! lost-zones conj rover-pos)
            (conj rover-pos "LOST"))
          (do
            (recur (rest commands) new-state))))
      rover-pos)))

(defn run-rover-file
  [filename]
  (let [lost-zones (atom #{})]
    (with-open [reader (io/reader filename)]
      (let [grid-size (parse-grid (.readLine reader))
            rover-setups (->> (line-seq reader)
                              (reduce conj [])
                              (remove empty?)
                              (partition 2)
                              (map (fn [[rover-start rover-instrs]]
                                     [(parse-rover-start rover-start) (parse-instructions rover-instrs)])))
            rover-ramblings (pmap #(run-rover grid-size lost-zones %) rover-setups)]
        (->> rover-ramblings
             (map flatten)
             (map #(interpose " " %))
             (map #(apply str %))
             (interpose "\n")
             (apply str))))))

(defn -main
  "Main Method."
  ([]
   (do
     (-main "")))
  ([filename]
   (if (.exists (io/file filename))
       (println (run-rover-file filename))
       (println "please specify a valid file path..."))
   (System/exit 0)))
