package com.apogee.surveydemo.utility;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.HomeActivity;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.RoverConfigsNewActivity;
import com.apogee.surveydemo.tasklist;

import java.util.ArrayList;
import java.util.List;
public class DeviceScanActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    DatabaseOperation dbTask;
    private BluetoothGatt mGatt;
    public static final int RequestPermissionCode = 1;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private static final long SCAN_PERIOD = 3000;
    BluetoothLeScanner bluetoothLeScanner;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    List<String> ls = new ArrayList<>();
    TextView textView;
    private BluetoothAdapter mBtAdapter;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    String device_name1="",address="";
    private final static int REQUEST_ENABLE_BT = 1;
    boolean is_deviceAvailable=false;
    boolean reSearch=true;
    String device_id="",dgps_device_id;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    public static final String deviceaaddress = "macaddresspref";
    public static final String devicename = "devicenamepref";
    public static final String  EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    String selectedmodeule = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_device_scan);
        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        toolbar=findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.device_scan));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        ListView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
       selectedmodeule =  intent.getStringExtra(" selectedmodeule");
        initialiseBluetooth();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            scanLeDevice(true);
        }
        if (!checkPermission()) {
            requestPermission();
        } else {
            scanLeDevice(true);
        }
        locationcheck();
    }

    void locationcheck(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
            Button button1 = dialogView.findViewById(R.id.positive);
            Button button2 = dialogView.findViewById(R.id.negativebutton);
            TextView textView1 = dialogView.findViewById(R.id.header);
            TextView textView2 = dialogView.findViewById(R.id.messaggg);

            textView1.setText(getString(R.string.location));
            textView2.setText(getString(R.string.location_not_enabled));
            button1.setText(getString(R.string.open_setting));
            button2.setText(getString(R.string.cancel));
            dialogBuilder.setCancelable(true);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
              /*  Intent intent = new Intent(tasklist.this,HomeActivity.class);
                startActivity(intent);*/
                    dialogBuilder.dismiss();
                }
            });

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBuilder.dismiss();
                    DeviceScanActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void initialiseBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void scanclk(View view) {
        mNewDevicesArrayAdapter.clear();
        scanLeDevice(true);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(DeviceScanActivity.this, new
                String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, RequestPermissionCode);
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!ls.contains(""+device))
                            {
                                        is_deviceAvailable = true;
                                        ls.add("" + device);
                                        if(device.getName() != null /*&& device.getName().startsWith("G_")*/){
                                            mNewDevicesArrayAdapter.insert(device.getName() + "\n" + device.getAddress(),0  );
                                        }


                            }

                        }
                    });
                }
            };



    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            // mBtAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            showAlert2(device.getName(), device.getAddress());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EXTRAS_DEVICE_NAME, device.getName());
            editor.putString(EXTRAS_DEVICE_ADDRESS, device.getAddress());
            editor.commit();
        }
    };

    @Override
    public void onBackPressed() {
        showAlert();
    }

    public void showAlert() {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);

        textView1.setText(getString(R.string.back_options));
        textView2.setText(getString(R.string.which_page_you_want_to_go));
        button1.setText(getString(R.string.task_page));
        button2.setText(getString(R.string.home_page));

        dialogBuilder.setCancelable(true);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Intent intent = new Intent(DeviceScanActivity.this, tasklist.class);
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", device_id);
                intent.putExtra("dgps_device_id", dgps_device_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceScanActivity.this,HomeActivity.class);
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", String.valueOf(device_id));
                intent.putExtra("dgps_device_id", dgps_device_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
                dialogBuilder.dismiss();
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

    public void showAlert2(final String devicename, final String deviceaaddress) {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);

        textView1.setText(getString(R.string.work_options));
        textView2.setText(getString(R.string.open_saved_configuration));
        button1.setText(getString(R.string.open));
        button2.setText(getString(R.string.skip));

        dialogBuilder.setCancelable(true);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                final Intent intent = new Intent(DeviceScanActivity.this, RoverConfigsNewActivity.class);
                intent.putExtra("device_name", devicename);
                intent.putExtra("device_address", deviceaaddress);
                /*intent.putExtra("device_id","61");
                intent.putExtra("dgps_device_id","1");*/
                intent.putExtra("device_id",device_id);
                intent.putExtra("dgps_device_id",dgps_device_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                intent.putExtra("device_name", devicename);
                intent.putExtra("device_address", deviceaaddress);
       /*         intent.putExtra("device_id","61");
                intent.putExtra("dgps_device_id","1");*/
                intent.putExtra("device_id",device_id);
                intent.putExtra("dgps_device_id",dgps_device_id);
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


}
