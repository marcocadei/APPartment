package com.unison.appartment;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.unison.appartment.model.Home;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class HomeListActivity extends ListActivity {

    private ListView mListView;
    private ArrayAdapter<Home> mAdapter;
    private List<Home> mModel = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        mListView = getListView();
        mAdapter = new ArrayAdapter<Home>(this,
                R.layout.activity_home_list_item,
                mModel);
        mListView.setAdapter(mAdapter);

        FloatingActionButton floatNew = findViewById(R.id.activity_home_list_float_new);
        floatNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeListActivity.this, CreateHomeActivity.class);
                startActivity(i);
            }
        });

        FloatingActionButton floatJoin = findViewById(R.id.activity_home_list_float_join);
        floatJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(HomeListActivity.this, JoinHomeActivity.class);
//                startActivity(i);

                Intent i = new Intent(HomeListActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}
