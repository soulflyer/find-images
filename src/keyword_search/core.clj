(ns keyword-search.core
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all]))

(defn find-keyword
  "Searches database collection for entries containing keyword in the keyword array"
  [database keyword-collection image-collection keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    ))
