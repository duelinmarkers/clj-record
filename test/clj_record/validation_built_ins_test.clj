(ns clj-record.validation-built-ins-test
  (:use clojure.contrib.test-is)
  (:require [clj-record.validation.built-ins :as v]))


(deftest match-creates-a-matching-fn-for-the-given-pattern
  (let [matches-foo? (v/match #"foo")]
    (is (matches-foo? "yes foo does"))
    (are (not (matches-foo? _1))
      "no bar doesn't"
      nil
      123))
  (testing "non-strings are str'd so they may match"
    (is ((v/match #"123") 41234))))

(deftest non-match-creates-an-anti-matching-fn-for-the-given-pattern
  (let [non-matches-foo? (v/non-match #"foo")]
    (is (not (non-matches-foo? "foo doesn't not")))
    (are (non-matches-foo? _1)
      "bar non-matches"
      nil
      123)))

(deftest numeric?
  (are (v/numeric? _1)
    123
    "123")
  (are (not (v/numeric? _1))
    "foo"))

(deftest email?
  (are (v/email? _1)
    "a@b.cd"
    "abcdef@abcdef.abcdef.abc"
    "a.b@c.de"
    "a_b@c.de"
    "a-b@c.de"
    "a@b.c.de"
    "a@b-c.de")
  (are (not (v/email? _1))
    ""
    "a"
    "abcdef"
    "a@"
    "@bc"
    "a@bc"
    ".@a.bc"
    "a@..."
    "a..b@c.de"
    "a.b@c..de"
    "hi a@b.cd"
    "a@b.cd hi"))
