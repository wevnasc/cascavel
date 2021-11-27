(ns cascavel.pipeline)

(defn- execute-job
  "Executes the next job on the pipeline if the params are not null
  this function returns a list with the first argument being
  the command and the second one the args."
  [[command args :as params] pipe-fn]
  (let [continue-fn #(vector %1 %2)]
    (when-not (nil? params)
     (pipe-fn command args continue-fn))))

(defn execute
  "Execute all jobs on the pipeline and returs the command and args."
  [command args]
  (reduce execute-job [command args] (:pipeline command)))
