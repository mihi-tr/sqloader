# sqloader

A Clojure library and client to load csv files into sql databases - based
on mappings

## Usage

```
lein run <settings> 
```

to use as a library add the following to leiningen
```clojure
[sqloader "0.1.0-SNAPSHOT"]
```

Use it:

```clojure
(use 'sqloader.core)
(sqload sql [{:table "foo"
  :file "bar.csv"
  :map {
    "foo" {:column "bar" :type "varchar(100)"}
    }
    }])

## Concepts

Sqloader will create tables in a specified sql database and load data from
csv files into the database. Generally it produces one table per file -
however this can be changed quite easily though the configuration.

Mappings is a clojure vector containing maps that define how columns in the
csv and the sql correspond with each other. 

## License

Copyright © 2013 Michael Bauer

Distributed under the Eclipse Public License, the same as Clojure.
