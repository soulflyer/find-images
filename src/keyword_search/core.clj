(ns keyword-search.core
  (:require [clojure.set :as set]
            [clojure.tools.cli :refer :all]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all])
  (:gen-class))

(def cli-options
  [["-s" "--sub" "list all the sub keywords (recursively)"]
   ["-h" "--help"]])

(defn find-images-with-keyword
  "Searches database collection for entries containing keyword in the keyword array"
  [database image-collection given-keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)
        images (mc/find-maps db image-collection {:keywords given-keyword})]
    ))

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
        database (first arguments)
        keyword-collection (second arguments)
        image-collection (nth arguments 2)
        given-keyword (nth arguments 3)]

    (cond
     (:help options)
     (println (str "Usage:\nkeyword-search [options] database keyword-coll image-coll keyword\n\noptions:\n" summary))

     (:sub options)
     (find-sub-keywords database keyword-collection given-keyword)

     :else
     (println "default option here"))))
