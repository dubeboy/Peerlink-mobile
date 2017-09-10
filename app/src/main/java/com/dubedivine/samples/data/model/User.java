package com.dubedivine.samples.data.model;

import java.util.List;

public class User {
    private String id;  // initialised by my faithful mongodb!!
    private String name;
    private String email;
    private List<Tag> tags;

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public User(String name, String email, List<Tag> tags) {
        this.name = name;
        this.email = email;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
