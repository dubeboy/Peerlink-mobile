package com.dubedivine.samples.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by divine on 2017/09/10.
 */

// todo: should not deserialize the the nested objects?

public class Question implements Serializable {

    private String id;  // protected because it has a setter in the Elastic question child class
    private String title;
    private String body;
    private long votes;
    private List<Comment> comments;
    private List<Answer> answers;
    private List<Tag> tags;  //todo: bad it should be mapping!!
    private User user; // the user the
    private String type;
    private Media video;
    private List<Media> files; //this can be combined with video dwag
    private Date createdAt = new Date();
    private Boolean isAnswered;

    public Question(String title, String body, long votes, List<Tag> tags, String type) {
        this.title = title;
        this.body = body;
        this.votes = votes;
        this.tags = tags;
        this.type = type;
    }

    //for video we will add another constructor which has video here

    public String getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getVotes() {
        return votes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Media getVideo() {
        return video;
    }

    public String getType() {
        return type;
    }

    public List<Media> getFiles() {
        return files;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setVideo(Media video) {
        this.video = video;
    }

    public void setFiles(List<Media> files) {
        this.files = files;
    }


    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", votes=" + votes +
                ", comments=" + comments +
                ", answers=" + answers +
                ", tags=" + tags +
                ", user=" + user +
                ", type='" + type + '\'' +
                ", video=" + video +
                ", files=" + files +
                ", createdAt=" + createdAt +
                '}';
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public Boolean getAnswered() {
        return isAnswered;
    }

    public void setAnswered(Boolean answered) {
        isAnswered = answered;
    }
}
