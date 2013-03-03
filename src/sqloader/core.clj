(ns sqloader.core (:use '(csv-map core)))

(defn column-definition 
  "creates the definition of a single column"
  [column]
  (let [name (get column 0)
        type (get (get column 1) :type)
        foreign (get (get column 1) :foreign)]
    (if foreign
      (format "%s %s REFERENCES %s(%s)"
              name type (get foreign :table) (get foreign :key))
      (format "%s %s" name type))))

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

