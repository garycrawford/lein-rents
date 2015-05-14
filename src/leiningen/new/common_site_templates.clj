(ns leiningen.new.common-site-templates
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [clostache.parser :refer [render]]
            [leiningen.new.db-template :refer :all]
            [leiningen.new.mongo-template :refer :all]))

(defn site-vals
  [ns-name]
  {:ns-name ns-name
   :path (string/replace ns-name "-" "_")
   :docker-name (string/replace ns-name "-" "")
   :dockerised-svr (str (->PascalCase ns-name) "DevSvr")})

(defn dev-profile
  [ns-name args]
  (let [lines (db-environment-variables args)
        template (string/join "\n             " lines)]
    (render template (site-vals ns-name))))

(defn project-deps
  [args]
  (->> (db-dependencies args)
       (string/join "\n                ")))

(defn healthcheck-list-template
  []
  (->> ["<ul>"
        "{{#healthchecks}}"
        "  <li>{{name}}: {{status}}</li>"
        "{{/healthchecks}}"
        "</ul>"]
       (string/join \newline)))

(defn page-template
  []
  (->> ["{{>header}}"
        "  <div class=\"default\">"
        "    {{{content}}}"
        "  </div>"
        "{{>footer}}"]
       (string/join \newline)))

(defn person-list
  []
  (->> ["<ul>"
        "{{#people}}"
        "    <li>{{name}},"
        "        {{location}}"
        "        <a href=\"/person/{{id}}/update\">edit</a>"
        "        <a href=\"/person/{{id}}/delete\">delete</a>"
        "    </li>"
        "{{/people}}"
        "</ul>"]
       (string/join \newline)))

(defn add-person
  []
  (->> ["<form action=\"person\" method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <label for=\"name\">Name:</label>"
        "  <input type=\"text\" id=\"name\" name=\"name\"><br>"
        "  <label for=\"location\">Location:</label>"
        "  <input type=\"text\" id=\"location\" name=\"location\">"
        "  <input type=\"submit\" id=\"submit\" value=\"Submit\">"
        "</form>"]
       (string/join \newline)))

(defn update-person
  []
  (->> ["<form method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <input type=\"hidden\" name=\"id\" value=\"{{id}}\">"
        "  <label for=\"name\">Name:</label>"
        "  <input type=\"text\" id=\"name\" name=\"name\" value=\"{{name}}\"><br>"
        "  <label for=\"location\">Location:</label>"
        "  <input type=\"text\" id=\"location\" name=\"location\" value=\"{{location}}\">"
        "  <input type=\"submit\" value=\"Edit\">"
        "</form>"]
       (string/join \newline)))

(defn delete-person
  []
  (->> ["<form method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <input type=\"hidden\" name=\"id\" value=\"{{id}}\">"
        "  Are you sure you want to delete {{name}}, {{location}}?<br>"
        "  <input type=\"submit\" value=\"Yes\">"
        "</form>"]
       (string/join \newline)))

(defn introduction
  []
  (->> ["<h1>Care to add yourself to the list of people...?</h1>"
        ""
        "{{>add-person}}"
        ""
        "{{>person-list}}"]
       (string/join \newline)))

(defn site-var-map
  [ns-name options]
  {:healthcheck-list-template (healthcheck-list-template)
   :page-template (page-template)
   :add-person (add-person)
   :update-person (update-person)
   :delete-person (delete-person)
   :person-list (person-list)
   :introduction (introduction)
   :project-deps (project-deps options)
   :dev-profile (dev-profile ns-name options)})
