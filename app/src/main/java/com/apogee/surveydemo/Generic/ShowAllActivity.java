package com.apogee.surveydemo.Generic;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    ShowAllDataAdapter showAllDataAdapter;
    RecyclerView recordsView;
    List<Record> recordlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        recordlist = (List<Record>) getIntent().getSerializableExtra("record");
        showAllDataAdapter = new ShowAllDataAdapter(this, new ArrayList<Record>());
        recordsView = findViewById(R.id.records_view);
        recordsView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.scrollToPositionWithOffset(2, 20);
        recordsView.setLayoutManager(linearLayoutManager);
        recordsView.setAdapter(showAllDataAdapter);
        showAllDataAdapter.add(recordlist);


    }
}
