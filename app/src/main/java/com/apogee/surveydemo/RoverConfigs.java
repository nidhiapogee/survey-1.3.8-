package com.apogee.surveydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.apogee.surveydemo.utility.BluetoothLeService.counter;
import static com.apogee.surveydemo.utility.BluetoothLeService.issuccess;

public class RoverConfigs extends AppCompatActivity implements OnItemValueListener,TextToSpeech.OnInitListener {
    Toolbar toolbar;
    boolean deviceinfo=false;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    int  device_id = 0;
    String devicedetail;
    LinearLayout connect, home;
    private TextView mConnectionState,bttxt;
    DeviceControlActivity dca = new  DeviceControlActivity();
    ImageView bleimg,btimg;
    String mDeviceAddress;
    ProgressDialog dialog;
    RecycerlViewAdapter recycerlViewAdapter;
    RecycerlViewAdapter recycerlViewAdapter2;
    RecyclerView recylcerview,recyclerView2;
    List<ItemType> itemTypeList = new ArrayList<>();
    List<ItemType> itemTypeList2 = new ArrayList<>();
    DatabaseOperation dbTask = new DatabaseOperation(this);
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();
    public List<String> newCommandList = new ArrayList<>();
    List<String> delaylist = new ArrayList<>();
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();
    public static boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    String operation = "";
    private TextToSpeech tts;

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
                mPlayer = MediaPlayer.create(RoverConfigs.this, R.raw.btconnected);
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
                mPlayer = MediaPlayer.create(RoverConfigs.this, R.raw.btdiscnctd);
                mPlayer.start();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try{
                        mBluetoothLeService.conectToService(device_id, 1);
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

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
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
        setContentView(R.layout.activity_rover_configs);
        recylcerview = findViewById(R.id.baselist);
        recyclerView2 = findViewById(R.id.roverlist);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        recycerlViewAdapter2 = new RecycerlViewAdapter(itemTypeList2, this);
        recylcerview.setAdapter(recycerlViewAdapter);
        recyclerView2.setAdapter(recycerlViewAdapter2);
        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("DeviceAddress");
        mConnectionState = findViewById(R.id.connection_state);
        connect = findViewById(R.id.blelay);
        bleimg = findViewById(R.id.bleimg);
        home = findViewById(R.id.homelay);
        btimg = findViewById(R.id.btimg);
        bttxt = findViewById(R.id.bttxt);
        toolbar=findViewById(R.id.tool);
        tts = new TextToSpeech(this, this);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
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
            Intent intent1 = new Intent(RoverConfigs.this, HomeActivity.class);
            intent1.putExtra("mode",operation);
            intent.putExtra("deviceaddress",mDeviceAddress);
            startActivity(intent1);
        });

        dbTask.open();
        ArrayList<String> roverlist;
        roverlist = dbTask.getRoverList("Rover");
        if(roverlist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_configuration_found), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < roverlist.size(); k++) {
            String val = roverlist.get(k);
            String tname = val.split(",")[0];
            String vall = val.split(",")[1];
            String time = val.split(",")[2];
            itemTypeList.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, vall, time,null));
            recycerlViewAdapter.notifyDataSetChanged();
        }

        ArrayList<String> baselistlist;
        baselistlist = dbTask.getRoverList("Auto base");
        if(baselistlist.isEmpty()){
            Toast.makeText(this, getString(R.string.no_any_base_configuration_found), Toast.LENGTH_SHORT).show();
        }
        for (int k = 0; k < baselistlist.size(); k++) {
            String val = baselistlist.get(k);
            String tname = val.split(",")[0];
            String vall = val.split(",")[1];
            String time = val.split(",")[2];
            itemTypeList2.add(new ItemType(ItemType.INPUTTYPEPROJECT, tname, null, vall, time,null));
            recycerlViewAdapter2.notifyDataSetChanged();
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }
    public void configsetting() {
        dialog = ProgressDialog.show(RoverConfigs.this, getString(R.string.bluetooth_connection),
                getString(R.string.try_to_connect), true);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = (av, v, arg2, arg3) -> {
            };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*Don,t unregisterReceiver your service in onPause because it stops getting data from BLE
         * when you are out of this page*/
        //  unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        stopSpeak();
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
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

    public void alertdialog(StringBuilder msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RoverConfigs.this);
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
        String param = newCommandList.get(0);
        String[] colums = param.split("(?i)2C");
        if(devicedetail!=null){
            String[] columss = devicedetail.split(",");
            String getvall = columss[2].trim();
            getvall = stringtohex(getvall);
            colums[2]=getvall;
        }
        StringBuffer sb = new StringBuffer();
        for (String colum : colums) {
            sb.append(colum + "2C");
        }
        String str = sb.toString();
        newCommandList.remove(0);
        newCommandList.add(0,str);
    }

    public String stringtohex(String str){
        char ch[] = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : ch) {
            sb.append(Integer.toHexString((int) c));
        }
        return sb.toString();
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
            Intent intent = new Intent(RoverConfigs.this, HomeActivity.class);
            intent.putExtra("mode",operation);
            startActivity(intent);
            dialogBuilder.dismiss();
        });

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            mBluetoothLeService.serverDisconnect();
            mConnected=false;
            dca.mConnected=false;
            Intent intent = new Intent(RoverConfigs.this, HomeActivity.class);
            intent.putExtra("mode",operation);
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

    private void
    commandout() {
        runOnUiThread(() -> mBluetoothLeService.send("item", RoverConfigs.this, false, false, newCommandList, delaylist,null));
    }

    private void test(){
        final ProgressDialog dddialog;
        dddialog = new ProgressDialog(RoverConfigs.this);
        int val = newCommandList.size();

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

        new Thread(() -> {
            try {
                while (dddialog.getProgress() <= dddialog
                        .getMax()) {
                    if (counter==dddialog.getProgress()){
                        dddialog.incrementProgressBy(1);
                    }else if(counter==19977){
                        msg.arg1=1;
                        handler.sendMessage(msg);
                        dddialog.dismiss();
                    }
                    if (dddialog.getProgress() == dddialog
                            .getMax()) {
                        dddialog.dismiss();
                        if(issuccess){
                            msg.arg1=2;
                            handler.sendMessage(msg);
                        }else{
                            msg.arg1=1;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    Handler handler = new Handler(msg -> {
        if(msg.arg1==1)
        {
            dialogsuccess("OOPS!");
        }else if(msg.arg1==2){
            dialogsuccess("YES");
        }
        return false;
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
        String title = null;
            title="Device";

        if(msg.equalsIgnoreCase(getString(R.string.yes))){
            t2.setText(title+" "+ getString(R.string.configured_successfully));
            imgvw.setImageResource(R.drawable.successgif);
        }else{
            t2.setText(title+" "+getString(R.string.not_configured));
            imgvw.setImageResource(R.drawable.alertgif);
        }


        button.setOnClickListener(v -> dialogBuilder.dismiss());

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }



    private void updateConnectionState(final int resourceId) {
        runOnUiThread(() -> mConnectionState.setText(resourceId));
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
                delayBatteryStatus();
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

    private void speakOut(String msg) {
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }

    private void delayBatteryStatus(){
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Write whatever to want to do after delay specified (1 sec)
            Log.d("Handler", "Running Handler");
            speakOut(getString(R.string.battery_low));
            delayBatteryStatus();
        }, 5000);
    }
}