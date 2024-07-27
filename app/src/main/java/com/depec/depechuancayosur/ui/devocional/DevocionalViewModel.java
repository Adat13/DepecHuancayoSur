package com.depec.depechuancayosur.ui.devocional;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevocionalViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DevocionalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}