(ns keyword-search.core
  (:require [clojure.set :as set]
            [clojure.tools.cli :refer :all]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all])
  (:gen-class))

(def cli-options
  [["-s" "--sub" "list all the sub keywords (recursively)"]
   ["-c" "--count" "Counts the results"]
   ["-d" "--database DATABASE" "specifies database to use"
    :default "soulflyer"]
   ["-i" "--image-collection IMAGE-COLLECTION" "specifies the image collection"
    :default "images"]
   ["-k" "--keyword-collection KEYWORD-COLLECTION" "specifies the keyword collection"
    :default "keywords"]
   ["-m" "--metadata-field" "field to be searched"
    :default :Keywords]
   ["-h" "--help"]])

(defn find-images
  "Searches database collection for entries where the given field is (or contains) the given value"
  [database image-collection field value]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (mc/find-maps db image-collection {field value} [:_id])))

(defn find-sub-keywords
  "given a keyword entry returns all the sub keywords"
  [database keyword-collection given-keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)
        keyword-entry (first (mc/find-maps db keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found: " given-keyword ))
      (if (= 0 (count (:sub keyword-entry)))
        given-keyword
        (flatten (conj
                  (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
                  given-keyword))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        ;;database (first arguments)
        ;;keyword-collection (second arguments)
        ;;image-collection (nth arguments 2)
        given-keyword (first arguments)
        ]

    (cond
     (:help options)
     (println (str "Usage:\nkeyword-search [options] database keyword-coll image-coll keyword\n\nvoptions:\n" summary))

     (:sub options)
     (find-sub-keywords (:database options) (:keyword-collection options) given-keyword)

     (:count options)
     (println
      (str "Found "
           (count (find-images (:database options) (:image-collection options) (:metadata-field options) given-keyword))
           " images."))

     :else
     (find-images (:database options) (:image-collection options) (:metadata-field options) given-keyword)
     ;;(println "default case")
     )))
