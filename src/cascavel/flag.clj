(ns cascavel.flag
  (:require
   [cascavel.functions :as functions]
   [cascavel.parse :as parse]))

(def parse->fn
  {:boolean parse/str->boolean
   :long    parse/str->long})

(defn parse-fn
  [parse]
  (cond
    (keyword? parse)
    (get parse->fn parse identity)

    (fn? parse)
    parse

    :else
    identity))

(defn- boolean-flag?
  "Validates if it is a boolean flag"
  [{:keys [parse] :as flag-schema}]
  (and (functions/not-nil? flag-schema)
       (= parse :boolean)))

(defn- partition-flags
  "Partitionate the args by flag and value, in case of the boolean flag
  the true value is used. All partitioned values are grouped in an array with
  two items, the remaining arrays with just one item are considered arguments"
  ([flag-group args] (partition-flags flag-group args []))
  ([flag-group [key value & args :as all-args] args-partition-list]
   (let [flag-schema (some-> flag-group (get key) first)]
     (cond
       (empty? all-args)
       args-partition-list

       (boolean-flag? flag-schema)
       (recur flag-group (drop 1 all-args) (conj args-partition-list [key "true"]))

       (functions/not-nil? flag-schema)
       (recur flag-group args (conj args-partition-list [key value]))

       :else (recur flag-group (drop 1 all-args) (conj args-partition-list [key]))))))

(defn- group-by-flag
  "Group flags by the short and long flags"
  [flag-schemas]
  (->> (map #(group-by % flag-schemas) [:short :long])
       (into {})))

(defn- split-args-flags
  "Split a partitionated list of flags between flags and args by the
  numbers of items on the array"
  [partition-list]
  (let [args  (filter #(= (count %) 1) partition-list)
        flags (filter #(> (count %) 1) partition-list)]
    [args flags]))

(defn- list->flag
  "Transform a list with a flag and the value to a map with the name
  of the flag and your value parsed"
  [flag-group [key value]]
  (when-let [{:keys [name parse] :as _flag-schema} (some-> flag-group (get key) first)]
    {name ((parse-fn parse) value)}))

(defn- list->flags
  "Transform a list of flags in a map with the names and parsed values"
  [flag-group flags]
  (->> (map (partial list->flag flag-group) flags)
       (into {})))

(defn- parse-args-flags
  "Transform a list of list of args to a list of arguments and
  a list of list of flags to a map with the flag names and parsed values"
  [flag-group args flags]
  [(flatten args) (list->flags flag-group flags)])

(defn- extract-args-flags
  "Extract the args and flags from a list of args"
  [flag-group args]
  (-> flag-group
      (partition-flags args)
      split-args-flags))

(defn args->flags
  "Creates a map of flags from the arguments and adds to the command context"
  [{:keys [flags] :as command} args continue]
  (let [flag-group   (group-by-flag flags)
        [args flags] (->> (extract-args-flags flag-group args)
                          (apply (partial parse-args-flags flag-group)))
        command      (assoc-in command [:context :flags] flags)]

    (continue command args)))
