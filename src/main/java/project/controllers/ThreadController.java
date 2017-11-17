package project.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DAO.ForumDAO;
import project.DAO.ThreadDAO;
import project.DAO.UserDAO;
import project.models.*;
import project.models.Thread;
import project.utils.Response;

import java.util.List;

@ResponseBody
@RestController
@RequestMapping("/api/thread")
public class ThreadController {
    private final ThreadDAO threadDAO;
    private final UserDAO userDAO;
    private final ForumDAO forumDAO;


    public ThreadController(ThreadDAO threadDAO,UserDAO userDAO,ForumDAO forumDAO ) {
        this.threadDAO = threadDAO;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;

    }

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> createForumThread(@RequestBody  List<Post> posts, @PathVariable("slug_or_id") String slug_or_id) {

        Response<Thread> exists = threadDAO.getThreadBySLugOrId(slug_or_id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
          ErrMsg msg = new ErrMsg();
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
         }
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(posts);
        }


        for (Post body: posts) {
            body.setForum(exists.getBody().getForum());
            body.setThread(exists.getBody().getId());
            Response<User> userExists = userDAO.getUserByNick(body.getAuthor());
            if (userExists.getStatus() == HttpStatus.NOT_FOUND) {
                ErrMsg msg = new ErrMsg();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
        }

        Response<List<Post>> res = threadDAO.createPosts(posts);
        if (res.getStatus() == HttpStatus.CONFLICT) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
        }
        return ResponseEntity.status(res.getStatus()).body(res.getBody());
    }

    @RequestMapping(path = "/{slug_or_id}/vote", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> vote(@RequestBody  Vote vote, @PathVariable("slug_or_id") String slug_or_id) {
        Response<Thread> exists = threadDAO.getThreadBySLugOrId(slug_or_id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        int threadID = exists.getBody().getId();
        Response<User> userExists = userDAO.getUserByNick(vote.getNickname());
        if (userExists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        else {
            Response<Vote> userVoted = threadDAO.getVote(vote.getNickname(),userExists.getBody().getId(), threadID);
            if (userVoted.getStatus() == HttpStatus.NOT_FOUND) {
                threadDAO.vote(vote.getNickname(),userExists.getBody().getId(),threadID,vote.getVoice(), exists.getBody().getForum());
                threadDAO.updateThreadVoice(exists.getBody(), vote.getVoice(), false);
                return ResponseEntity.status(HttpStatus.OK).body(threadDAO.getThreadBySLugOrId(slug_or_id).getBody());
            }
            else {
                if (userVoted.getBody().getVoice() != vote.getVoice()) {
                    threadDAO.reVote(vote.getNickname(),userExists.getBody().getId(),threadID, vote.getVoice());
                    threadDAO.updateThreadVoice(exists.getBody(), vote.getVoice(), true);
                }
                return ResponseEntity.status(HttpStatus.OK).body(threadDAO.getThreadBySLugOrId(slug_or_id).getBody());
            }
        }
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getThread(@PathVariable("slug_or_id") String slug_or_id) {
        Response<Thread> exists = threadDAO.getThreadBySLugOrId(slug_or_id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        return ResponseEntity.status(exists.getStatus()).body(exists.getBody());
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getPosts(@PathVariable("slug_or_id") String slug_or_id,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "since", required = false) Integer since,
                                      @RequestParam(value = "sort", required = false) String sort,
                                      @RequestParam(value = "desc", required = false) boolean desc) {
        Response<Thread> exists = threadDAO.getThreadBySLugOrId(slug_or_id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        Response<List<Post>> res =  threadDAO.getPosts(exists.getBody(), limit, since, sort, desc);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("dgdias");

        }
        return ResponseEntity.status(HttpStatus.OK).body(res.getBody());

    }


    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updateThread(@RequestBody  Thread thread ,@PathVariable("slug_or_id") String slug_or_id) {
        Response<Thread> exists = threadDAO.getThreadBySLugOrId(slug_or_id);
        if (exists.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        Response<Thread> res = threadDAO.updateThread(thread, exists.getBody().getId());
        return ResponseEntity.status(res.getStatus()).body(threadDAO.getThreadBySLugOrId(slug_or_id).getBody());
    }
}
