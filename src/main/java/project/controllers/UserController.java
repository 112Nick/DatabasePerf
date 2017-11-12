package project.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.DAO.UserDAO;
import project.models.ErrMsg;
import project.models.User;
import org.springframework.http.HttpStatus;
import project.utils.Response;

import java.util.ArrayList;
import java.util.List;


@ResponseBody
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserDAO userDAO;


    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User body, @PathVariable("nickname") String nickname) { //TODO rewrite
        body.setNickname(nickname);
        List<Response<User>> arr = userDAO.getUserByNickOrEmail(nickname, body.getEmail());
        if (arr.isEmpty()) {
            Response<User> res = userDAO.createUser(body);
            return ResponseEntity.status(res.getStatus()).body(res.getBody());
        }
        else {
            List<User> result = new ArrayList<>();
            for(Response<User> i : arr) {
                result.add(i.getBody());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.toArray());
        }

    }


    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUser(@PathVariable("nickname") String nickname) {
        Response<User> res = userDAO.getUserByNick(nickname);
        if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(res.getStatus()).body(msg);
        }
        return ResponseEntity.status(res.getStatus()).body(res.getBody());
    }


    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody User body, @PathVariable("nickname") String nickname) {
        body.setNickname(nickname);
        if (body.getEmail() == null) {
            if (userDAO.getUserByNick(body.getNickname()).getStatus() == HttpStatus.NOT_FOUND) {
                ErrMsg msg = new ErrMsg();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            if (body.getAbout() == null && body.getFullname() == null) {
                return ResponseEntity.status(HttpStatus.OK).body(userDAO.getUserByNick(nickname).getBody());
            }
        }

        Response<User> res  = userDAO.updateUser(body);

        if (res.getStatus() == HttpStatus.CONFLICT) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
        }
        else if (res.getStatus() == HttpStatus.NOT_FOUND) {
            ErrMsg msg = new ErrMsg();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        return ResponseEntity.status(res.getStatus()).body(userDAO.getUserByNick(nickname).getBody());

    }
}