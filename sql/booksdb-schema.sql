drop database if exists booksdb;
create database booksdb;

use booksdb;
create table users (
	username	varchar(20) not null primary key,
	userpass	char(32) not null,
	name		varchar(70) not null,
	email		varchar(255) not null
);


create table authors (
authorid		int not null auto_increment primary key,
	name	varchar(20) not null 
);


create table user_roles (
	username			varchar(20) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);

create table books(
	bookid		int  auto_increment primary key,
	title		varchar(80) not null,
	author      varchar(80) not null,
	language	varchar(15) not null,
	edition		varchar(20) not null,
	editiondate	date,
	printdate	date,
	editorial	varchar(20) not null,
	  Last_modified timestamp

);
create table books_authors (
	bookid int not null,
	authorid int not null,
	foreign key(authorid) references authors(authorid),
	foreign key(bookid) references books(bookid),
	primary key (bookid, authorid)
);

create table reviews(
reviewid	int not null auto_increment unique,
	username	varchar(20) not null,
	dateupdate	date ,
	text		varchar(500),
	bookid		int not null,
	  Last_modified timestamp,
	foreign key (username) references users(username),
	foreign key (bookid) references books(bookid),
	primary key (bookid,username)
);