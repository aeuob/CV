package com.example.donation.ui.list_item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListItemViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListItemViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}