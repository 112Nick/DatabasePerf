package project.DAO;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import project.models.Post;
import project.models.Thread;
import project.models.Vote;
import project.utils.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

@Service
@Transactional
public class ThreadDAO {
    private final JdbcTemplate template;
    private final PostDAO postDAO;
    public ThreadDAO(JdbcTemplate template, PostDAO postDAO) {
        this.template = template;
        this.postDAO = postDAO;
    }

    public Integer count() {
        return template.queryForObject(
                "SELECT COUNT(*) FROM thread;",
                new Object[]{}, Integer.class);
    }

    public Response<Vote> getVote (String nickname,int uid, int threadID) {
        Response<Vote> result = new Response<>();
        try {
            final Vote vt =  template.queryForObject(
                    "SELECT * FROM vote WHERE userID = ? AND threadID = ?",
                    new Object[]{uid, threadID},  voteMapper);
            result.setResponse(vt, HttpStatus.OK);
            return result;
        }
        catch (DataAccessException e) {
            result.setResponse(new Vote(), HttpStatus.NOT_FOUND);
            return result;
        }
    }

    public Response<List<Post>> getPosts(Thread thrd, Integer limit, Integer since, String sort, Boolean desc) {
        List<Object> tempObj = new ArrayList<>();
        Integer[] myarr = new Integer[2];

        final StringBuilder postQuery = new StringBuilder( "SELECT * FROM post WHERE thread = ? " );
        tempObj.add(thrd.getId());
        myarr[0] = thrd.getId();
        if (sort == null) {
            sort = "flat";
        }
        Response<List<Post>> result = new Response<>();
        List<Post> posts = new ArrayList<>();
        switch (sort) {

            case "flat":
                if (since != null) {
                    if (desc != null && desc) {
                        postQuery.append(" AND id < ? ");
                    } else {
                        postQuery.append(" AND id > ? ");
                    }
                    tempObj.add(since);
                }
                postQuery.append(" ORDER BY created ");
                if (desc != null && desc) {
                    postQuery.append(" DESC, id DESC ");
                }
                else {
                    postQuery.append(" , id ");

                }
                if (limit != null) {
                    postQuery.append(" LIMIT ? ");
                    tempObj.add(limit);
                }
                break;

            case "tree":
                if (since != null) {
                    if (desc != null && desc) {
                        postQuery.append(" AND path < (SELECT path FROM post WHERE id = ?) ");
                    } else {
                        postQuery.append(" AND path > (SELECT path FROM post WHERE id = ?) ");
                    }
                    tempObj.add(since);
                }
                postQuery.append(" ORDER BY path ");
                if (desc != null && desc) {
                    postQuery.append(" DESC, id DESC ");
                }
                else {
                    postQuery.append(" , id ");
                }
                if (limit != null) {
                    postQuery.append (" LIMIT ?");
                    tempObj.add(limit);
                }
                break;

            case "parent_tree":
                Response<List<Post>> parentsArray = getParents(thrd, since, limit, desc);

                if (parentsArray.getStatus() == HttpStatus.NOT_FOUND) {
                    return parentsArray;
                }
                else {
                        postQuery.append("AND path[1] = ?");
                        postQuery.append(" ORDER BY path ");
                        if (desc != null && desc) {
                            postQuery.append(" DESC, id DESC ");
                        }
                        else {
                            postQuery.append(" , id ");
                        }
                    try {
                            for (Post cur : parentsArray.getBody()) {
                                myarr[1] = cur.getId();
                                posts.addAll(template.query(postQuery.toString(), myarr, postMapper));

                            }
                            result.setResponse(posts, HttpStatus.OK);
                            return result;
                        } catch (DataAccessException e) {
                        result.setResponse(posts, HttpStatus.NOT_FOUND);
                            return result;
                        }
                }
        }
        try {
            posts = template.query(postQuery.toString(),
                    tempObj.toArray(), postMapper);
            result.setResponse(posts, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(posts, HttpStatus.NOT_FOUND);
            return result;
        }
    }

    private Response<List<Post>> getParents(Thread thrd, Integer since, Integer limit, Boolean desc) {
        List<Object> tempObj = new ArrayList<>();
        final StringBuilder postQuery = new StringBuilder( "SELECT * FROM post WHERE thread = ? AND parent = 0 " );
        tempObj.add(thrd.getId());
        if (since != null) {
            if (desc != null && desc) {
                postQuery.append(" AND path[1] < (SELECT path[1] FROM post WHERE id = ?)  ");
            } else {
                postQuery.append(" AND path[1] > (SELECT path[1] FROM post WHERE id = ?)  ");
            }
            tempObj.add(since);
        }
        postQuery.append("ORDER BY id ");
        if (desc != null && desc) {
            postQuery.append(" DESC ") ;
        }
        if (limit != null) {
            postQuery.append(" LIMIT ? ");
            tempObj.add(limit);
        }
        final List<Post> res = template.query(
                postQuery.toString(),
                tempObj.toArray(), postMapper);

        Response<List<Post>> result = new Response<>();
        if (res.isEmpty()) {
            result.setResponse(res, HttpStatus.OK);
            return result;
        }
        result.setResponse(res, HttpStatus.OK);
        return result;
    }

    private void threadsInc(String slug) {
        try {
            String sql = "UPDATE forum SET threads = threads + 1 WHERE LOWER (slug) = lower(?)";
            template.update(sql,slug);
        } catch (DuplicateKeyException e) {

        } catch (DataAccessException e ) {

        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Thread> createThread(Thread body) {
        Response<Thread> result = new Response<>();
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder(); //here

        try {
            //String sql = "INSERT INTO thread(votes, slug, author, forum, title, message, created) VALUES(?,?,?,?,?,?,?::TIMESTAMPTZ) returning id";
            //int id = template.update(sql,body.getVotes(),body.getSlug(),body.getAuthor(),body.getForum(),body.getTitle(),body.getMessage(),body.getCreated(), Statement.RETURN_GENERATED_KEYS);

                        template.update(con -> {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO thread(votes, slug, author, forum, title, message, created)"
                                + " VALUES(?,?,?,?,?,?,?::TIMESTAMPTZ) "+" returning id",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setInt(1, body.getVotes());
                statement.setString(2, body.getSlug());
                statement.setString(3, body.getAuthor());
                statement.setString(4, body.getForum());
                statement.setString(5, body.getTitle());
                statement.setString(6, body.getMessage());
                statement.setString(7, body.getCreated());
                return statement;
            }, keyHolder);
            threadsInc(body.getForum());
            body.setId(keyHolder.getKey().intValue()); // set Id
//            body.setId(id); // set Id
            result.setResponse(body, HttpStatus.CREATED);
            return result;
        } catch (DuplicateKeyException e) {
            result.setResponse(new Thread(), HttpStatus.CONFLICT);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Thread(), HttpStatus.NOT_FOUND);
            return result;
        }
    }

    public Response<Thread> getThread(String slug) {
        Response<Thread> result = new Response<>();
        try {
            final Thread t= template.queryForObject(
                    "SELECT * FROM thread WHERE LOWER(slug) = LOWER(?)",
                    new Object[]{slug},  threadMapper);
            result.setResponse(t, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Thread(), HttpStatus.NOT_FOUND);
            return result;
        }
    }

    public Response<Thread> getThreadById(int id) {
        Response<Thread> result = new Response<>();
        try {
            final Thread t= template.queryForObject(
                    "SELECT * FROM thread WHERE id = ?",
                    new Object[]{id},  threadMapper);
            result.setResponse(t, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Thread(), HttpStatus.NOT_FOUND);
            return result;
        }

    }

    public Response<Thread> getThreadBySLugOrId(String slug_or_id) {
        int id = -1;
        try {
            id = Integer.parseInt(slug_or_id);
        } catch (java.lang.NumberFormatException e ) {

        }
        Response<Thread> res;
        if (id != -1) {
           res = getThreadById(id);

        }
        else {
            res = getThread(slug_or_id);
        }
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            res.setResponse(new Thread(), HttpStatus.NOT_FOUND);
            return res;
        }
        return res;
    }

    public Response<List<Thread>> getThreadByForum(String forum) {
        final List<Thread> res = template.query(
                "SELECT * FROM thread WHERE lower(forum) = lower(?)",
                new Object[]{forum}, threadMapper);
        Response<List<Thread>> result = new Response<>();

        if (res.isEmpty()) {
            result.setResponse(res, HttpStatus.NOT_FOUND);
            return result;
        }
        result.setResponse(res, HttpStatus.OK);
        return result;
    }


    public Response<List<Thread>> getThreads(String forum, Integer limit, String since, Boolean desc) {
        Response<List<Thread>> threads = getThreadByForum(forum);
        if (threads.getStatus() == HttpStatus.NOT_FOUND) {
            return threads;
        }
        List<Object> tempObj = new ArrayList<>();

        final StringBuilder postQuery = new StringBuilder(
                "SELECT * FROM thread WHERE LOWER(forum) = LOWER(?) ");
        tempObj.add(forum);
        if (since != null) {
            if (desc != null && desc) {
                postQuery.append("AND created <= ?::timestamptz "); // ::timestamptz
            } else {
                postQuery.append("AND created >= ?::timestamptz ");
            }
            tempObj.add(since);
        }
        postQuery.append("ORDER BY created ");
        if (desc != null && desc) {
            postQuery.append("DESC ");
        }
        if (limit != null) {
            postQuery.append("LIMIT ?");
            tempObj.add(limit);
        }
        Response<List<Thread>> result = new Response<>();
        List<Thread> threads1 = new ArrayList<>();
        try {
            threads1 = template.query(postQuery.toString(),
                    tempObj.toArray(), threadMapper);
            result.setResponse(threads1, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(threads1, HttpStatus.NOT_FOUND);
            return result;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Vote> vote (String nickname,int userID, int threadID, int voice, String forum) {
        try {
            String sql = "INSERT INTO vote(nickname, userID, threadID, voice, forum) VALUES (?,?,?,?,?)";
            template.update(sql, nickname,userID, threadID, voice, forum);
            return new Response<>(new Vote(nickname,userID,voice), HttpStatus.CREATED);
        } catch(DuplicateKeyException e){
                Response<Vote> res = new Response<>();
                res.setResponse(new Vote(), HttpStatus.CONFLICT);
                return res;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Thread> updateThreadVoice (Thread thread, int voice, boolean revotes) {
        if (revotes) {
            voice *= 2;
        }
        int threadID = thread.getId();
        int cur = thread.getVotes();
        int newVoice = cur + voice;
        try {
            String sql = "UPDATE thread SET votes = ? WHERE id = ?";
            template.update(sql, newVoice, threadID);
            Response<Thread> res = new Response<>();
            res.setResponse(thread, HttpStatus.OK);
            return res;
        } catch(DuplicateKeyException e){
            Response<Thread> res = new Response<>();
            res.setResponse(new Thread(), HttpStatus.CONFLICT);
            return res;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Thread> updateThread (Thread thread, int id) {
        try {
            String sql = "UPDATE thread SET title = COALESCE (?, title), message = COALESCE (?,message)  WHERE id = ?";
            template.update(sql, thread.getTitle(), thread.getMessage(), id);
            Response<Thread> res = new Response<>();
            res.setResponse(thread, HttpStatus.OK);
            return res;
        } catch(DuplicateKeyException e){
            Response<Thread> res = new Response<>();
            res.setResponse(new Thread(), HttpStatus.CONFLICT);
            return res;
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Vote> reVote (String nickname, int userID, int threadID, int voice) {
        try {
            String sql = "UPDATE vote SET voice = ? WHERE userID = ? AND threadID = ?";
            template.update(sql, voice, userID, threadID);
            return new Response<>(new Vote(nickname, userID, voice), HttpStatus.OK);
        } catch(DuplicateKeyException e){
            Response<Vote> res = new Response<>();
            res.setResponse(new Vote(), HttpStatus.CONFLICT);
            return res;
        }

    }

    public void postsInc(String slug, int inc) {
        try {
            String sql = "UPDATE forum SET posts = posts + ? WHERE LOWER(slug) = lower(?)";
            template.update(sql, inc, slug);
        } catch (DuplicateKeyException e) {

        } catch (DataAccessException e) {

        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<List<Post>> createPosts(List<Post> posts) {
        if (posts.isEmpty()) {
            return new Response<>(posts, HttpStatus.CREATED);
        }

        int threadId = posts.get(0).getThread();

        String sql2;
        sql2 = "SELECT count(id) from Post WHERE thread = ? AND parent = 0";

        boolean flag = (0 == (int) template.queryForObject(sql2,new Object[] { threadId }, Integer.class))
                ? Boolean.FALSE : Boolean.TRUE;

        if(flag == Boolean.FALSE) { // may be in current posts will be parent post
            ListIterator<Post> listIter = posts.listIterator();

            while(listIter.hasNext()){

                if (listIter.next().getParent() == 0) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag == Boolean.FALSE) {    // no parent message
            Response<List<Post>> res = new Response<>();

            res.setResponse(posts, HttpStatus.CONFLICT);
            return res;
        }
        try {
            int maxId = template.query("SELECT MAX(id) from post", (res, num) -> res.getInt(1)).get(0);
            String sql = "INSERT INTO post(parent, author, message, isEdited, forum, created, thread, path) " +
                    "VALUES (?,?,?,?,?,?::timestamptz,?, (SELECT path FROM Post WHERE id = ?) || ?)";
            Response<List<Post>> res = new Response<>();

            template.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement statement, int i) throws SQLException {
                    Post body = posts.get(i);
                    body.setCreated(posts.get(0).getCreated());
//                    Response<Post> parent = postDAO.getPostById(body.getParent());
//                    if (parent.getStatus() != HttpStatus.NOT_FOUND && parent.getBody().getThread() != body.getThread()) {
//                        res.setResponse(posts, HttpStatus.CONFLICT);
//                        System.out.println("1");
//
//
//                    } else if (parent.getStatus() == HttpStatus.NOT_FOUND && body.getParent() != 0) {
//                        res.setResponse(posts, HttpStatus.CONFLICT);
//                        System.out.println("2");
//
//                    }
                    body.setId(maxId + i + 1);
                    statement.setInt(1, body.getParent());
                    statement.setString(2, body.getAuthor());
                    statement.setString(3, body.getMessage());
                    statement.setBoolean(4, body.getIsEdited());
                    statement.setString(5, body.getForum());
                    statement.setString(6, body.getCreated());
                    statement.setInt(7, body.getThread());
                    if (body.getParent() != 0) {
                        statement.setInt(8, body.getParent());
                    } else {
                        statement.setInt(8, body.getId());
                    }
                    statement.setInt(9, body.getId());


                }

                @Override
                public int getBatchSize() {
                    return posts.size();
                }

            });

            postsInc(posts.get(0).getForum(), posts.size());
            if (res.getStatus() == HttpStatus.CONFLICT) {

                return res;
            }
            res.setResponse(posts, HttpStatus.CREATED);
            return res;
        } catch (DataIntegrityViolationException e) {
            Response<List<Post>> res = new Response<>();
            res.setResponse(posts, HttpStatus.CONFLICT);
            System.out.println("wertyuio");
            return res;
        }

    }

    private static RowMapper<Thread> threadMapper = (res, num) -> {
        int id = res.getInt("id");
        int votes = res.getInt("votes");
        String slug = res.getString("slug");
        String author = res.getString("author");
        String forum = res.getString("forum");
        String title = res.getString("title");
        String message = res.getString("message");
        Timestamp created = res.getTimestamp("created");

        return new Thread(id, votes, slug, author, forum, title, message, created);
    };

    private static RowMapper<Vote> voteMapper = (res, num) -> {
        String nickname = res.getString("nickname");
        int voice = res.getInt("voice");
        int userID = res.getInt("userID");

        return new Vote(nickname, userID, voice);
    };

    private static final RowMapper<Post> postMapper = (res, num) -> {
        int id = res.getInt("id");
        int parent = res.getInt("parent");
        int thread = res.getInt("thread");
        boolean isEdited = res.getBoolean("isEdited");
        String forum = res.getString("forum");
        String author = res.getString("author");
        String message = res.getString("message");
        Timestamp created = res.getTimestamp("created");
        Array path = res.getArray("path");

        return new Post(id, parent, thread, isEdited, author, message,forum, created, (Object[])path.getArray());

    };

}
