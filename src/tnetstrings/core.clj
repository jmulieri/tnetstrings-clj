(ns tnetstrings.core)

(def parse-map (hash-map "," #(str %)
                         "#" #(Integer/parseInt %)
                         "^" #(Double/parseDouble %)
                         "!" #(Boolean/parseBoolean %)))

(defn parse [tnetstrings]
  (let [colon-idx   (.indexOf tnetstrings ":")
        length      (Integer/parseInt (subs tnetstrings 0 colon-idx))
        payload     (subs tnetstrings (+ colon-idx 1) (+ colon-idx length 1))
        string_type (subs tnetstrings (+ colon-idx length 1) (+ colon-idx length 2))]
    ((parse-map string_type) payload)))

(defn -main [& args]
  (parse (first args)))