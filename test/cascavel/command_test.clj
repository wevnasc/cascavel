(ns cascavel.command-test
  (:require [clojure.test :refer :all]
            [cascavel.command :as command]))

(def deep-command
  {:name         :deep
   :run          (fn [_command args]
                   (format "***Hello, %s !***" (nth args 0 "World")))})

(def sub-command
  {:name         :pretty
   :sub-commands #{deep-command}
   :run          (fn [_command args]
                   (format "***Hello, %s !***" (nth args 0 "World")))})

(def root-command
  {:name         :gretting
   :sub-commands #{sub-command deep-command}
   :run          (fn [_command args]
                   (format "Hello, %s!" (nth args 0 "World")))})

(deftest find-command
  (testing "should return the root command"
    (is (= [root-command nil]
           (command/find-command root-command '("gretting")))))

  (testing "should return the root command and the remaning arguments"
    (is (= [root-command '("universe")]
           (command/find-command root-command '("gretting" "universe")))))

  (testing "should return nil when the command where not found"
    (is (= [nil nil]
           (command/find-command root-command '("pretty")))))

  (testing "should return sub command"
    (is (= [sub-command nil]
           (command/find-command root-command '("gretting" "pretty")))))

  (testing "should return deepest command"
    (is (= [deep-command nil]
           (command/find-command root-command '("gretting" "pretty" "deep")))))

  (testing "should return second command on the sub-command set"
    (is (= [deep-command nil]
           (command/find-command root-command '("gretting" "deep"))))))
