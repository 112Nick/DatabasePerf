package project.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Post {
    private  static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private int id;
    private int parent;
    private int thread;
    private boolean isEdited;
    private String forum;
    private String author;
    private String message;
    private String created;
    private Object[] path;


    public Post () {

    }
    @JsonCreator
    public Post(
            @JsonProperty("id") int id,
            @JsonProperty("parent") int parent,
            @JsonProperty("thread") int thread,
            @JsonProperty("getIsEdited") boolean isEdited,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("forum") String forum,
            @JsonProperty("created") Timestamp created,
            @JsonProperty("path") Object[] path

    ) {
        this.id = id;
        this.parent = parent;
        this.forum = forum;
        if (created != null) {
            this.created = DATE_FORMAT.format(new Date(created.getTime()));
        } else {
            this.created = DATE_FORMAT.format(new Date());
        }


//        if (created == null) {
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            this.created = timestamp.toInstant().toString();
//        } else {
//            this.created = created.toInstant().toString();
//        }

        this.message = message;
        this.isEdited = isEdited;
        this.thread = thread;
        this.author = author;
        this.path = path;
    }


    public int getId() {
        return id;
    }

    public int getParent() {
        return parent;
    }

    public int getThread() {
        return thread;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public String getForum() {
        return forum;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public String getCreated() {
        return created;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setPath(Object[] path) {
        this.path = path;
    }

    public Object[] getPath() {
        return path;
    }

    public void setIsEdited(boolean edited) {
        isEdited = edited;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

