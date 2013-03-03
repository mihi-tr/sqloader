(ns sqloader.core (:use '(csv-map core)))

(defn column-definitions 
  "create the column definition string"
  [table-map]
  (let [column-map (get table-map :column-map)]
   (clojure.string/join ", " 
                        (map (fn [x] 
                               (format "%s %s" 
                                       (get x 0) 
                                       (get (get x 1) :type)))
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

