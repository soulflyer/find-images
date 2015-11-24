(ns keyword-search.core
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all]))

(defn find-images-with-keyword
  "Searches database collection for entries containing keyword in the keyword array"
  [database keyword-collection image-collection keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (do
      (println (str "database:   " database))
      (println (str "collection: " keyword-collection))
      (println (str "keyword:    " keyword))
      (mc/find-maps db keyword-collection {:_id keyword}))
    ))
(defn -main [& args]
  (let [database (first args)
        keyword-collection (second args)
        image-collection (nth args 2)
        keyword (nth args 3)]
    (find-images-with-keyword database keyword-collection image-collection keyword)))
