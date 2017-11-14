package project.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Thread {
    private  static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private int id;
    private int votes;
    private String slug;
    private String author;
    private String forum;
    private String title;
    private String message;
    private String created;

    public Thread() {
        this.id = -1;
        this.votes = -1;
        this.slug = "";
        this.author = "";
        this.forum = "";
        this.title = "";
        this.message = "";
        this.created = "";
    }
    @JsonCreator
    public Thread(
            @JsonProperty("id") int id,
            @JsonProperty("votes") int votes,
            @JsonProperty("slug") String slug,
            @JsonProperty("author") String author,
            @JsonProperty("forum") String forum,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("created") Timestamp created
    ) {
        this.id = id;
        this.votes = votes;
        this.slug = slug;
        this.author = author;
        this.forum = forum;
        this.title = title;
        this.message = message;
//        if (created != null) {
//            this.created = DATE_FORMAT.format(new Date(created.getTime()));
//        } else {
//            this.created = DATE_FORMAT.format(new Date());
//        }
        if (created != null) {
            //            this.created =Long.toString( created.toLocalDateTime().toEpochSecond(ZoneOffset.ofHours(3)));

            this.created = created.toInstant().toString();
//            System.out.println(created);
//            System.out.println(this.created);

        } else {
            this.created = null;
        }
        //this.created = created;
    }

    public int getId() {
        return id;
    }

    public int getVotes() {
        return votes;
    }

    public String getSlug() {
        return slug;
    }

    public String getAuthor() {
        return author;
    }

    public String getForum() {
        return forum;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCreated() {
        return created;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}


