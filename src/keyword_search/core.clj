(ns keyword-search.core
  (:require [clojure.set :as set]
            [clojure.tools.cli :refer :all]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all])
  (:gen-class))

(def cli-options
  [["-s" "--sub" "list all the sub keywords (recursively)"]
   ["-r" "--recursive" "Finds all the matches for the given keyword and all of its sub keywords"]
   ["-c" "--count" "Counts the results"]
   ["-d" "--database DATABASE" "specifies database to use"
    :default "soulflyer"]
   ["-i" "--image-collection IMAGE-COLLECTION" "specifies the image collection"
    :default "images"]
   ["-k" "--keyword-collection KEYWORD-COLLECTION" "specifies the keyword collection"
    :default "keywords"]
   ["-m" "--metadata-field METADATA-FIELD" "field to be searched"
    :default :Keywords]
   ["-h" "--help"]])

(defn find-images
  "Searches database collection for entries where the given field is (or contains) the given value"
  [database image-collection field value]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (mc/find-maps db image-collection {field {$regex value}} [:Year :Month :Project :Version])))

(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  [database keyword-collection given-keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)
        keyword-entry (first (mc/find-maps db keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found: " given-keyword ))
      (if (= 0 (count (:sub keyword-entry)))
        (conj '() given-keyword)
        (flatten (conj
                  (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
                  given-keyword))))))

(defn image-path
  "return a string containing the year/month/project/version path of an image"
  [image-map]
  (str (:Year image-map) "/"
       (:Month image-map) "/"
       (:Project image-map) "/"
       (:Version image-map) ".jpg"))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]

    (cond
     (:help options)
     (println (str "Usage:\nkeyword-search [options] keyword\n\nvoptions:\n" summary))

     (:sub options)
     (doall
      (map
       println
       (find-sub-keywords (:database options) (:keyword-collection options) (first arguments))))

     (:count options)
     (println
      (str "Found "
           (count (find-images (:database options) (:image-collection options) (:metadata-field options) (first arguments)))
           " images."))

     (:recursive options)
     (let [keywords (find-sub-keywords (:database options) (:keyword-collection options) (first arguments))]
       (doall
        (map
         println
         (map
          image-path
          (flatten
           (map
            #(find-images (:database options)
                          (:image-collection options)
                          (:metadata-field options)
                          % )
            keywords))))))

     :else
     (doall
      (map
       println
       (map
        image-path
        (find-images
         (:database options)
         (:image-collection options)
         (:metadata-field options)
         (first arguments))))))))
