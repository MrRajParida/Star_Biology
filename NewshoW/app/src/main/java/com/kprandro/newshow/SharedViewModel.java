package com.kprandro.newshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<NewsItemClass> selectedNewsItem = new MutableLiveData<>();

    public void setSelectedNewsItem(NewsItemClass newsItem) {
        selectedNewsItem.setValue(newsItem);
    }

    public LiveData<NewsItemClass> getSelectedNewsItem() {
        return selectedNewsItem;
    }
}
