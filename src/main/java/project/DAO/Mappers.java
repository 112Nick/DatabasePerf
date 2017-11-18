package project.DAO;

import org.springframework.jdbc.core.RowMapper;
import project.models.*;
import project.models.Thread;

import java.sql.Array;
import java.sql.Timestamp;

public class Mappers {
    public static RowMapper<Forum> forumMapper = (res, num) -> {
        int id = res.getInt("id");
        String slug = res.getString("slug");
        String user = res.getString("user");
        String title = res.getString("title");
        int posts = res.getInt("posts");
        int threads = res.getInt("threads");
        return new Forum(id, slug, user, title, posts, threads);
    };

    public static final RowMapper<Post> postMapper = (res, num) -> {
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

    public static RowMapper<Thread> threadMapper = (res, num) -> {
        int id = res.getInt("id");
        int fid = res.getInt("forumID");
        int votes = res.getInt("votes");
        String slug = res.getString("slug");
        String author = res.getString("author");
        String forum = res.getString("forum");
        String title = res.getString("title");
        String message = res.getString("message");
        Timestamp created = res.getTimestamp("created");

        return new Thread(id, fid, votes, slug, author, forum, title, message, created);
    };

    public static RowMapper<Vote> voteMapper = (res, num) -> {
        String nickname = res.getString("nickname");
        int voice = res.getInt("voice");
        return new Vote(nickname, voice);
    };

    public static final RowMapper<User> userMapper = (res, num) -> {
        String nickname = res.getString("nickname");
        String email = res.getString("email");
        String fullname = res.getString("fullname");
        String about = res.getString("about");
        if (res.wasNull()) {
            about = null;
        }
        return new User(fullname, nickname, email, about);
    };
}
