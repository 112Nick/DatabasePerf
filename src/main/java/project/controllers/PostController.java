package project.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DAO.ForumDAO;
import project.DAO.PostDAO;
import project.DAO.ThreadDAO;
import project.DAO.UserDAO;
import project.models.ErrMsg;
import project.models.Post;
import project.models.PostFull;
import project.utils.Response;

import java.util.ArrayList;
import java.util.List;

@ResponseBody
@RestController
@RequestMapping("/api/post")
public class PostController {

    private PostDAO postDAO;
    private ForumDAO forumDAO;
    private UserDAO userDAO;
    private ThreadDAO threadDAO;



    public PostController(PostDAO postDAO,ForumDAO forumDAO, UserDAO userDAO,ThreadDAO threadDAO) {
        this.postDAO = postDAO;
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;


    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updatePost(@RequestBody Post body, @PathVariable("id") int id) {
        Response<Post> exists = postDAO.getPostById(id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        if (body.getMessage() == null ){
            return ResponseEntity.status(HttpStatus.OK).body(postDAO.getPostById(id).getBody());
        }
        if (!exists.getBody().getMessage().equals(body.getMessage())) {
            Response<Post> res = postDAO.updatePost(body, exists.getBody().getId());
            return ResponseEntity.status(res.getStatus()).body(postDAO.getPostById(id).getBody());
        }
        else {

            return ResponseEntity.status(HttpStatus.OK).body(postDAO.getPostById(id).getBody());

        }


    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getPost(@RequestParam(value = "related", required = false) String[] related,
                                        @PathVariable("id") int id) {

        Response<Post> exists = postDAO.getPostById(id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        Response<PostFull> res = new Response<>();
        res.setResponse(new PostFull(),HttpStatus.OK);
        res.getBody().setPost(exists.getBody());
        if (related != null) {
            for (String model : related) {
                switch (model) {
                    case "user":
                        res.getBody().setAuthor(userDAO.getUserByNick(exists.getBody().getAuthor()).getBody());
                        break;
                    case "thread":
                        res.getBody().setThread(threadDAO.getThreadById(exists.getBody().getThread()).getBody());
                        break;
                    case "forum":
                        res.getBody().setForum(forumDAO.getForum(exists.getBody().getForum()).getBody());
                        break;
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(res.getBody());


    }
}
