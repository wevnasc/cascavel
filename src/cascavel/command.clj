(ns cascavel.command 
  (:require [cascavel.functions :as functions]))

(defn- contains-command?
  "Verify is the inputted command has the same name that the command's name"
  [input-command {:keys [name] :as _command}]
  (= name (keyword input-command)))

(defn- find-sub-command?
  "Verify if is necessary to look for a sub command"
  [{:keys [sub-commands] :as command} args]
  (boolean (and (functions/not-nil? command)
                (not-empty args)
                (not-empty sub-commands))))

(defn- command-not-found?
  "Verify if the command was found"
  [command]
  (nil? command))

(defn- find-inputted-command
  "Try to find the inputted command inside a list of commands"
  [inputted-command commands]
  (functions/find-first (partial contains-command? inputted-command) commands))

(defn- find-command+args
  "Find the command or subcommand recusivelly"
  [latest-command+args-found
   commands
   [inputted-command & args]]
  (let [{:keys [sub-commands] :as command} (find-inputted-command inputted-command commands)]
     (cond (find-sub-command? command args)
           (recur [command args] sub-commands args)

           (command-not-found? command)
           latest-command+args-found

           :else
           [command args])))

(defn find-command
  [root-command args]
  (find-command+args [nil nil] [root-command] args))

(defn execute
  "Executes the command provided"
  [root-command args]
  (let [[{:keys [run] :as found-command} args] (find-command root-command args)]
    (when-not (nil? found-command)
     (run found-command args))))

