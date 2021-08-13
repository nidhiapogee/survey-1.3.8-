package com.apogee.surveydemo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.Generic.GPSTrack;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.apogee.surveydemo.Generic.taskGeneric.Name;

public class Stakeselection extends AppCompatActivity implements OnItemValueListener {
    Spinner datatypespnr;
    String TAG = "Manual";
    BottomNavigationView bottomNavigationView;
    String datatypoe;
    DatabaseOperation dbTask = new DatabaseOperation(this);
    int tskid;
    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();
    List<ItemType> filteredDataList = new ArrayList<>();
    String tit;
    String fnvl;
    int pstn;
    Toolbar toolbar;
    HashMap<String,String> pointmap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stakeselection);
        datatypespnr=findViewById(R.id.spndatatype);
        bottomNavigationView = findViewById(R.id.stknvgtn);
        recylcerview = findViewById(R.id.stklist);
        toolbar=findViewById(R.id.tool);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.points));
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        btmvgtn();
        recylcerview.setAdapter(recycerlViewAdapter);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Stakeselection.this);
        String taskname = sharedPreferences.getString(Name, "default value");
        dbTask.open();
        tskid = dbTask.gettaskid(taskname);
        final ArrayList<String> typelist = new ArrayList<>();
        typelist.add("--select--");
        typelist.add(getString(R.string.auto_survey));
        typelist.add(getString(R.string.topo_survey));
        typelist.add(getString(R.string.manual));

        final ArrayAdapter<String> model_typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typelist);
        model_typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datatypespnr.setAdapter(model_typeAdapter);

        datatypespnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                datatypoe = parent.getItemAtPosition(position).toString();
                if(!datatypoe.equalsIgnoreCase("--select--")){
                    itemTypeList.clear();
                    recycerlViewAdapter.notifyDataSetChanged();
                    showView(tskid,datatypoe);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private List<ItemType> filter(List<ItemType> dataList, String newText) {
        try {
            newText = newText.toLowerCase();
            String text;
            filteredDataList = new ArrayList<>();
            for (ItemType dataFromDataList : dataList) {
                text = dataFromDataList.title.toLowerCase();

                if (text.contains(newText)) {
                    filteredDataList.add(dataFromDataList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return filteredDataList;
    }

    public void
    showView(int tid, String dtype){
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
            String videoPath = val.split(",")[6];
            pointmap.put(pnm,id);
            itemTypeList.add(new ItemType(ItemType.INPUTONLYTEXT,pnm , null,"Easting : "+est+", Northing : "+nrth+", Elevation : "+elvtn+", Zone : "+zone+","+videoPath,null,null));
            recycerlViewAdapter.notifyDataSetChanged();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                boolean retur=false;
                if(!newText.equalsIgnoreCase("")) {
                    if (itemTypeList.isEmpty()) {
                        Toast.makeText(Stakeselection.this, getString(R.string.choose_any_survey_type_first), Toast.LENGTH_SHORT).show();
                       retur = false;
                    } else {
                        filteredDataList = filter(itemTypeList, newText);
                        setFilter();
                        retur = true;
                    }
                }
                return retur;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void setFilter() {
        itemTypeList.clear();
        itemTypeList.addAll(filteredDataList);
        recycerlViewAdapter. notifyDataSetChanged();
    }

    public void btmvgtn(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.p:
                            if(tit!=null){
                                alert(tit,fnvl);
                            }else{
                                Toast.makeText(Stakeselection.this, getString(R.string.choose_point_first), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.p1:
                                editpoint(getString(R.string.add));
                            break;
                        case R.id.p2:
                            if(tit!=null){
                                editpoint(getString(R.string.edit));
                            }else{
                                Toast.makeText(Stakeselection.this, getString(R.string.choose_point_first), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.p3:
                            if(tit!=null){
                                alertdelete();
                            }else{
                                Toast.makeText(Stakeselection.this, getString(R.string.choose_point_first), Toast.LENGTH_SHORT).show();
                            }
                            break;

                    }
                    return false;
                });
    }

    @Override
    public void returnValue(String title, String finalvalue) {

    }

    public  void  alert(final String tt, final String fnvl){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Stakeselection.this);
        builder1.setTitle(getString(R.string.stake_point));
        builder1.setMessage(getString(R.string.choose_stake_option));
        builder1.setCancelable(true);

        // check if GPS enabled

       GPSTrack gps = new GPSTrack(this);

       Location locationA = new Location("pointA");
            locationA.setLatitude(gps.getLatitude());
            locationA.setLongitude(gps.getLongitude());
        locationA.setLatitude(28.5272803);
        locationA.setLongitude(77.0688968);
      String buttonName = "";

          buttonName = getString(R.string.google_map);


      builder1.setPositiveButton(getString(R.string.stake_map), (dialog, which) -> {
          Intent intent = new Intent(Stakeselection.this, StakeMapCustom.class);
          intent.putExtra("FromStake",true);
          intent.putExtra("location", fnvl);
          startActivity(intent);
          dialog.cancel();
      });

        builder1.setNegativeButton(
                buttonName,
                (dialog, id) -> {
                    Intent i = new Intent(Stakeselection.this, StakeMap.class);
                    String strName = null;
                    i.putExtra("Point_nm", tt);
                    i.putExtra("location", fnvl);
                    startActivity(i);
                    dialog.cancel();

                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public  void  alertdelete(){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);


        textView1.setText(getString(R.string.delete_point));
        button1.setText(getString(R.string.yes_delete));
        button2.setText(getString(R.string.no));
        textView2.setText(getString(R.string.are_you_sure_want_to_delete_pont)+" "+tit);
        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(view -> dialogBuilder.dismiss());

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            itemTypeList.remove(pstn);
            recycerlViewAdapter.notifyItemRemoved(pstn);
            dbTask.open();
            boolean result =dbTask.deletepoint(tit,fnvl);
            if(result){
                Toast.makeText(Stakeselection.this, tit+" " + getString(R.string.removed), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Stakeselection.this, getString(R.string.sorry), Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setView(dialogView);
        Window window = dialogBuilder.getWindow();
        dialogBuilder.show();
        if(window != null){ // After the window is created, get the SoftInputMode
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void editpoint(final String type){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogtime, null);


        final EditText editText = dialogView.findViewById(R.id.et);
        final EditText editText1 = dialogView.findViewById(R.id.et1);
        final EditText editText2 = dialogView.findViewById(R.id.et2);
        final EditText editText3 = dialogView.findViewById(R.id.et3);
        final TextView textview = dialogView.findViewById(R.id.cmethodtext);
        Button button1 = dialogView.findViewById(R.id.timebt);

        if(type.equalsIgnoreCase(getString(R.string.add))){
            textview.setText(getString(R.string.add_stake_point));
            editText.setVisibility(View.VISIBLE);
        }else if(type.equalsIgnoreCase(getString(R.string.edit))){
            textview.setText(getString(R.string.edit_stake_point));
            editText.setVisibility(View.INVISIBLE);
        }



        button1.setOnClickListener(view -> {
            String lat = editText1.getText().toString().trim();
            String lonnngg = editText2.getText().toString().trim();
            String alttt = editText3.getText().toString().trim();
            if (pointmap.containsKey(tit) || lat!=null || lonnngg!=null || alttt!=null){
                String id = pointmap.get(tit);
                if(type.equalsIgnoreCase(getString(R.string.edit))){
                    long result = dbTask.updatepoint(lat,lonnngg,alttt,id);
                    if(result > 0){
                        Toast.makeText(Stakeselection.this, tit+" "+getString(R.string.updated), Toast.LENGTH_SHORT).show();
                        dialogBuilder.dismiss();
                        recycerlViewAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(Stakeselection.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }else if(type.equalsIgnoreCase(getString(R.string.add))){
                    String finalpoint = editText.getText().toString().trim();
                    if(!finalpoint.equalsIgnoreCase("") && !lat.equalsIgnoreCase("") && !lonnngg.equalsIgnoreCase("")){
                        boolean result = dbTask.insertTopo(TAG,finalpoint,"13",tskid,Double.parseDouble(lat),Double.parseDouble(lonnngg),alttt,0.0,0.0,"2.0","","43Q");
                        if (result) {
                            System.out.println("Data inserted");
                            Toast.makeText(Stakeselection.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(Stakeselection.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Stakeselection.this, getString(R.string.point_added), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Stakeselection.this, getString(R.string.select_all_fields_first), Toast.LENGTH_SHORT).show();
                    }

                }

            }
            // DO SOMETHINGS

        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {
        tit = title;
        fnvl = finalvalue;
        pstn = position;

    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
