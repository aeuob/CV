package com.example.donation.ui.verification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VerificationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VerificationViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}