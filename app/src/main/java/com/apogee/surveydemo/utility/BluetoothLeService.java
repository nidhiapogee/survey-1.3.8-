package com.apogee.surveydemo.utility;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.NotificationCenter;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.model.BleModel;
import com.google.android.gms.common.util.ArrayUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Created by Abhijeet on 19/11/2019.
 */
public class BluetoothLeService extends Service {
    connect_thread connect_thread;
    public static boolean issuccess = false;
    boolean isinvalid=false;
    int srvrrqsttime;
    int pktintrvltime;
    int pktttszzzzzz;
    private final IBinder mBinder = new LocalBinder();
    public static int counter = 0;
    String purpose = "";
    String timer = "";
    public boolean isConnected;
    Context context;
    receive_thread1 thread1, thread2;
    public byte[] data1 = null;
    DatabaseOperation dbTask;
    boolean istrue = false;
    private final static String TAG = "BluetoothLeService";
    final static UUID MY_UUID_RN4020_CHARACTERISTIC_WRITE = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    BluetoothGattCharacteristic bluetoothGattCharacteristic;
    BluetoothGattCharacteristic bluetoothGattReadCharacteristic;
    BluetoothGattService bluetoothGattService;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.navitus.bleexample.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    String command = "";
    String device_address = "";
    int operation_id = 0, device_id = 0;
    private Socket socket = null;
    public static String strIP = "";
    public static int nPort = 0;
    public static boolean cancelWrite = false;
    private BufferedOutputStream out = null;
    Boolean bConnect = Boolean.valueOf(false);
    Boolean bTelnet = Boolean.valueOf(false);
    TelnetOpt mtelnet;
    static boolean isrover = false;
    String serverData = "";
    String serverData1 = "";
   // byte[] readBuf=("The programming language Python was conceived in the late 1980s, and its implementation was started in December 1989 by Guido van Rossum at CWI in the Netherlands as a successor to ABC capable of exception handling and interfacing with the Amoeba operating system.Van Rossum is Python's principal author, and his continuing central role in deciding the direction of Python is reflected in the title given to him by the Python community, Benevolent Dictator for Life (BDFL). (However, van Rossum stepped down as leader on July 12, 2018.) Python was named for the BBC TV show Monty Python's Flying Circus.Python 2.0 was released on October 16, 2000, with many major new features, including a cycle-detecting garbage collector (in addition to reference counting) for memory management and support for Unicode. However, the most important change was to the development process itself, with a shift to a more transparent and community-backed process.Python 3.0, a major, backwards-incompatible release,12").getBytes();
    String data = "";
    byte[] readBuf;
    DeviceControlActivity dle = new DeviceControlActivity();
    Connection_Thread connection_thread;
    static int lsSize = 0;
    List<String> finalcommand;


    /*Gatt callback method*/
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /*Broadcast updated here with connection state*/
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }


        /*Service discovered*/
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        /*Characteristics read and action adata available*/
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        /*Action data available*/
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /*Write your string or msgs*/
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                long startTime = System.currentTimeMillis();
                String result = "success";
                intent.putExtra(EXTRA_DATA, "" + command + getString(R.string.command_write_successfully) + startTime + "_" + timer);
                sendBroadcast(intent);
            } else {
                issuccess=false;
                counter=19977;
                String result = "not success";
                intent.putExtra(EXTRA_DATA, "" + command + getString(R.string.command_write_fail_click));
                sendBroadcast(intent);
            }
        }


        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String res = "succes";
            }
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /*This method is basically used for receiving the string and messages from BLE hardware device*/
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {

        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        String finalString = new String(data, 0, data.length);
        String navvalue = null;
       // onlySpecialCharacters(finalString);
        System.out.println("msg get-"+finalString);
        String response = bytesToHex(data);
        System.out.println("msg hex-"+response);
        if ((response.contains("b5620501") || finalString.equalsIgnoreCase("4G ON\n") || finalString.contains("LoRa ON"))) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //Do something after 10000ms
                    counter++;
                  //  System.out.println("Data Acknowledged"+response+finalString);
                    intent.putExtra(EXTRA_DATA, "Data Acknowledged");
                    sendBroadcast(intent);
                    issuccess=true;
                }
            }, 2000);

        /*    counter++;
            //  System.out.println("Data Acknowledged"+response+finalString);
            intent.putExtra(EXTRA_DATA, "Data Acknowledged");
            sendBroadcast(intent);
            issuccess=true;*/

        } else if(response.contains("b5620500")) {
            System.out.println("Data not Acknowledged");
            intent.putExtra(EXTRA_DATA, "Data not Acknowledged");
            sendBroadcast(intent);
            issuccess=false;
            counter=19977;
        }else if(finalString.contains("INVALID COMMAND")){
            isinvalid=true;
        }else if(response.contains("b562013c")){
            navvalue = response.split("0d0a")[1];
            intent.putExtra(EXTRA_DATA, navvalue);
            sendBroadcast(intent);
        }
        DeviceControlActivity.finalResponse = finalString;
        intent.putExtra(EXTRA_DATA, finalString);
        sendBroadcast(intent);
    }





    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public String send(String purposest, Context con, boolean sendSt, boolean is_newTask, List<String> newCommandList,List<String> delayList,List<String> commandsFormat) {
        String msg = "Success";
         List<String> finalCommandList = new ArrayList<>();
        boolean is_new = is_newTask;
        boolean resendStatus = sendSt;
        purpose = purposest;
        context = con;
        String addval = "";
        for (int k = 0; k < newCommandList.size(); k++) {
            if(commandsFormat.get(k).equals("hex")) {
                addval  = newCommandList.get(k) + "0D0A";
            }else {
                addval  = newCommandList.get(k) + "\r\n";
            }
            System.out.println(addval);
            finalCommandList.add(addval);
        }
        writeDataToCharacteristic(con,bluetoothGattCharacteristic, resendStatus, is_new, finalCommandList,delayList,commandsFormat);
        return msg;
    }


    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    /**
     * Request a read on a given {@code
     * <p>
     * <p>
     * <p>
     * }. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (MY_UUID_RN4020_CHARACTERISTIC_WRITE.equals(characteristic.getUuid())) {
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                String a = descriptor.getUuid().toString();
            }
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    (MY_UUID_RN4020_CHARACTERISTIC_WRITE));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            if (mBluetoothGatt.writeCharacteristic(characteristic) == false) {
                Log.w(TAG, "Failed to write characteristic");
            }
        }
    }

    public void writeDataToCharacteristic(Context con,final BluetoothGattCharacteristic ch, boolean resendStatus, boolean isnewtask, List<String> newCommandList,List<String> delayList,List<String> commandsFormat) {
        try {
            finalcommand = newCommandList;
            for (int K = 0; K <= newCommandList.size(); K++) {
                if(!cancelWrite){
                    try {
                        String s = newCommandList.get(K);
                        int len = s.length();
                        byte[] data = new byte[len / 2];
                        if(commandsFormat.get(K).equals("hex")) {
                            for (int i = 0; i < len; i += 2) {
                                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
                            }
                        }

                        ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        if(commandsFormat.get(K).equals("hex")) {
                            ch.setValue(data);
                        }else {
                             ch.setValue(s);
                        }
                        final long startTime = System.currentTimeMillis();
                        Thread.sleep(1000);
                        boolean result = mBluetoothGatt.writeCharacteristic(ch);
                        if(isinvalid){
                            counter=19977;
                            break;
                        }
                        lsSize=K;
                        int dlay = Integer.parseInt(delayList.get(K));
                        Thread.sleep(dlay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    break;
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean writeCorrectionDataToCharacteristic(final BluetoothGattCharacteristic ch, String msg) {
        try {
            //new receive_thread().start();
            boolean mtu = mBluetoothGatt.requestMtu(512);
            try {
                ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                ch.setValue(msg);
                Thread.sleep(1000);

                boolean a = mBluetoothGatt.writeCharacteristic(ch);
                System.out.println("boolean " + a);
                return a;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
         return false;
    }

    /*Service connection with service uuid*/
    public void conectToService(int device_id, int opid) {
        // mBluetoothGatt=gatt;
        dbTask = new DatabaseOperation(context);
       // String service_char = dbTask.getserviceCharop(device_id, opid);
      /*  String service_uuid = service_char.split("_")[0];
        String charachtristics_uuid = service_char.split("_")[1];
        String charachtristics_read_uuid = service_char.split("_")[2];*/
        String service_uuid="6e400001-b5a3-f393-e0a9-e50e24dcca9e";
        String charachtristics_uuid="6e400002-b5a3-f393-e0a9-e50e24dcca9e";
        String charachtristics_read_uuid="6e400003-b5a3-f393-e0a9-e50e24dcca9e";
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        for (BluetoothGattService service : services) {
            UUID test = service.getUuid();
            String original = test.toString();
            if (original.equals(service_uuid)) {
                try {
                    bluetoothGattService = service;
                    bluetoothGattCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_uuid));
                    bluetoothGattReadCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_read_uuid));
                    if (bluetoothGattCharacteristic == null) {
                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        intent.putExtra(EXTRA_DATA, new String("Error in charachtristics uuid"));
                        sendBroadcast(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (BluetoothGattDescriptor descriptor : bluetoothGattReadCharacteristic.getDescriptors()) {
                    setCharacteristicNotification(bluetoothGattReadCharacteristic, true);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                    final Intent intent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                    sendBroadcast(intent);
                }
            }
        }
        if (bluetoothGattService == null) {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA, "service " + service_uuid + "not found");
            sendBroadcast(intent);
        }

    }

    /*This thread is basically used for TCP connection thread*/
    public void connectTcp() {
        connect_thread = new connect_thread();
        this.connect_thread.start();

    }

    class connect_thread extends Thread {
        Boolean bRun = Boolean.valueOf(true);

        public void run() {
            Connect();
        }

        public void onTerminate() {
            this.bRun = Boolean.valueOf(false);
        }
    }


    /*Here we create socket object and declare IP and port for TCP connection*/

    void Connect() {
        try {
            socket = new Socket();
            strIP = "120.138.10.146";
            nPort = 8079;
            //strIP=
            socket.connect(new InetSocketAddress(strIP, nPort), 5000);
            try {
                this.out = new BufferedOutputStream(this.socket.getOutputStream());
                new receive_thread().start();

                this.bTelnet = Boolean.valueOf(true);
                this.mtelnet = new TelnetOpt();
                this.mtelnet.nState = 0;
                try {

                    sendRequestToServer();
//                    String user_id, password;
//                    if (isrover) {
//                        user_id = "jjjjjjjjrtgrg";
//                        password = "jyoti1";
//                    } else {
//                        user_id = "jjjjjjjjhthyhh";
//                        password = "jyoti12";
//                    }
//                    int user_length = user_id.length();
//                    byte len = (byte) user_length;
//                    byte[] namearr = user_id.getBytes();
//                    int passlength = password.length();
//                    byte pasbyte = (byte) passlength;
//                    byte[] passarray = password.getBytes();
//                    byte[] startdel = {125, 125, 125, 125, 16, 20, 00, 06, 40, 51, 49, 73, 64, 70, 03, 02, 00, 30, 00, 06, 41, 42, 43, 44, 45, 46, 00, len};
//                    //this.out.write(startdel);
//                    this.out.write(namearr);
////                    byte[] bytes = {00, pasbyte};
////                    byte[] enddel = {126, 126, 126};
////                    this.out.write(bytes);
////                    this.out.write(passarray);
////                    this.out.write(enddel);
////                    this.out.flush();
//                    while(true)
//                    {
//                        sendRequestToServer();
//                        try
//                        {
//                            Thread.sleep(200);
//                        }
//                       catch (Exception e){
//                            e.printStackTrace();
//
//                        }
//                    }


                    // pairedDevicesArrayAdapter.add("Server " + connect);

                } catch (Exception e) {
                    e.printStackTrace();

                }

                this.bConnect = Boolean.valueOf(true);
                //this.handler.sendMessage(this.handler.obtainMessage(2, "Connected\n"));

            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();

                //  this.handler.sendMessage(this.handler.obtainMessage(2, "Don't support encoding\n"));

            } catch (IOException e3) {
                e3.printStackTrace();
                // this.handler.sendMessage(this.handler.obtainMessage(2, "Don't create object\n"));
            }
        } catch (Exception e4) {
            e4.printStackTrace();
            if (this.socket != null) {
                this.socket = null;
                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                intent.putExtra(EXTRA_DATA, new String("Fail to Connect!"));
                sendBroadcast(intent);
                // this.handler.sendMessage(this.handler.obtainMessage(2, "Fail to connect\n"));
            }
        }
    }

    /*Requesting to the server with encoded base protocol for the NTRIP server*/

    boolean sendRequestToServer() {
        try {
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            //String data = "jjjjjjjjjjrewrfsfsdgfdgfhyghghgymoti";
            //String data = "$$$$,abhijeet,hjhjhjhjhjhjj,####";

            String encode = "APOGEEROV:random";
            byte[] dataa = encode.getBytes("UTF-8");
            String base64 = Base64.encodeToString(dataa, Base64.DEFAULT);

            String data = "GET /APOGEE1 HTTP/1.0\n" +
                    "User-Agent: NTRIP RTKLIB/2.4.2\n" +
                    "Authorization: Basic "+base64;

            new receive_thread().start();
            this.out.write(data.getBytes());
            this.out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean sendchat(String readBuf) {

        return writeCorrectionDataToCharacteristic(bluetoothGattCharacteristic, readBuf);
    }

    class TelnetOpt {
        byte[] OptBuf = new byte[32];
        Boolean bACK_ECHO;
        Boolean bACK_SGA;
        Boolean bECHO;
        Boolean bSGA;
        int nOptLen;
        int nState = 0;

        TelnetOpt() {

        }
    }



    class receive_thread extends Thread {
        private byte[] byte_data = new byte[2000];
        private int size = 0;

//        receive_thread() {
//            try {
//                //Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        //  }

        public void run() {
            while (true) {

                BleModel bleModel = new BleModel(context);
                // byte_data[]=bleModel.funcRequest("jhdsiufhifugriu");
                try {
                    this.size = socket.getInputStream().read(this.byte_data);
                    if (this.size < 1) {
                        //SubActivity.this.disconnect();
                        return;
                    }
                    if (true) {

                        byte[] telnet_data = new byte[2000];
                        int nLen = this.size;
                        serverData1 = new String(byte_data, 0, nLen);
                        readBuf = Arrays.copyOfRange(byte_data, 0, nLen);
                       /* if(serverData1.equalsIgnoreCase("base is not live")){
                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, new String("Base is not live"));
                            sendBroadcast(intent);
                        }
                        byte[] regex = {'@', '@', '@', '@'};
                        byte [] readBu = Arrays.copyOfRange(byte_data, 0, nLen);
                        List<byte[]> splitval = tokens(readBu,regex);
                        for(int i=0; i<splitval.size(); i++){
                            firstdata = splitval.get(0);
                            actualcorrection = splitval.get(1);
                        }
                        this.size = 0;
                        String str = new String(firstdata);
                        String pktlnthsrvr = str.split(",")[2];
                        String coreectiondatalength = String.valueOf(actualcorrection.length);
                        if(pktlnthsrvr.equalsIgnoreCase(coreectiondatalength)){
                            readBuf = actualcorrection;
                        }else{
                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, new String("Length mismatch"));
                            sendBroadcast(intent);
                        }*/

                        //readBuf=null;
                        String recmsg = "";
                        char d = 0;
                        String temp = "";
                        boolean connectionServer = false;
                        boolean sendToServer = false;
                        thread1 = new receive_thread1();
                        thread1.start();
                        //sendRequestToServer();
                        //sendCorrection(readBuf,"rover_configuration");
                        if (serverData.contains("Not")) {

                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, new String("" + serverData));
                            //intent.putExtra(EXTRA_DATA, new String(""+"NOT Authrize shweta"));
                            sendBroadcast(intent);
                        } else if (serverData.contains("Authorised")) {
                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, new String(" " + "server response:" + serverData));
                            sendBroadcast(intent);
                            //connectionServer = true;
                            //connectionServer = sendRequestToServer();
                        }

//                            if (connectionServer) {
//                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//                                intent.putExtra(EXTRA_DATA, new String(" " + serverData));
//                                sendBroadcast(intent);
//                                sendToServer = sendCorrection(readBuf, "rover_configuration");
//                                while (sendToServer) {
//                                    connectionServer = sendRequestToServer();
//                                    sendToServer = sendCorrection(readBuf, "rover_configuration");
//                                }
//                            }

                    }
                    //  }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//                intent.putExtra(EXTRA_DATA, new String(" " + "Error"));
//                sendBroadcast(intent);
            }

        }
    }

    public static List<byte[]> tokens(byte[] array, byte[] delimiter) {
        List<byte[]> byteArrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return byteArrays;
        }
        int begin = 0;

        outer:
        for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j]) {
                    continue outer;
                }
            }
            byteArrays.add(Arrays.copyOfRange(array, begin, i));
            begin = i + delimiter.length;
        }
        byteArrays.add(Arrays.copyOfRange(array, begin, array.length));
        return byteArrays;
    }

    public void serverDisconnect() {
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class receive_thread1 extends Thread {
        private byte[] byte_data = new byte[600];
        private int size = 0;

        @Override
        public void run() {
            try {
                byte[] myvar1 = readBuf;
                int range = myvar1.length;
                int totalarray = (range / pktttszzzzzz);
                ArrayList<byte[]> arrayList = new ArrayList();
                try {
                    for (int as = 0; as <= totalarray; as++) {
                        if (as == 0) {
                            int a = (as + 1) * pktttszzzzzz;
                            arrayList.add(Arrays.copyOfRange(myvar1, 0, (as + 1) * pktttszzzzzz + 1));
                        } else if (as == totalarray) {
                            int b = as * pktttszzzzzz + 1;
                            arrayList.add(Arrays.copyOfRange(myvar1, as * pktttszzzzzz + 1, range));
                        } else {
                            int a = (as) * pktttszzzzzz + 1;
                            int b = (as + 1) * pktttszzzzzz;
                            arrayList.add(Arrays.copyOfRange(myvar1, (as) * pktttszzzzzz + 1, (as + 1) * pktttszzzzzz + 1));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int as = 0; as <= arrayList.size(); as++) {
                    Thread.sleep(pktintrvltime);
                    if (as == arrayList.size()) {
                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        String result = "total: " + range;
                        intent.putExtra(EXTRA_DATA, new String("" + result));
                        sendBroadcast(intent);
                        // Thread.sleep(1000);
                             Thread.sleep(srvrrqsttime);
                        sendRequestToServer();
                    }

                    byte[] val = arrayList.get(as);
                    int rane = val.length;
                    int pktno= as+1;
                    int totlpkt = arrayList.size();
                    String getbtar = new String(val);
                    String valll = "$$$$,"+pktno+","+rane+","+totlpkt+",";
                    String valll2 = ",####";
                    byte[] byteArr1 = valll.getBytes(StandardCharsets.UTF_8);
                    byte[] byteArr2 = valll2.getBytes(StandardCharsets.UTF_8);
                    byte[] concatBytes = ArrayUtils.concatByteArrays(byteArr1,val,byteArr2);
                    long startTime = System.currentTimeMillis();
                    timer = "" + startTime;
                    command = new String(val, 0, val.length);
                    bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bluetoothGattCharacteristic.setValue(concatBytes);
//                        //Thread.sleep(2000);
                    boolean a = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                    System.out.println("boolean " + a);
                }
               /* final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                String result = "total: " + range;
                intent.putExtra(EXTRA_DATA, new String("" + result));
                sendBroadcast(intent);

                long startTime = System.currentTimeMillis();
                timer = "" + startTime;
                command = new String(myvar1, 0, myvar1.length);
                bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bluetoothGattCharacteristic.setValue(myvar1);
//                        //Thread.sleep(2000);
                boolean a = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                System.out.println("boolean " + a);
                Thread.sleep(1000);
                sendRequestToServer();*/

                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /* final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        String result = "total: " + range;
                        intent.putExtra(EXTRA_DATA, new String("" + result));
                        sendBroadcast(intent);

                long startTime = System.currentTimeMillis();
                timer = "" + startTime;
                command = new String(myvar1, 0, myvar1.length);
                bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bluetoothGattCharacteristic.setValue(myvar1);
//                        //Thread.sleep(2000);
                boolean a = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                System.out.println("boolean " + a);
                Thread.sleep(1000);
                sendRequestToServer();*/




//    class receive_thread2 extends Thread {
//        private byte[] byte_data = new byte[600];
//        private int size = 0;
//
//        @Override
//        public void run() {
//            try {
//                byte[] myvar1 = readBuf;
//                int range = myvar1.length;
//                int totalarray = (range / 100);
//                ArrayList<byte[]> arrayList = new ArrayList();
//                try {
//                    for (int as = 0; as <= totalarray; as++) {
//                        if (as == 0) {
//                            int a = (as + 1) * 100;
//                            arrayList.add(Arrays.copyOfRange(myvar1, 0, (as + 1) * 100 + 1 + 45));
//                        } else if (as == totalarray) {
//                            int b = as * 100 + 1;
//                            arrayList.add(Arrays.copyOfRange(myvar1, as * 100 + 1, range));
//                        } else {
//                            int a = (as) * 100 + 1;
//                            int b = (as + 1) * 100;
//                            arrayList.add(Arrays.copyOfRange(myvar1, (as) * 100 + 1, (as + 1) * 100 + 1 + 45));
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                int test = 1;
//                for (int as = 0; as <= arrayList.size(); as++) {
//                    sleep(100);
//                    if (as == arrayList.size()) {
//                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//                        String result = "total: " + range;
//                        intent.putExtra(EXTRA_DATA, new String("" + result));
//                        sendBroadcast(intent);
//                        Thread.sleep(5000);
//                        sendRequestToServer();
//                    }
//                    byte[] val = arrayList.get(as);
//                    command = new String(val, 0, val.length);
//                    bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//                    bluetoothGattCharacteristic.setValue(val);
////                        //Thread.sleep(2000);
//                    boolean a = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//                    System.out.println("boolean " + a);
//
//
//                }
//
//                // }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    class Connection_Thread extends Thread {
        private byte[] byte_data = new byte[600];
        private int size = 0;

        @Override
        public void run() {
            try {
                while (true) {


                    if (!isConnected) {
                        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(device_address);
                        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
                    }
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public OnShowDailogListener onShowDailogListener;
    public void setOnShowDialogListner(OnShowDailogListener onShowDialogListner){
        this.onShowDailogListener = onShowDialogListner;
    }

    interface OnShowDailogListener {
        void showDailog(Context context);

        void hideDialog(Context context);
    }

}






