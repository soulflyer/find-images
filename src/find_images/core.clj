(ns find-images.core
  (:require [clojure.tools.cli :refer :all]
            [image-lib.core    :refer [find-images
                                       find-images-containing
                                       find-sub-keywords
                                       image-path]])
  (:gen-class))

(def cli-options
  [["-r" "--recursive" "Finds all the matches for the given keyword and all of its sub keywords"]
   ["-c" "--count" "Counts the results"]
   ["-x" "--regex" "Matches a regex pattern. Combining with -r may not give what you expect."]
   ["-d" "--database DATABASE" "specifies database to use"
    :default "soulflyer"]
   ["-i" "--image-collection IMAGE-COLLECTION" "specifies the image collection"
    :default "images"]
   ["-k" "--keyword-collection KEYWORD-COLLECTION" "specifies the keyword collection"
    :default "keywords"]
   ["-m" "--metadata-field METADATA-FIELD" "field to be searched"
    :default :Keywords]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        find-function (if (:regex options) find-images-containing find-images)
        keywords (if (:recursive options)
                   (find-sub-keywords (:database           options)
                                      (:keyword-collection options)
                                      (first             arguments))
                   (vector            (first             arguments)))
        images (map image-path
                    (flatten
                     (map #(find-function (:database         options)
                                          (:image-collection options)
                                          (:metadata-field   options)
                                          % )
                          keywords)))]

    (cond
      (:help options)
      (println (str "Usage:\nfind-images [options] keyword\n\nvoptions:\n" summary))

      (:count options)
      (println
       (str "Found " (count images) " images."))

      :else
      (doall (map println images )))))
