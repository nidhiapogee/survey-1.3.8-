package com.apogee.surveydemo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.Generic.taskGeneric;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.apogee.surveydemo.utility.DeviceScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class tasklist extends AppCompatActivity implements OnItemValueListener {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();
    DatabaseOperation dbTask = new DatabaseOperation(this);
    int posit;
    String val;
    String pname;
    String time;
    public static final String Name = "nameKey";
    public static final String project_name = "prjctnameKey";
    String device_name1="",address="";
    String device_id="",dgps_device_id;
    String selectedmodeule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        toolbar=findViewById(R.id.tool);
        toolbar.setTitle(getString(R.string.task_list));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
        selectedmodeule = intent.getStringExtra(" selectedmodeule" );
        recylcerview = findViewById(R.id.tlist);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(tasklist.this);
        String p2_name = sharedPreferences.getString(project_name, "default value");
        recylcerview.setAdapter(recycerlViewAdapter);
        bottomNavigationView = findViewById(R.id.task_navigation);
        dbTask.open();
        ArrayList<String> tlist;
        tlist = dbTask.gettaskList(p2_name);
        if(tlist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_task_found_create_first), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < tlist.size(); k++) {
            String val = tlist.get(k);
            String tname = val.split(",")[0];
            String prsn = val.split(",")[1];
            String time = val.split(",")[2];
            itemTypeList.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, prsn, time,null));
            recycerlViewAdapter.notifyDataSetChanged();

        }
        dbTask.close();

        btmvgtn();
    }

    public void btmvgtn(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.p1:
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(tasklist.this);
                            String taskname = sharedPreferences.getString(Name, "default value");
                            if(pname!=null){
                                if(pname.equalsIgnoreCase(taskname)){
                                    alertdialog(getString(R.string.already_current_task)+" "+pname);
                                }else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Name, pname);
                                    editor.apply();
                                    alertdialog(getString(R.string.task_selected)+" "+pname);
                                    Toast.makeText(tasklist.this, getString(R.string.task_selected)+pname, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(tasklist.this, getString(R.string.select_task_first), Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.p2:
                            Intent intent = new Intent(tasklist.this, taskGeneric.class);
                            intent.putExtra("device_name", device_name1);
                            intent.putExtra("device_address", address);
                            intent.putExtra("device_id", device_id);
                            intent.putExtra("dgps_device_id", dgps_device_id);
                            intent.putExtra(" selectedmodeule" ,selectedmodeule);
                            startActivity(intent);
                            break;

                        case R.id.p5:
                            if(val!=null){
                                itemTypeList.remove(posit);
                                recycerlViewAdapter.notifyItemRemoved(posit);
                                dbTask.open();
                                boolean result =dbTask.deletetask(pname,time);
                                if(result){
                                    Toast.makeText(tasklist.this, getString(R.string.project_removed), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(tasklist.this, getString(R.string.sorry), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(tasklist.this, getString(R.string.select_project_first), Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                    return false;
                });
    }

    public void alertdialog(String msg) {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);

        textView1.setText(getString(R.string.work_mode));
        textView2.setText(getString(R.string.choose_any_one));
        button1.setText(getString(R.string.connection_page));
        button2.setText(getString(R.string.home_page));

        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(tasklist.this,HomeActivity.class);
            intent.putExtra("device_name", device_name1);
            intent.putExtra("device_address", address);
            intent.putExtra("device_id", String.valueOf(device_id));
            intent.putExtra("dgps_device_id", dgps_device_id);
            intent.putExtra(" selectedmodeule" ,selectedmodeule);
            startActivity(intent);
            dialogBuilder.dismiss();
        });

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            Intent intent = new Intent(tasklist.this, DeviceScanActivity.class);
            intent.putExtra("device_name", device_name1);
            intent.putExtra("device_address", address);
            intent.putExtra("device_id", device_id);
            intent.putExtra("dgps_device_id", dgps_device_id);
            intent.putExtra(" selectedmodeule" ,selectedmodeule);
            startActivity(intent);
        });

        dialogBuilder.setView(dialogView);
        Window window = dialogBuilder.getWindow();
        dialogBuilder.show();
        if(window != null){ // After the window is created, get the SoftInputMode
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void returnValue(String title, String finalvalue) {

    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {
        val = title +","+ finalvalue ;
        if(val!=null){
            pname = title.split("##")[0];
            time = finalvalue;
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(tasklist.this);
            String taskname = sharedPreferences.getString(Name, "default value");
            if(pname!=null){
                if(pname.equalsIgnoreCase(taskname)){
                    alertdialog(getString(R.string.already_current_task)+" "+pname);
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Name, pname);
                    editor.apply();
                    alertdialog(getString(R.string.task_selected)+" "+pname);
                    Toast.makeText(tasklist.this, getString(R.string.task_selected)+pname, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(tasklist.this, getString(R.string.select_task_first), Toast.LENGTH_SHORT).show();
            }
        }
        posit=position;
    }
}
