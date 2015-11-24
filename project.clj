(defproject keyword-search "0.1.0-SNAPSHOT"
  :description "Find entries in metadata db with keywords that matcha  given string, including all nested keywords."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.novemberain/monger "3.0.1"]]
  :main keyword-search.core
  :bin {:name "find-keyword"
        :bin-path "~/bin"})
