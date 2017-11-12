package project.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DAO.ForumDAO;
import project.DAO.PostDAO;
import project.DAO.ThreadDAO;
import project.DAO.UserDAO;
import project.models.Status;

@ResponseBody
@RestController
@RequestMapping("/api/service")
public class ServiceController {
    private final ForumDAO forumDAO;
    private final ThreadDAO threadDAO;
    private final UserDAO userDAO;
    private final PostDAO postDAO;

    public ServiceController(ForumDAO forumDAO, ThreadDAO threadDAO, UserDAO userDAO, PostDAO postDAO) {
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.userDAO = userDAO;
        this.postDAO = postDAO;
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getStatus() {
        int forumLength = forumDAO.count();
        int threadLength = threadDAO.count();
        int userLength = userDAO.count();
        int postLength = postDAO.count();
        Status status = new Status(forumLength,threadLength,userLength,postLength);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
    @RequestMapping(path = "/clear", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> clear() {
        forumDAO.clear();
        return ResponseEntity.status(HttpStatus.OK).body("done");
    }

}
