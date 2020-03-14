package com.app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String name;
    private String birthday;
    private String sex;
    private int avt;
    private String id;
    private String tel;
    private String linkFb;

    public User() {
    }

    public User(String name, String birthday, String sex, int avt, String id) {
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.avt = avt;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAvt() {
        return avt;
    }

    public void setAvt(int avt) {
        this.avt = avt;
    }

    public String getId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        id = user.getUid();
        return id;
    }
}
