(ns cascavel.core
  (:require [clojure.pprint :as pp]
            [cascavel.command :as command])
  (:gen-class))

(def pretty-print
  {:name         :pretty
   :sub-commands #{pretty-print}
   :run          (fn [_command args]
                   (pp/pprint
                    (format "***Hello, %s!***" (nth args 0 "World"))))})

(def root-command
  {:name         :gretting
   :sub-commands #{pretty-print}
   :run          (fn [_command args]
                   (pp/pprint
                    (format "Hello, %s!" (nth args 0 "World"))))})

(defn -main
  [& args]
  (command/execute root-command args))








