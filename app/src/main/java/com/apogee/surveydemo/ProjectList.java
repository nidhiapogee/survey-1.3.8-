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
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
/*This class basically shows all the saved projrct and you can alter nd modify the changes*/
public class ProjectList extends AppCompatActivity implements OnItemValueListener {

    BottomNavigationView bottomNavigationView;
    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();
    DatabaseOperation dbTask = new DatabaseOperation(this);
    int posit;
    String val;
    String pname;
    String time;
    public static final String project_name = "prjctnameKey";
    Toolbar toolbar;
    String device_name1="",address="";
    String device_id="",dgps_device_id;
    String selectedmodeule = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        toolbar=findViewById(R.id.tool);
        toolbar.setTitle(getString(R.string.project_list));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
       selectedmodeule = intent.getStringExtra(" selectedmodeule" );
        recylcerview = findViewById(R.id.plist);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        recylcerview.setAdapter(recycerlViewAdapter);
        bottomNavigationView = findViewById(R.id.project_navigation);
        /*Checking is database empty or not*/
        dbTask.open();
        ArrayList<String> plist;
        plist = dbTask.getProjectList();
        if(plist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_project_found), Toast.LENGTH_SHORT).show();
        }

        for (int k = 0; k < plist.size(); k++) {
            String val = plist.get(k);
            String pname = val.split(",")[0];
            String oprtr = val.split(",")[1];
            String time = val.split(",")[2];
            itemTypeList.add(new ItemType(ItemType.INPUTTYPEPROJECT, pname, null, oprtr, time,null));
            recycerlViewAdapter.notifyDataSetChanged();

        }
        dbTask.close();

        btmvgtn();

    }


    /*Bottom navigationview */
    public void btmvgtn(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.p1:
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(ProjectList.this);
                            String p_name = sharedPreferences.getString(project_name, "default value");
                            if (pname!=null){
                                if(pname.equalsIgnoreCase(p_name)){
                                    alertdialog(getString(R.string.already_current_project)+" "+pname);
                                }else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(project_name, pname);
                                    editor.apply();
                                    alertdialog(getString(R.string.project_selected)+" "+pname);
                                    Toast.makeText(ProjectList.this, getString(R.string.project_selected)+pname, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ProjectList.this, getString(R.string.select_project_first), Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.p2:
                            Intent intent = new Intent(ProjectList.this, newproject.class);
                            intent.putExtra("device_name", device_name1);
                            intent.putExtra("device_address", address);
                            intent.putExtra("device_id", device_id);
                            intent.putExtra("dgps_device_id", dgps_device_id);
                            intent.putExtra(" selectedmodeule" ,selectedmodeule);
                            startActivity(intent);
                            break;
                        case R.id.p3:

                            break;
                        case R.id.p5:
                            if(val!=null){
                                itemTypeList.remove(posit);
                                recycerlViewAdapter.notifyItemRemoved(posit);
                                dbTask.open();
                                boolean result =dbTask.deleteproject(pname,time);
                                if(result){
                                    Toast.makeText(ProjectList.this, getString(R.string.project_removed), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ProjectList.this, getString(R.string.sorry), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ProjectList.this, getString(R.string.select_project_first), Toast.LENGTH_SHORT).show();
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

        textView1.setText(getString(R.string.current_project));
        textView2.setText(msg);
        button1.setText(getString(R.string.continue1));
        button2.setText(getString(R.string.no));

        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(view -> dialogBuilder.dismiss());

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            Intent intent = new Intent(ProjectList.this,tasklist.class);
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
        String val = title + finalvalue;
    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {
        val = title +","+ finalvalue ;
        if(val!=null){
            String getpname = title.split("##")[0];
            String status = title.split("##")[1];
            pname = getpname;
            time = finalvalue;
            if(status.equalsIgnoreCase(getString(R.string.onclick))){
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(ProjectList.this);
                String p_name = sharedPreferences.getString(project_name, "default value");
                if (pname!=null){
                    if(pname.equalsIgnoreCase(p_name)){
                        alertdialog(getString(R.string.already_current_project)+" "+pname);
                    }else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(project_name, pname);
                        editor.commit();
                        alertdialog(getString(R.string.project_selected)+" "+pname);
                        Toast.makeText(ProjectList.this, getString(R.string.project_selected)+pname, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ProjectList.this, getString(R.string.select_project_first), Toast.LENGTH_SHORT).show();
                }
            }
        }
        posit=position;
    }
}
