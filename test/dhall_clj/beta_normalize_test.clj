(ns dhall-clj.beta-normalize-test
  (:require  [clojure.test :refer :all]
             [medley.core :refer [map-vals]]
             [dhall-clj.ast :refer :all]
             [dhall-clj.parse :refer [parse expr]]
             [dhall-clj.import :refer [resolve-imports]]
             [dhall-clj.beta-normalize :refer [beta-normalize]]
             [dhall-clj.state :as s]
             [dhall-clj.test-utils :refer :all]
             [me.raynes.fs :as fs]))

;; Simple tests from the spec

(def bnormalize-cases
  "These tests come from the `β-normalization` section of the standard"
  ;; (λ(x : Bool) → x == False) True ⇥ False
  [[(beta-normalize
      (->App
        (->Lam "x" (->BoolT)
               (->BoolEQ
                 (->Var "x" 0)
                 (->BoolLit false)))
        (->BoolLit true)))
    (->BoolLit false)]
   ;; List/length Natural [1, 2, 3] ⇥ 3
   [(beta-normalize
      (->App
        (->App (->ListLength) (->NaturalT))
        (->ListLit nil [(->NaturalLit 1)
                        (->NaturalLit 2)
                        (->NaturalLit 3)])))
    (->NaturalLit 3)]
   ;; List/length Integer ⇥ List/length Integer
   [(beta-normalize
      (->App (->ListLength) (->IntegerT)))
    (->App (->ListLength) (->IntegerT))]
   ;; λ(x : Integer) → List/length Integer [x, x, x] ⇥ λ(x : Integer) → 3
   [(beta-normalize
      (->Lam "x" (->IntegerT)
             (->App
               (->App (->ListLength) (->IntegerT))
               (->ListLit nil [(->Var "x" 0)
                               (->Var "x" 0)
                               (->Var "x" 0)]))))
    (->Lam "x" (->IntegerT) (->NaturalLit 3))]])


(deftest bnormalize-spec-test
  (doseq [[in out] bnormalize-cases]
    (testing (str "β-normalization: " in " => " out)
      (is (= in out)))))



;; Haskell implementation test suite

(def test-folder "dhall-haskell/tests/normalization")

(def problematic
  "Here we list all the tests that blow up, so we categorize and exclude them"
  [
   ;; Waiting for single quote strings to be standardized
   "dhall-haskell/tests/normalization/remoteSystems"])


(defn valid-testcases []
  (let [all (success-testcases test-folder)]
    (apply dissoc all problematic)))

(deftest normalization-suite
  (doseq [[testcase {:keys [actual expected]}] (valid-testcases)]
    (let [parent (fs/parent testcase)
          f #(fs/with-mutable-cwd
               (fs/chdir parent)
               (-> %
                  parse
                  expr
                  (resolve-imports (s/new))
                  beta-normalize))]
      (println "TESTCASE" testcase)
      (testing testcase
        (is (= (f actual)
               (f expected)))))))
