package project.DAO;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import project.models.Forum;
import project.models.User;
import project.utils.Response;

@Service
@Transactional
public class ForumDAO {

    private final JdbcTemplate template;
    private final UserDAO userDAO;

    public ForumDAO(JdbcTemplate template, UserDAO userDAO) {
        this.template = template;
        this.userDAO = userDAO;
    }

    public void clear() {
        template.update(
                "TRUNCATE users, forum, post, thread, vote RESTART IDENTITY CASCADE;"
        );
    }

    public Integer count() {
        return template.queryForObject(
                "SELECT COUNT(*) FROM forum;",
                new Object[]{}, Integer.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Response<Forum> createForum(Forum body) {
        Response<Forum> result = new Response<>();
        Response<User> res = userDAO.getUserByNick(body.getUser());
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            result.setResponse(new Forum(), HttpStatus.NOT_FOUND);
            return result;
        }
        body.setUser(res.getBody().getNickname());
        try {
            String sql = "INSERT INTO forum(slug, title ,\"user\", posts, threads) VALUES(?,?,?,?,?)";
            template.update(sql, body.getSlug(), body.getTitle(),res.getBody().getNickname(),body.getPosts(),body.getThreads());
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
                    new Object[]{slug},  forumMapper);
            result.setResponse(f, HttpStatus.OK);
            return result;
        } catch (DataAccessException e) {
            result.setResponse(new Forum(), HttpStatus.NOT_FOUND);
            return result;
        }
    }


    private static RowMapper<Forum> forumMapper = (res, num) -> {
        String slug = res.getString("slug");
        String user = res.getString("user");
        String title = res.getString("title");
        int posts = res.getInt("posts");
        int threads = res.getInt("threads");
        return new Forum(slug, user, title, posts, threads);
    };

}








