


package com.apogee.surveydemo;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import org.apache.commons.lang3.StringUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;
import static com.apogee.surveydemo.TopoMap.baseinfpstring;
import static com.apogee.surveydemo.Generic.taskGeneric.Name;

public class AutoMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,TextToSpeech.OnInitListener {
    /*AutoMAp variables defined here*/
    int[] point = {1};
    boolean firsttime=false;
    LocationManager lm;
   public static boolean distancemode = false;
    Thread t;
    Thread t2;
    public  String pointname;
    String payloadfinal;
    int pktno = 0;
    int totalnoofpkts = 0;
    ArrayList<String> datalist = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public static final String antennapref = "antenapref";
    ArrayList<LatLng> points = null;
    ArrayList<LatLng> points2 = null;
    private GoogleMap mMap;
    MediaPlayer mediaPlayer = new MediaPlayer();
    LatLon2UTM latLon2UTM = new LatLon2UTM();
    double latitude = 0.0;
    double longitude = 0.0;
    double accuracy = 0.0;
    double estng2 =0.0;
    double northing2 = 0.0;
    String altitude = null;
    Marker marker;
    FloatingTextButton btn1,stakeMap;
    DeviceControlActivity dle = new DeviceControlActivity();
     public static EditText pname, vall, pcode;
    TextView status, st, estng, nrthng, hzpcn,vtpcn, devicebtrystss, zval, accuracybasetext, antennaht;
    String finalpoint;
   public static Switch simpleSwitch;
    public   int threadtime = 0;
   public static int distance;
    DatabaseOperation dbTask = new DatabaseOperation(AutoMap.this);
    String TAG = "Auto Survey";
    int tskid;
    ImageView btrystats, accuracybase, antennaheighttt,stlitetopo;
    Toolbar toolbar;
    boolean finish = false;
    List<Polyline> polylines = new ArrayList<Polyline>();

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
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

    public static String latitu ="";
    public static String longti = "";
    List<String> taskid = new ArrayList<>();
    List<String> recordSurvey = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn1 = findViewById(R.id.point);
        pname = findViewById(R.id.autopoint);
        status = findViewById(R.id.status);
        st = findViewById(R.id.stliteautosts);
        hzpcn = findViewById(R.id.hzpcn);
        vtpcn = findViewById(R.id.vtpcn);
        toolbar = findViewById(R.id.tool);

        estng = findViewById(R.id.estngauto);
        nrthng = findViewById(R.id.nrthngauto);
        vall = findViewById(R.id.tdval);
        btrystats = findViewById(R.id.btrystats);
        zval = findViewById(R.id.zval);
        pcode = findViewById(R.id.editText);
        accuracybasetext = findViewById(R.id.accuracybasetext);
        antennaht = findViewById(R.id.antennaht);
        accuracybase = findViewById(R.id.accuracybase);
        stlitetopo = findViewById(R.id.stlitetopo);
        antennaheighttt = findViewById(R.id.antennaheight);
        stakeMap = findViewById(R.id.stakeMap);
        accuracybase.setColorFilter(getResources().getColor(R.color.white));
        stlitetopo.setColorFilter(getResources().getColor(R.color.white));
        antennaheighttt.setColorFilter(getResources().getColor(R.color.white));
        simpleSwitch =  findViewById(R.id.simpleSwitch); // initiate Switch
        t = new Thread();
        t2 = new Thread();
        tts = new TextToSpeech(this, this);
        configTTs = new TextToSpeech(this,this);
        /*Toolbar setup*/
        setActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // displayed text of the Switch whenever it is in checked or on state
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AutoMap.this);
        String taskname = sharedPreferences.getString(Name, "default value");
        String antenna = sharedPreferences.getString(antennapref, "default value");
        if (!antenna.equalsIgnoreCase("default value")) {
            String height = antenna.split("_")[1];
            antennaht.setText(height);
        }
        dbTask.open();
        tskid = dbTask.gettaskid(taskname);
        taskid =  dbTask.gettaskid();
        recordSurvey =  dbTask.gettaskSurvey();

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch.isChecked()) {
                    simpleSwitch.setText(getString(R.string.time));
                    vall.setHint("Sec");
                    Toast.makeText(AutoMap.this, getString(R.string.time), Toast.LENGTH_SHORT).show();
                } else {
                    simpleSwitch.setText(getString(R.string.distance));
                    vall.setHint("Meter");
                    Toast.makeText(AutoMap.this, getString(R.string.distance), Toast.LENGTH_SHORT).show();
                }

            }
        });


        stakeMap.setOnClickListener(v -> {
            if(!taskid.contains(String.valueOf(tskid)) || !recordSurvey.contains("Auto Survey")){
                    if(!latitu.equals("")&&!longti.equals("") && checkValidation()) {
                        Intent intent = new Intent(AutoMap.this, StakeMapCustom.class);
                        intent.putExtra("FromStake",false);
                        startActivity(intent);
                    }else if(checkValidation()) {
                        Intent intent = new Intent(AutoMap.this, StakeMapCustom.class);
                        intent.putExtra("FromStake",false);
                        startActivity(intent);
                        Toast.makeText(AutoMap.this, "Lat/Lng not available", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AutoMap.this, "Please create another task", Toast.LENGTH_SHORT).show();
                }


        });

        /*Button for switch configuration*/
        btn1.setOnClickListener(v -> {
            if(!taskid.contains(String.valueOf(tskid)) && !recordSurvey.contains("Auto Survey")){
                    if (!finish) {
                        finish = true;
                        btn1.setBackgroundColor(getResources().getColor(R.color.colorred));
                        if (simpleSwitch.isChecked()) {
                            int tdtime;
                            try {
                                tdtime = Integer.parseInt(vall.getText().toString());
                            } catch (NumberFormatException ex) {
                                finish = false;
                                btn1.setBackgroundColor(getResources().getColor(R.color.colorgreen));
                                //They didn't enter a number.  Pop up a toast or warn them in some other way
                                Toast.makeText(getApplicationContext(), getString(R.string.please_enter_a_valid_number), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String lat_long = dle.lat_lang;
                            if (latitude !=0.0 && longitude != 0.0) {
                                threadtime = tdtime * 1000;
                                timesurvey();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.configure_rtk_first), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            try {
                                String lat_long = dle.lat_lang;
                                if (latitude !=0.0 && longitude != 0.0) {
                                    distance = Integer.parseInt(vall.getText().toString());
                                    distancemode=true;
                                    //  distancepoints();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.configure_rtk_first), Toast.LENGTH_SHORT).show();
                                }


                            } catch (NumberFormatException ex) {
                                finish = false;
                                btn1.setBackgroundColor(getResources().getColor(R.color.colorgreen));
                                pname.setText("");
                                pcode.setText("");
                                point = new int[]{1};
                                distancemode=false;
                                //They didn't enter a number.  Pop up a toast or warn them in some other way
                                Toast.makeText(getApplicationContext(), getString(R.string.please_enter_a_valid_number), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else if (finish) {
                        finish = false;
                        threadtime = 0;
                        pname.setText("");
                        pcode.setText("");
                        pointname=null;
                        distancemode=false;
                        point = new int[]{1};
                        if (t.isAlive()) {
                            t.interrupt();
                        }
                        if(t2.isAlive()){
                            t2.interrupt();
                            firsttime=false;
                        }
                        btn1.setBackgroundColor(getResources().getColor(R.color.colorgreen));
                        Toast.makeText(AutoMap.this, getString(R.string.survey_finished), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AutoMap.this, "Please create another task", Toast.LENGTH_SHORT).show();
                }



        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        points = new ArrayList<>();
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        /*Customicon Bitmap Drwable*/
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.orangepin);
        Bitmap b = bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        /*Showing Previous points */
        ArrayList<String> topotaskdata = new ArrayList<>();
        topotaskdata = dbTask.topotaskdata(TAG,tskid);
        if(topotaskdata.isEmpty()){
            Toast.makeText(this, getString(R.string.start_new_survey), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.your_previous_points), Toast.LENGTH_SHORT).show();
            points2 = new ArrayList<>();
            for (int k = 0; k < topotaskdata.size(); k++) {
                String val = topotaskdata.get(k);
                String est = val.split(",")[0];
                String nrth = val.split(",")[1];
                String zone = val.split(",")[5];
                int len=zone.length()-1;
                zone = StringUtils.overlay(zone," ", len, len);
                String converted = latLon2UTM.UTM2Deg(zone+" "+est+" "+nrth);
                String lat = converted.split(",")[0];
                String lon = converted.split(",")[1];
                double lt = Double.parseDouble(lat);
                double ln = Double.parseDouble(lon);
                String pnm = val.split(",")[2];
                LatLng india = new LatLng(lt, ln);
                points2.add(india);
                marker = mMap.addMarker(new MarkerOptions().position(india).title(pnm).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
                points.add(india);
            }
            polylinedraw(mMap);
        }


        // Add marker and move the camera
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                /*
                 * for (Marker marker : markers) {
                 * builder.include(marker.getPosition()); }
                 */
                if(!points.isEmpty()){
                    for (LatLng poin : points) {
                        builder.include(poin);
                    }

                    final LatLngBounds bounds = builder.build();
                    int padding = 20; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                            padding);
                    mMap.moveCamera(cu);
                    mMap.animateCamera(cu, 2000, null);
                }
            }
        });
    }


    /*CurrenLocation according to RTK data*/
    public void currentlocation() {
        try{
            if (marker != null) {
                marker.remove();
            }
            if (latitude != 0.0 || longitude != 0.0) {
                int height = 50;
                int width = 50;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.clc);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                LatLng india = new LatLng(latitude, longitude);
                marker = mMap.addMarker(new MarkerOptions().position(india).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
                points.add(india);

            } else {
                Toast.makeText(AutoMap.this, getString(R.string.null_value), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // BROADCAST RECIEVER USED FOR CONNECTION OF BLE
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayData(String data) {
        if (data != null) {
            /*Phone battery method*/
            /******/
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
            }else if(data.contains("b562013c")){
                BasePositionParsing(data);
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



   public static String hAcc = "0.0";
   public static String vAcc = "0.0";
    String HAE;
    public static  String getlocale = "0.0";
    double easting;
    double northing;
    public  static String finalalti =" ";
    public  static String antennaheight = " ";
    public  static  String StatusData = " ";
    /*Parsing the normal data here*/
    void normalparse(String data){
        try{
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                try{
                    String accuracytime = null;
                    String lati = data.split(",")[2];
                    String longi = data.split(",")[4];
                    altitude = data.split(",")[9];
                    zval.setText(altitude);
                    String acc = data.split(",")[8];
                    String fix = data.split(",")[6];
                    try{
                        accuracytime = data.split(",")[13];
                        accuracybasetext.setText(accuracytime);
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                   if (fix.equalsIgnoreCase("4")) {
                        accuracy = Double.parseDouble(acc) * 2;
                    } else if (fix.equalsIgnoreCase("5")) {
                        accuracy = Double.parseDouble(acc) * 20;
                    } else {
                        accuracy = Double.parseDouble(acc) * 250;
                    }
                    String vlll = latLon2UTM.latlngcnvrsn(lati,longi);
                    String latttllll = vlll.split("_")[0];
                    latitude = new Double(latttllll);
                    String lonngggll = vlll.split("_")[1];
                    longitude = new Double(lonngggll);
                    currentlocation();
                    /*This part belongs to Easting nothing conversion and displaying that data on textview*/

                    String get = latLon2UTM.convertLatLonToUTM(latitude,longitude);
                    getlocale = get.split(" ")[0];
                    String geteasting = get.split(" ")[1];
                    easting = Double.parseDouble(geteasting);
                    easting = Double.parseDouble(new DecimalFormat("##.###").format(easting));
                    String getnorthing = get.split(" ")[2];
                    northing = Double.parseDouble(getnorthing);
                    northing = Double.parseDouble(new DecimalFormat("##.###").format(northing));
                    estng.setText(String.valueOf(easting));
                    nrthng.setText(String.valueOf(northing));
                    finalalti =  antennadatacalculation(altitude);
                    antennaheight=antennaht.getText().toString().trim();

                    latitu = lati;
                    longti = longi;
                    if(distancemode){
                        distancepoints(easting,northing,estng2,northing2);
                    }
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
                    }  else if (fix.equalsIgnoreCase("3")) {
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
                    } else if (fix.equalsIgnoreCase("4")) {
                        StatusData = getString(R.string.rtk_fixed);

                        latitu = lati;
                        longti = longi;
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
                    }
                    status.setText(StatusData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (data.contains(getString(R.string.battery_status))) {
                /*Check batterystatus string*/
                bateerydata(data);
            }else if(data.contains("$PUBX")) {
                try {
                    //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                  String  correction = data.split(",")[8];
                  String  pubxlatitude = data.split(",")[3];
                  String  pubxlongitude = data.split(",")[5];
                    hAcc = data.split(",")[9];
                    vAcc = data.split(",")[10];
                    HAE = data.split(",")[7];
                    String numsvrs = data.split(",")[18];
                    st.setText(numsvrs);
                    hzpcn.setText(getString(R.string.h_prec)+" ="+" "+hAcc+"m");
                    vtpcn.setText(getString(R.string.v_prec)+" ="+" "+vAcc+"m");

                    if(correction.equalsIgnoreCase("TT")){
                        latitu=pubxlatitude;
                        longti=pubxlongitude;
                        Toast.makeText(getApplicationContext(), "Lat long"+latitude+","+longitude, Toast.LENGTH_SHORT).show();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*This method is basically for Time Auto Survey */
    public void timesurvey() {
        pointname = pname.getText().toString();
        t = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str1 = Integer.toString(point[0]++);
                                if(pointname!=null || !pointname.equals("")){
                                    finalpoint =  pointname+str1;
                                }else{
                                    finalpoint = "p"+str1;
                                }
                                int height = 50;
                                int width = 50;
                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.clc);
                                Bitmap b = bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                LatLng india = new LatLng(latitude, longitude);
                                marker = mMap.addMarker(new MarkerOptions().position(india).title(finalpoint ).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                marker.showInfoWindow();
                                points.add(india);
                                pname.setText(finalpoint);
                                String pointcode = pcode.getText().toString().trim();
                                //*Database insertion part*//*
                                boolean result = dbTask.insertTopo(TAG,finalpoint,pointcode,tskid,easting,northing,finalalti,Double.parseDouble(hAcc),Double.parseDouble(vAcc),antennaheight,StatusData,getlocale);
                                if (result) {
                                    mediaPlayer = MediaPlayer.create(AutoMap.this, R.raw.beeppointtwo);
                                    mediaPlayer.start();
                                    Toast.makeText(AutoMap.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AutoMap.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                        Thread.sleep(threadtime);
                    }
                } catch (InterruptedException e) {

                }

            }
        };
        t.start();
    }
    /*This method is basically call for Auto distance calculation*/
    public void distancepoints(double eastingg,double northingg,double estngg2, double nrthngg2) {
        String str1;
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.clc);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        if(!firsttime){
            pointname = pname.getText().toString();
            str1 = Integer.toString(point[0]++);
            firsttime=true;

            if (pointname !=null && !pointname.equalsIgnoreCase("")){
                finalpoint = pointname + str1;
            }else{
                finalpoint = "p"+str1;
            }
            LatLng india = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(india).title(finalpoint).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            marker.showInfoWindow();
            points.add(india);
            pname.setText(finalpoint);
            String pointcode = pcode.getText().toString().trim();
            //*Database insertion part*//*
            boolean result = dbTask.insertTopo(TAG, finalpoint, pointcode, tskid, easting, northing, finalalti, Double.parseDouble(hAcc), Double.parseDouble(vAcc), antennaheight, StatusData, getlocale);
            if (result) {
                Toast.makeText(AutoMap.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                estng2=easting;
                northing2=northing;
            } else {
                Toast.makeText(AutoMap.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
            }
        }else{
          //  String str1 = Integer.toString(point[2]++);
            double distancee = latLon2UTM.calculateDistanceBetweenPoints(eastingg,northingg, estngg2, nrthngg2);
            Toast.makeText(AutoMap.this, "Check"+distancee, Toast.LENGTH_SHORT).show();
            if((int)distancee>=distance) {
                str1 = Integer.toString(point[0]++);
                if (pointname !=null || !pointname.equalsIgnoreCase("")){
                    finalpoint = pointname + str1;
                }else{
                    finalpoint = "p"+str1;
                }
                LatLng india = new LatLng(latitude, longitude);
                marker = mMap.addMarker(new MarkerOptions().position(india).title(finalpoint).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
                points.add(india);
                pname.setText(finalpoint);
                String pointcode = pcode.getText().toString().trim();
                //*Database insertion part*//*
                boolean result = dbTask.insertTopo(TAG, finalpoint, pointcode, tskid, easting, northing, finalalti, Double.parseDouble(hAcc), Double.parseDouble(vAcc), antennaheight, StatusData, getlocale);
                if (result) {
                    Toast.makeText(AutoMap.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                    estng2=easting;
                    northing2=northing;
                } else {
                    Toast.makeText(AutoMap.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                }
            }
        }


    }



    void bateerydata(String data){
        if(data!=null){
            if(data.contains("Battery Status:2.")){
                btrystats.setImageResource(R.drawable.batteryone);
                stopSpeak();
               // devicebtrystss.setText("Battery");
            }else if(data.contains("Battery Status:3.")){
                btrystats.setImageResource(R.drawable.batterytwo);
                stopSpeak();
               // devicebtrystss.setText("Battery");
            }else if(data.contains("Battery Status:4.")){
                btrystats.setImageResource(R.drawable.batterythree);
                stopSpeak();
               // devicebtrystss.setText("Battery");
            }else if(data.contains("Battery Status:5.")){
                btrystats.setImageResource(R.drawable.batteryfour);
                stopSpeak();
               // devicebtrystss.setText("Battery");
            }else if(data.contains("Battery Status:1.")){
                btrystats.setImageResource(R.drawable.ic_battery_alert_black_24dp);
               // devicebtrystss.setText("Battery");
                delayBatteryStatus();
            }else if(data.contains("Battery Status: Charging")){
                btrystats.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
                btrystats.setColorFilter(getResources().getColor(R.color.black));
                stopSpeak();
               // devicebtrystss.setText("Charging");
            }else if(data.contains("Battery Status: Fully Charged")){
                btrystats.setImageResource(R.drawable.batteryfive);
                stopSpeak();
               // devicebtrystss.setText("Fully Charged");
            } else if(data.contains("Battery Status:6.")){
                btrystats.setImageResource(R.drawable.batteryfive);
                stopSpeak();
               // devicebtrystss.setText("Battery");
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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Write whatever to want to do after delay specified (1 sec)
                Log.d("Handler", "Running Handler");
                speakOut(getString(R.string.battery_low));
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
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /*Option menu for satelliteview/streetview and nomapview*/
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater oMenu = getMenuInflater();
        oMenu.inflate(R.menu.mapsmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.streetview:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.satview:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;

            case R.id.noneview:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            return true;
        }
        return false;
    }

    public void antennaauto(View view){
        dialogantenna();
    }

    /*Antennaheight calculation method*/
    String antennadatacalculation(String msg){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AutoMap.this);
        String antenna = sharedPreferences.getString(antennapref, "default value");
        double finaloutput=0.0;
        if(!antenna.equalsIgnoreCase("default value")){
            double deefault = Double.parseDouble(msg);
            double deefault2 = 0.09;
            String height = antenna.split("_")[1];
            String type = antenna.split("_")[2];
            if(type.equalsIgnoreCase(getString(R.string.vertical))){
                finaloutput = deefault-(Double.parseDouble(height)+deefault2);
            }else if(type.equalsIgnoreCase(getString(R.string.slope))){
                finaloutput = deefault - Math.sqrt(Math.pow(Double.parseDouble(height),2)-Math.pow(0.0675,2));
            }
        }
        return String.valueOf(finaloutput);
    }

    /*Antenna dialog*/
    public void dialogantenna(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(AutoMap.this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogantenna, null);
        final String[] antennatype = new String[1];
        final EditText editText = dialogView.findViewById(R.id.nm);
        final EditText editText1 = dialogView.findViewById(R.id.rds);
        final ImageView imgvw = dialogView.findViewById(R.id.imgv);
        final Spinner spinner = dialogView.findViewById(R.id.antennatype);
        Button button1 = dialogView.findViewById(R.id.antnabtn);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AutoMap.this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String antenna = sharedPreferences.getString(antennapref, "default value");
        if(!antenna.equalsIgnoreCase("default value")) {
            String pname = antenna.split("_")[0];
            String height = antenna.split("_")[1];
            editText.setText(pname);
            editText1.setText(height);
        }

        final ArrayList<String> typelist = new ArrayList<>();
        typelist.add(getString(R.string.vertical));
        typelist.add(getString(R.string.slope));
        final ArrayAdapter<String> model_typeAdapter = new ArrayAdapter<>(AutoMap.this, android.R.layout.simple_spinner_item, typelist);
        model_typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(model_typeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                antennatype[0] = parent.getItemAtPosition(position).toString();
                if(antennatype[0].equalsIgnoreCase(getString(R.string.vertical))){
                    imgvw.setImageResource(R.drawable.vertical);
                }else if(antennatype[0].equalsIgnoreCase(getString(R.string.slope))){
                    imgvw.setImageResource(R.drawable.slope);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                double deefault = 25.3;
                double deefault2 = 0.09;
                String pointname = editText.getText().toString().trim();
                String height = editText1.getText().toString().trim();
                if(!pointname.equalsIgnoreCase("") && !height.equalsIgnoreCase("")){
                    if(antennatype[0].equalsIgnoreCase(getString(R.string.vertical))){
                        editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.vertical));
                        editor.commit();
                    }else if(antennatype[0].equalsIgnoreCase(getString(R.string.slope))){
                        editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.slope));
                        DecimalFormat df=new DecimalFormat();
                    }

                }else{
                    Toast.makeText(AutoMap.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
                dialogBuilder.dismiss();
                antennaht.setText(height);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    /*This method is basically draws polyline between all the previous points*/
    public void polylinedraw(GoogleMap googleMap){
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().addAll(points2));
        polylines.add(polyline1);
        // [END maps_poly_activity_add_polyline]
        // [START_EXCLUDE silent]
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // [END maps_poly_activity_add_polyline_set_tag]
        // Style the polyline.
        stylePolyline(polyline1);
        /*I wish now that you call once*/
    }
    private static final int COLOR_BLACK_ARGB = 0xff388E3C;
    private static final int POLYLINE_STROKE_WIDTH_PX = 10;

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
               /* polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));*/
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // [END maps_poly_activity_style_polyline]


    // [START maps_poly_activity_on_polyline_click]
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, getString(R.string.route_type)+" " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }
    // [END maps_poly_activity_on_polyline_click]


    void BasePositionParsing(String msg){
        LatLon2UTM latLon2UTM = new LatLon2UTM();
        double northinnggbase=0.0;
        double eastinggbase=0.0;
        double elevationbase=0.0;
        double distancebase=0.0;
        try{
            /*Northing Base*/
            String norBase = msg.substring(28,36);
            long value1 = Long.parseLong(norBase, 16);
            norBase = bytesToHex(intToLittleEndian1(value1)).toUpperCase();
            String res = latLon2UTM.hexToBin(norBase);
            String substring = norBase.substring(0,2);
            if(substring.equalsIgnoreCase("FF")){
                String val = latLon2UTM.twosCompliment(res);
                long decimal=Long.parseLong(val,2);
                double finalmeter = (decimal/100.0);
                northinnggbase = (0-finalmeter);
                System.out.println(northinnggbase);
            }else{
                long decimalll=Long.parseLong(norBase,16);
                double finalmeter = (decimalll/100.0);
                northinnggbase = (finalmeter);
                System.out.println(northinnggbase);
            }

            /*Easting base*/
            /**/ String eastBase = msg.substring(36,44);
            long value2 = Long.parseLong(eastBase, 16);
            eastBase = bytesToHex(intToLittleEndian1(value2)).toUpperCase();
            String res2 = latLon2UTM.hexToBin(eastBase);
            String substring2 = eastBase.substring(0,2);

            if(substring2.equalsIgnoreCase("FF")){
                String val = latLon2UTM.twosCompliment(res2);
                long decimal2=Long.parseLong(val,2);
                double finalmeter2 = (decimal2/100.0);
                eastinggbase = (0-finalmeter2);
                System.out.println(eastinggbase);
            }else{
                long decimalll2=Long.parseLong(eastBase,16);
                double finalmeter2 = (decimalll2/100.0);
                eastinggbase = (finalmeter2);

            }


            /*Elevation base*/
            /**/ String elevatBase = msg.substring(44,52);
            long value3 = Long.parseLong(elevatBase, 16);
            elevatBase = bytesToHex(intToLittleEndian1(value3)).toUpperCase();
            String res3 = latLon2UTM.hexToBin(elevatBase);
            String substring3 = elevatBase.substring(0,2);

            if(substring3.equalsIgnoreCase("FF")){
                String val = latLon2UTM.twosCompliment(res3);
                long decimal3=Long.parseLong(val,2);
                double finalmeter3 = (decimal3/100.0);
                elevationbase = (0-finalmeter3);
                System.out.println(elevationbase);
            }else{
                long decimalll3=Long.parseLong(elevatBase,16);
                double finalmeter3 = (decimalll3/100.0);
                elevationbase = (finalmeter3);
                System.out.println(elevationbase);
            }



            /*Distance base*/
            /**/ String distBase = msg.substring(52,60);
            long value4 = Long.parseLong(distBase, 16);
            distBase = bytesToHex(intToLittleEndian1(value4)).toUpperCase();
            String res4 = latLon2UTM.hexToBin(distBase);
            String substring4 = distBase.substring(0,2);

            if(substring4.equalsIgnoreCase("FF")){
                String val = latLon2UTM.twosCompliment(res4);
                long decimal4=Long.parseLong(val,2);
                double finalmeter4 = (decimal4/100.0);
                distancebase = (0-finalmeter4);
                System.out.println(distancebase);
            }else{
                long decimalll4=Long.parseLong(distBase,16);
                double finalmeter4 = (decimalll4/100.0);
                distancebase = (finalmeter4);
                System.out.println(distancebase);
            }


            double baseeasting = (easting - (eastinggbase));
            double basenorthingg = (northing - (northinnggbase));
            double baseElevation = ((Double.parseDouble(HAE)) - (Double.parseDouble(altitude)));
            double baseelevation2 = (((Double.parseDouble(HAE)) + (elevationbase)) - baseElevation);
            double pubxbase = (Double.parseDouble(HAE)) + (elevationbase);

            String converted = latLon2UTM.UTM2Deg("43 Q"+" "+baseeasting+" "+basenorthingg);
            String lat = converted.split(",")[0];
            String lon = converted.split(",")[1];
            double latiii = Double.parseDouble(lat);
            double longii = Double.parseDouble(lon);
            baseinfpstring = baseeasting +","+ basenorthingg +","+ distancebase +","+ baseelevation2 + "/" + pubxbase + "/" + elevationbase +","+ latiii +","+ longii;

            baselocation(latiii,longii);

            System.out.println(baseeasting +String.valueOf(basenorthingg));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    Marker marker2 = null;
    public void baselocation(double latitude, double longitude) {


        try{
            if (marker2 != null) {
                marker2.remove();
            }
            if (latitude != 0.0 || longitude != 0.0) {
                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.basepngicon);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                LatLng india = new LatLng(latitude, longitude);
                marker2 = mMap.addMarker(new MarkerOptions().position(india).title(getString(R.string.base_location)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker2.showInfoWindow();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static byte[] intToLittleEndian1(long numero) {
        byte[] b = new byte[4];
        b[0] = (byte) (numero & 0xFF);
        b[1] = (byte) ((numero >> 8) & 0xFF);
        b[2] = (byte) ((numero >> 16) & 0xFF);
        b[3] = (byte) ((numero >> 24) & 0xFF);
        return b;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }




   /* @Override
    public void onBackPressed() {
        Intent intent = new Intent(AutoMap.this,HomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }*/

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
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
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }

    public boolean checkValidation(){
        if(pname.getText().toString().trim().isEmpty()){
            Toast.makeText(AutoMap.this, "Please Fill Point Name", Toast.LENGTH_SHORT).show();
            return false;
        }else if(pcode.getText().toString().trim().isEmpty()){
            Toast.makeText(AutoMap.this, "Please Fill Point Code", Toast.LENGTH_SHORT).show();
            return false;
        }else if(vall.getText().toString().trim().isEmpty()){
            Toast.makeText(AutoMap.this, "Please Fill Point Time/Distance", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }

    }
}
