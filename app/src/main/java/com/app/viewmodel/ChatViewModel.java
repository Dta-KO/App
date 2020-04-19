package com.app.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> avt;
    private MutableLiveData<String> nameUser;
    private MutableLiveData<String> lastMessage;

    public ChatViewModel(MutableLiveData<String> avt, MutableLiveData<String> nameUser, MutableLiveData<String> lastMessage) {
        this.avt = avt;
        this.nameUser = nameUser;
        this.lastMessage = lastMessage;
    }

    public MutableLiveData<String> getAvt() {
        return avt;
    }

    public MutableLiveData<String> getNameUser() {
        return nameUser;
    }

    public MutableLiveData<String> getLastMessage() {
        return lastMessage;
    }

    public void setAvt(MutableLiveData<String> avt) {
        this.avt = avt;
    }

    public void setNameUser(MutableLiveData<String> nameUser) {
        this.nameUser = nameUser;
    }

    public void setLastMessage(MutableLiveData<String> lastMessage) {
        this.lastMessage = lastMessage;
    }
}