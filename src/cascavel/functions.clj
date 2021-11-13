(ns cascavel.functions)

(def not-nil? (comp not nil?))

(defn find-first
  "Returns the first element that match with the fn predicate"
  [fn coll]
  (first (filter fn coll)))
