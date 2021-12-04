(ns cascavel.flag-test
  (:require
   [cascavel.flag :as flag]
   [clojure.test :refer :all]))

(defn continue-flags
  [command _args]
  (-> command :context :flags))

(defn continue-args
  [_command args]
  args)

(deftest args->flags
  (testing "Should create a context inside the command"
    (let [command {:flags [{:name :file  :short "-f" :long "--file"}]}
          args    ["-f" "out.txt"]]

      (testing "when a short flag is provided"
        (is (= {:file "out.txt"}
               (flag/args->flags command args continue-flags))))

      (testing "when a long flag is provided"
        (is (= {:file "out.txt"}
               (flag/args->flags command args continue-flags))))))

  (testing "Should return a empty context flags"
    (let [command {:flags [{:name :file  :short "-f" :long "--file"}]}
          args    []]

      (testing "when any flag is added on the args"
        (is (empty? (flag/args->flags command args continue-flags))))

      (testing "when has no flags in the command"
        (is {empty? (flag/args->flags {} args continue-flags)}))

      (testing "when has no flags in the command and args are provided"
        (is {empty? (flag/args->flags {} ["foo"] continue-flags)}))))

  (testing "Should create a context inside the command with parsed values"
    (testing "when a parse function is provided"
      (let [command {:flags [{:name :amount  :short "-a" :long "--amount" :parse :long}]}
            args    ["-a" "2000"]]
        (is (= {:amount 2000}
               (flag/args->flags command args continue-flags)))))

    (testing "when a boolean function is provided"
      (let [command {:flags [{:name :print  :short "-p" :long "--print" :parse :boolean}]}
            args    ["-p"]]
        (is (= {:print true}
               (flag/args->flags command args continue-flags))))))

  (testing "Should remove flags from args"
    (let [command {:flags [{:name :file  :short "-f" :long "--file"}]}
          args    ["-f" "out.txt"]]

      (testing "when a short flag is provided"
        (is (= []
               (flag/args->flags command args continue-args))))

      (testing "when a long flag is provided"
        (is (= []
               (flag/args->flags command args continue-args))))

      (testing "when args and flags are provided"
        (is (= ["foo" "bar"]
               (flag/args->flags command (conj args ["foo" "bar"]) continue-args)))))))
