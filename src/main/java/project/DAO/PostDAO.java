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
import project.models.Post;
import project.utils.Response;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class PostDAO {

    private final JdbcTemplate template;

    public PostDAO(JdbcTemplate template) {
        this.template = template;
    }


    public Integer count() {
        return template.queryForObject(
                "SELECT COUNT(*) FROM post;",
                new Object[]{}, Integer.class);
    }

    public Response<Post> getPostById(int id) {
        Response<Post> result = new Response<>();
        try {
            final Post pst =  template.queryForObject(
                    "SELECT * FROM post WHERE id = ?",
                    new Object[]{id},  postMapper);
            result.setResponse(pst, HttpStatus.OK);
            return result;
        }
        catch (DataAccessException e) {
            result.setResponse(new Post(), HttpStatus.NOT_FOUND);
            return result;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Response<Post> updatePost(Post body, int id) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement statement = con.prepareStatement(
                        "UPDATE post SET message = COALESCE (?, message)," +
                                " isedited = COALESCE(true, isedited) " +
                                "WHERE id = ?",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1 , body.getMessage());
                statement.setInt(2, id);
                return statement;
            }, keyHolder);

            Response<Post> res = new Response<>();
            res.setResponse(body, HttpStatus.OK);
            return res;
        } catch(DuplicateKeyException e){
            Response<Post> res = new Response<>();
            res.setResponse(new Post(), HttpStatus.CONFLICT);
            return res;
        }
    }

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
