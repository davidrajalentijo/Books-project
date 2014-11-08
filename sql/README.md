Books - SQL scripts
This folder contains the scripts to create the user associated to the books database (user: books, default password books) and the schema of the database. It also contains script to configure the Tomcat realm.

Installation

Connect as root to mysql, execute script booksdb-user.sql and realmdb-user.sql, then exit.
Connect as realm (password: realm) to mysql, execute script booksdb-schema.sql, then exit.
Connect as books (password: books) to mysql, execute script booksdb-schema.sql, then exit.