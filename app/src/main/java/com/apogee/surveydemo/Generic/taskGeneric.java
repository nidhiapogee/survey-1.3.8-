package com.apogee.surveydemo.Generic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.HomeActivity;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.utility.DeviceScanActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.apogee.surveydemo.newproject.project_name;

public class taskGeneric extends AppCompatActivity {

    TextView titlepage, addtitle, adddesc, adddate;
    EditText titledoes, descdoes, datedoes,aboutyourself;
    Button btnSaveTask, btnCancel;
    Integer doesNum = new Random().nextInt();
    String keydoes = Integer.toString(doesNum);
    DatabaseOperation dbTask = new DatabaseOperation(taskGeneric.this);
    SharedPreferences sharedPreferences;
    public static final String mypreference = "taskpref";
    public static final String Name = "nameKey";
    String device_name1="",address="";
    String device_id="",dgps_device_id;
     String selectedmodeule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_generic);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        titlepage = findViewById(R.id.titlepage);

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);

        titledoes = findViewById(R.id.titledoes);
        descdoes = findViewById(R.id.descdoes);
        datedoes = findViewById(R.id.datedoes);
        aboutyourself = findViewById(R.id.aboutyourself);

        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
       selectedmodeule = intent.getStringExtra(" selectedmodeule");

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String p_name = sharedPreferences.getString(project_name, "default value");
        dbTask.open();
        final int prjctid = dbTask.getprojectid(p_name);
        final int[] ids = new int[]
                {
                        R.id.titledoes,
                        R.id.descdoes,
                        R.id.aboutyourself
                };
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String currentDateTime = dateFormat.format(new Date()); // Find todays date
        datedoes.setText(currentDateTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);

                btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title =   titledoes.getText().toString();
                String desc =    descdoes.getText().toString();
                String whoareyou =    aboutyourself.getText().toString();
                String date =    datedoes.getText().toString();

                if(!validateEditText(ids))
                {
                   
                    //if not empty do something


                    dbTask.open();
                    boolean resulllt = dbTask.columnExists(title);
                    if(!resulllt){
                        boolean result = dbTask.inserttask(title, whoareyou, desc,date,prjctid);
                        if (result) {
                            System.out.println("Data inserted");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Name, title);
                            editor.commit();
                            alertdialog(getString(R.string.your_task)+" "+Name);
                            Toast.makeText(taskGeneric.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(taskGeneric.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                        }
                        dbTask.close();
                    }else{
                        Toast.makeText(taskGeneric.this, getString(R.string.sorry_title_already_exist), Toast.LENGTH_SHORT).show();

                    }

                }else{
                     //if empty do somethingelse
                     Toast.makeText(taskGeneric.this, getString(R.string.select_all_fields_first), Toast.LENGTH_SHORT).show();
                }
            }
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

        textView1.setText(getString(R.string.current_task));
        textView2.setText(msg);
        button1.setText(getString(R.string.continue1));
        button2.setText(getString(R.string.no));

        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(taskGeneric.this,HomeActivity.class);
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", device_id);
                intent.putExtra("dgps_device_id", dgps_device_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
                dialogBuilder.dismiss();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Intent intent = new Intent(taskGeneric.this, DeviceScanActivity.class);
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", device_id);
                intent.putExtra("dgps_device_id", dgps_device_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
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

    public boolean validateEditText(int[] ids)
    {
        boolean isEmpty = false;

        for(int id: ids)
        {
            EditText et = findViewById(id);

            if(TextUtils.isEmpty(et.getText().toString()))
            {
                et.setError(getString(R.string.must_enter_value));
                isEmpty = true;
            }
        }
        return isEmpty;
    }
}