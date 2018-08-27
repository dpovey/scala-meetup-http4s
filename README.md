# scala-meetup-http4s

This project demonstrates how to build a simple REST service using the Typelevel stack of:
  * http4s
  * Cats Effect
  * Doobie
  * FS2
  
It also demonstrates unit and integration testing using:
  * Specs2
  * Scalamock

You will need a PostgreSQL database (by default called meetup_, load the DDL given in ddl/books.sql

```
psql -U postgres meetup < ddl/books.sql
```

To start the service do:

```
sbt run
```

