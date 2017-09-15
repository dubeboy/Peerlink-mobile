package com.dubedivine.samples.data.model;




import java.io.Serializable;
import java.util.Date;

/**
 * Created by divine on 2017/08/13.
 */
public class Media implements Serializable {
    private String name;
    private long size;
    private char type;  // P-> picture, V-> video, f -> docs
    private String location;
    private Date createAt = new Date();

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public char getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public int getLimit() {
        return 5120;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public Media(String name, long size, char type, String location) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.location = location;
    }
}
