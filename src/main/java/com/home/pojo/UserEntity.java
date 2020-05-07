package com.home.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class UserEntity {

    private String id;

    private Date date;

    public UserEntity(String id, Date date) {
        this.id = id;
        this.date = date;
    }
}
