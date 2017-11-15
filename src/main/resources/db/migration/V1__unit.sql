CREATE EXTENSION IF NOT EXISTS citext;


DROP TABLE IF EXISTS forum;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS thread;

CREATE TABLE IF NOT EXISTS forum (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  "user" TEXT NOT NULL,
  slug CITEXT UNIQUE,
  posts INTEGER,
  threads INTEGER
);

CREATE TABLE IF NOT EXISTS thread (
  id SERIAL PRIMARY KEY,
  votes INTEGER,
  slug CITEXT UNIQUE ,
  forum TEXT NOT NULL ,
  author TEXT,
  title TEXT,
  message TEXT,
  created TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS users (
  fullname text,
  id SERIAL PRIMARY KEY,

  nickname  CITEXT COLLATE ucs_basic NOT NULL UNIQUE,
  email CITEXT NOT NULL UNIQUE,
  about text
);

CREATE TABLE IF NOT EXISTS post (
  id SERIAL PRIMARY KEY ,
  parent INTEGER DEFAULT 0,
  author TEXT,
  message TEXT,
  isedited BOOLEAN,
  forum TEXT,
  created TIMESTAMP DEFAULT now(),
  thread INTEGER ,
  path INTEGER[]
);

CREATE TABLE IF NOT EXISTS vote (
  id SERIAL PRIMARY KEY,
  nickname TEXT,
  threadID INTEGER,
  voice int,
  forum CITEXT
);




