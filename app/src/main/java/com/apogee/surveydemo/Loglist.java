package com.apogee.surveydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apogee.surveydemo.utility.BluetoothLeService;

import java.util.ArrayList;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

/*This class shows all the logs which can be imitate through RTK device*/
public class Loglist extends AppCompatActivity {
    private final static String TAG = CRS_sattelite.class.getSimpleName();
    int pktno;
    int totalnoofpkts=0;
    String payloadfinal;
    ArrayList<String> datalist = new ArrayList<>();
    Toolbar toolbar;
    ChatView chatView;
    String dname;
    public static final String devicename = "devicenamepref";
    private BluetoothLeService mBluetoothLeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loglist);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Loglist.this);


        dname = sharedPreferences.getString(devicename, "default value");
        toolbar=findViewById(R.id.tool);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        chatView = findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(chatMessage -> {
            String msg = chatView.getTypedMessage();
            mBluetoothLeService.sendchat(msg);
            return true;
        });

        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });
    }

    public void clearlog(View view){
        chatView.clearMessages();
    }

    /*Binding service*/
    protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    // BROADCAST RECIEVER USED FOR CONNECTION OF BLE
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    // Do something after 5s = 5000ms
                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }, 5000);

            }
        }
    };

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
            }else{
                String[] somedata = data.split("\\r?\\n");
                int length=somedata.length;
                if(length>1){
                    for (String somedatum : somedata) {
                        chatView.addMessage(new ChatMessage(somedatum, System.currentTimeMillis(), ChatMessage.Type.RECEIVED, dname));
                    }
                }else{
                    chatView.addMessage(new ChatMessage(data,System.currentTimeMillis(), ChatMessage.Type.RECEIVED, dname));

                }

            }
        }
    }

    public void dataparse(ArrayList<String> dataval){
        String actualKey = null;
        String finalpayv = null;
        int totalnoofpkts = 0,datalenghth,pktno=0;
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
            for (String line : lines) {
                chatView.addMessage(new ChatMessage(line, System.currentTimeMillis(), ChatMessage.Type.RECEIVED, dname));

            }

        }

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
        return intentFilter;
    }

    @Override
    public void onBackPressed() {
        unregisterReceiver(mGattUpdateReceiver);
        super.onBackPressed();
    }
}