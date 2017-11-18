package project.DAO;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import project.models.Forum;
import java.sql.PreparedStatement;

import project.models.User;
import project.utils.Response;

@Service
@Transactional
public class ForumDAO {

    private final JdbcTemplate template;
    private final UserDAO userDAO;
    private final ThreadDAO threadDAO;

    public ForumDAO(JdbcTemplate template, UserDAO userDAO,ThreadDAO threadDAO) {
        this.template = template;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
    }

    public void clear() {
        template.update(
                "TRUNCATE users, forum, post, thread, vote RESTART IDENTITY CASCADE;" //TODO only users when connected
        );
    }

    public Integer count() {
        return template.queryForObject(
                "SELECT COUNT(*) FROM forum;",
                new Object[]{}, Integer.class);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Response<Forum> createForum(Forum body) {
        Response<Forum> result = new Response<>();
        Response<User> res = userDAO.getUserByNick(body.getUser());
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            result.setResponse(new Forum(), HttpStatus.NOT_FOUND);
            return result;
        }
        body.setUser(res.getBody().getNickname());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO forum(slug, title , \"user\", posts, threads)"
                                + " VALUES(?,?,?,?,?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1, body.getSlug());
                statement.setString(2, body.getTitle());
                statement.setString(3, res.getBody().getNickname());
                statement.setInt(4, body.getPosts());
                statement.setInt(5, body.getThreads());
                return statement;
            }, keyHolder);
            result.setResponse(body, HttpStatus.CREATED);
            return result;
        } catch (DuplicateKeyException e) {
            result.setResponse(new Forum(), HttpStatus.CONFLICT);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Forum(), HttpStatus.NOT_FOUND);
            return result;
        }

    }


    public Response<Forum> getForum(String slug) {
        Response<Forum> result = new Response<>();
        try {
            final Forum f= template.queryForObject(
                    "SELECT * FROM forum WHERE LOWER(slug) = LOWER(?)",
                    new Object[]{slug},  Mappers.forumMapper);
            result.setResponse(f, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Forum(), HttpStatus.NOT_FOUND);
            return result;
        }
    }


//    private static RowMapper<Forum> forumMapper = (res, num) -> {
//        String slug = res.getString("slug");
//        String user = res.getString("user");
//        String title = res.getString("title");
//        int posts = res.getInt("posts");
//        int threads = res.getInt("threads");
//        return new Forum(slug, user, title, posts, threads);
//    };

}








