package dev.bugakov.testapp;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;

public class ItemDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, ItemQuestion>> itemLiveDataSource = new MutableLiveData<>();

    @Override
    public DataSource<Integer, ItemQuestion> create() {
        ItemDataSource itemDataSource = new ItemDataSource();

        itemLiveDataSource.postValue(itemDataSource);

        return itemDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, ItemQuestion>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}