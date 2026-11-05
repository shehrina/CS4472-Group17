BEGIN TRANSACTION;
CREATE TABLE accounts (
 username text NOT NULL,
 type integer,
 balance real,
 PRIMARY KEY(username, type));
INSERT INTO "accounts" VALUES('ktsiounis',2,2424.0);
INSERT INTO "accounts" VALUES('ktsiounis',0,791.485);
INSERT INTO "accounts" VALUES('ktsiounis',1,968.965);
CREATE TABLE cards (
 card text NOT NULL,
 username text NOT NULL,
 pin text NOT NULL,
 PRIMARY KEY(card));
INSERT INTO "cards" VALUES('4000000000000000','ktsiounis','5555');
CREATE TABLE clients (
 username text NOT NULL,
 first text NOT NULL,
 last text NOT NULL,
 student integer,
 PRIMARY KEY(username));
INSERT INTO "clients" VALUES('ktsiounis','Kostas','Tsiounis',1);
COMMIT;
