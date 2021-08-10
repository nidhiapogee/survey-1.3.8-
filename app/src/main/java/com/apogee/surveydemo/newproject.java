package com.apogee.surveydemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.bean.BleBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*This class all about creating a new project for survey.*/
public class newproject extends AppCompatActivity {
    EditText ep1,ep2,ep3;
    Button btn;
    //String item;
    Spinner datumspinner;
    ArrayList<String> datumlist;
    DatabaseOperation dbTask;
    public static String datumitem;
    SharedPreferences sharedPreferences;
    public static final String project_name = "prjctnameKey";
    Toolbar toolbar;
    String device_name1="",address="";
    String device_id="",dgps_device_id;
    String selectedmodeule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newproject);
        dbTask=new DatabaseOperation(newproject.this);
        dbTask.open();
        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
        selectedmodeule =  intent.getStringExtra(" selectedmodeule" );
        ep1 = findViewById(R.id.ep1);
        ep2 = findViewById(R.id.ep2);
        ep3 = findViewById(R.id.ep3);
        btn = findViewById(R.id.okbutton);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String currentDateTime = dateFormat.format(new Date()); // Find todays date
        ep1.setText(currentDateTime);
        datumspinner = findViewById(R.id.sp1);
        //Creating the instance of ArrayAdapter containing list of countries name
        toolbar=findViewById(R.id.tool);
        toolbar.setTitle(getString(R.string.new_project));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        final int[] ids = new int[]
                {
                        R.id.ep1,
                        R.id.ep2,
                        R.id.ep3
                };
        datumlist=new ArrayList<>();
        datumlist.add("--Select--");
        datumlist = dbTask.getdatum();

        final ArrayAdapter<String> model_typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datumlist);
        model_typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datumspinner.setAdapter(model_typeAdapter);

        datumspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                datumitem = parent.getItemAtPosition(position).toString();
                BleBean bleBean = new BleBean();
                bleBean.setDatumselection(datumitem);


            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn.setOnClickListener(v -> {

            String p_name;
            String oprtr;

            p_name  = ep1.getText().toString();
            oprtr  = ep3.getText().toString();

            if(!validateEditText(ids) && !datumitem.equalsIgnoreCase("--Select--")){
                if(p_name!=null || oprtr!=null){
                    boolean result = dbTask.insertmainproject(p_name, oprtr, datumitem);
                    if (result) {
                        System.out.println("Data inserted");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(project_name, p_name);
                        editor.apply();
                        Intent intent1 = new Intent(newproject.this, tasklist.class);
                        intent1.putExtra("device_name", device_name1);
                        intent1.putExtra("device_address", address);
                        intent1.putExtra("device_id", device_id);
                        intent1.putExtra("dgps_device_id", dgps_device_id);
                        intent1.putExtra(" selectedmodeule" ,selectedmodeule);
                        startActivity(intent1);
                        Toast.makeText(newproject.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(newproject.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                    }
                    dbTask.close();
                }

            }else{
                Toast.makeText(newproject.this, getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show();
            }

        });


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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
