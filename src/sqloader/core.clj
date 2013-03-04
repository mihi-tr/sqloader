(ns sqloader.core (:use (csv-map core)))

(defn create-constraints
  "creates the constraints for a column"
  [column]
  (let [constraints {:foreign (fn [x] 
                                (format "REFERENCES %s(%s)"
                                        (get x :table)
                                        (get x :key)))
                     :unique (fn [x] "UNIQUE")}]
        (clojure.string/join " " (map (fn [x] (let [constraint (get x 0)
                           function (get x 1)
                           cns (get (get column 1) constraint)]
                       (if cns
                         (function cns))))
             constraints))))

(defn empty-string?
  "checks if a string is empty or just whitespaces"
  [strn]
  (= "" (. strn replace " " "")))

(defn column-definition 
  "creates the definition of a single column"
  [column]
  (let [name (get column 0)
        type (get (get column 1) :type)
        constraints (create-constraints column)]
    (if (empty-string? constraints) 
      (format "%s %s" name type)
      (format "%s %s %s"
              name type constraints))))

(defn column-definitions 
  "create the column definition string"
  [table-map]
  (let [column-map (get table-map :column-map)]
   (clojure.string/join ", " 
                        (map column-definition
                             column-map))))

(defn create-table 
  "generate the create-table string"
  [table-map]
  (let [table (get table-map :table)
        column-defs (if (get table-map :create-id)
                      (format "%s, %s" "id SERIAL PRIMARY KEY" 
                              (column-definitions table-map))
                      (column-definitions table-map))]
    (format "CREATE TABLE %s (%s);" table column-defs)))

(defn sqlescape
  [strn]
  (. strn replace "'" "\\'"))

(defn quotes?
  "returns whether a value should be quoted for the sql statement"
  [type]
  (let [needs #{"text" "varchar" "date" "time" "datetime"}]
    (contains? needs 
               (clojure.string/replace type
                                       #"\([0-9]+\)"
                                       ""))))

(defn get-quote-function 
  "returns the quote function"
  [type]
  (if (quotes? type)
    (fn [x] (format "'%s'" (sqlescape x)))
    (fn [x] (sqlescape x))))

"FIXME: THIS FUNCTION SMELLS BAD"

(defn get-value-function
  "returns the value function for a line"
  [column]
  (let [dbc (get column 0)
        column-def (get column 1)
        type (get column-def :type)
        csvc (get column-def :column)
        transform (get column-def :transform)
        create (get column-def :create)
        foreign (get column-def :foreign)
        qf (get-quote-function type)]
    (if foreign
      (let [key (get foreign :key)
            table (get foreign :table)
            fc (get foreign :column)]
        (fn [x] (qf "FIXME FOREIGN")))
      (if create
        (fn [x] (qf (create x)))
        (if transform
          (fn [x] (qf (transform (get x csvc))))
          (fn [x] (qf (get x csvc))))))))

(defn get-value-functions
  "return the value functions for a line"
  [column-map]
  (let [value-functions (map get-value-function column-map)]
    (fn [line] (map (fn [fx] (fx line)) value-functions))))
    

(defn insert-line
  "create the insert line"
  [table columns value-functions line]
  (let [values (clojure.string/join ", " (value-functions line))]
    (format "INSERT INTO \"%s\" (%s) VALUES (%s);" table 
            columns 
            values)))

(defn has-unique?
  "checks whether the column-map has unique columns"
  [column-map]
  (reduce (fn [x y] (or x y)) 
          (map (fn [x] 
                 (let [
                       unique (get (get x 1) :unique)]
                   (if unique (get x 0)
                       nil)))
                                   column-map)))

(defn insert-or-update-line
  "creates an insert or update line depending on whether
   there is already a line wiht the unique value or not"
  [table column-map columns value-functions line]
  "FIXME: IMPLEMENT"
  )

(defn insert-table
  "insert the table"
  [table-map]
  (let [column-map (get table-map :column-map)
        columns (keys column-map)
        table (get table-map :table)
        value-functions (get-value-functions column-map)
        filename (get table-map :file)
        csv (parse-csv (slurp filename))
        unique (has-unique? column-map)]
    (clojure.string/join "\n" (map 
                               (if unique
                                 (partial
                                  insert-or-update-line
                                  table
                                  column-map
                                  columns
                                  value-functions
                                  )
                                 (partial 
                                  insert-line
                                  table 
                                  columns 
                                  value-functions))
                               csv))))

(defn execute
  "execute a command or dry run"
  [line & {:as opts}]
  (if (get opts :dryrun)
    (do (println (format "%s" line)))
    (println (format "FIXME: execute %s" line))))














(defn -main 
  "main function"
  [& [:as args]]
  (let [known-args ["dryrun" "create"]
        filename (last args)]
    (load-file filename)
    (map execute (map create-table mapping))
  ))