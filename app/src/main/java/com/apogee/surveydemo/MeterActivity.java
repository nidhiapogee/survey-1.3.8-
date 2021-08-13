package com.apogee.surveydemo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apogee.surveydemo.Generic.GPSTrack;
import com.apogee.surveydemo.adapter.MeterAdapter;
import com.apogee.surveydemo.retrofit.ApiClient;
import com.apogee.surveydemo.retrofit.ApiInterface;
import com.apogee.surveydemo.retrofitModel.HelloImageModel;
import com.apogee.surveydemo.retrofitModel.HelloModel;
import com.apogee.surveydemo.retrofitModel.ImageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.apogee.surveydemo.Configuration.ADD_USER_URL;
import static com.apogee.surveydemo.Configuration.KEY_ACTION;
import static com.apogee.surveydemo.Configuration.KEY_ADDRESS;
import static com.apogee.surveydemo.Configuration.KEY_FUNCTIONAL;
import static com.apogee.surveydemo.Configuration.KEY_IMAGE;
import static com.apogee.surveydemo.Configuration.KEY_IVRS;
import static com.apogee.surveydemo.Configuration.KEY_LATITUDE;
import static com.apogee.surveydemo.Configuration.KEY_LONGTITUDE;
import static com.apogee.surveydemo.Configuration.KEY_METER;
import static com.apogee.surveydemo.Configuration.KEY_NUMBER;
import static com.apogee.surveydemo.Configuration.KEY_PHASE;
import static com.apogee.surveydemo.Configuration.KEY_POLE;
import static com.apogee.surveydemo.Configuration.KEY_READING;
import static com.apogee.surveydemo.Configuration.KEY_TIMESTAMP;

public class MeterActivity extends AppCompatActivity {

    FloatingActionButton fab_camera;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_REQUEST = 1888;
    ArrayList<Bitmap> imageList = new ArrayList<>();
    private  MeterAdapter meterAdapter;
    RecyclerView recycler_view;
    Button syncSave, neww,ViewRoute;


    TextInputEditText pole_number,IVRS_number,meter_reading,meter_number,meter_address;
    RadioGroup rg_meter,rg_meter_phase,rg_functional;
    PreferenceStore preferenceStore;
    String latlng , latitude, longtitude;
    ProgressBar progress;
    String currentDate;

    RadioButton rb;
    LatLon2UTM latLon2UTM = new LatLon2UTM();
    static final String TAG = "MeterActivity.java";
    GPSTrack gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);
        fab_camera = findViewById(R.id.fab_camera);
        meterAdapter = new MeterAdapter(this, new ArrayList<>());
        recycler_view =  findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(meterAdapter);
        meterAdapter.setListerner(pos -> imageList.remove(pos));
        syncSave = findViewById(R.id.syncSave);
        pole_number = findViewById(R.id.pole_number);
        IVRS_number = findViewById(R.id.IVRS_number);
        rg_meter = findViewById(R.id.rg_meter);
        rg_meter_phase = findViewById(R.id.rg_meter_phase);
        rg_functional = findViewById(R.id.rg_functional);
        meter_reading = findViewById(R.id.meter_reading);
        meter_number = findViewById(R.id.meter_number);
        meter_address = findViewById(R.id.meter_address);
        progress = findViewById(R.id.progress);
        neww = findViewById(R.id.neww);
        ViewRoute = findViewById(R.id.ViewRoute);
        preferenceStore = new PreferenceStore(this);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
         currentDate = df.format(c.getTime());
        try {
            if(!preferenceStore.getLatitude().isEmpty() && !preferenceStore.getLongtitude().isEmpty()){
                latlng  = latLon2UTM.latlngcnvrsn(preferenceStore.getLatitude(),preferenceStore.getLongtitude());
                if(latlng != null && !latlng.isEmpty()){
                    String[] spilitString = latlng.split("_");
                    latitude = spilitString[0];
                    longtitude = spilitString[1];
                }
            }else {
                showAlertDialog();
            }

        } catch(NumberFormatException ex){
            // handle exception
        }



        fab_camera.setOnClickListener(v -> {
            if (checkPermission()) {
                //main logic or main code

                // . write your main code to execute, It will execute if the permission is already given.

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else {
                requestPermission();
            }
        });


        syncSave.setOnClickListener(v -> {
            if(isValidate()){
                synSpreadsheet();
            }

        });


       rg_meter.setOnCheckedChangeListener((group, checkedId) -> {
           rb= findViewById(checkedId);
           if(rb.getText().equals("YES")){
               meter_number.setVisibility(View.VISIBLE);
               meter_reading.setVisibility(View.VISIBLE);
           }else  if(rb.getText().equals("NO")){
               meter_number.setVisibility(View.GONE);
               meter_reading.setVisibility(View.GONE);
           }
       });




        neww.setOnClickListener(v -> {
            pole_number.getText().clear();
            IVRS_number.getText().clear();
            meter_reading.getText().clear();
            meter_number.getText().clear();
            meter_address.getText().clear();
            imageList.clear();
            meterAdapter.add(imageList);
        });



        ViewRoute.setOnClickListener(v -> {
            try {
                viewRouteMap(latlng);
            } catch (Exception e) {
                Log.d(TAG, "Exception In enter button listener: " + e);
            }
        });


    }

    protected void viewRouteMap(String latlon) {
        try {
            gps = new GPSTrack(this);
            if(!preferenceStore.getLatitude().isEmpty() && !preferenceStore.getLongtitude().isEmpty()){
                latlng  = latLon2UTM.latlngcnvrsn(preferenceStore.getLatitude(),preferenceStore.getLongtitude());
                if(latlng != null && !latlng.isEmpty()){
                    String[] spilitString = latlng.split("_");
                    latitude = spilitString[0];
                    longtitude = spilitString[1];

                }
            }
            Intent intent;
               String latlng = latlon.replace("_",",");
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + gps.getLatitude() + "," + gps.getLongitude()
                                + "&daddr=" + latlng));


            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Exception in viewRouteMap: " + e);
        }
    }



    private void showAlertDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.lat_lng_alert));

        alertDialog.show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageList.add(photo);
            meterAdapter.add(photo);


        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();

                // main logic
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel(getString(R.string.you_need_to_allow_access_permissions), (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission();
                            }
                        });
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MeterActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private boolean isValidate(){
      if(pole_number.getText().toString().trim().isEmpty()){
          Toast.makeText(MeterActivity.this,getText(R.string.please_enter_pole_number),Toast.LENGTH_SHORT).show();
          return false;
      }else if(IVRS_number.getText().toString().trim().isEmpty()){
          Toast.makeText(MeterActivity.this,getText(R.string.please_enter_ivrs_number),Toast.LENGTH_SHORT).show();
          return false;
      }else if(meter_reading.getText().toString().trim().isEmpty() && rb.getText().equals("Yes")){
          Toast.makeText(MeterActivity.this,getText(R.string.please_enter_meter_reading),Toast.LENGTH_SHORT).show();
          return false;
      }else if(meter_number.getText().toString().trim().isEmpty()&& rb.getText().equals("Yes")){
          Toast.makeText(MeterActivity.this,getText(R.string.please_enter_meter_number),Toast.LENGTH_SHORT).show();
          return false;
      }else if(meter_address.getText().toString().trim().isEmpty()){
          Toast.makeText(MeterActivity.this,getText(R.string.please_enter_meter_address),Toast.LENGTH_SHORT).show();
          return false;
      }else if(imageList == null || imageList.size() == 0){
          Toast.makeText(MeterActivity.this,getText(R.string.please_select_image),Toast.LENGTH_SHORT).show();
          return false;
      }else if(!preferenceStore.isbtConnected()){
          showAlertDialog();
          return false;
      } else if(!isNetworkConnectionAvailable()){
          checkNetworkConnection();
          return false;
      }else {
          return true;
      }

    }

    private void synSpreadsheet(){
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        if(!preferenceStore.getLatitude().isEmpty() && !preferenceStore.getLongtitude().isEmpty()){
            latlng  = latLon2UTM.latlngcnvrsn(preferenceStore.getLatitude(),preferenceStore.getLongtitude());
            if(latlng != null && !latlng.isEmpty()){
                String[] spilitString = latlng.split("_");
                latitude = spilitString[0];
                longtitude = spilitString[1];
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,ADD_USER_URL,
                response -> {
                    loading.dismiss();
                    sync();
                    Toast.makeText(MeterActivity.this,response,Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(MeterActivity.this,error.toString(),Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                int selectedId= rg_meter.getCheckedRadioButtonId();
                RadioButton rb_meter = findViewById(selectedId);
                int meter_phase = rg_meter_phase.getCheckedRadioButtonId();
                RadioButton rb_meter_phase = findViewById(meter_phase);
                int functional = rg_functional.getCheckedRadioButtonId();
                RadioButton rb_functional = findViewById(functional);
                params.put(KEY_ACTION,"insert");

                params.put(KEY_POLE,pole_number.getText().toString().trim());
                params.put(KEY_IVRS, IVRS_number.getText().toString().trim());
                params.put(KEY_METER,rb_meter.getText().toString().trim());
                params.put(KEY_PHASE,rb_meter_phase.getText().toString().trim());
                params.put(KEY_FUNCTIONAL,rb_functional.getText().toString().trim());
                params.put(KEY_READING,meter_reading.getText().toString().trim());
                params.put(KEY_NUMBER,meter_number.getText().toString().trim());
                params.put(KEY_ADDRESS,meter_address.getText().toString().trim());
                params.put(KEY_LATITUDE,latitude);
                params.put(KEY_LONGTITUDE,longtitude);
                params.put(KEY_TIMESTAMP,currentDate);
                for (int i=0 ; i< imageList.size();i++){
                    params.put(KEY_IMAGE,bytearrayconverter(imageList.get(i)));
                }


                return params;
            }

        };

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
    }

    private void sync(){

            try {
                progress.setVisibility(View.VISIBLE);
                int selectedId= rg_meter.getCheckedRadioButtonId();
               RadioButton rb_meter = findViewById(selectedId);
               int meter_phase = rg_meter_phase.getCheckedRadioButtonId();
               RadioButton rb_meter_phase = findViewById(meter_phase);
               int functional = rg_functional.getCheckedRadioButtonId();
               RadioButton rb_functional = findViewById(functional);

               HelloModel helloModel = new HelloModel("",rb_meter.getText().toString().trim(),"Tube Well","","","","",
                        "","","","","",rb_functional.getText().toString().trim(),meter_number.getText().toString().trim(),pole_number.getText().toString().trim(),"",""
                ,"","","","","","",currentDate,"",IVRS_number.getText().toString().trim(),"","","",
                        "","","","","","","","","","",
                        rb_meter_phase.getText().toString().trim(),meter_reading.getText().toString().trim(),latitude,longtitude,pole_number.getText().toString().trim(),"","","","","",meter_address.getText().toString().trim());

                ApiClient apiClient = new ApiClient();
                ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);
                 Call<String> call =apiService.hello(helloModel);
                 call.enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(Call<String> call, Response<String> response) {
                       String res = response.body();
                       if(res != null  && res.equals("success")){
                           Toast.makeText(MeterActivity.this, getText(R.string.please_wait_image_syncing),Toast.LENGTH_SHORT).show();
                           syncImage();
                       }else {
                           Toast.makeText(MeterActivity.this, "Having Some Issue!! Please try again later",Toast.LENGTH_SHORT).show();

                       }
                     }

                     @Override
                     public void onFailure(Call<String> call, Throwable t) {
                         progress.setVisibility(View.GONE);
                     }
                 });
            } catch ( Exception ex) {
                ex.printStackTrace();
            }
        }

    private void syncImage(){

        try {

             int counter = 0;
            ArrayList<ImageModel> imgModelList = new ArrayList<>();
            for (Bitmap bitmap : imageList) {
                // do something with object
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                long tsLong = System.currentTimeMillis()/1000;
                String ts = Long.toString(tsLong);
                counter++;
                ImageModel imageModel = new ImageModel(imgString,ts+"_"+counter+".jpg","survey");
                imgModelList.add(imageModel);
            }

            HelloImageModel helloModel = new HelloImageModel(IVRS_number.getText().toString().trim(),"Tube Well",currentDate,imgModelList);
            ApiClient apiClient = new ApiClient();
            ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);
            Call<String> call =apiService.helloImage(helloModel);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progress.setVisibility(View.GONE);
                    String res = response.body();

                    if(res != null ){
                        Toast.makeText(MeterActivity.this, getText(R.string.register_successfully


                        ),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MeterActivity.this, getText(R.string.something_went_wrong_new


                        ),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                }
            });
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
    }



    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        return  isConnected;
    }

    /*CheckNetworkConnection*/
    public void checkNetworkConnection(){
        android.app.AlertDialog.Builder builder =new android.app.AlertDialog.Builder(this);
        builder.setIcon(R.drawable.noconnection);
        builder.setTitle(getText(R.string.no_internet_connection));
        builder.setMessage(getText(R.string.please_turn_on_internet_connection_to_continue));
        builder.setNegativeButton(getText(R.string.ok), (dialog, which) -> dialog.dismiss());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    String bytearrayconverter(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        return imgString;
    }


}
