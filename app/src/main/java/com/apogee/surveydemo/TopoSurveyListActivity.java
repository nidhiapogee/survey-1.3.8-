package com.apogee.surveydemo;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.apogee.surveydemo.Generic.taskGeneric.Name;

public class TopoSurveyListActivity extends AppCompatActivity implements OnItemValueListener {

    int tskid;
    DatabaseOperation dbTask = new DatabaseOperation(this);
    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();
    HashMap<String,String> pointmap = new HashMap<>();
    String tit;
    String fnvl;
    int pstn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topo_survey_list);
        recylcerview = findViewById(R.id.stklist);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        recylcerview.setAdapter(recycerlViewAdapter);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(TopoSurveyListActivity.this);
        String taskname = sharedPreferences.getString(Name, "default value");
        dbTask.open();
        tskid = dbTask.gettaskid(taskname);
        showView(tskid,getString(R.string.topo_survey));
    }

    public void showView(int tid, String dtype){
        ArrayList<String> topotaskdata;
        topotaskdata = dbTask.topotaskdata(dtype,tid);
        for (int k = 0; k < topotaskdata.size(); k++) {
            String val = topotaskdata.get(k);
            String est = val.split(",")[0];
            String nrth = val.split(",")[1];
            String pnm = val.split(",")[2];
            String id = val.split(",")[3];
            String elvtn = val.split(",")[4];
            String zone = val.split(",")[5];
            pointmap.put(pnm,id);
            itemTypeList.add(new ItemType(ItemType.INPUTONLYTEXT,pnm , null,est+","+nrth+","+elvtn+","+zone,null,null));
            recycerlViewAdapter.notifyDataSetChanged();


        }
    }

    @Override
    public void returnValue(String title, String finalvalue) {

    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {
        tit = title;
        fnvl = finalvalue;
        pstn = position;
        alert(tit,fnvl);
    }

    public  void  alert(final String tt, final String fnvl){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TopoSurveyListActivity.this);
        builder1.setTitle(getString(R.string.stake_point));
        builder1.setMessage(getString(R.string.choose_stake_option));
        builder1.setCancelable(true);


        builder1.setNegativeButton(
                getString(R.string.google_map),
                (dialog, id) -> {
                    Intent i = new Intent(TopoSurveyListActivity.this, StakeMap.class);
                    i.putExtra("Point_nm", tt);
                    i.putExtra("location", fnvl);
                    startActivity(i);
                    dialog.cancel();
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
