package com.app.navigation.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public HomeViewModel(MutableLiveData<String> mText) {
        this.mText = mText;
        mText.setValue("This is home fragment");
    }

    public HomeViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}
