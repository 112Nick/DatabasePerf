CREATE EXTENSION IF NOT EXISTS citext;


-- DROP TABLE IF EXISTS forum;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS post;
-- DROP TABLE IF EXISTS vote;
-- DROP TABLE IF EXISTS thread;
-- DROP TABLE IF EXISTS forum_users;


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
  forumID INTEGER,
  author TEXT,
  title TEXT,
  message TEXT,
  created TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  fullname text,
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
  created TIMESTAMP WITH TIME ZONE ,

  thread INTEGER ,
  path INTEGER[]
);

CREATE TABLE IF NOT EXISTS vote (
  id SERIAL PRIMARY KEY,
  userId INTEGER,
  nickname TEXT,
  threadID INTEGER,
  voice int,
  forum CITEXT
);

CREATE TABLE IF NOT EXISTS forum_users (
  id SERIAL PRIMARY KEY,
  --userId INTEGER,
  fullname text,
  nickname  CITEXT COLLATE ucs_basic NOT NULL,
  email CITEXT,
  about text,
  forumID INTEGER,
  UNIQUE (forumID, nickname)
);


CREATE INDEX index1 ON post (thread,path,id); -- true
CREATE INDEX index2 ON post (thread, parent, path, id); -- +-
CREATE INDEX index3 ON post (parent, thread, id); -- -+
CREATE INDEX index4 ON post (forum, id);-- +

-- -- --All works
CREATE INDEX IF NOT EXISTS index_vote_id ON vote (id);
CREATE INDEX IF NOT EXISTS index_vote_id_nickname ON vote (id, nickname);
CREATE INDEX IF NOT EXISTS index_vote_threadID_nickname ON vote (threadID, nickname);
-- -- --
-- --All works
CREATE INDEX IF NOT EXISTS index_thread_slug ON thread (LOWER(slug));
CREATE INDEX IF NOT EXISTS index_thread_forum ON thread (LOWER(forum));
CREATE INDEX IF NOT EXISTS index_thread_forum ON thread (created);
CREATE INDEX IF NOT EXISTS index_thread_forum_created ON thread (LOWER(forum), created);
-- --
-- --All works
CREATE UNIQUE INDEX IF NOT EXISTS index_user_nickname ON users (LOWER(nickname));
CREATE UNIQUE INDEX IF NOT EXISTS index_user_email ON users (LOWER(email));
CREATE INDEX IF NOT EXISTS index_user_nickname_email ON users (nickname, email);
CREATE INDEX IF NOT EXISTS index_user_nickname_id ON users (nickname, id);
-- -- --







