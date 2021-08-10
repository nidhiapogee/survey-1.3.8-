
package com.apogee.surveydemo.utility;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.HomeActivity;
import com.apogee.surveydemo.NotificationCenter;
import com.apogee.surveydemo.PreferenceStore;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.adapter.AutoBAseListAdapter;
import com.apogee.surveydemo.adapter.ExpandableListAdapter;
import com.apogee.surveydemo.model.Operation;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.apogee.surveydemo.newproject;


import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.apogee.surveydemo.newproject.project_name;
import static com.apogee.surveydemo.utility.BluetoothLeService.cancelWrite;
import static com.apogee.surveydemo.utility.BluetoothLeService.counter;
import static com.apogee.surveydemo.utility.BluetoothLeService.issuccess;


public class DeviceControlActivity extends AppCompatActivity implements BluetoothLeService.OnShowDailogListener, OnItemValueListener, TextToSpeech.OnInitListener {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String  EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    Spinner config, suboperation;
    String payloadfinal;
    public static String  Configname;
    public static String radiomodulename,gnssmodulename;
    Thread t;
    String datumcommand=null;
    MediaPlayer mPlayer;
    boolean ispubx = false;
    List<String> configls;
    List<String> cmcmthd;
    List<String> parameterlist;
    List<Integer> commandls1;
    List<String> commandsfromlist = new ArrayList<>();
    List<String> commandsformatList  = new ArrayList<>();
    List<String> delaylist = new ArrayList<>();
    List<String> gnsscommands = new ArrayList<>();
    List<String> radiocommands = new ArrayList<>();
    List<String> gnssdelay = new ArrayList<>();
    List<String> radiodelay = new ArrayList<>();
    List<String> gnnsFormatCommands = new ArrayList<>();
    List<String> radioFormatCommands = new ArrayList<>();
    private TextView mConnectionState,bttxt;
    private String mDeviceName= "";
    boolean sendst = true;
    boolean isbasesetup = false;
    LinearLayout connect, home, toolback;
    ImageView bleimg,btimg;
    String item = "";
    public static String textview;
    ListView deviceListView;
    String correction="";
    String devicedetail;
    private String mDeviceAddress;
    public static String lat_lang;
    public static String Batterystatus;
    public static String pubxstring;
    public static String latlongvalue;
    public static String StatusData;
    public static String dvcstatus=null;
    public byte[] data = null;
    private ArrayList<String> list;
    private ArrayList<String> list1;
    private ArrayList<String> list2;
    private ArrayList<String> list3;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> arrayAdapter4;
    String latitude = "";
    String pubxlatitude = "";
    String longitude = "";
    String pubxlongitude = "";
    String altitude = "";
    String pubxaltitude = "";
    String modee="";
    String finalpayval = null;
    int pktno,datalenghth=0;
    int totalnoofpkts=0;
    ArrayList<String> datalist = new ArrayList<>();
    ProgressDialog dialog;
    boolean service = false;
    boolean deviceinfo=false;
    String abLat = "";
    String ablng = "";
    public static Activity finishActivity;
    PreferenceStore preferenceStore;
    boolean gnggaenable=false;
    /*tAKING VARIABLE FOR SPINNER TEXT*/
    public List<String> newCommandList = new ArrayList<>();
    public List<String> finalCommandList = new ArrayList<>();
    public List<String> newRadioCommandList = new ArrayList<>();
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> selectionValue1 = new LinkedHashMap<>();

    //private ExpandableListView mGattServicesList;
    public static BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();
    public static boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> deviceList;
    DatabaseOperation dbTask = new DatabaseOperation(this);
    int ble_operation_id = 0, device_id = 0, opid = 0, dgps_id = 0;
    public static boolean tcpconnect = true;
    public static String finalResponse;
    private List<Operation> operationArrayList;
    private ArrayAdapter<Operation> arrayAdaptersuboperation;
    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();

    private TextToSpeech tts;
    private TextToSpeech configTTs;
    final Handler speakhandler = new Handler();
    Boolean isFirstTime = true;
    public  static  boolean isLatLng = true;
    Callingserver1 cs;
     String selectedmodeule = "";

     TextView sattxt;


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
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                preferenceStore.setBTConnected(true);
                service=false;
                updateConnectionState(R.string.connected);
                mPlayer = MediaPlayer.create(DeviceControlActivity.this, R.raw.btconnected);
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
                ispubx=false;
                deviceinfo=false;
                updateConnectionState(R.string.disconnected);
                preferenceStore.setBTConnected(false);
                bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();

                }
                invalidateOptionsMenu();
                service=false;
                mPlayer = MediaPlayer.create(DeviceControlActivity.this, R.raw.btdiscnctd);
                mPlayer.start();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                if(!service){
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        editdialog();
                    }
                    try{
                        mBluetoothLeService.conectToService(device_id, 1);
                    }catch (Exception e){
                        e.printStackTrace();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    service = true;
                }

                // Show all the supported services and characteristics on the user interface.
               // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
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
        setContentView(R.layout.gatt_services_characteristics);
        preferenceStore = new PreferenceStore(this);
        recylcerview = findViewById(R.id.recylcerview);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);
        recylcerview.setAdapter(recycerlViewAdapter);
        finishActivity = this;
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("device_name");
        mDeviceAddress = intent.getStringExtra("device_address");
        String d_id = intent.getStringExtra("device_id");
        if(d_id != null){
            device_id = Integer.parseInt(d_id);
        }

        String dgpsid = intent.getStringExtra("dgps_device_id");
        if(dgpsid != null){
            dgps_id = Integer.parseInt(dgpsid);
        }

        selectedmodeule =  intent.getStringExtra(" selectedmodeule");
        deviceListView = findViewById(R.id.deviceListView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        config = findViewById(R.id.config);
        suboperation = findViewById(R.id.suboprtnspnr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        configTTs = new TextToSpeech(this,this);
        actionBar.hide();
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceName);
        mConnectionState = findViewById(R.id.connection_state);
        connect = findViewById(R.id.blelay);
        bleimg = findViewById(R.id.bleimg);
        home = findViewById(R.id.homelay);
        toolback = findViewById(R.id.backlay);
        btimg = findViewById(R.id.btimg);
        sattxt = findViewById(R.id.sattxt);
        bttxt = findViewById(R.id.bttxt);
        dbTask = new DatabaseOperation(DeviceControlActivity.this);
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        deviceList = new ArrayList<>();
        deviceListView.setAdapter(listAdapter);
        deviceListView.setOnItemClickListener(mDeviceClickListener);
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        operationArrayList = new ArrayList<>();
        tts = new TextToSpeech(this, this);
        arrayAdaptersuboperation = new ArrayAdapter<>(this, android.R.layout.simple_gallery_item, operationArrayList);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        arrayAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list3);
        list.add("--Select--");
        list1.add("--Select--");
        list2.add("--Select--");
        list3.add("--Select--");
        datumcommand();
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnected) {
                    mBluetoothLeService.disconnect();
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                } else {
                    mBluetoothLeService.connect(mDeviceAddress);
                    bleimg.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent1 = new Intent(DeviceControlActivity.this, HomeActivity.class);
                intent1.putExtra("configName", Configname);
                intent1.putExtra("radiomodulename", radiomodulename);
                intent1.putExtra("gnssmodulename",gnssmodulename);
                 intent1.putExtra("Survey Accuracy",map1.get("Survey Accuracy")) ;
                intent1.putExtra("device_name", mDeviceName);
                intent1.putExtra("device_address", mDeviceAddress);
                intent1.putExtra("device_id", String.valueOf(device_id));
                intent1.putExtra("dgps_device_id", String.valueOf(dgps_id));
                intent1.putExtra(" selectedmodeule" ,selectedmodeule);
                 startActivity(intent1);
               //  finish();
            }
        });

        toolback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });

        configls = new ArrayList<>();
        configls.add("--select--");
        dbTask.open();
        List<Operation> configls = dbTask.getoperationParent(dgps_id,getString(R.string.gnss_module));
        ArrayAdapter<Operation> configAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, configls);
        configAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        config.setAdapter(configAdapter);
        config.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Operation operation = (Operation) parent.getSelectedItem();
                if (!operation.getName().equals("--select--")) {
                    gnssmodulename = operation.getName();
                    itemTypeList.clear();
                    recycerlViewAdapter.notifyDataSetChanged();
                    operationArrayList = dbTask.getchild(operation.getId());
                    arrayAdaptersuboperation.notifyDataSetChanged();
                    if(operation.getName().equalsIgnoreCase(getString(R.string.auto_base))){
                        modee="31";
                        itemTypeList.clear();
                        newCommandList.clear();

                        recycerlViewAdapter.notifyDataSetChanged();
                    }else if(operation.getName().equalsIgnoreCase(getString(R.string.manual_base))){
                        modee="31";
                        itemTypeList.clear();
                        newCommandList.clear();
                        dialogAutoBase();
                        recycerlViewAdapter.notifyDataSetChanged();
                    }else if(operation.getName().equalsIgnoreCase(getString(R.string.rover))){
                        modee="32";
                        itemTypeList.clear();
                        newCommandList.clear();
                        recycerlViewAdapter.notifyDataSetChanged();
                    }
                    getcommandid(operation.getName());
                    gnsscommands.clear();
                    gnssdelay.clear();
                    commandsfromlist.clear();
                    getcommandforparsing(opid,0);
                    opid = dbTask.detopnameid(operation.getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cmcmthd = new ArrayList<>();
        cmcmthd.add("--select--");
        dbTask.open();
        List<Operation> cmcmthd = dbTask.getoperationParent(dgps_id,getString(R.string.radio_module));
        ArrayAdapter<Operation> cmctmthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cmcmthd);
        cmctmthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suboperation.setAdapter(cmctmthsAdapter);
        suboperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Operation operation = (Operation) parent.getSelectedItem();
                 int opppid = 0;
                if (!operation.getName().equals("--select--")) {
                    radiomodulename = operation.getName();
                    if(operation.getName().equalsIgnoreCase(getString(R.string.gsm))){
                        opppid = dbTask.detopnameid(operation.getName());
                        radiocommands.clear();
                        radiodelay.clear();
                        commandsfromlist.clear();
                        delaylist.clear();
                        getcommandforparsing(0,opppid);
                        editpoint(opppid,getString(R.string.gsm_configuration));
                        if(map1.containsKey("Toggle Previous Configuration") || map1.containsKey("Channel")
                                || map1.containsKey("Power") || map1.containsKey("Baud-Rate") || map1.containsKey("Frequency") || map1.containsKey("Data-Rate")){
                            map1.remove("Toggle Previous Configuration");
                            map1.remove("Channel");
                            map1.remove("Power");
                            map1.remove("Baud-Rate");
                            map1.remove("Frequency");
                            map1.remove("Data-Rate");

                        }
                    }else if(operation.getName().equalsIgnoreCase(getString(R.string.radio))){
                        opppid = dbTask.detopnameid(operation.getName());
                        radiocommands.clear();
                        radiodelay.clear();
                        commandsfromlist.clear();
                        delaylist.clear();
                        getcommandforparsing(0,opppid);
                        editpoint(opppid,getString(R.string.radio_configuration));

                        if(map1.containsKey("Toggle Previous Configuration") || map1.containsKey("Password")
                                || map1.containsKey("IP") || map1.containsKey("Port") || map1.containsKey("MountPoint")){
                            map1.remove("Toggle Previous Configuration");
                            map1.remove("Password");
                            map1.remove("IP");
                            map1.remove("Port");
                            map1.remove("MountPoint");

                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    public void editpoint(int opid,String config){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fourgdialog, null);
        RecycerlViewAdapter recycerlViewAdapter;
        Map<String, String> selectionV;
        List<ItemType> TypeList = new ArrayList<>();
        recycerlViewAdapter = new RecycerlViewAdapter(TypeList, this);
        final RecyclerView recyclerView = dialogView.findViewById(R.id.recylcerview4g);
        recyclerView.setAdapter(recycerlViewAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        Button button1 = dialogView.findViewById(R.id.timebt);
        TextView textView = dialogView.findViewById(R.id.cmethodtext);
        List<Integer> commandls;
        commandls = dbTask.commandidls1(opid, dgps_id);
        String joined = TextUtils.join(", ", commandls);
        ArrayList<Integer> parameteridlist = new ArrayList<>();
        parameteridlist = (ArrayList<Integer>) dbTask.parameteridlist(joined);

        ArrayList<Integer> selectionidlist;
        selectionidlist = (ArrayList<Integer>) dbTask.selectionidlist1(joined);
        String joined2 = TextUtils.join(", ", selectionidlist);

        /*View for selection recyclerview*/

        ArrayList<Integer> inputlist;
        inputlist = (ArrayList<Integer>) dbTask.inputlist(joined);
        String joined3 = TextUtils.join(", ", inputlist);
        Map<String, Map<String, String>> selectionList;
        selectionList = dbTask.displayvaluelist1(joined2);
        Set<String> selectionParameter = selectionList.keySet();
        for (String param : selectionParameter) {
            selectionV = selectionList.get(param);
            Set<String> baudratekey = selectionV.keySet();
            Collection<String> baudratevalue = selectionV.values();
            List<String> baudratevaluelist = new ArrayList<>(baudratekey);
            baudratevaluelist.add(0, "--select--");
            TypeList.add(new ItemType(ItemType.DROPDOWNTYPE, param, selectionV));
            recycerlViewAdapter.notifyDataSetChanged();
            System.out.println("Initial values : " + baudratevalue);//

        }
        /*View for Input recyclerview*/
        ArrayList<String> inputparameterlist;
        inputparameterlist = (ArrayList<String>) dbTask.inputparameterlist(joined3);
        for (String inputparam : inputparameterlist) {
            TypeList.add(new ItemType(ItemType.INPUTTYPE, inputparam, null,null,null,null));
            recycerlViewAdapter.notifyDataSetChanged();
        }
        textView.setText(config);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                /*This is mandatory to request focus so that last ediitext get the actual data from recyclerview*/
                dialogView.requestFocus();
            }
        });
        dialogBuilder.setView(dialogView);
        Window window = dialogBuilder.getWindow();
        dialogBuilder.show();
        if(window != null){ // After the window is created, get the SoftInputMode
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }

    public void configsetting() {
         dialog = ProgressDialog.show(DeviceControlActivity.this, getString(R.string.bluetooth_connection),
                getString(R.string.try_to_connect), true);
    }

    public void onclearlist(View view) {
        deviceListView.setAdapter(null);
        listAdapter.clear();
        deviceListView.setAdapter(listAdapter);
    }

    /*THIS BUTTON IS USED FOR FINAL COMMAND OUT*/
    public void Finaldone(View view){
        if(ispubx || !mConnected){
            Toast.makeText(this, getString(R.string.device_already_configured), Toast.LENGTH_SHORT).show();
        }else{
               /*REQUEST FOCUS */
               View current = getCurrentFocus();
               if (current != null) current.clearFocus();
               counter=0;
               hideKeyboard(view);
               final SharedPreferences sharedPreferences = PreferenceManager
                       .getDefaultSharedPreferences(this);
               String p_name = sharedPreferences.getString(project_name, "default value");
               dbTask.open();
               datumcommand = dbTask.selectdatumfromproject(p_name);

            test();

             cs = new Callingserver1();
               cs.execute();


        }
       
    }


    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

    /*IN THIS METHOD ALL THE DATA CONVERTED AND ASSEMBLE FOR FINAL COMMAND FORMATION*/

    public void dataconversion() {
        commandsfromlist.addAll(gnsscommands);
        delaylist.addAll(gnssdelay);
        commandsformatList.addAll(gnnsFormatCommands);
        for(String param : radiodelay){
            delaylist.add(0,param);
        }
        for(String param : radioFormatCommands){
            commandsformatList.add(0,param);
        }
        for(String param : radiocommands){
            String[] colums = param.split("(?i)2C");
            if(devicedetail!=null){
                String[] columss = devicedetail.split(",");
                String getvall = columss[2].trim();
                getvall = stringtohex(getvall);
                colums[2]=getvall;
            }
            colums[4]=modee;
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < colums.length; i++) {
                sb.append(colums[i]+"2C");
            }
            String str = sb.toString();
          //  param = param.substring(0, 34)+modee+ param.substring(34 + 2);
            commandsfromlist.add(0,str);
        }
        editCommand(commandsfromlist,delaylist);
    }

    public String stringtohex(String str){
        char ch[] = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ch.length; i++) {
            sb.append(Integer.toHexString((int) ch[i]));
        }
        return sb.toString();
    }

    public void datumcommand() {
        dbTask.open();
        newproject np = new newproject();
        String datumname = np.datumitem;
        int datumopid = dbTask.datumopid(datumname);
        int datumcmndid = dbTask.datumcommandid(datumopid);
        String datumcommand = dbTask.datumcommand(datumcmndid);
    }


    /*This method basically get command id according to operation and through command id fetched all related parameters
    * their selection and related commands*/
    public void getcommandid(String operationname) {
        dbTask.open();
        opid = dbTask.detopnameid(operationname);
        commandls1 = new ArrayList<>();
        parameterlist = new ArrayList<>();
        commandls1 = dbTask.commandidls1(opid, dgps_id);
        String joined = TextUtils.join(", ", commandls1);
        ArrayList<Integer> parameteridlist;
        parameteridlist = (ArrayList<Integer>) dbTask.parameteridlist(joined);
        String joined1 = TextUtils.join(", ", parameteridlist);

        //All the parameters and their input and selection are parshed here and displayd through recyclerview//
        ArrayList<String> parameter_namelist = new ArrayList<>();
        parameter_namelist = (ArrayList<String>) dbTask.parameternamelist(joined1);

        ArrayList<Integer> selectionidlist;
        selectionidlist = (ArrayList<Integer>) dbTask.selectionidlist(joined);
        String joined2 = TextUtils.join(", ", selectionidlist);

        ArrayList<Integer> inputlist;
        inputlist = (ArrayList<Integer>) dbTask.inputlist(joined);
        String joined3 = TextUtils.join(", ", inputlist);

        ArrayList<Integer> selectionValue = new ArrayList<>();
        selectionValue = (ArrayList<Integer>) dbTask.getSelection_value_id(joined1,joined);
        String joined4 = TextUtils.join(", ", selectionValue);

        Map<String, Map<String, String>> selectionList;
        selectionList = dbTask.displayvaluelist(joined4);
        Set<String> selectionParameter = selectionList.keySet();
        for (String param : selectionParameter) {
            selectionValue1 = selectionList.get(param);
            Set<String> baudratekey = selectionValue1.keySet();
            Collection<String> baudratevalue = selectionValue1.values();
            List<String> baudratevaluelist = new ArrayList<>(baudratekey);
            selectionValue1.keySet().remove("0");
            selectionValue1.keySet().remove("20");
            selectionValue1.keySet().remove("25");
            selectionValue1.keySet().remove("30");
            baudratevaluelist.add(0, "--select--");
                itemTypeList.add(new ItemType(ItemType.DROPDOWNTYPE, param, selectionValue1));
                recycerlViewAdapter.notifyDataSetChanged();
                System.out.println("Initial values : " + baudratevalue);


        }
        ArrayList<String> inputparameterlist;
        inputparameterlist = (ArrayList<String>) dbTask.inputparameterlist(joined3);
        for (String inputparam : inputparameterlist) {
           /* if(!inputparam.equals("Survey Time") && !inputparam.equals("Survey Accuracy")){
                itemTypeList.add(new ItemType(ItemType.INPUTTYPE, inputparam, null,null,null));
                recycerlViewAdapter.notifyDataSetChanged();
            }*/

            itemTypeList.add(new ItemType(ItemType.INPUTTYPE, inputparam, null,null,null,null));
            recycerlViewAdapter.notifyDataSetChanged();


        }
        dbTask.close();
    }

    public void getcommandforparsing(int opid,int oppid) {
        dbTask.open();
        if(opid>0){
            gnssdelay = dbTask.delaylist(opid, dgps_id);
            gnsscommands = dbTask.commandforparsinglist(opid, dgps_id);
            gnnsFormatCommands = dbTask.commandformatparsinglist(opid,dgps_id);
        }else if(oppid>0){
            radiodelay = dbTask.delaylist(oppid, dgps_id);
            radiocommands = dbTask.commandforparsinglist(oppid, dgps_id);
            radioFormatCommands = dbTask.commandformatparsinglist(oppid,dgps_id);
        }
    }

    /**/
    /* THIS METHOD IS USED FOR MAKING FINAL COMMAND FOR BASE AND ROVER */

    public void editCommand(List<String> commandsfromlist, List<String> delaylist) {
        int i = 1;
        int[] index = new int[3];
        for (String command : commandsfromlist) {
            int index1 = command.indexOf('/');
            while (index1 >= 0) {
                System.out.println(index1);
                index[i] = index1;
                index1 = command.indexOf('/', index1 + 1);
                if (i == 2) {
                    String key = command.substring(index[1] + 1, index[2]);
                    if (key.equals("CRC")) {
                        String checksum = checksum(command.substring(4, index[1]));
                        String actualKey = command.substring(index[1], index[2] + 1);
                        command = command.replace(actualKey, checksum);
                        i = 1;
                        index = new int[3];
                        break;
                    }
                    String actualKey = command.substring(index[1], index[2] + 1);
                    String value = "";
                    if(key.equals("RTKOBSMODE ")){
                        value = "0";
                    }else {
                        value = map1.get(key);
                    }
                    command = command.replace(actualKey, value);
                    i = 1;
                    index = new int[3];
                    index1 = command.indexOf('/');
                } else {
                    i++;
                }
            }
            newCommandList.add(command);
        }
          // newCommandList.clear();
        if(selectedmodeule.equals("GNSS REC F9P")){
            newCommandList.add(1,datumcommand);
            commandsformatList.add(1,"hex");
        }


          //  newCommandList.add(3,"b562068a0900000100008e00912001daeb0D0A");
           /* newCommandList.add(1,"B56206090D00FFFF000000000000FFFF0000031B9A");
            newCommandList.add(2,"b562068a9a0000010000ba00912000be00912000bb00912000bc00912000bd00912000c900912000cd00912000ca00912000cb00912000cc00912000bf00912000c300912000c000912000c100912000c200912000c400912000c800912000c500912000c600912000c700912000ab00912000af00912000ac00912000ad00912000ae00912000b000912000b400912000b100912000b200912000b3009120000a8b");
            newCommandList.add(3,"B5620600140001000000D0080000009600002300030000000000AF70");
            newCommandList.add(4,"B56206090D0000000000FFFF000000000000031DAB");*/

          /*  delaylist.add(1,"500");*/
           /* delaylist.add(1,"150");
            delaylist.add(2,"150");
            delaylist.add(3,"150");
            delaylist.add(4,"150");*/
            delaylist.add(1,"150");
            delaylist.add(3,"150");

            if(Configname!=null){
                insertconfig();
            }
        /*Clear all list of commands and delaylist on success response*/
      String getmsg =  mBluetoothLeService.send(item, DeviceControlActivity.this, false, false, newCommandList, delaylist,commandsformatList);
      if(getmsg.equalsIgnoreCase("Success")){
          newCommandList.clear();
          commandsfromlist.clear();
          delaylist.clear();
          dialog.dismiss();
      }
    }


    void insertconfig(){
       long id = dbTask.insertconfiguration(Configname,gnssmodulename,radiomodulename);

        for (Map.Entry<String,String> entry : map1.entrySet())
        {
             dbTask.insertparams(id,entry.getKey(),entry.getValue());
        }
        dbTask.insertCommands(id,newCommandList,delaylist,commandsformatList);

    }




    /*THIS METHOD IS USED FOR CRC VALUE OF COMMAND*/
    public String checksum(String command) {
        String[] commandPair = new String[(command.length() / 2) + 1];
        int j = 0;
        int size = command.length();
        for (int i = 0; i < size; i += 2) {
            commandPair[j] = command.substring(i, i + 2);
            j++;
        }
        String ch_A = "0";
        String ch_B = "0";
        int length = commandPair.length - 1;
        for (int i = 0; i < length; i++) {
            ch_A = addCheckSum(ch_A, commandPair[i]);
            ch_B = addCheckSum(ch_B, ch_A);
        }
        ch_A = Integer.toHexString(Integer.parseInt(ch_A, 16) & 0xFF).toUpperCase();
        ch_B = Integer.toHexString(Integer.parseInt(ch_B, 16) & 0xFF).toUpperCase();
        if (ch_A.length() == 1) {
            ch_A = "0" + ch_A;
        }
        if (ch_B.length() == 1) {
            ch_B = "0" + ch_B;
        }
        return ch_A + ch_B;
    }

    /*Calculate checksum for the command*/
    public String addCheckSum(String ch_A, String ch_B) {
        int A = Integer.parseInt(ch_A, 16);
        int B = Integer.parseInt(ch_B, 16);
        int sum = A + B;
        return Integer.toHexString(sum);

    }

    /*Overridden method of interface (onItemvaluelistner)*/
    @Override
    public void returnValue(String title, String finalvalue) {
        if (!finalvalue.equalsIgnoreCase("--select--")) {
            String titl = title;
            String returnname = finalvalue;
            map1.put(titl, finalvalue);
        }
    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
              //  Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
            }

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


    /*Asynctask for execute command*/
    private class Callingserver1 extends AsyncTask<String, String, String> {
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result = "";

            try {

                if (newCommandList.size() == 0) {
                    dataconversion();

                } else  {
                    sendst = true;
                    mBluetoothLeService.send(item, DeviceControlActivity.this, false, false, newCommandList, delaylist,commandsformatList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    private void test(){
        final ProgressDialog dddialog;
        dddialog = new ProgressDialog(DeviceControlActivity.this);
        String title = null;
        int val=0;
        dvcstatus=modee;
        if(modee.equals("31")){
            title= getString(R.string.base_config);
        }else if(modee.equals("32")){
            title= getString(R.string.rover_config);
        }
        if(datumcommand!=null){
            val = gnsscommands.size()+radiocommands.size()+1;
        }else{
            val = gnsscommands.size()+radiocommands.size();
        }
        // Set your progress dialog Title
        dddialog.setTitle(title);
        // Set your progress dialog Message
        dddialog.setMessage(getString(R.string.processing_please_wait));
        speakOut(getString(R.string.processing_please_wait));
        dddialog.setIndeterminate(false);
        dddialog.setCancelable(false);
        dddialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            cancelWrite = true;
            dddialog.dismiss();//dismiss dialog
        });
        dddialog.setMax(val);
        dddialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Show progress dialog

        dddialog.show();
        final Message msg = new Message();

        new Thread(() -> {
            try {

                while (dddialog.getProgress() <= dddialog.getMax()) {
                    if (counter==dddialog.getProgress()){
                        dddialog.incrementProgressBy(1);
                    }else if(counter==19977){
                        msg.arg1=1;
                        handler.sendMessage(msg);
                        dddialog.dismiss();
                    }
                    if (dddialog.getProgress() == dddialog.getMax()) {
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


    /**
     * customizable toast
     *
     * @param message
     */


    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        }
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
        if (configTTs != null) {
            configTTs.stop();
            configTTs.shutdown();
        }
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }


    /*HERE WE GET FINAL RESPONSE AND STRING FROM BASE AND ROVER MAINLY ROVER'S $GNGGA,$PUBX STRING*/
    private void displayData(String data) {
        if (data != null) {

            if (data.contains("$$$$,03")) {
                devicedetail=data;
                System.out.println(devicedetail);
            }
            if (deviceList.size() > 10) {
                listAdapter.clear();
                deviceList.clear();
            }
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

            }else if (data.contains("Battery Status")) {
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
                if(data.contains("$PUBX")){
                    ispubx=true;
                    //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                    String cmd[] = data.split("\\$");
                    if(cmd.length == 2){
                        correction = cmd[1].split(",")[8];
                        String numsvrs = cmd[1].split(",")[18];
                        sattxt.setText(numsvrs);
                    }

                    pubxlatitude = data.split(",")[3];
                    pubxlongitude = data.split(",")[5];
                    pubxaltitude = data.split(",")[7];
                   // String cmd[] = data.split("\\$");
                  //  String numsvrs = data.split(",")[18];
               /* if(isLatLng && Configname!=null){
                    isLatLng = false;
                    long id = dbTask.getConfigurtionid(Configname,gnssmodulename,radiomodulename);
                    dbTask.insertlatlngparams(id,"Latitude",pubxlatitude);
                    dbTask.insertlatlngparams(id,"Longtitude",pubxlongitude);
                }*/
                    if(modee.equals("31") && !correction.equalsIgnoreCase("")){
                        if(!isbasesetup){
                            dialogbaseinprocss();
                        }
                    }

                }
            }


            listAdapter.add(data);
            deviceList.add(data);
        }
    }

    /*Parsing the normal data here*/
    void normalparse(String data){
        try{
            if (data.contains("$$$$,03")) {
                devicedetail=data;
                System.out.println(devicedetail);
            }
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                ispubx=true;
                gnggaenable=true;
                String accuracytime = null;
                String fixTime = data.split(",")[1];
                latitude = data.split(",")[2];
                longitude = data.split(",")[4];
                altitude = data.split(",")[9];
                String satellite = data.split(",")[7];
                String accuracy = data.split(",")[8];
                String fix = data.split(",")[6];
                try{
                    accuracytime = data.split(",")[13];
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(isLatLng && Configname!=null){
                    isLatLng = false;
                    long id = dbTask.getConfigurtionid(Configname,gnssmodulename,radiomodulename);
                    dbTask.insertlatlngparams(id,"Latitude",latitude);
                    dbTask.insertlatlngparams(id,"Longtitude",longitude);
                }

                preferenceStore.setLatitude(latitude);
                preferenceStore.setLongtitude(longitude);

                latlongvalue = latitude + "," + longitude + "," + altitude + "," + accuracy + "," + fix;
                StatusData = "";
                if (fix.equalsIgnoreCase("0")) {
                    StatusData = getString(R.string.invalid);
                    listAdapter.add(getString(R.string.invalid));
                    deviceList.add(data);
                } else if (fix.equalsIgnoreCase("1") || fix.equalsIgnoreCase("2") ) {
                    StatusData = getString(R.string.standalone_mode);
                    listAdapter.add(getString(R.string.standalone_mode));
                    deviceList.add(getString(R.string.standalone_mode));
                } else if (fix.equalsIgnoreCase("3")) {
                    StatusData = getString(R.string.not_applicable);
                    listAdapter.add(getString(R.string.not_applicable));
                    deviceList.add(getString(R.string.not_applicable));
                } else if (fix.equalsIgnoreCase("4")) {
                    StatusData = getString(R.string.rtk_fixed);
                    listAdapter.add(getString(R.string.rtk_fixed));
                    deviceList.add(getString(R.string.rtk_fixed));
                } else if (fix.equalsIgnoreCase("5")) {
                    StatusData = getString(R.string.rtk_float);
                    listAdapter.add(getString(R.string.rtk_float));
                    deviceList.add(getString(R.string.rtk_float));
                } else if (fix.equalsIgnoreCase("6")) {
                    StatusData = getString(R.string.estimated);
                    listAdapter.add(getString(R.string.estimated));
                    deviceList.add(getString(R.string.estimated));
                } else if (fix.equalsIgnoreCase("7")) {
                    StatusData = getString(R.string.manual_input_mode);
                    listAdapter.add(getString(R.string.manual_input_mode));
                    deviceList.add(getString(R.string.manual_input_mode));
                } else {
                    StatusData = getString(R.string.simulation_mode);
                    listAdapter.add(getString(R.string.simulation_mode));
                    deviceList.add(getString(R.string.simulation_mode));
                }
                lat_lang = latitude + "_" + longitude + "_" + StatusData + "_" + accuracy + "_" + fix+"_"+altitude+"_"+satellite+"_"+accuracytime+"_";

            } else if (data.contains(getString(R.string.battery_status))) {
                /*Check batterystatus string*/
                bateerydata(data);
                Batterystatus = data;
            }else if(data.contains("$PUBX")){
                ispubx=true;
                //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                correction = data.split(",")[8];
                pubxlatitude = data.split(",")[3];
                pubxlongitude = data.split(",")[5];
                String hAcc = data.split(",")[9];
                String vAcc = data.split(",")[10];
                String HDOP = data.split(",")[15];
                String VDOP = data.split(",")[16];
                String numsvrs = data.split(",")[18];
             /*   if(isLatLng && Configname!=null){
                    isLatLng = false;
                    long id = dbTask.getConfigurtionid(Configname,gnssmodulename,radiomodulename);
                    dbTask.insertlatlngparams(id,"Latitude",pubxlatitude);
                    dbTask.insertlatlngparams(id,"Longtitude",pubxlongitude);
                }*/
                sattxt.setText(numsvrs);
                pubxstring=hAcc+"_"+vAcc+"_"+HDOP+"_"+VDOP+"_"+numsvrs;
                if(modee.equals("31") && !correction.equalsIgnoreCase("")){
                    if(!isbasesetup){
                        dialogbaseinprocss();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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
                        datalenghth = actualKey.length() ;
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

    /*Intent filters for gatt update*/
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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
                if(isFirstTime){
                    isFirstTime = false;
                    batteryLowAudio(getString(R.string.battery_low));
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

    ProgressDialog proDialog;
    @Override
    public void showDailog(Context context) {
        proDialog = new ProgressDialog(context);
        proDialog = ProgressDialog.show(context, getString(R.string.command_loading), getString(R.string.please_wait));
    }

    @Override
    public void hideDialog(Context context) {
        proDialog = new ProgressDialog(context);
        proDialog.dismiss();
    }

    void editdialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        final EditText et = new EditText(this);
        et.setHint(getString(R.string.type_here));
        String etStr = et.getText().toString();

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle(getString(R.string.configuration_name));
        alertDialogBuilder.setCancelable(true);


        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Configname = et.getText().toString().trim();
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would
            // not display the 'Force Close' message
            e.printStackTrace();
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

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class);
                intent.putExtra("configName", Configname);
                intent.putExtra("radiomodulename", radiomodulename);
                intent.putExtra("gnssmodulename",gnssmodulename);
                intent.putExtra("Survey Accuracy",map1.get("Survey Accuracy")) ;
                intent.putExtra("device_name", mDeviceName);
                intent.putExtra("device_address", mDeviceAddress);
                intent.putExtra("device_id", String.valueOf(device_id));
                intent.putExtra("dgps_device_id", String.valueOf(dgps_id));
                intent.putExtra(" selectedmodeule" ,selectedmodeule);

                startActivity(intent);
                dialogBuilder.dismiss();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                mBluetoothLeService.serverDisconnect();
                mConnected=false;
                Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class);
                intent.putExtra("configName", Configname);
                intent.putExtra("radiomodulename", radiomodulename);
                intent.putExtra("gnssmodulename",gnssmodulename);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("Survey Accuracy",map1.get("Survey Accuracy")) ;
                intent.putExtra("device_name", mDeviceName);
                intent.putExtra("device_address", mDeviceAddress);
                intent.putExtra("device_id", String.valueOf(device_id));
                intent.putExtra("dgps_device_id", String.valueOf(dgps_id));
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                finish();
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
        if(modee.equals("31")){
            title= getString(R.string.base);
        }else if(modee.equals("32")){
            title= getString(R.string.rover);
        }
        if(msg.equalsIgnoreCase(getString(R.string.yes))){
            t2.setText(title+" "+ getString(R.string.configured_successfully));
            speakOut(title + " " + getString(R.string.configured_successfully));
            imgvw.setImageResource(R.drawable.successgif);
        }else{
            t2.setText(title+" "+ getString(R.string.not_configured));
            speakOut(title + " " + getString(R.string.not_configured));
            imgvw.setImageResource(R.drawable.alertgif);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modee.equals("31")){
                    dialogbleDisconnect();
                }

                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                Intent intent = new Intent(DeviceControlActivity.this, DeviceScanActivity.class);
                intent.putExtra("device_name", mDeviceName);
                intent.putExtra("device_address", mDeviceAddress);
                intent.putExtra("device_id", device_id);
                intent.putExtra("dgps_device_id", dgps_id);
                intent.putExtra(" selectedmodeule" ,selectedmodeule);
                startActivity(intent);
                finish();
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

    public void dialogbaseinprocss(){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_config_alert, null);
        final TextView t1 = dialogView.findViewById(R.id.t1);
        final TextView t2 = dialogView.findViewById(R.id.t2);
        final ImageView imgvw = dialogView.findViewById(R.id.gifload);
        final Button button = dialogView.findViewById(R.id.btnDialog);
        isbasesetup=true;

        t = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(correction.equalsIgnoreCase("G3")){
                                    t1.setText(getString(R.string.wait_base_setup));
                                    t2.setText(getString(R.string.in_progress));
                                    imgvw.setImageResource(R.drawable.inprogress);
                                    button.setText(getString(R.string.discard));
                                }else  if( correction.equalsIgnoreCase("TT")) {
                                    t1.setText(getString(R.string.done_base_setup));
                                    t2.setText(getString(R.string.completed));
                                    imgvw.setImageResource(R.drawable.successgif);
                                    button.setText(getString(R.string.ok));

                                }
                            }
                        });
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {

                }

            }
        };
        t.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
                if(t.isAlive()){
                    t.interrupt();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public void onBackPressed() {
        alertback();
    }


    public void dialogAutoBase(){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_autobase_list, null);
        final RecyclerView rvAuto = dialogView.findViewById(R.id.rvAuto);
        final Button button = dialogView.findViewById(R.id.btnDialog);


        ArrayList<String> baselistlist;
        baselistlist = dbTask.getRoverList(getString(R.string.auto_base));
        List<ItemType> itemTypeList2 = new ArrayList<>();
        if(baselistlist.isEmpty()){
            dialogBuilder.dismiss();
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAuto.setLayoutManager(linearLayoutManager);

        AutoBAseListAdapter listAdapter = new AutoBAseListAdapter(this, itemTypeList2);
        rvAuto.setAdapter(listAdapter);


          button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  dialogBuilder.dismiss();
              }
          });

          listAdapter.setListerner(new AutoBAseListAdapter.ClickListerner() {
              @Override
              public void onSuccess(String latitude, String longtitude, String altitude, String accuracy) {
                  dialogBuilder.dismiss();
                  abLat = latitude;
                  ablng = longtitude;
                  dialogAutoBaseDetails(latitude,longtitude,altitude,accuracy);
                 // recycerlViewAdapter.setListAdapter(latitude,longtitude, altitude);
              }
          });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void dialogAutoBaseDetails(final  String latitude, final  String longitude, final  String altitude, final String accuracy){
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ab_details, null);
        final TextView tvLat = dialogView.findViewById(R.id.tvLat);
        final TextView tvLng = dialogView.findViewById(R.id.tvLng);
        final TextView tvAlt = dialogView.findViewById(R.id.tvAlt);
        final TextView tvAccuracy = dialogView.findViewById(R.id.tvAccuracy);
        final Button cancel = dialogView.findViewById(R.id.cancel);
        final Button done = dialogView.findViewById(R.id.done);

        tvLat.setText(latitude);
        tvLng.setText(longitude);
        tvAlt.setText(altitude + "  meters");
        tvAccuracy.setText(String.valueOf(hexToInt(accuracy)/100) +" cm");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
                recycerlViewAdapter.setListAdapter(latitude,longitude, altitude, accuracy);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public int hexToInt(String hex){
        // Parse hex to int
        if(hex.contains(".")){
            hex = hex.replace(".","");
        }
        int value = Integer.parseInt(hex, 16);
        // Flip byte order using ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asIntBuffer().put(value);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int flipped = buffer.asIntBuffer().get();

        System.out.println("hex: 0x" + hex);
        System.out.println("flipped: " + flipped);

        return flipped;
    }


}
