package com.dubedivine.samples.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by divine on 2017/08/13.
 */


public class Tag {
    private String name;  // name is the ID means
    private Date createAt = new Date();
    private List<Question> questions;

    public Tag(String name) {
        this.name = name;
    }

    public Tag() {

    }


    public String getName() {
        return name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question q) {
        if (questions != null) {
            questions.add(q);
        } else {
            questions = new ArrayList<>();
            questions.add(q);
        }
    }
}
