package com.newbiechen.searchview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchView = (SearchView) findViewById(R.id.main_search_view);
    }

    public void startSearch(View view){
        mSearchView.startSearch();
    }

    public void finishSearch(View view){
        mSearchView.finishSearch();
    }
}
