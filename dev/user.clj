(ns user
  [:use [clojure.java.io]])

(let [filename "resources/input.txt"
      parse-grid (fn [grid]
                    (->> (clojure.string/split grid #" ")
                      (map #(Integer/parseInt %))
                      (into [])))]
  (with-open [rdr (reader filename)]
    (parse-grid (.readLine rdr))))
