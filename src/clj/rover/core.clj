(ns rover.core
  [:require [clojure.java.io :as io]
            [rover.drive :as drive]])

(->> "RFRFRFRF"
  (seq)
  (into [])
  (map #(String/valueOf %))
  (map keyword))

(let [rover-start-seq (clojure.string/split "1 1 E" #" ")
      rover-start (->> rover-start-seq
                      (butlast)
                      (map #(Integer/parseInt %))
                      (into []))
      rover-orient (->> rover-start-seq
                      (last)
                      (keyword))]
    [rover-start rover-orient])

(let [filename "resources/input.txt"
      parse-grid (fn [grid]
                    (->> (clojure.string/split grid #" ")
                      (map #(Integer/parseInt %))
                      (into [])))]
  (with-open [reader (io/reader filename)]
    (let [grid-size (parse-grid (.readLine reader))
          rover-setups (->> (line-seq reader)
                          (reduce conj [])
                          (remove empty?)
                          (partition 2))]
        rover-setups)))

(let [grid-size [10 10]
      rover (atom [[2 2] :W])
      commands [:R :F :F :F :R :F :F :R :F :L :F :L :F :F]
      lost-zones (atom #{})]
    (for [command commands]
      (let [last-known-pos (first @rover)
            new-state (drive/drive last-known-pos (second @rover) command @lost-zones)]
        (if (= new-state :LOST)
            (swap! lost-zones conj (first @rover))
            (reset! rover new-state)))))


(defn -main
  "Main"
  ([] (println "no args"))
  ([x] (println "1 arg")))
