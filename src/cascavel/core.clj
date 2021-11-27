(ns cascavel.core
  (:require [clojure.pprint :as pp]
            [cascavel.command :as command]
            [cascavel.pipeline :as pipeline])
  (:gen-class))

(defn validate-args
  [command args continue]
  (if (= (count args) 1)
    (continue command ["Weverson"])
    (print "This command expects just one argument")))

(def pretty-print
  {:name         :pretty
   :sub-commands #{pretty-print}
   :run          (fn [_command args]
                   (pp/pprint
                    (format "***Hello, %s!***" (nth args 0 "World"))))})

(def root-command
  {:name         :gretting
   :pipeline     #{validate-args}
   :sub-commands #{pretty-print}
   :run          (fn [_command args]
                   (pp/pprint
                    (format "Hello, %s!" (nth args 0 "World"))))})

(defn execute
  "Executes the command provided"
  [root-command args]
  (some->> (command/find-deep root-command args)
           (apply pipeline/execute)
           (apply command/execute)))

(defn -main
  [& args]
  (execute root-command args))
