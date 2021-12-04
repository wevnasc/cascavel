(ns cascavel.core
  (:require
   [cascavel.command :as command]
   [cascavel.flag :as flag]
   [cascavel.pipeline :as pipeline])
  (:gen-class))

(defn- add-global-jobs
  [{:keys [pipeline] :as command} args]
  (let [command (->> (into [flag/args->flags] pipeline)
                     (assoc command :pipeline))]
    [command args]))

(defn execute
  "Executes the command provided"
  [root-command args]
  (some->> (command/find-deep root-command args)
           (apply add-global-jobs)
           (apply pipeline/execute)
           (apply command/execute)))

(def root-command
  {:name         :execute
   :flags        [{:name :times :short "-t" :long "--times"}]
   :run          (fn [command _args] (dotimes [_n (-> command :context :flags :times)]
                                       (println "hello!")))})

(defn -main
  [& args]
  (execute root-command args))
