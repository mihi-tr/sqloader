(ns sqloader.core (:use '(csv-map core)))

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
    
(defn column-definition 
  "creates the definition of a single column"
  [column]
  (let [name (get column 0)
        type (get (get column 1) :type)
        constraints (create-constraints column)]
      (format "%s %s %s"
              name type constraints)))

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

