(ns find-images.core
  (:require [clojure.tools.cli :refer :all]
            [image-lib.core    :refer [find-images
                                       find-images-containing
                                       find-sub-keywords
                                       image-path]])
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

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]

    (cond
     (:help options)
     (println (str "Usage:\nfind-images [options] keyword\n\nvoptions:\n" summary))

     (:sub options)
     (doall
      (map
       println
       (find-sub-keywords
        (:database options)
        (:keyword-collection options) (first arguments))))

     (:count options)
     (println
      (str "Found "
           (count
            (find-images
             (:database options)
             (:image-collection options)
             (:metadata-field options)
             (first arguments)))
           " images."))

     (:recursive options)
     (let [keywords
           (find-sub-keywords
            (:database options)
            (:keyword-collection options) (first arguments))]
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
        (find-images-containing
         (:database options)
         (:image-collection options)
         (:metadata-field options)
         (first arguments))))))))
