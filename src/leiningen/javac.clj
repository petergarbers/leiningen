(ns leiningen.javac
  "Compile Java source files."
  (:use [leiningen.classpath :only [get-classpath]])
  (:require [lancet])
  (:import [java.io File]))

(def ^{:doc "Default options for the java compiler."} *default-javac-options*
  {:debug "false"
   :fork "true"
   :include-java-runtime "yes"
   :target "1.5"})

(defn- extract-javac-task
  "Extract a compile task from the given spec."
  [project [path & options]]
  (merge *default-javac-options*
         (:javac-options project)
         {:destdir (:compile-path project)
          :srcdir path
          :classpath (get-classpath project)}
         (apply hash-map options)))

(defn- extract-javac-tasks
  "Extract all compile tasks of the project."
  [project]
  (let [specs (:java-source-path project)]
    (map #(extract-javac-task project %)
         (if (string? specs) [[specs]] specs))))

(defn- run-javac-task
  "Compile the given task spec."
  [task-spec]
  (lancet/mkdir {:dir (:destdir task-spec)})
  (lancet/javac task-spec))

(defn javac
  "Compile Java source files."
  [project & [directory]]
  (doseq [task (extract-javac-tasks project)
          :when (or (nil? directory) (= directory (:srcdir task)))]
    (run-javac-task task)))

