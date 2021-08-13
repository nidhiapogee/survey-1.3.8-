package com.apogee.surveydemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.adapter.ExpandableListAdapter;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.apogee.surveydemo.utility.BluetoothLeService.counter;
import static com.apogee.surveydemo.utility.BluetoothLeService.issuccess;

public class RoverConfigsNewActivity extends AppCompatActivity implements OnItemValueListener, TextToSpeech.OnInitListener,NotificationCenter.NotificationCenterDelegate {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    ExpandableListView expListView;
    private TextToSpeech tts;
    private TextToSpeech configTTs;
    DatabaseOperation dbTask = new DatabaseOperation(this);
    String operation = "";
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();
    List<String> formatList = new ArrayList<>();
    public List<String> newCommandList = new ArrayList<>();
    List<String> delaylist = new ArrayList<>();
    String devicedetail;
    private BluetoothLeService mBluetoothLeService;
    String mDeviceAddress;
    public static boolean mConnected = false;
    DeviceControlActivity dca = new  DeviceControlActivity();
    ImageView bleimg,btimg;
    ProgressDialog dialog;
    int  device_id1 = 0;
    boolean deviceinfo=false;
    private TextView mConnectionState,bttxt;

    List<String> listDataHeader;
    HashMap<String, List<ItemType>> listDataChild;
    ExpandableListAdapter listAdapter;
    List<ItemType> itemTypeList = new ArrayList<>();
    List<ItemType> itemTypeList2 = new ArrayList<>();
    List<ItemType> itemTypeList3 = new ArrayList<>();
    Toolbar toolbar;
    LinearLayout connect,home;
    final Handler speakhandler = new Handler();
    Boolean isFirstTime = true;

    String device_name1="",address="";
    String selectedmodeule = "";
    String device_id="",dgps_device_id;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                dca.mConnected=true;
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                updateConnectionState(R.string.connected);
                MediaPlayer mPlayer;
                mPlayer = MediaPlayer.create(RoverConfigsNewActivity.this, R.raw.btconnected);
                mPlayer.start();
                try{
                    if(!isFinishing())
                    {
                        configsetting();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                dca.mConnected=false;
                updateConnectionState(R.string.disconnected);
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                invalidateOptionsMenu();
                MediaPlayer mPlayer;
                mPlayer = MediaPlayer.create(RoverConfigsNewActivity.this, R.raw.btdiscnctd);
                mPlayer.start();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try{
                    mBluetoothLeService.conectToService(device_id1, 1);
                }catch (Exception e){
                    e.printStackTrace();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    public void configsetting() {
        dialog = ProgressDialog.show(RoverConfigsNewActivity.this, getString(R.string.bluetooth_connection),
                getString(R.string.try_to_connect), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


    public  void  alertback(){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);

        textView1.setText(getString(R.string.connection));
        textView2.setText(getString(R.string.do_you_want_to_disconnect_bluetooth));
        button1.setText(getString(R.string.yes_back));
        button2.setText(getString(R.string.no_back));

        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(RoverConfigsNewActivity.this, HomeActivity.class);
            intent.putExtra("mode",operation);
            intent.putExtra("device_name", device_name1);
            intent.putExtra("device_address", address);
            intent.putExtra("device_id", String.valueOf(device_id));
            intent.putExtra("dgps_device_id", String.valueOf(dgps_device_id));
            intent.putExtra(" selectedmodeule" ,selectedmodeule);
            startActivity(intent);
            dialogBuilder.dismiss();
        });

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            mBluetoothLeService.serverDisconnect();
            mConnected=false;
            dca.mConnected=false;
            Intent intent = new Intent(RoverConfigsNewActivity.this, HomeActivity.class);
            intent.putExtra("mode",operation);
            intent.putExtra("device_name", device_name1);
            intent.putExtra("device_address", address);
            intent.putExtra("device_id", String.valueOf(device_id));
            intent.putExtra("dgps_device_id", String.valueOf(dgps_device_id));
            intent.putExtra(" selectedmodeule" ,selectedmodeule);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            finish();
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
    protected void onPause() {
        super.onPause();
        /*Don,t unregisterReceiver your service in onPause because it stops getting data from BLE
         * when you are out of this page*/
        //  unregisterReceiver(mGattUpdateReceiver);
    }

    /*Intent filters for gatt update*/
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rover_configs_new);
        // get the listview
        expListView = findViewById(R.id.lvExp);
        bleimg = findViewById(R.id.bleimg);
        tts = new TextToSpeech(this, this);
        configTTs = new TextToSpeech(this,this);
        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("device_address");

        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
        selectedmodeule =  intent.getStringExtra(" selectedmodeule");
        bttxt = findViewById(R.id.bttxt);
        btimg = findViewById(R.id.btimg);
        mConnectionState = findViewById(R.id.connection_state);
        prepareListData();
        toolbar=findViewById(R.id.tool);
        connect = findViewById(R.id.blelay);
        home = findViewById(R.id.homelay);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.counter);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        connect.setOnClickListener(view -> {
            if (mConnected) {
                mBluetoothLeService.disconnect();
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
            } else {
                mBluetoothLeService.connect(mDeviceAddress);
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
            }
        });
        home.setOnClickListener(view -> {
            Intent intent1 = new Intent(RoverConfigsNewActivity.this, HomeActivity.class);
            intent1.putExtra("mode",operation);
            intent1.putExtra("device_name", device_name1);
            intent1.putExtra("device_address", address);
            intent1.putExtra("device_id", String.valueOf(device_id));
            intent1.putExtra("dgps_device_id", String.valueOf(dgps_device_id));
            intent1.putExtra(" selectedmodeule" ,selectedmodeule);
            startActivity(intent1);
        });
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            ItemType itemType = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
            dbTask.open();
            newCommandList.clear();
            operation = itemType.oprtr;
            StringBuilder disp = new StringBuilder();
            map2 = dbTask.getRovercommanddetails(itemType.title,itemType.time);
            formatList = dbTask.getRoverformatdetails(itemType.title,itemType.time);
            map1 = dbTask.getRoverparamdetails(itemType.title,itemType.time);
            for (Map.Entry<String,String> entry : map2.entrySet())
            {

                newCommandList.add(entry.getKey());
                delaylist.add(entry.getValue());
            }
            String maskValue = map1.get("Mask angle");
            String surveyAccuracy = map1.get("Survey Accuracy");
            String Accuracy = map1.get("Accuracy");
            String surveyTime = map1.get("Survey Time");
            String NavMode = map1.get("Nav. Mode");
            String altitude = map1.get("Altitude");
            long maskDec = 0;
            int surveyAccuracyDec = 0;
            long surveyTimeDec = 0;
            int accuracyTime = 0;
            int altitudeTime = 0;
         if(maskValue != null){
                 maskDec =  hexToLong(maskValue);
            }

               if(Accuracy != null){
                accuracyTime = hexToInt(Accuracy);
            }


            if(surveyAccuracy != null){
                 surveyAccuracyDec = hexToInt(surveyAccuracy);
            }

            if(surveyTime != null){
                 surveyTimeDec = hexToInt(surveyTime);
            }

           if(altitude != null){
               altitudeTime = hexToInt(altitude);
           }


             disp.append("Mask Angle :"+" "+maskDec+"\r\n");
             if(itemType.oprtr.equals(getString(R.string.auto_base)) ){
                 disp.append("Survey Accuracy :" +" "+surveyAccuracyDec/100+"\r\n");
                 disp.append("Survey Time :" +" "+surveyTimeDec/60+"\r\n");
                 disp.append("Altitude :"+" "+altitudeTime+"\r\n");
             }else if(itemType.oprtr.equals(getString(R.string.rover))) {
                 switch (NavMode){
                     case "00":
                         disp.append("Nav. Mode :" +" "+"Portable"+"\r\n");
                         break;
                     case "02":
                         disp.append("Nav. Mode :" +" "+"Stationary"+"\r\n");
                         break;
                     case "03":
                         disp.append("Nav. Mode :" +" "+"Pedestrian"+"\r\n");
                         break;
                     case "04":
                         disp.append("Nav. Mode :" +" "+"Automotive"+"\r\n");
                         break;
                     case "10":
                         disp.append("Nav. Mode :" +" "+"Bike"+"\r\n");
                         break;
                 }
             }else  if(itemType.oprtr.equals("Manual Base")){
                 disp.append("Accuracy :" +" "+accuracyTime/100+"\r\n");
                 disp.append("Altitude :"+" "+altitudeTime+"\r\n");
            }

             Log.d("maskDec===", String.valueOf(maskDec));
            for (Map.Entry<String,String> entry : map1.entrySet()) {
                if( !entry.getKey().equals("Mask angle") && !entry.getKey().equals("Survey Accuracy") && !entry.getKey().equals("Survey Time")
                        && !entry.getKey().equals("Toggle Previous Configuration") && !entry.getKey().equals("Password") && !entry.getKey().equals("Nav. Mode")
                        && !entry.getKey().equals("Latitude")  && !entry.getKey().equals("Longtitude") && !entry.getKey().equals("Accuracy")
                        && !entry.getKey().equals("Longitude") && !entry.getKey().equals("Altitude")){
                    StringBuilder output = new StringBuilder();
                    for (int i = 0; i < entry.getValue().length(); i+=2) {
                        String str = entry.getValue().substring(i, i+2);
                        output.append((char)Integer.parseInt(str, 16));
                    }
                    Log.d("check===", String.valueOf(output));
                    disp.append(entry.getKey()+" : "+output+"\r\n");
                }


            }



            if(itemType.oprtr.equals(getString(R.string.auto_base))){
                String latitude = map1.get("Latitude");
                String longtitude = map1.get("Longtitude");
                disp.append("Latitude"+" : "+latitude+"\r\n");
                disp.append("longtitude"+" : "+longtitude+"\r\n");

            }else if(itemType.oprtr.equals("Manual Base")){


             /*   disp.append("Latitude"+" : "+hextToString( map1.get("Latitude"))+"\r\n");
                disp.append("longtitude"+" : "+hextToString( map1.get("Longitude"))+"\r\n");*/

            }

            conversion();

            alertdialog(disp);
            return false;
        });
        dbTask.open();
        ArrayList<String> roverlist;
        roverlist = dbTask.getRoverList(getString(R.string.rover));
        if(roverlist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_configuration_found), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < roverlist.size(); k++) {
            String val = roverlist.get(k);
            String tname = val.split(",")[0];
            String vall = val.split(",")[1];
            String time = val.split(",")[2];
            String timeStamp = val.split(",")[3];
            itemTypeList.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, vall, time,timeStamp));
        }

        ArrayList<String> baselistlist;
        baselistlist = dbTask.getRoverList(getString(R.string.auto_base));
        if(baselistlist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_base_configuration_found), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < baselistlist.size(); k++) {
            String val = baselistlist.get(k);
            String tname = val.split(",")[0];
            String vall = val.split(",")[1];
            String time = val.split(",")[2];
            String timeStamp = val.split(",")[3];
            itemTypeList2.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, vall, time,timeStamp));
        }

        ArrayList<String> mannualList;
        mannualList = dbTask.getRoverList(getString(R.string.manual_base));
        if(mannualList.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_base_configuration_found), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < mannualList.size(); k++) {
            String val = mannualList.get(k);
            String tname = val.split(",")[0];
            String vall = val.split(",")[1];
            String time = val.split(",")[2];
            String timeStamp = val.split(",")[3];
            itemTypeList3.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, vall, time,timeStamp));
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if(!dca.mConnected){
            updateConnectionState(R.string.disconnected);
            bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
            invalidateOptionsMenu();
        }else if(dca.mConnected){
            updateConnectionState(R.string.connected);
            bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
            invalidateOptionsMenu();
        }
    }

    public static long hexToLong(String hex) {
        return Long.parseLong(hex, 16);
    }

    public StringBuilder hextToString(String hex){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output;
    }

    public int hexToInt(String hex){
        // Parse hex to int
        if(hex.contains(".")){
            hex = hex.replace(".","");
        }
        int flipped = 0;
        if(!hex.equals("F0000000") && !hex.equals("B4000000")){
            int value = Integer.parseInt(hex, 16);
            // Flip byte order using ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.asIntBuffer().put(value);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
             flipped = buffer.asIntBuffer().get();

            System.out.println("hex: 0x" + hex);
            System.out.println("flipped: " + flipped);
        }else {
            if(hex.equals("F0000000")){
                flipped = 240;
            }else if(hex.equals("B4000000")){
                flipped = 180;
            }

        }


        return flipped;
    }

  /*  public long hexToLongnew(String hex){
        // Parse hex to int
        if(hex.contains(".")){
            hex = hex.replace(".","");
        }
        long value = Long.parseLong(hex, 16);
        // Flip byte order using ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asLongBuffer().put(value);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long flipped = buffer.asLongBuffer().get();

        System.out.println("hex: 0x" + hex);
        System.out.println("flipped: " + flipped);

        return flipped;
    }*/



    @Override
    public void onBackPressed() {
        alertback();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.init_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void returnValue(String title, String finalvalue) {

    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String mode) {
        dbTask.open();
        operation = mode;
        String getpname = title.split("##")[0];
        StringBuilder disp = new StringBuilder();
        map2 = dbTask.getRovercommanddetails(getpname,finalvalue);
        map1 = dbTask.getRoverparamdetails(getpname,finalvalue);
        formatList = dbTask.getRoverformatdetails(getpname,finalvalue);
        for (Map.Entry<String,String> entry : map2.entrySet())
        {
            newCommandList.add(entry.getKey());
            delaylist.add(entry.getValue());
        }
        for (Map.Entry<String,String> entry : map1.entrySet())
        {
            disp.append(entry.getKey()+" "+entry.getValue()+"\r\n");
        }

        conversion();

        alertdialog(disp);

    }
    void conversion(){
        if(newCommandList != null && newCommandList.size() > 0){
            String param = newCommandList.get(0);
            String[] colums = param.split("(?i)2C");
            if(devicedetail!=null){
                String[] columss = devicedetail.split(",");
                String getvall = columss[2].trim();
                getvall = stringtohex(getvall);
                colums[2]=getvall;
            }
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < colums.length; i++) {
                sb.append(colums[i]+"2C");
            }
            String str = sb.toString();
            newCommandList.remove(0);
            newCommandList.add(0,str);
        }

    }


    public String stringtohex(String str){
        char ch[] = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ch.length; i++) {
            sb.append(Integer.toHexString((int) ch[i]));
        }
        return sb.toString();
    }

    public void alertdialog(StringBuilder msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RoverConfigsNewActivity.this);
        builder1.setTitle(getString(R.string.details));
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.ok_configure),
                (dialog, id) -> {
                    dialog.cancel();
                    test();
                    commandout();

                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void commandout() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Write whatever to want to do after delay specified (1 sec)
            Log.d("Handler", "Running Handler");
            runOnUiThread(() -> mBluetoothLeService.send("item", RoverConfigsNewActivity.this, false, false, newCommandList, delaylist,formatList));
        }, 2000);



    }





    private void test(){
        final ProgressDialog dddialog;
        dddialog = new ProgressDialog(RoverConfigsNewActivity.this);
        int val=0;
        val = newCommandList.size();

        // Set your progress dialog Title
        dddialog.setTitle(getString(R.string.command_configuration));
        // Set your progress dialog Message
        dddialog.setMessage(getString(R.string.processing_please_wait));
        dddialog.setIndeterminate(false);
        dddialog.setCancelable(false);
        dddialog.setMax(val);
        dddialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Show progress dialog

        dddialog.show();

        final Message msg = new Message();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (dddialog.getProgress() <= dddialog.getMax()) {
                        if (counter==dddialog.getProgress()){
                            dddialog.incrementProgressBy(1);
                           /* runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.counter, dddialog, msg);
                                }
                            });*/

                            if(operation.equals("Manual Base") && counter == 15 ){
                                dddialog.dismiss();
                                if (issuccess) {
                                    msg.arg1 = 2;
                                    handler.sendMessage(msg);
                                } else {
                                    msg.arg1 = 1;
                                    handler.sendMessage(msg);
                                }
                            }

                            if (dddialog.getProgress() == dddialog.getMax()) {
                                dddialog.dismiss();
                                if (issuccess) {
                                    msg.arg1 = 2;
                                    handler.sendMessage(msg);
                                } else {
                                    msg.arg1 = 1;
                                    handler.sendMessage(msg);
                                }
                            }


                        }else if(counter==19977){
                            msg.arg1=1;
                            handler.sendMessage(msg);
                            dddialog.dismiss();
                        }
                       /* */
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1) {
                dialogsuccess(getString(R.string.oops));

            }else if(msg.arg1==2){
                dialogsuccess(getString(R.string.yes));
            }
            return false;
        }
    });

    public void dialogsuccess(String msg){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_config_alert, null);
        final TextView t1 = dialogView.findViewById(R.id.t1);
        final TextView t2 = dialogView.findViewById(R.id.t2);
        final ImageView imgvw = dialogView.findViewById(R.id.gifload);
        final Button button = dialogView.findViewById(R.id.btnDialog);
        t1.setText(msg);
        String title;
        title=getString(R.string.device);

        if(msg.equalsIgnoreCase(getString(R.string.yes))){
            t2.setText(title+" "+ getString(R.string.configured_successfully));
            speakOut(title+" "+getString(R.string.configured_successfully));
            imgvw.setImageResource(R.drawable.successgif);
        }else{
            t2.setText(title+" "+getString(R.string.not_configured));
            speakOut(title+" "+getString(R.string.not_configured));
            imgvw.setImageResource(R.drawable.alertgif);
        }


        button.setOnClickListener(new View.OnClickListener() {
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

            if (data.contains(getString(R.string.battery_status))) {
                if(!deviceinfo){
                    try{
                        String msg = "$$$$,03,01,DeviceId,0000,####\r\n";
                        mBluetoothLeService.sendchat(msg);
                        deviceinfo=true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                bateerydata(data);
            }

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
            }else if(data.contains("Battery Status: Charging")){
                btimg.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
                bttxt.setText(getString(R.string.charging));
                stopSpeak();
            }else if(data.contains("Battery Status: Fully Charged")){
                btimg.setImageResource(R.drawable.batteryfive);
                bttxt.setText(getString(R.string.fully_charged));
                stopSpeak();
            }else if(data.contains("Battery Status:6.")){
                btimg.setImageResource(R.drawable.batteryfive);
                bttxt.setText(getString(R.string.battery));
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

    private void speakOut(String msg) {
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        configTTs.speak(msg, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }

    private void batteryLowAudio(String msg){
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }


    @Override
    protected void onDestroy() {
        stopSpeak();
        if (configTTs != null) {
            configTTs.stop();
            configTTs.shutdown();
        }
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.counter);
    }


    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add(getString(R.string.mannual_base));
        listDataHeader.add(getString(R.string.base_list_new));
        listDataHeader.add(getString(R.string.rover_list_new));

        listDataChild.put(listDataHeader.get(0), itemTypeList3);
        listDataChild.put(listDataHeader.get(1), itemTypeList2); // Header, Child data
        listDataChild.put(listDataHeader.get(2), itemTypeList);

    }


    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.counter) {
            ProgressDialog dddialog = (ProgressDialog) args[0] ;
            Message msg  =  (Message) args[1];

            Log.d("counter===", String.valueOf(counter));

         Toast.makeText(RoverConfigsNewActivity.this,"checkkk"+counter,Toast.LENGTH_SHORT).show();
        }
    }


}
