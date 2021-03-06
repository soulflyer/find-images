(defproject find-images "0.1.0-SNAPSHOT"
  :description "Find entries in metadata db with metadata that matches a given string, including all nested keywords."
  :url "https://github.com/soulflyer/find-images"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [com.novemberain/monger "3.0.1"]
                 [org.slf4j/slf4j-nop "1.7.12"]
                 [image-lib "0.1.0-SNAPSHOT"]]
  :main find-images.core
  :bin {:name "find-images"
        :bin-path "~/bin"})
