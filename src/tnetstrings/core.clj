(ns tnetstrings.core)

(declare parse)
(declare parse-array)
(declare parse-hash)

(def parse-map (hash-map "," #(str %)
                         "#" #(Integer/parseInt %)
                         "^" #(Double/parseDouble %)
                         "!" #(Boolean/parseBoolean %)
                         "]" #(parse-array %)
                         "}" #(parse-hash %)))

(defn parse-array [array-str]
  (if (= (.length array-str) 0)
    []
    (let [colon-idx   (.indexOf array-str ":")
          length      (Integer/parseInt (subs array-str 0 colon-idx))
          end-of-str  (+ colon-idx length 2)]
      (into [(parse (subs array-str 0 end-of-str))] (parse-array (subs array-str end-of-str (.length array-str)))))))

(defn parse-hash [hash-str]
  (if (= (.length hash-str) 0)
    {}
    (let [key-colon-idx (.indexOf hash-str ":")
          key-length    (Integer/parseInt (subs hash-str 0 key-colon-idx))
          end-of-key    (+ key-colon-idx key-length 2)
          val-colon-idx (.indexOf hash-str ":" end-of-key)
          val-length    (Integer/parseInt (subs hash-str end-of-key val-colon-idx))
          end-of-val    (+ val-colon-idx val-length 2)]
      (conj
       (sorted-map (parse (subs hash-str 0 end-of-key)) (parse (subs hash-str end-of-key end-of-val)))
       (parse-hash (subs hash-str end-of-val (.length hash-str)))))))

(defn parse [tnetstrings]
  (if (= (.length tnetstrings) 0)
    ""
    (let [colon-idx   (.indexOf tnetstrings ":")
          length      (Integer/parseInt (subs tnetstrings 0 colon-idx))
          payload     (subs tnetstrings (+ colon-idx 1) (+ colon-idx length 1))
          string-type (subs tnetstrings (+ colon-idx length 1) (+ colon-idx length 2))]
      ((parse-map string-type) payload))))

(defn -main [& args]
  (parse (first args)))