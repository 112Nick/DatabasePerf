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





CREATE UNIQUE INDEX IF NOT EXISTS u_nickname_uidx
  ON users (nickname);

CREATE UNIQUE INDEX IF NOT EXISTS u_email_uidx
  ON users (email);

CREATE INDEX IF NOT EXISTS u_nickname_email_idx
  ON users (nickname, email);

CREATE INDEX IF NOT EXISTS u_nickname_id_idx ON users (nickname, id);



CREATE INDEX IF NOT EXISTS f_slug_id_idx ON forum(slug, id);

CREATE INDEX IF NOT EXISTS p_parent_t_id_id_idx
  ON post (id, thread, parent);

CREATE INDEX IF NOT EXISTS p_parent_t_id_idx
  ON post (parent, thread);

CREATE INDEX IF NOT EXISTS p_forum_slug
  ON post (forum);

CREATE INDEX IF NOT EXISTS p_thread_id_id_idx
  ON post (thread, id);

CREATE INDEX IF NOT EXISTS p_thread_id_path_idx
  ON post (thread, path);

CREATE INDEX IF NOT EXISTS p_parent_t_id_path_idx
  ON post (parent, thread, path);

CREATE INDEX IF NOT EXISTS p_path_t_id_idx
  ON post (path, thread);

CREATE INDEX IF NOT EXISTS p_path_created_idx
  ON post (path, created);

CREATE UNIQUE INDEX IF NOT EXISTS v_user_id_thread_id_uindex
  ON vote (nickname, threadID);


CREATE UNIQUE INDEX IF NOT EXISTS t_slug_uindex
  ON thread (slug);

CREATE INDEX IF NOT EXISTS t_forum_id_idx
  ON thread (forum);

CREATE INDEX IF NOT EXISTS t_created_idx
  ON thread (created);

CREATE INDEX IF NOT EXISTS t_created_idx
  ON thread (forum, created);

CREATE INDEX IF NOT EXISTS p_id_f_slug_idx
  ON post (id, forum);

CREATE INDEX IF NOT EXISTS p_id_t_id_idx
  ON post (id, thread);

CREATE INDEX IF NOT EXISTS p_id_au_id_idx
  ON post (id, author);

