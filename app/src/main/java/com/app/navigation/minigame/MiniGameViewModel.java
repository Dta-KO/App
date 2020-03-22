package com.app.navigation.minigame;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MiniGameViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public MiniGameViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mini game fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
