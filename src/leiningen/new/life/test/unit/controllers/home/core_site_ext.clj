(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query]]))

(def collection "people")

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(def response [{:name     "Anonomous User"
                :location "Timbuktu"}])

(facts "for each call to index"
  (let [mongo-component {:db ..db..}
        home-component {:mongodb mongo-component}]
    (fact "the response has a 200 status code"
      (home home-component) => (status? 200)
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "the response has a text/html content type"
      (home home-component) => (content-type? "text/html")
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "the response model is well formed"
      (let [response (home home-component)]
        (get-in response [:body :model])) => (contains {:people [{:name     "Anonomous User"
                                                                  :location "Timbuktu"}]})
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "the correct view is returned for a first time visitor"
      (let [response (home home-component)]
        (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
      (provided
        (find-by-query mongo-component collection {}) => nil))

    (fact "a view function is returned"
      (let [response (home home-component)]
        (get-in response [:body :view :fn])) => fn?
      (provided
        (find-by-query mongo-component collection {}) => response))))
