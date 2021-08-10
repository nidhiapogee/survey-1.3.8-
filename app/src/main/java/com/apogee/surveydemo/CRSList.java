package com.apogee.surveydemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Sattelite.CircularTextView;
import com.apogee.surveydemo.Sattelite.Skymodel;
import com.apogee.surveydemo.adapter.CRSListAdapter;
import com.apogee.surveydemo.adapter.ExpandableListAdapter;
import com.apogee.surveydemo.multiview.ItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CRSList extends AppCompatActivity {

    RecyclerView recyclerView;
    CRSListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> itemTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crs_list);

        recyclerView = findViewById(R.id.recylcerview);

        Intent intent = getIntent();
        if(intent != null){
            itemTypeList =   intent.getStringArrayListExtra("drawList");
        }
        listAdapter = new CRSListAdapter(this, itemTypeList);
        // setting list adapter
        recyclerView.setAdapter(listAdapter);
    }




}
