(ns dhall-clj.fail
  (:require [com.gfredericks.catch-data :refer [throw-data]]
            [qbits.ex :as ex]))

;;
;; Top hierarchy
;;

(ex/derive ::read      ::dhall-clj)
(ex/derive ::imports   ::dhall-clj)
(ex/derive ::typecheck ::dhall-clj)


;;
;; Parsing
;;

(ex/derive ::parsing      ::read)
(ex/derive ::ast-building ::read)

(defn parsing!
  "Throws an ex-info from a failure in parsing the string"
  [gll-failure]
  (throw-data
    "Failed to parse Dhall string"
    {:type ::parsing
     :failure gll-failure
     :failure-printed (pr-str gll-failure)}))

(defn ast-building!
  "Throws an ex-info from a failed build of the AST"
  [tree]
  (throw-data
    "Failed to build the AST from the parse-tree; unmatched rule `%unmatched~s`"
    {:type ::ast-building
     :tree tree
     :unmatched (:t tree)
     :tree-printed (pr-str tree)}))


;;
;; Import
;;

(ex/derive ::missing-keyword ::imports)
(ex/derive ::missing-env     ::imports)
(ex/derive ::missing-file    ::imports)
(ex/derive ::missing-imports ::imports)
(ex/derive ::cyclic-import   ::imports)

(defn missing-keyword!
  "Throws an ex-info from the `missing` keyword"
  []
  (throw-data
    "Found `missing` keyword"
    {:type ::missing-keyword}))

(defn missing-env!
  "Throws an ex-info from a missing environment variable"
  [name]
  (throw-data
    "Missing environment variable: `%name~s`"
    {:type ::missing-env
     :name name}))

(defn missing-file!
  [path import]
  (throw-data
    "File not found: `%path~s`"
    {:type ::missing-file
     :path path
     :import import}))

(defn missing-imports!
  "Throws an ex-info from a list of import errors"
  [errors imported]
  (throw-data
    "Got errors while resolving imports"
    {:type ::missing-imports
     :errors errors
     :imported imported}))

(defn cyclic-import!
  "Throws an ex-info on finding a cyclic import"
  [import]
  (throw-data
    "Cyclic import"
    {:type ::cyclic-import
     :import import}))


;;
;; Serialization
;;

(defn unsupported-version-encoding!
  "Throws an ex-info if the version we are trying to encode with is not supported"
  [version versions]
  (throw-data
    "Serialization error: version not supported"
    {:type ::unsupported-version-encoding
     :supported-versions versions
     :current-version version}))

(defn unsupported-version-decoding!
  "Throws an ex-info if the version we are trying to decode with is not supported"
  [version versions]
  (throw-data
    "Deserialization error: version not supported"
    {:type ::unsupported-version-decoding
     :supported-versions versions
     :current-version version}))

(defn vector-too-short!
  "Throws an ex-info if the vector `e` has less than `n` elems"
  [e n]
  (throw-data
    "Deserialization error: the provided vector is too short"
    {:type ::vector-too-short
     :vec e
     :expected-count n
     :actual-count (count e)}))

(defn empty-val!
  "Throws an ex-info if some data was expected but it's not there"
  [e]
  (throw-data
    "Deserialization error: the provided expression is missing some data"
    {:type ::empty-val
     :expression e}))

(defn fn-label-mismatch!
  "Throws an ex-info if the label of the function being deserialized is not right"
  [label e]
  (throw-data
    "Deserialization error: label of the function cannot be `_`"
    {:type ::fn-label-mismatch
     :label label
     :expr e}))

(defn empty-list-must-have-type!
  "Throws an ex-info if the list `e` to be deserialized is empty and has an empty type"
  [e]
  (throw-data
    "Deserialization error: empty list with empty type cannot be decoded"
    {:type ::empty-list-must-have-type
     :expr e}))


;;
;; Typecheck
;;

(defn type-error!
  "Throws an ex-info on a Typecheck issue"
  ([typ ctx ex]
   (type-error! typ ctx ex {}))
  ([typ ctx ex more-data]
   (throw-data
     "Typecheck error: `%type~s`"
     {:type typ
      :context ctx
      :expression ex
      :more-data more-data})))

(defmacro typecheck-ex
  [typ]
  (let [kw (keyword (str *ns*) (str typ))
        sym (symbol (str typ "!"))]
    `(do
       (ex/derive ~kw ::typecheck)
       (def ~sym (partial type-error! ~kw)))))

(typecheck-ex annot-mismatch)
(typecheck-ex cant-add)
(typecheck-ex cant-and)
(typecheck-ex cant-eq)
(typecheck-ex cant-interpolate)
(typecheck-ex cant-list-append)
(typecheck-ex cant-multiply)
(typecheck-ex cant-neq)
(typecheck-ex cant-or)
(typecheck-ex cant-text-append)
(typecheck-ex combine-records-requires-record-type)
(typecheck-ex constructors-require-a-union-type)
(typecheck-ex duplicate-alternative)
(typecheck-ex field-annotation-mismatch)
(typecheck-ex field-collision)
(typecheck-ex field-mismatch)
(typecheck-ex handler-input-type-mismatch)
(typecheck-ex handler-not-a-function)
(typecheck-ex handler-output-type-mismatch)
(typecheck-ex if-branch-mismatch)
(typecheck-ex if-branch-must-be-term)
(typecheck-ex invalid-alternative-type)
(typecheck-ex invalid-field)
(typecheck-ex invalid-field-type)
(typecheck-ex invalid-handler-output-type)
(typecheck-ex invalid-input-type)
(typecheck-ex invalid-list-element)
(typecheck-ex invalid-list-type)
(typecheck-ex invalid-optional-element)
(typecheck-ex invalid-optional-type)
(typecheck-ex invalid-output-type)
(typecheck-ex invalid-predicate)
(typecheck-ex invalid-some)
(typecheck-ex list-append-mismatch)
(typecheck-ex mismatched-list-elements)
(typecheck-ex missing-field)
(typecheck-ex missing-handler)
(typecheck-ex missing-list-type)
(typecheck-ex missing-merge-type)
(typecheck-ex must-combine-a-record)
(typecheck-ex must-merge-a-record)
(typecheck-ex must-merge-union)
(typecheck-ex no-dependent-types)
(typecheck-ex not-a-function)
(typecheck-ex not-a-record)
(typecheck-ex record-mismatch)
(typecheck-ex record-type-mismatch)
(typecheck-ex type-mismatch)
(typecheck-ex unbound-variable)
(typecheck-ex untyped)
(typecheck-ex unused-handler)
