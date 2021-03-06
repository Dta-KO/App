package com.app.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String name;
    private String birthday;
    private String sex;
    private String id;
    private String tel;
    private String linkFb;
    private String avt;
    private String lastMessage;

    public User() {
    }

    public User(String name, String birthday, String sex, String id, String tel, String linkFb, String avt, String lastMessage) {
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.id = id;
        this.tel = tel;
        this.linkFb = linkFb;
        this.avt = avt;
        this.lastMessage = lastMessage;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLinkFb() {
        return linkFb;
    }

    public void setLinkFb(String linkFb) {
        this.linkFb = linkFb;
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

    public String getId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        id = user.getUid();
        return id;
    }
}
