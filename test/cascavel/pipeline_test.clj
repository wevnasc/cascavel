(ns cascavel.pipeline-test
  (:require [cascavel.pipeline :as pipeline]
            [clojure.test :refer :all]))

(defn count-job
  [command args continue]
  (let [updated-command (assoc command :count (inc (get command :count 0)))
        updated-args    (conj args (:count updated-command))]
    (continue updated-command updated-args)))

(defn stop-job
  [_command _args _continue]
  nil)

(def sequential-command
  {:pipeline [count-job count-job count-job]})

(def break-command
  {:pipeline [count-job count-job stop-job count-job]})

(deftest execute
  (testing "should run all jobs sequentially"
    (is (= [(assoc sequential-command :count 3) [1 2 3]]
           (-> (pipeline/execute sequential-command [])))))

  (testing "should stop pipeline when continue is not called "
    (is (nil? (-> (pipeline/execute break-command []))))))
