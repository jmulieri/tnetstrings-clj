(ns tnetstrings.core)

(declare parse)
(declare parse-array)

(def parse-map (hash-map "," #(str %)
                         "#" #(Integer/parseInt %)
                         "^" #(Double/parseDouble %)
                         "!" #(Boolean/parseBoolean %)
                         "]" #(parse-array %)))

(defn parse-array [array-str]
  (if (= (.length array-str) 0)
    []
    (let [colon-idx   (.indexOf array-str ":")
          length      (Integer/parseInt (subs array-str 0 colon-idx))
          end-of-str  (+ colon-idx length 2)]
      (into [(parse (subs array-str 0 end-of-str))] (parse-array (subs array-str end-of-str (.length array-str)))))))

(defn parse [tnetstrings]
  (let [colon-idx   (.indexOf tnetstrings ":")
        length      (Integer/parseInt (subs tnetstrings 0 colon-idx))
        payload     (subs tnetstrings (+ colon-idx 1) (+ colon-idx length 1))
        string-type (subs tnetstrings (+ colon-idx length 1) (+ colon-idx length 2))]
    ((parse-map string-type) payload)))

(defn -main [& args]
  (parse (first args)))