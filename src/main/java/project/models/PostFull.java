package project.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostFull {
    private Post post;
    private User author;
    private Forum forum;
    private Thread thread;

    public PostFull() {
        this.post = null;
        this.author = null;
        this.forum = null;
        this.thread = null;
    }

    @JsonCreator
    public PostFull(
            @JsonProperty("post") Post post,
            @JsonProperty("author") User author,
            @JsonProperty("forum") Forum forum,
            @JsonProperty("thread") Thread thread

    ) {
        this.post = post;
        this.author = author;
        this.forum = forum;
        this.thread = thread;
    }


    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
