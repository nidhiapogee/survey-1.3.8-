package com.apogee.surveydemo;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.apogee.surveydemo.Sattelite.AzimElev;
import com.apogee.surveydemo.Sattelite.CircularTextView;
import com.apogee.surveydemo.Sattelite.Skymodel;
import com.apogee.surveydemo.Sattelite.Skyview;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class CRS_sattelite extends AppCompatActivity {
    /*Variables defined here*/
    FloatingTextButton sats,position,hzpcn,vtpcn,hdop,vdop,listing;
    private final static String TAG = CRS_sattelite.class.getSimpleName();
    String payloadfinal;
    TextView stgps,stglonass,stsbas,stgalieleo,stbeidou;
    int pktno=0;
    int totalnoofpkts=0;
    int SBAS=0;
    int gps=0;
    int galileo=0;
    int beidou=0;
    int glonass=0;
    public ArrayList<String> datalist = new ArrayList<>();
    public ArrayList<Skymodel> mDataSet = new ArrayList<>();
    ArrayList<String> total = new ArrayList<>();
    Skyview mRadarCustom;
    Toolbar toolbar;
    ProgressBar progressBar;
   // TextView sats,position,hzpcn,vtpcn,hdop,vdop;
    private AzimElev mLatCenter = new AzimElev(0, 0);
    DeviceControlActivity dle = new DeviceControlActivity();
    String item;
    public List<String> newCommandList = new ArrayList<>();
    public List<String> delaylist = new ArrayList<>();
    public List<String> newCommandFormatList = new ArrayList<>();
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public  ArrayList<String> drawList = new ArrayList<>();
    boolean isFirstTime = true;


    /*Broadcast Reciever for getting Action Data*/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //connect.setText("Disconnect");
                Toast.makeText(context, getString(R.string.your_connection_request_successfully), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //connect.setText("Connect");
                Toast.makeText(context, getString(R.string.your_connection_request_fail), Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "SD Card not readable", Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crs_sattelite);
        toolbar=findViewById(R.id.tool);
        toolbar.setTitle(getText(R.string.sky_plot));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mRadarCustom = findViewById(R.id.mRadarCustom);
        progressBar = findViewById(R.id.progress);
        sats = findViewById(R.id.sats);
        position = findViewById(R.id.postn);
        hzpcn = findViewById(R.id.hzpcn);
        vtpcn = findViewById(R.id.vtpcn);
        hdop = findViewById(R.id.hdop);
        vdop = findViewById(R.id.vdop);
        stgps = findViewById(R.id.stgps);
        stglonass = findViewById(R.id.stglonass);
        stsbas = findViewById(R.id.stsbas);
        stgalieleo = findViewById(R.id.stgalieleo);
        stbeidou = findViewById(R.id.stbeidou);
        listing = findViewById(R.id.listing);
        progressBar.setVisibility(View.VISIBLE);

        listing.setOnClickListener(v -> {
          Intent intent = new Intent(CRS_sattelite.this, CRSList.class);
            intent.putStringArrayListExtra("drawList", (ArrayList<String>) drawList);
            startActivity(intent);
        });
        /*Thred for enabling GSV string*/
        try{
            handlerrequest();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.seems_you_are_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    void handlerrequest(){
        delaylist.add("100");
        newCommandList.add("B562068A090000010000C50091200111FE");
        newCommandFormatList.add("hex");

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mBluetoothLeService.send( item,CRS_sattelite.this, false, false, newCommandList, delaylist,newCommandFormatList);
            }

        }, 2000);
    }



   /* *//*BROADCAST RECIEVER USED FOR CONNECTION OF BLE *//*
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
           if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
           }
        }
    };*/

    /*Action Data method
    * Getting protocol string and and added to a list*/
    AzimElev azimElev = new AzimElev(0, 0);
    private void displayData(String data) {
        if (data != null) {
          if(data.contains("@@@@")){
                try {
                    String packet_no = data.split(",")[1];
                    pktno = Integer.parseInt(packet_no);
                    String total_no_of_packets = data.split(",")[3];
                    totalnoofpkts = Integer.parseInt(total_no_of_packets);
                    datalist.add(data);

                    /*Removing all views for child view creation which is satellite custom view*/
                    if(pktno==totalnoofpkts && pktno>0){
                        /*if(mRadarCustom!=null){
                            mRadarCustom.removeAllViews();
                        }*/
                        dataparse(datalist);
                        datalist.clear();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    /*Parsing the protocol data here.*/
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
                        }else{

                          //  Toast.makeText(this, "Packet size not matched", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            /*Added payload in a third variable*/
            if(payloadfinal!=null){
                payloadfinal = payloadfinal.concat(finalpayv);
            }else{
                payloadfinal = finalpayv;
            }

            /*Reinitialize third varibale after all packets recieved and pass the finalpayload for finalview*/
            if(pktno==totalnoofpkts && pktno>0){
                lastparse(payloadfinal);
                System.out.println(payloadfinal);
                payloadfinal=null;
            }
        }
    }

    /*Parse finalpayload string into list splitted by \\r?\\n*/
    public void lastparse(String val){
        if(val != null){
            ArrayList<String> aList = new ArrayList<>();
            String lines[] = val.split("\\r?\\n");
            aList.addAll(Arrays.asList(lines));
            drawList = aList;
            if(isFirstTime){
                isFirstTime = false;
                drawview(aList);
            }
        //    drawview(aList);

        }

    }

    String StatusData = "";
    /*Final draw of satellite view*/
    public void drawview(ArrayList<String> view) {
        mDataSet.clear();
        gps=0;
        SBAS=0;
        galileo=0;
        beidou=0;
        glonass=0;
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCenterView = inflater.inflate(R.layout.layout_center, null);
        TextView textView = mCenterView.findViewById(R.id.mTVText);

       for(int i=0; i<view.size() ;i++) {
            String data = view.get(i);
        final TextView[] myTextViews = new TextView[view.size()]; // create an empty array;
        int circleColor[] = new int[view.size()];
        final CircularTextView rowTextView = new CircularTextView(CRS_sattelite.this);
        if (data.contains("$GPGSV")) {//GPS, SBAS, QZSS
            try {
                String totalsat = data.split(",")[2];
                String noofsat = data.split(",")[4];
                int satno = Integer.parseInt(noofsat);
                String selvtn = data.split(",")[5];
                int elvtn = Integer.parseInt(selvtn);
                String sazmth = data.split(",")[6];
                int azmth = Integer.parseInt(sazmth);
                if (satno >= 1 && satno < 33) {//GPS
                    ArrayList<Skymodel> mDataSet1 = new ArrayList<>();
                    circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.colorgreen);
                    rowTextView.setBackgroundColor(circleColor[i]);
                    rowTextView.setPadding(8, 0, 0, 0);
                    rowTextView.setGravity(10);
                    rowTextView.setText(noofsat);
                    gps++;
                   mDataSet.add(new Skymodel(360 - elvtn * 4, azmth, rowTextView));
                } else if (satno >= 120 && satno < 159) {//SBAS
                    circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.color_blued);
                    rowTextView.setBackgroundColor(circleColor[i]);
                    rowTextView.setPadding(8, 0, 0, 0);
                    rowTextView.setGravity(10);
                    rowTextView.setText(noofsat);
                    SBAS++;
                    mDataSet.add(new Skymodel(360 - elvtn * 4, azmth, rowTextView));
                } else if (satno >= 193 && satno < 197) {//QZSS
                    circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.colorPrimary1);
                    rowTextView.setBackgroundColor(circleColor[i]);
                    rowTextView.setPadding(8, 0, 0, 0);
                    rowTextView.setGravity(10);
                    rowTextView.setText(noofsat);
                    mDataSet.add(new Skymodel(360 - elvtn * 4, azmth, rowTextView));
                }
            }catch (Exception e){
                gps--;
                e.printStackTrace();
            }

        } else if (data.contains("$GLGSV")) {//GLONASS
            try{
                String noofsat = data.split(",")[4];
                int satno = Integer.parseInt(noofsat);
                String selvtn = data.split(",")[5];
                int elvtn = Integer.parseInt(selvtn);
                String sazmth = data.split(",")[6];
                int azmth = Integer.parseInt(sazmth);
                circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.coloryellow);
                rowTextView.setBackgroundColor(circleColor[i]);
                rowTextView.setPadding(8, 0, 0, 0);
                rowTextView.setGravity(10);
                rowTextView.setText(noofsat);
                glonass++;
               // mDataSet.add(new Skymodel(360-elvtn*4, azmth,rowTextView));
                mDataSet.add(new Skymodel(360, 30,rowTextView));
            }catch (Exception e){
                glonass--;
                e.printStackTrace();
            }

        } else if (data.contains("$GAGSV")) {//Galileo
            try{
                String noofsat = data.split(",")[4];
                int satno = Integer.parseInt(noofsat);
                String selvtn = data.split(",")[5];
                int elvtn = Integer.parseInt(selvtn);
                String sazmth = data.split(",")[6];
                int azmth = Integer.parseInt(sazmth);
                circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.colorlightblue);
                rowTextView.setBackgroundColor(circleColor[i]);
                rowTextView.setPadding(8, 0, 0, 0);
                rowTextView.setGravity(10);
                rowTextView.setText(noofsat);
                galileo++;
                mDataSet.add(new Skymodel(360-elvtn*4, azmth,rowTextView));
            }catch (Exception e){
                galileo--;
                e.printStackTrace();
            }
        } else if (data.contains("$GBGSV")) {//BeiDou
            try {
                String noofsat = data.split(",")[4];
                int satno = Integer.parseInt(noofsat);
                String selvtn = data.split(",")[5];
                int elvtn = Integer.parseInt(selvtn);
                String sazmth = data.split(",")[6];
                int azmth = Integer.parseInt(sazmth);
                circleColor[i] = ContextCompat.getColor(CRS_sattelite.this, R.color.coloret);
                rowTextView.setBackgroundColor(circleColor[i]);
                rowTextView.setPadding(8, 0, 0, 0);
                rowTextView.setGravity(10);
                rowTextView.setText(noofsat);
                beidou++;
                mDataSet.add(new Skymodel(360-elvtn*4, azmth,rowTextView));
            }catch (Exception e){
                beidou--;
                e.printStackTrace();
            }

        }else if(data.contains("$PUBX")){
            try{
                //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                String ttmode = data.split(",")[8];
                String hAcc = data.split(",")[9];
                String vAcc = data.split(",")[10];
                String HDOP = data.split(",")[15];
                String VDOP = data.split(",")[16];
                String numsvrs = data.split(",")[18];
                sats.setTitle("Sats used ="+" "+numsvrs);
                hzpcn.setTitle("Hz prec ="+" "+hAcc+"m");
                vtpcn.setTitle("Vt prec ="+" "+vAcc+"m");
                hdop.setTitle("HDOP = "+" "+HDOP);
                vdop.setTitle("VDOP ="+" "+VDOP);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(data.contains("$GNGGA")){
            try{
                String fix = data.split(",")[6];
                if (fix.equalsIgnoreCase("0")) {
                    StatusData = getString(R.string.invalid);
                } else if (fix.equalsIgnoreCase("1") || fix.equalsIgnoreCase("2") ) {
                    StatusData = getString(R.string.standalone_mode);
                } else if (fix.equalsIgnoreCase("3")) {
                    StatusData = getString(R.string.not_applicable);
                } else if (fix.equalsIgnoreCase("4")) {
                    StatusData = getString(R.string.rtk_fixed);
                } else if (fix.equalsIgnoreCase("5")) {
                    StatusData = getString(R.string.rtk_float);
                } else if (fix.equalsIgnoreCase("6")) {
                    StatusData = getString(R.string.estimated);
                } else if (fix.equalsIgnoreCase("7")) {
                    StatusData = getString(R.string.manual_input_mode);
                } else {
                    StatusData = getString(R.string.simulation_mode);
                }
                position.setTitle(getString(R.string.pos)+" "+StatusData);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

            if(StatusData.equalsIgnoreCase("")){
                position.setVisibility(View.INVISIBLE);
            }else {
                position.setVisibility(View.VISIBLE);
            }
        // save a reference to the textview for later
             myTextViews[i] = rowTextView;
        //  mDataSet.add(new Skymodel(azmth, elvtn, myTextViews[i]));
       }
       stgps.setText(getString(R.string.gps)+" "+ gps);
       stglonass.setText(getString(R.string.glonass)+" "+ glonass);
       stsbas.setText(getString(R.string.sbas)+" "+ SBAS);
       stgalieleo.setText(getString(R.string.galileo)+" "+ galileo);
       stbeidou.setText(getString(R.string.beidou)+" "+ beidou);
       mRadarCustom.setupData(90, mDataSet, azimElev, mCenterView);
    }

    /*Binding service*/
    protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
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
           // mBluetoothLeService.connect(mDeviceAddress,CRS_sattelite.this,device_id,opid);
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
        super.onDestroy();
        if(mBluetoothLeService!=null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }

    @Override
    public void onBackPressed() {
        newCommandList.clear();
        newCommandList.add("B562068A090000010000C50091200010FD");

        mBluetoothLeService.send( item,CRS_sattelite.this, false, false, newCommandList, delaylist,newCommandFormatList);
        unregisterReceiver(mGattUpdateReceiver);
        super.onBackPressed();
    }
}