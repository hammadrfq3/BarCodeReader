package com.qrcodereader.barcodereader.historyViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;


    public MFactory(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == HistoryViewModel.class) {
            return (T) new HistoryViewModel(application);
        }
        return null;
    }
}