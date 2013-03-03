(def mapping [
  {:table "spaceship"
    :create-id true
    :column-map {
      "name" {:column "Name" :type "varchar(150)" :unique true}
      }}
  {:table "crew"
   :file "doc/sample-file.doc"
   :set ["DateStyle YMD"]
   :create-id true
   :column-map {
    "name" {:column "Character Name" :type "varchar(150)"}
    "role" {:column "Character Role" :type "varchar(150)"}
    "ship" {:foreign {:table "spaceship" :key "id" :column "Spaceship"}
    :type "integer"}
    "age" {:column "Age" :type "integer"}
    "age_class" {:column "Age" :type "varchar(10)" :transform 
      (fn [age]
        (if (> age 60) "old" (if (< age 20) "young" "adult")))}
     "adressed_as" {:type "varchar(150)" :create (fn [x] 
        (str (get x "Character Role") " " (get x "Character Role")))}
        }}])
    
