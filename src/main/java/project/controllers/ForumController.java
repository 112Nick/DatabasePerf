package project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DAO.ForumDAO;
import project.DAO.ThreadDAO;
import project.DAO.UserDAO;
import project.models.Forum;
import project.models.Thread;
import project.models.ErrMsg;

import project.models.User;
import project.utils.Response;

import java.util.List;


@ResponseBody
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumDAO forumDAO;
    private final ThreadDAO threadDAO;
    private final UserDAO userDAO;

    public ForumController(ForumDAO forumDAO, ThreadDAO threadDAO, UserDAO userDAO) {
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createForum(@RequestBody Forum body) {
        Response<Forum> res= forumDAO.createForum(body);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(res.getStatus()).body(msg);
        }
        else if (res.getStatus() == HttpStatus.CONFLICT) {
            Response<Forum> conf= forumDAO.getForum(body.getSlug());
            return ResponseEntity.status(res.getStatus()).body(conf.getBody());
        }
        return ResponseEntity.status(res.getStatus()).body(res.getBody());

    }

    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> createForumThread( @RequestBody Thread body,@PathVariable("slug") String slug) {

        Response<Forum> res1 = forumDAO.getForum(slug);
        if (res1.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        Response<User> res2 = userDAO.getUserByNick(body.getAuthor());
        if (res2.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        body.setAuthor(res2.getBody().getNickname());
        body.setForum(res1.getBody().getSlug());
        Response<Thread> res = threadDAO.createThread(body, res1.getBody().getThreads());
        if (res.getStatus() == HttpStatus.CONFLICT) {
            return ResponseEntity.status(res.getStatus()).body(threadDAO.getThread(body.getSlug()).getBody());
        }
        else if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();

            return ResponseEntity.status(res.getStatus()).body(msg);
        }
        //return ResponseEntity.status(res.getStatus()).body(body);
        return ResponseEntity.status(res.getStatus()).body(threadDAO.getThreadById(body.getId()).getBody());

    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getForum( @PathVariable("slug") String slug) {
        Response<Forum> res= forumDAO.getForum(slug);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(res.getStatus()).body(msg);
        }
        return ResponseEntity.status(res.getStatus()).body(res.getBody());
    }

    @RequestMapping(path = "/{forum}/users", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUsers(@PathVariable String forum,
                                        @RequestParam(value = "limit", required = false) Integer limit,
                                        @RequestParam(value = "since", required = false) String since,
                                        @RequestParam(value = "desc", required = false) boolean desc) {
        Response<Forum> res = forumDAO.getForum(forum);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        } else {
            Response<List<User>> result = userDAO.getUsers(res.getBody().getSlug(), limit, since, desc);
            if (result.getStatus() == HttpStatus.NOT_FOUND) {
                ErrMsg msg = new ErrMsg();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);

            }
            return ResponseEntity.status(HttpStatus.OK).body(result.getBody());
        }
    }

    @RequestMapping(path = "/{forum}/threads", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getThreads(@PathVariable String forum,
                                        @RequestParam(value = "limit", required = false) Integer limit,
                                        @RequestParam(value = "since", required = false) String since,
                                        @RequestParam(value = "desc", required = false) boolean desc) {
        Response<List<Thread>> res = threadDAO.getThreads(forum, limit, since, desc);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(res.getBody());
        }
    }

}


