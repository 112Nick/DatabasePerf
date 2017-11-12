package project.models;

public class Status {
    private int forum ;
    private int thread ;
    private int user ;
    private int post ;


    public Status(int forum, int thread, int user, int post) {
        this.forum = forum;
        this.thread = thread;
        this.user = user;
        this.post = post;
    }

    public int getForum() {
        return forum;
    }

    public void setForum(int forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
