package com.apogee.surveydemo.Data;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.apogee.surveydemo.Connect;
import com.apogee.surveydemo.IPActivity;
import com.apogee.surveydemo.LatLon2UTM;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.utility.BLEService;


import org.jetbrains.annotations.NotNull;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {
    /*Splash variables declared here*/
    public static final int RequestPermissionCode = 7;
    Button btn,btn_ip;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        btn= findViewById(R.id.btn_letstart);
        btn_ip = findViewById(R.id.btn_ip);
        LatLon2UTM latLon2UTM = new LatLon2UTM();
        latLon2UTM.convertLatLonToUTM(22.869635, 72.593179);
        RequestMultiplePermission();
        CheckingPermissionIsEnabledOrNot();
        /*SAving package name for check first time installation*/
        prefs = getSharedPreferences("com.apogee.surveydemo", MODE_PRIVATE);
        /*Button click event*/
        btn.setOnClickListener(view -> {
            Intent i = new Intent(SplashActivity.this, Connect.class);
            finish();
            startActivity(i);
        });
        btn_ip.setOnClickListener(v -> {
            Intent i = new Intent(SplashActivity.this, IPActivity.class);
            startActivity(i);
        });

    }

    /*Check first run in onResume*/
    @Override
    protected void onResume() {
        super.onResume();
        isNetworkConnectionAvailable();
        if (prefs.getBoolean("firstrun", true)) {
            Intent intentService = new Intent(SplashActivity.this, BLEService.class);
            startService(intentService);
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }


    /*CheckNetworkConnection*/
    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.noconnection);
        builder.setTitle(getText(R.string.no_internet_connection));
        builder.setMessage(getText(R.string.please_turn_on_internet_connection_to_continue));
        builder.setNegativeButton(getText(R.string.ok), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
        }
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                        ACCESS_FINE_LOCATION,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_COARSE_LOCATION,
                        ACCESS_NETWORK_STATE
                }, RequestPermissionCode);

    }

    // overriden method.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String permissions[], @NotNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean LocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean RExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean WExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean Location2Permission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                boolean ntwrkstatePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                if (LocationPermission && RExternalStoragePermission && WExternalStoragePermission && Location2Permission && ntwrkstatePermission) {
                    Toast.makeText(SplashActivity.this, getText(R.string.permission_granted), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashActivity.this, getText(R.string.permission_denied), Toast.LENGTH_LONG).show();

                }
            }
        }
    }
    //  Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {


        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int sixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);


        return
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED&&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                sixthPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }

}
