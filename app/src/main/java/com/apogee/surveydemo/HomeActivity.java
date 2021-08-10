package com.apogee.surveydemo;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.Fragment.Surveyfragment;
import com.apogee.surveydemo.Fragment.DeviceFragment;
import com.apogee.surveydemo.Fragment.ProjectFragment;
import com.apogee.surveydemo.Generic.taskGeneric;
import com.apogee.surveydemo.model.BleModel;
import com.apogee.surveydemo.utility.BLEService;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;
import com.apogee.surveydemo.utility.DeviceScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.apogee.surveydemo.tasklist.Name;
import static com.apogee.surveydemo.utility.DeviceControlActivity.Configname;
import static com.apogee.surveydemo.utility.DeviceControlActivity.dvcstatus;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,TextToSpeech.OnInitListener {
    BottomNavigationView bottomNavigationView;
    private final static String TAG = CRS_sattelite.class.getSimpleName();
    LinearLayout satlay,btrylay,blelay;
    FrameLayout statuslay;
    ImageButton imgbtn;
    //This is our viewPager
    private ViewPager viewPager;
    ProgressDialog progressDoalog;
    ArrayList<String> checksumlist = new ArrayList<>();
    int pktno,datalenghth=0;
    int totalnoofpkts=0;
    ArrayList<String> datalist = new ArrayList<>();
    private TextView mConnectionState,bttxt,sattxt,stsssstxt;
    ImageView bleimg,btimg;
    String payloadfinal;
    boolean gnggaenable=false;
    //Fragments
    public static final String deviceaaddress = "macaddresspref";
    DeviceControlActivity dca = new DeviceControlActivity();
    DeviceFragment deviceFragment;
    ProjectFragment projectFragment;
    Surveyfragment contactFragment;
    MenuItem prevMenuItem;
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();
    private boolean mConnectedd = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    String operation = "";

    private TextToSpeech tts;
    private TextToSpeech configTTs;

    private boolean isSpeak = true;
    private boolean isInvalidSpeak = true;
    private boolean isStanaloneSpeak = true;
    private boolean isNotApplicable = true;
    private boolean isRTKSpeak = true;
    private boolean isRTKFSpeak = true;
    private boolean isEstimatedSpeak = true;
    private boolean isManualSpeak = true;
    final Handler speakhandler = new Handler();
    Boolean isFirstTime = true;
    boolean isLatLng = true;
    DatabaseOperation dbTask = new DatabaseOperation(this);

    String configName;
    String radiomodulename;
    String gnssmodulename;
    String surveyAccuracy;

   public static String device_name1="",address="";
   public static String device_id1="",dgps_device_id1;
   public static String selectedmodeule;

    public static String latitu ="";
    public static String longti = "";

    // BROADCAST RECIEVER USED FOR CONNECTION OF BLE
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnectedd = true;
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnectedd = false;
                sattxt.setText("N/A");
                bttxt.setText("N/A");
                stsssstxt.setText("N/A");
                btimg.setImageResource(R.drawable.ic_baseline_battery_unknown_24);
                updateConnectionState(R.string.disconnected);
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                imgbtn = null;
                invalidateOptionsMenu();
            }
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(HomeActivity.this);
        String p_name = sharedPreferences.getString(Name, "default value");
        mDeviceAddress = sharedPreferences.getString(deviceaaddress, "default value");
        if(!p_name.equalsIgnoreCase("default value")) {
            this.setTitle(p_name);


        }



        Bundle intent = getIntent().getExtras();
        if(intent != null){

            operation = intent.getString("mode");
            if(operation != null && !operation.isEmpty() && operation.equalsIgnoreCase(getString(R.string.auto_base))){
                dvcstatus ="31";
            }else if(operation != null && !operation.isEmpty() && operation.equalsIgnoreCase(getString(R.string.manual_base))){
                dvcstatus="31";
            }else if(operation != null && !operation.isEmpty() && operation.equalsIgnoreCase(getString(R.string.rover))){
                dvcstatus="32";

            }
        }

        final Intent intents = getIntent();
        if(intents != null){
            configName = intents.getStringExtra("configName");
            radiomodulename = intents.getStringExtra("radiomodulename");
            gnssmodulename = intents.getStringExtra("gnssmodulename");
            surveyAccuracy = intents.getStringExtra("Survey Accuracy");
            device_name1=intents.getStringExtra("device_name");
            address=intents.getStringExtra("device_address");
            device_id1=intents.getStringExtra("device_id");
            dgps_device_id1=intents.getStringExtra("dgps_device_id");
            selectedmodeule = intents.getStringExtra(" selectedmodeule");
        }



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customtoolbar);
        mConnectionState = findViewById(R.id.connection_state);
        btimg = findViewById(R.id.btimg);
        bleimg = findViewById(R.id.bleimg);
        bttxt = findViewById(R.id.bttxt);
        sattxt = findViewById(R.id.sattxt);
        stsssstxt = findViewById(R.id.stsssstxt);

        satlay = findViewById(R.id.satlay);
        btrylay = findViewById(R.id.btrylay);
        blelay = findViewById(R.id.blelay);
        statuslay = findViewById(R.id.statuslay);
        imgbtn = findViewById(R.id.btn);

        satlay.setOnClickListener(this);
        btrylay.setOnClickListener(this);
        blelay.setOnClickListener(this);
        statuslay.setOnClickListener(this);
        tts = new TextToSpeech(this, this);
        configTTs = new TextToSpeech(this,this);
        //Initializing viewPager
        viewPager = findViewById(R.id.viewpager);

        //Initializing the bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.p1:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.p2:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.p3:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


       if(!dca.mConnected){
           updateConnectionState(R.string.disconnected);
           bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
           invalidateOptionsMenu();
           mConnectedd=false;
       }else if(dca.mConnected){
           updateConnectionState(R.string.connected);
           bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
           invalidateOptionsMenu();
           mConnectedd=true;
       }


      /*  Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);*/

        setupViewPager(viewPager);
    }
    
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        projectFragment=new ProjectFragment();
        projectFragment.setListener(new ProjectFragment.onClick() {
            @Override
            public void onSuccess() {
                dialogbleDisconnect();

                  }

            @Override
            public void onConfig() {
                final SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(HomeActivity.this);
                final Intent intent = new Intent(HomeActivity.this, DeviceControlActivity.class);
              /* intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, sharedPreferences.getString(DeviceControlActivity.EXTRAS_DEVICE_NAME,""));
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, sharedPreferences.getString(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,""));
                intent.putExtra("device_id","61");
                intent.putExtra("dgps_device_id","1");*/
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", device_id1);
                intent.putExtra("dgps_device_id", dgps_device_id1);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
            }
        });
        deviceFragment=new DeviceFragment();
        contactFragment=new Surveyfragment();
        adapter.addFragment(projectFragment);
        adapter.addFragment(deviceFragment);
        adapter.addFragment(contactFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    public void alertdialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
        builder1.setTitle("About SurveyApp!");
        builder1.setMessage("Survey App Version 1.3.8"+"\n"+
                        "SurveyApp is a professional Android-based surveying software\n" +
                "developed by Apogee Precision Lasers.SurveyApp has user-friendly interfaces, which provides you a\n" +
                "convenient and effective surveying experience.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "oK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
                alertdialog();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.updatedata:
                prgrsdlg();
                Intent intentService = new Intent(HomeActivity.this, BLEService.class);
                BleModel bleModel = new BleModel(this);
                startService(intentService);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void prgrsdlg(){
        progressDoalog = new ProgressDialog(HomeActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage(getString(R.string.please_wait));
        progressDoalog.setTitle(getString(R.string.updating_database));
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progressDoalog.getProgress() <= progressDoalog
                            .getMax()) {
                        Thread.sleep(200);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDoalog.getProgress() == progressDoalog
                                .getMax()) {
                            progressDoalog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(5);
        }
    };


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.satlay:
                Toast.makeText(HomeActivity.this, getString(R.string.no_satellite_visible_here), Toast.LENGTH_LONG).show();
                break;
            case R.id.btrylay:
                Toast.makeText(HomeActivity.this, getString(R.string.your_reciever_battery_status), Toast.LENGTH_LONG).show();
                break;
            case R.id.blelay:
               /* if (mConnectedd && dca.mConnected) {
                    mBluetoothLeService.disconnect();
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                } else {
                    mBluetoothLeService.connect(mDeviceAddress);
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                }*/
                //  mBluetoothLeService.connect(mDeviceAddress,DeviceControlActivity.this,1);
               // Toast.makeText(HomeActivity.this, "Bluetooth connection status", Toast.LENGTH_LONG).show();

                /*if (mConnectedd) {
                    mBluetoothLeService.disconnect();
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                } else {
                    mBluetoothLeService.connect(mDeviceAddress);
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                }*/
                break;
            case R.id.statuslay:
                Toast.makeText(HomeActivity.this, getString(R.string.rtk_status), Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            if(data.contains("@@@@")){
                try {
                    String packet_no = data.split(",")[1];
                    pktno = Integer.parseInt(packet_no);
                    String total_no_of_packets = data.split(",")[3];
                    totalnoofpkts = Integer.parseInt(total_no_of_packets);
                    datalist.add(data);
                    if(pktno==totalnoofpkts && pktno>0){
                        dataparse(datalist);
                        datalist.clear();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else if (data.contains(getString(R.string.battery_status))) {
                bateerydata(data);
            }else{
                String[] somedata = data.split("\\r?\\n");
                int length=somedata.length;
                if(length>1){
                    for(int i=0; i<somedata.length;i++){
                        normalparse(somedata[i]);
                    }
                }
                somedata=null;
            }
            if(!gnggaenable){
                if(data.contains("$PUBX")) {
                    try {
                        //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                        //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                        String cmd[] = data.split("\\$");
                        String correction = cmd[1].split(",")[8];
                        String numsvrs = cmd[1].split(",")[18];
                        String pubxlatitude = data.split(",")[3];
                        String pubxlongitude = data.split(",")[5];
                        String pubxaltitude = data.split(",")[7];






                        if(correction.equalsIgnoreCase("G3")){
                           // if(dvcstatus!=null && dvcstatus.equalsIgnoreCase("31")){
                                stsssstxt.setText(getString(R.string.in_process));
                                imgbtn.setImageResource(R.drawable.ic_baseline_sync_24);


                            //}



                        }else  if( correction.equalsIgnoreCase("TT")) {
                            //if(dvcstatus!=null && dvcstatus.equalsIgnoreCase("31")){

                            if(isLatLng && configName!=null){
                                dbTask.open();
                                isLatLng = false;
                                long id = dbTask.getConfigurtionid(configName,gnssmodulename,radiomodulename);
                                Toast.makeText(HomeActivity.this,"id"+id,Toast.LENGTH_SHORT).show();

                                dbTask.insertlatlngparams(id,"Latitude",pubxlatitude);
                                dbTask.insertlatlngparams(id,"Longtitude",pubxlongitude);
                                dbTask.insertlatlngparams(id, "Altitude", pubxaltitude);
                                dbTask.insertlatlngparams(id, "Survey Accuracy",surveyAccuracy);
                                Toast.makeText(HomeActivity.this,"Lat/lng++"+pubxlatitude,Toast.LENGTH_SHORT).show();

                            }

                            stsssstxt.setText(getString(R.string.success));
                                imgbtn.setImageResource(R.drawable.ic_baseline_done_24);
                            //}
                        }
                        sattxt.setText(numsvrs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void dataparse(ArrayList<String> dataval){
        String actualKey = null;
        String finalpayv = null;
        int totalnoofpkts = 0,datalenghth=0,pktno=0;
        for(int i=0;i<dataval.size();i++){
            String val = dataval.get(i);
            if(val.contains("@@@@")){
                try {
                    //@@@@,1,200,2,nsangoisnisnignoignsogsogn…200bytes,####
                    final int secondLast = val.length() - 5;
                    String packet_no = val.split(",")[1];
                    pktno = Integer.parseInt(packet_no);
                    String packet_size = val.split(",")[2];
                    int pactsize = Integer.parseInt(packet_size);
                    if (pactsize > 0) {
                        int lenghth = packet_size.length();
                        if (lenghth == 1) {
                            actualKey = val.substring(11, secondLast);
                        } else if (lenghth == 2) {
                            actualKey = val.substring(12, secondLast);
                        } else if (lenghth == 3) {
                            actualKey = val.substring(13, secondLast);
                        }
                        String total_no_of_packets = val.split(",")[3];
                        totalnoofpkts = Integer.parseInt(total_no_of_packets);
                        datalenghth = actualKey.length();
                        if (pktno <= totalnoofpkts && pactsize == datalenghth) {
                            finalpayv = actualKey;
                            System.out.println(finalpayv);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if(payloadfinal!=null){
                payloadfinal = payloadfinal.concat(finalpayv);
            }else{
                payloadfinal = finalpayv;
            }
            if(pktno==totalnoofpkts && pktno>0){
                lastparse(payloadfinal);
                System.out.println(payloadfinal);
                payloadfinal=null;
            }

        }
    }

    public void lastparse(String val){
        if(val != null){
            String lines[] = val.split("\\r?\\n");
            for(int i=0; i<lines.length;i++){
                normalparse(lines[i]);
            }
            lines=null;
        }

    }

    /*Parsing the normal data here*/
    void normalparse(String data){
        try{
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                String[] cmd = data.split("\\$");
                gnggaenable=true;
                try{
                    String lati = cmd[1].split(",")[2];
                    String longi = cmd[1].split(",")[4];
                    String fix = cmd[1].split(",")[6];
                    String StatusData  ;

                    latitu = lati;
                    longti = longi;
                    if (fix.equalsIgnoreCase("0")) {
                        StatusData = getString(R.string.invalid);
                        if(isInvalidSpeak){
                            isInvalidSpeak = false;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    } else if (fix.equalsIgnoreCase("1") || fix.equalsIgnoreCase("2")) {
                        StatusData = getString(R.string.standalone_mode);
                        if(isStanaloneSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = false;
                            isNotApplicable = true;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    } else if (fix.equalsIgnoreCase("3")) {
                        StatusData = getString(R.string.not_applicable);
                        if(isNotApplicable){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = false;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    } else if (fix.equalsIgnoreCase("4")) {
                        StatusData = getString(R.string.rtk_fixed);


                        if(isRTKSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isRTKSpeak = false;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                        imgbtn.setImageResource(R.drawable.ic_baseline_done_24);
                    } else if (fix.equalsIgnoreCase("5")) {
                        StatusData = getString(R.string.rtk_float);
                        if(isRTKFSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isRTKSpeak = true;
                            isSpeak = true;
                            isRTKFSpeak = false;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                        imgbtn.setImageResource(R.drawable.ic_baseline_done_24);
                    } else if (fix.equalsIgnoreCase("6")) {
                        StatusData = getString(R.string.estimated);
                        if(isEstimatedSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isRTKSpeak = true;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = false;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    } else if (fix.equalsIgnoreCase("7")) {
                        StatusData = getString(R.string.manual_input_mode);
                        if(isManualSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isRTKSpeak = true;
                            isSpeak = true;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = false;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    } else {
                        StatusData = getString(R.string.simulation_mode);
                        if(isSpeak){
                            isInvalidSpeak = true;
                            isStanaloneSpeak = true;
                            isNotApplicable = true;
                            isRTKSpeak = true;
                            isSpeak = false;
                            isRTKFSpeak = true;
                            isEstimatedSpeak = true;
                            isManualSpeak = true;
                            speakOut(StatusData);
                        }
                        stsssstxt.setText(StatusData);
                    }




                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (data.contains(getString(R.string.battery_status))) {
                /*Check batterystatus string*/
                bateerydata(data);
            }else if(data.contains("$PUBX")) {
                try {
                    //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                    //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                    String correction = data.split(",")[8];
                    String numsvrs = data.split(",")[18];
                   String pubxlatitude = data.split(",")[3];
                   String pubxlongitude = data.split(",")[5];
                    String pubxaltitude = data.split(",")[7];
                    latitu=pubxlatitude;
                    longti=pubxlongitude;
                    if(correction.equalsIgnoreCase("G3")){
                        if(dvcstatus!=null && dvcstatus.equalsIgnoreCase("31")){
                            stsssstxt.setText(getString(R.string.in_process));
                            imgbtn.setImageResource(R.drawable.ic_baseline_sync_24);
                        }
                    }else  if( correction.equalsIgnoreCase("TT")) {

                        if(dvcstatus!=null && dvcstatus.equalsIgnoreCase("31")){
                            if(isLatLng && configName!=null){
                                dbTask.open();
                              isLatLng  = false;
                                long id = dbTask.getConfigurtionid(configName,gnssmodulename,radiomodulename);
                                dbTask.insertlatlngparams(id,"Latitude",pubxlatitude);
                                dbTask.insertlatlngparams(id,"Longtitude",pubxlongitude);
                                dbTask.insertlatlngparams(id,"Altitude",pubxaltitude);
                                dbTask.insertlatlngparams(id, "Survey Accuracy",surveyAccuracy);
                                    }

                            stsssstxt.setText(getString(R.string.success));
                            imgbtn.setImageResource(R.drawable.ic_baseline_done_24);
                        }
                    }
                    sattxt.setText(numsvrs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void bateerydata(String data){
        if(data!=null){
            if(data.contains("Battery Status:2.")){
                btimg.setImageResource(R.drawable.batteryone);
                bttxt.setText(getString(R.string.battery));
                stopSpeak();
            }else if(data.contains("Battery Status:3.")){
                btimg.setImageResource(R.drawable.batterytwo);
                bttxt.setText(getString(R.string.battery));
                stopSpeak();
            }else if(data.contains("Battery Status:4.")){
                btimg.setImageResource(R.drawable.batterythree);
                bttxt.setText(getString(R.string.battery));
                stopSpeak();
            }else if(data.contains("Battery Status:5.")){
                btimg.setImageResource(R.drawable.batteryfour);
                bttxt.setText(getString(R.string.battery));
                stopSpeak();
            }else if(data.contains("Battery Status:1.")){
                btimg.setImageResource(R.drawable.ic_battery_alert_black_24dp);
                bttxt.setText(getString(R.string.battery));
                if(isFirstTime) {
                    isFirstTime = false;
                    delayBatteryStatus();
                }

                //Battery Status: Charging
            }else if(data.contains("Battery Status: Charging")){
                btimg.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
                bttxt.setText(getString(R.string.charging));
                stopSpeak();
            }else if(data.contains("Battery Status: Fully Charged")){
                btimg.setImageResource(R.drawable.batteryfive);
                bttxt.setText(getString(R.string.fully_charged));
                stopSpeak();
            }
            else if(data.contains("Battery Status:6.")){
                btimg.setImageResource(R.drawable.batteryfive);
                bttxt.setText(getString(R.string.charging));
                stopSpeak();
            }
        }
    }

    private void stopSpeak(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private void delayBatteryStatus(){
        speakhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Write whatever to want to do after delay specified (1 sec)
                Log.d("Handler", "Running Handler");
                batteryLowAudio(getString(R.string.battery_low));

                delayBatteryStatus();
            }
        }, 60000);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            /* mBluetoothLeService.connect(mDeviceAddress);*/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            // final boolean result = mBluetoothLeService.connect(mDeviceAddress,CRS_sattelite.this,device_id,opid);
            //  Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
       stopSpeak();
        if (configTTs != null) {
            configTTs.stop();
            configTTs.shutdown();
        }
        super.onDestroy();
        if(mBluetoothLeService!=null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
             //   Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
            } /*else {
                button.setEnabled(true);
            }*/

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.init_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void speakOut(String msg) {
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        configTTs.speak(msg,TextToSpeech.QUEUE_FLUSH, params, "Dummy String");

    }

    private void batteryLowAudio(String msg){
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }


    public void dialogbleDisconnect(){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_disconnect_alert, null);
        final TextView t1 = dialogView.findViewById(R.id.t1);
        final Button button = dialogView.findViewById(R.id.yes);
        final Button cancel = dialogView.findViewById(R.id.cancel);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService = null;
                Intent intent = new Intent(HomeActivity.this, DeviceScanActivity.class);
                intent.putExtra("device_name", device_name1);
                intent.putExtra("device_address", address);
                intent.putExtra("device_id", device_id1);
                intent.putExtra("dgps_device_id", dgps_device_id1);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
                finish();
                DeviceControlActivity.finishActivity.finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_exit_app, null);
        final TextView exit = dialogView.findViewById(R.id.exit);
        final TextView button = dialogView.findViewById(R.id.turnof);
        final TextView cancel = dialogView.findViewById(R.id.cancel);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   finish();
                dialogBuilder.dismiss();
                finishAffinity();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService = null;
             //   finish();
                dialogBuilder.dismiss();
                finishAffinity();
               // DeviceControlActivity.finishActivity.finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

}
