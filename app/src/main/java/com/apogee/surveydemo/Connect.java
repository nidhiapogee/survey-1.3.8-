package com.apogee.surveydemo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.apogee.surveydemo.Fragment.User;
import com.apogee.surveydemo.model.Operation;


public class Connect extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener  {
    Switch simpleSwitch1;

    public SharedPreferences sharedPreferences;
    public String SHAREDPREFCONSTANT = "DeviceBleInfo";
    public SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        sharedPreferences = getSharedPreferences(SHAREDPREFCONSTANT, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadFragment(new User());
        simpleSwitch1 = (Switch) findViewById(R.id.simpleSwitch1);
        simpleSwitch1.setVisibility(View.INVISIBLE);


    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit(); // save the changes
    }

   /*     public  void showDialog() {
        dialog = new Dialog(Connect.this);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_connect);
        ImageView iv_createnew = dialog.findViewById(R.id.iv_createanew);
        iv_createnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               loadFragment(new User());
            // initiate view's

//
               simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(simpleSwitch1.isChecked()){
                    loadFragment(new Developer());
                    simpleSwitch1.setText("For Developer");
                    Toast.makeText(com.example.surveydemo.Connect.this, "For Developer", Toast.LENGTH_SHORT).show();
                }else{
                    loadFragment(new User());
                    simpleSwitch1.setText("For User");
                    Toast.makeText(com.example.surveydemo.Connect.this, "For User", Toast.LENGTH_SHORT).show();
                }

               }
               });

               dialog.dismiss();
                simpleSwitch1.setVisibility(View.VISIBLE);
            }
        });
        ImageView iv_chooseoption = dialog.findViewById(R.id.iv_chooseoption);
        iv_chooseoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(new ItemFragment());
                dialog.dismiss();
            }

        });

        ImageView iv_bydeviceId = dialog.findViewById(R.id.iv_bydeviceId);
        iv_bydeviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Intent intent = new Intent(com.example.surveydemo.Connect.this, ScanBydeviceId.class);
           startActivity(intent);
            dialog.dismiss();

            }
        });


        dialog.show();
    }*/

    @Override
    public void onListFragmentInteraction(Operation operation) {
        Intent intent = new Intent(Connect.this, ProjectList.class);
        intent.putExtra("device_name", operation.getDevicename());
        intent.putExtra("device_address", operation.getDevice_address());
        intent.putExtra("device_id", String.valueOf(operation.getBleid()));
        intent.putExtra("dgps_device_id", String.valueOf(operation.getDgpsid()));
        startActivity(intent);
    }

  /*  @Override
    public void onBackPressed() {
       Intent intent = new Intent(Connect.this,HomeActivity.class);
       startActivity(intent);
        dialog.dismiss();
    }*/


}

