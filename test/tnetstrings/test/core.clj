(ns tnetstrings.test.core
  (:use [tnetstrings.core])
  (:use [clojure.test]))

(deftest string
  (is (= "hello" (parse "5:hello,"))))

(deftest positive-integer
  (is (= 1234 (parse "4:1234#"))))

(deftest negative-integer
  (is (= -5555 (parse "5:-5555#"))))

(deftest positive-float
  (is (= 923.222 (parse "7:923.222^"))))

(deftest negative-float
  (is (= -323.212 (parse "8:-323.212^"))))

(deftest true-boolean
  (is (= true (parse "4:true!"))))

(deftest false-boolean
  (is (= false (parse "5:false!"))))

(deftest array
  (is (= [13 "word" true] (parse "19:2:13#4:word,4:true!]"))))

(deftest nested-array
  (is (= [13 "word" true [1]] (parse "26:2:13#4:word,4:true!4:1:1#]]"))))
