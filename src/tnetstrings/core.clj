(ns tnetstrings.core
  (require [clojure.string]))

(declare parse)

(defn parse-array [array-str]
  (if (= (count array-str) 0)
    []
    (let [colon-idx   (.indexOf array-str ":")
          length      (Integer/parseInt (subs array-str 0 colon-idx))
          end-of-str  (+ colon-idx length 2)]
      (into [(parse (subs array-str 0 end-of-str))] (parse-array (subs array-str end-of-str (count array-str)))))))

(defn parse-hash [hash-str]
  (if (= (count hash-str) 0)
    {}
    (let [key-colon-idx (.indexOf hash-str ":")
          key-length    (Integer/parseInt (subs hash-str 0 key-colon-idx))
          end-of-key    (+ key-colon-idx key-length 2)
          val-colon-idx (.indexOf hash-str ":" end-of-key)
          val-length    (Integer/parseInt (subs hash-str end-of-key val-colon-idx))
          end-of-val    (+ val-colon-idx val-length 2)]
      (into
       (sorted-map (parse (subs hash-str 0 end-of-key)) (parse (subs hash-str end-of-key end-of-val)))
       (parse-hash (subs hash-str end-of-val (count hash-str)))))))

(def parse-map { \, identity
                 \# #(Integer/parseInt %)
                 \^ #(Double/parseDouble %)
                 \! #(Boolean/parseBoolean %)
                 \] parse-array
                 \} parse-hash })

(defn parse [tnetstrings]
  (if (= (count tnetstrings) 0)
    ""
    (let [colon-idx   (.indexOf tnetstrings ":")
          length      (Integer/parseInt (subs tnetstrings 0 colon-idx))
          payload     (subs tnetstrings (+ colon-idx 1) (+ colon-idx length 1))
          string-type (nth tnetstrings (+ colon-idx length 1))]
      ((parse-map string-type) payload))))

(declare encode)

(defn encode-boolean [boolean]
  (let [bool-str (format "%s" boolean)]
    (str (count bool-str) ":" bool-str "!")))

(defn encode-float [float]
  (let [float-str (clojure.string/replace (format "%f" float) #"0+$" "")]
    (str (count float-str) ":" float-str "^")))

(defn encode-integer [integer]
  (let [int-str (format "%d" integer)]
    (str (count int-str) ":" int-str "#")))

(defn encode-string [string]
  (str (count string) ":" string ","))

(defn encode-array [array]
  (let [encoded-elements (clojure.string/join "" (map encode array))]
    (str (count encoded-elements) ":" encoded-elements "]")))

(defn encode-hash [hash]
  (let [encoded-elements (clojure.string/join "" (map #(str (encode (first %)) (encode (last %))) hash))]
    (str (count encoded-elements) ":" encoded-elements "}")))


(def encode-map { String                          encode-string
                  java.lang.Boolean               encode-boolean
                  java.lang.Long                  encode-integer
                  java.lang.Double                encode-float
                  clojure.lang.PersistentList     encode-array
                  clojure.lang.PersistentVector   encode-array
                  clojure.lang.PersistentTreeMap  encode-hash
                  clojure.lang.PersistentArrayMap encode-hash
                  clojure.lang.Keyword            #(encode-string (name %)) })

(defn encode [data]
  ((encode-map (class data)) data))

(defn -main [& args]
  (parse (first args)))