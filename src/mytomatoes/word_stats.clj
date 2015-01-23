(ns mytomatoes.word-stats
  (:require [clojure.string :as str]
            [mytomatoes.storage :as st]))

(defonce ^:private word-stats (atom {}))

(defn split-into-words [ss]
  (->> ss
       (mapcat #(str/split % #"\W+"))
       (map str/lower-case)
       (remove #(< (count %) 2))
       (set)))

(defn words-for-account [db id]
  (->> (st/get-tomatoes db id)
       (map :description)
       (split-into-words)))

(defn- track-word [stats word account-id]
  (update-in stats [word] (fn [s] (if s (conj s account-id) #{account-id}))))

(defn- track-words [stats words account-id]
  (reduce #(track-word %1 %2 account-id) stats words))

(defn- populate-from-account! [db account-id]
  (swap! word-stats track-words (words-for-account db account-id) account-id)
  nil)

(defn- populate! [db]
  (doseq [i (range 1 65341)]
    (when (= 0 (mod i 100)) (prn "And we're up to" i))
    (when (= 0 (mod i 1000))
      (prn "Writing out word-stats.edn")
      (spit "word-stats.edn" (prn-str @word-stats)))
    (populate-from-account! db i)))

(comment ;; find common words
  (->> @word-stats
       (map (fn [[w s]] [w (count s)]))
       (remove (fn [[w c]] (< c 500)))
       (map first)))

(def common-words
  #{"books" "total" "sources" "table" "abstract" "iv" "look" "map" "more"
    "sent" "sis" "lot" "ready" "essay" "parts" "lukemista" "going" "hw"
    "went" "lab" "desk" "org" "lis" "free" "50" "thesis" "got" "tomatoes"
    "34" "st" "slide" "organized" "loppuun" "created" "book" "development"
    "law" "see" "studies" "nearly" "cover" "begin" "mail" "min" "hk" "thing"
    "ii" "using" "outlined" "en" "la" "science" "progress" "downloaded"
    "list" "google" "are" "story" "didn" "right" "back" "calendar" "media"
    "started" "made" "word" "very" "next" "reading" "testing" "typed" "22"
    "teksti" "which" "26" "fix" "online" "call" "paper" "body" "of" "this"
    "decided" "model" "bio" "talk" "learning" "after" "discussion" "starting"
    "up" "al" "off" "food" "web" "mit" "copy" "find" "three" "tasks" "issue"
    "literature" "references" "bit" "28" "pg" "60" "typing" "figure" "14"
    "review" "two" "pages" "ll" "part" "checking" "etc" "printed" "cleaning"
    "ch" "para" "not" "ss" "hours" "put" "today" "readings" "group" "it"
    "over" "began" "class" "sections" "far" "teht" "emailed" "health" "job"
    "send" "el" "also" "by" "intro" "try" "sentences" "different" "plus"
    "design" "long" "structure" "something" "sorted" "reports" "fb" "is" "30"
    "21" "points" "method" "looked" "few" "like" "tables" "key" "interview"
    "process" "hard" "away" "form" "found" "methods" "adding" "project" "forms"
    "why" "researching" "doing" "second" "people" "focus" "good" "admin"
    "onto" "article" "together" "complete" "about" "80" "info" "website" "you"
    "guide" "33" "new" "management" "20" "valmis" "than" "chart" "lukua"
    "video" "artikkelin" "where" "looking" "completed" "small" "run" "stuff"
    "results" "just" "third" "email" "for" "post" "taking" "past" "read"
    "drafting" "files" "19" "revision" "should" "personal" "take" "searching"
    "tomorrow" "outline" "lecture" "fixed" "rest" "my" "continued" "17"
    "figured" "words" "update" "assignment" "note" "prepared" "materials"
    "25" "setting" "conclusion" "quotes" "2011" "short" "again" "tein" "laundry"
    "sort" "corrections" "couple" "computer" "draft" "summary" "formatting"
    "better" "15" "sheet" "42" "feedback" "document" "rough" "most" "grading"
    "ideas" "business" "office" "ei" "writing" "schedule" "practice" "lunch"
    "can" "power" "pi" "section" "math" "need" "main" "did" "was" "coding"
    "luin" "100" "that" "reviewing" "if" "check" "same" "another" "edit"
    "application" "make" "18" "citations" "36" "blog" "go" "half" "quiz"
    "don" "had" "overview" "12" "general" "student" "researched" "what" "an"
    "minutes" "nothing" "previous" "13" "dissertation" "nyt" "music"
    "introduction" "even" "or" "editing" "think" "de" "27" "finish" "start"
    "moved" "case" "shit" "have" "articles" "study" "problems" "am" "text"
    "question" "making" "pp" "ppt" "so" "them" "conference" "24" "things"
    "1st" "whole" "wrote" "changes" "almost" "presentation" "met" "etsin" "on"
    "week" "reviewed" "35" "break" "2010" "paragraphs" "paragraph" "statement"
    "old" "filled" "content" "graded" "site" "but" "getting" "methodology"
    "facebook" "self" "time" "policy" "added" "internet" "moving" "when"
    "syllabus" "searched" "excel" "task" "be" "se" "fixing" "out" "3rd"
    "watched" "tried" "200" "dinner" "teaching" "studying" "non" "continue"
    "mostly" "38" "and" "edits" "order" "ja" "final" "theory" "reference"
    "planning" "do" "last" "history" "myself" "proposal" "move" "source" "down"
    "type" "home" "app" "phone" "working" "too" "one" "comments" "revisions"
    "between" "et" "39" "pre" "talked" "everything" "state" "work" "version"
    "finding" "planned" "big" "argument" "updating" "how" "help" "other" "from"
    "submitted" "mails" "outlining" "library" "mind" "chapters" "really" "lots"
    "tests" "37" "answered" "trying" "no" "print" "took" "cv" "called" "revise"
    "ate" "luku" "social" "viel" "updated" "plan" "11" "with" "around" "actually"
    "response" "add" "file" "now" "set" "some" "will" "information" "all"
    "clean" "lesson" "aloitin" "worked" "re" "45" "then" "room" "thought"
    "through" "notes" "emails" "system" "exam" "ok" "well" "report" "session"
    "printing" "32" "prep" "chapter" "issues" "sorting" "problem" "much" "vs"
    "diss" "beginning" "papers" "research" "day" "plans" "survey" "analysis"
    "letters" "page" "before" "only" "still" "putting" "training" "way" "course"
    "to" "bibliography" "cleaned" "examples" "write" "into" "cut" "thinking"
    "cards" "little" "written" "distracted" "16" "use" "search" "revising"
    "edited" "data" "background" "get" "know" "sen" "art" "10" "tomato"
    "kirjoitin" "organization" "questions" "organizing" "checked" "we"
    "finishing" "as" "code" "40" "english" "done" "powerpoint" "life" "end" "me"
    "31" "at" "docs" "point" "stats" "meeting" "doc" "letter" "lit" "left"
    "description" "finally" "the" "though" "journal" "ty" "test" "yesterday"
    "slides" "change" "school" "sample" "23" "first" "there" "homework" "in"
    "29" "finished" "revised" "topic" "2nd" "studied" "organize" "material" "idea"})
