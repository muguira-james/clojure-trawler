(ns noob.core
  (:gen-class))


(require '[feedparser-clj.core :as rss])
(require '[clojure.edn :as edn])
(require '[clojure.java.io :as io])

;;
;; First setup and use the edn file to get configuration
;;
;; The next 4 forms:
;; feeds is the configuration
;; init-feeds pulls config in from disk
;; save-edn is my experimentation with creating a edn
;; read-end is experimentation with reading one from disk


;; 
;; set up the extensible data notation read (edn)
(def feeds (atom []))
;;
;; pull in the feeds from the file: fileName
(defn init-feeds [fileName]
  (reset! feeds
    (edn/read-string (slurp fileName))))
;;
;;
;; This is an example of what ann "EDN" file might look like
;; save a copy of the config to a file in resources dir
(defn save-edn 
  []
  (spit "resources/data.edn" (pr-str [{:source "cnn" :link "http://rss.cnn.com/rss/money_news_international.rss"}])))
;;
;; read back that edn from file and print 2 things
(defn read-edn
  [fileName]
  (let [z (edn/read-string (slurp fileName))]
    (println (first z))
    (println (get (first z) :link))
    (get (first z) :link)))


;; (defn -main [] (println "hello there"))
;;
;; cnn is a test map to learn with

(def cnn '({:source "hello", :link "bye"} {:source "hello2", :link "bye2"}))


;; first get the rss feed in a lazy-var.  
(def rss (rss/parse-feed (str "http://rss.cnn.com/rss/money_news_international.rss")))
;;
;; move over to point to just the entries in the RSS feed
(def money (get rss :entries))

;;
;; pall -> print all items in a map
(defn pall [m]
  (if (not (empty? m))
    (do
      (println (str "item: " (first m)))
      (recur (rest m)))))

;;
;; pop-title -> print just the title from the map :entries field
(defn pop-title [m]
  (println (str "item: " (get (first m) :title))))
;;
;; print the title from a map, preface with the "source)
(defn pop-title-2 [s m]
  (println (str s ": " (get (first m) :title))))
;;
;; just return the title
(defn get-title [m]
  (get m :title))
;;
;; pop-link -> print just the link from the map :entries
(defn ppop-link [m]
  (println (str "link: " (get (first m) :link))))
;;
;; just return the link
(defn pop-link [m]
  (get m :link))

;;
;; recurse through and print all the titles
(defn pall-title [m]
  (if (not (empty? m))
    (do
      (pop-title m)
      (recur (rest m)))))
;;
;; loop through a map, printing the "source: title"
(defn pall-title-2 [s m]
  (if (not (empty? m))
    (do
      (pop-title-2 s m)
      (recur s (rest m)))))
;;
;; recurse through and print all the links
(defn pall-link [m]
  (if (not (empty? m))
    (do
      (pop-link m)
      (recur (rest m)))))

        
;;
;; print news in the config file
(defn print-4
  [col]
  (if (not (empty? col))
    (do 
      (pall-title (get (rss/parse-feed (pop-link (first col))) :entries))
      (recur (rest col)))))
        
;;
;; pretty print all new in "source: title" format
;;
;; this assumes the collection a map (not an atom)
;;
(defn print-all-news [col]
  (if (not (empty? col))
    (let 
      [source (get (first col) :source)
       link (pop-link (first col))]
      (do
        (pall-title-2 source (get (rss/parse-feed link) :entries))
        (recur (rest col))))))
  
;;---------------------------------------------------------
;;
(defn -main []
  (do
    (init-feeds "resources/data.edn")
    (print-all-news @feeds)))

