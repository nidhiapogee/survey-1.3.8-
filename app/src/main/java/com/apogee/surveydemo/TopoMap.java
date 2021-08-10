package com.apogee.surveydemo;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.utility.BluetoothLeService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static com.apogee.surveydemo.Generic.taskGeneric.Name;

public class TopoMap extends FragmentActivity implements OnMapReadyCallback,TextToSpeech.OnInitListener {
    /*Variables defined here*/
    public static String baseinfpstring = null;
    String  pointname;
    int[] point = {1};
    double latitude =0.0;
    double longitude= 0.0;
    double accuracy= 0.0;
    String altitude = null;
    String payloadfinal;
    int pktno=0;
    int totalnoofpkts=0;
    ArrayList<String> datalist = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public static final String antennapref = "antenapref";
    ArrayList<LatLng> points = null;
    private GoogleMap mMap;
    Marker marker;
    FloatingTextButton btn1,viewMap;
    TextView status,st,estng,nrthng,zvalt,accuracybasetext,antennaht,hzpcn,vtpcn;
    EditText pname,pcode;
    String finalpoint;
    DatabaseOperation dbTask = new DatabaseOperation(TopoMap.this);
    MediaPlayer mediaPlayer = new MediaPlayer();
    int tskid;
    String TAG = "Topo Survey";
    Thread t;
    ImageView btrytopo,accuracybase,antennaheighttt,stlitetopo;
    Toolbar toolbar;
    boolean firstclk=false;
    private BluetoothLeService mBluetoothLeService;
    private TextToSpeech tts;

    private boolean isSpeak = true;
    private boolean isInvalidSpeak = true;
    private boolean isStanaloneSpeak = true;
    private boolean isNotApplicable = true;
    private boolean isRTKSpeak = true;
    private boolean isRTKFSpeak = true;
    private boolean isEstimatedSpeak = true;
    private boolean isManualSpeak = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topo_map);
        status=findViewById(R.id.tpstatus);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn1=findViewById(R.id.topobutton1);
        pname=findViewById(R.id.topopoint);
        st=findViewById(R.id.stlitetoposts);
        estng=findViewById(R.id.estngtopo);
        nrthng=findViewById(R.id.nrthngtopo);
        btrytopo=findViewById(R.id.btrytopo);
        toolbar=findViewById(R.id.tool);
        zvalt=findViewById(R.id.zvalt);
        hzpcn = findViewById(R.id.hzpcn);
        vtpcn = findViewById(R.id.vtpcn);
        viewMap = findViewById(R.id.viewMap);
        accuracybasetext=findViewById(R.id.accuracybasetext);
        antennaht=findViewById(R.id.antennaht);
        pcode=findViewById(R.id.pcode);
        accuracybase=findViewById(R.id.accuracybase);
        antennaheighttt=findViewById(R.id.antennaheight);
        stlitetopo=findViewById(R.id.stlitetopo);
        accuracybase.setColorFilter(getResources().getColor(R.color.white));
        stlitetopo.setColorFilter(getResources().getColor(R.color.white));
        antennaheighttt.setColorFilter(getResources().getColor(R.color.white));
        setActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setTitle(getString(R.string.title_activity_topo_map));
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(TopoMap.this);
        String taskname = sharedPreferences.getString(Name, "default value");
        String antenna = sharedPreferences.getString(antennapref, "default value");
        if(!antenna.equalsIgnoreCase("default value")){
            String height = antenna.split("_")[1];
            antennaht.setText(height);
        }
        dbTask.open();
        tskid = dbTask.gettaskid(taskname);
        tts = new TextToSpeech(this, this);
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
        /*Google maps basic functionality implemented here*/

        points = new ArrayList<>();
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(latitude !=0.0 && longitude != 0.0){
            currentlocation();
        }else{
            Toast.makeText(this, getString(R.string.this_is_your_phone_location), Toast.LENGTH_SHORT).show();
        }
        int height = 50;
        int width = 50;
        @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.orangepin);
        Bitmap b = bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        ArrayList<String> topotaskdata;
        topotaskdata = dbTask.topotaskdata(TAG,tskid);
        if(topotaskdata.isEmpty()){
            Toast.makeText(TopoMap.this, getString(R.string.start_new_survey), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.your_previous_points), Toast.LENGTH_SHORT).show();
            for (int k = 0; k < topotaskdata.size(); k++) {
                String val = topotaskdata.get(k);
                String est = val.split(",")[0];
                String nrth = val.split(",")[1];
                String zone = val.split(",")[5];
                int len=zone.length()-1;
                zone = StringUtils.overlay(zone," ", len, len);
                LatLon2UTM latLon2UTM = new LatLon2UTM();
                String converted = latLon2UTM.UTM2Deg(zone+" "+est+" "+nrth);
                String lat = converted.split(",")[0];
                String lon = converted.split(",")[1];
                double lt = Double.parseDouble(lat);
                double ln = Double.parseDouble(lon);
                String pnm = val.split(",")[2];
                LatLng india = new LatLng(lt, ln);
                marker = mMap.addMarker(new MarkerOptions().position(india).title(pnm).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
                points.add(india);
            }
        }
         /*Topo point calculation according to button click*/

      
        btn1.setOnClickListener(v -> new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                btn1.setBackgroundColor(getResources().getColor(R.color.colorred));
                btn1.setClickable(false);
            }

            public void onFinish() {
                if(!firstclk){
                    pointname = pname.getText().toString();
                    firstclk=true;
                }
                if(!pointname.equalsIgnoreCase("")){
                    mediaPlayer = MediaPlayer.create(TopoMap.this, R.raw.beeppointtwo);
                    mediaPlayer.start();
                    topopoints(pointname);
                    btn1.setBackgroundColor(getResources().getColor(R.color.colorgreen));
                    btn1.setClickable(true);
                }else{
                    Toast.makeText(TopoMap.this, getString(R.string.please_enter_any_point_name), Toast.LENGTH_SHORT).show();
                    btn1.setBackgroundColor(getResources().getColor(R.color.colorgreen));
                    btn1.setClickable(true);
                }

            }
        }.start());


        mMap.setOnMapLoadedCallback(() -> {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            /*
             * for (Marker marker : markers) {
             * builder.include(marker.getPosition()); }
             */

            if(!points.isEmpty()){
                for (LatLng poin : points) {
                    builder.include(poin);
                }

                /*Padding for all points*/
                final LatLngBounds bounds = builder.build();
                int padding = 20; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu, 2000, null);
            }
        });
        // Add marker and move the camera

        viewMap.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),TopoSurveyListActivity.class);
            intent.putExtra("points",points);
            startActivity(intent);
        });
    }



    /*Get individual points for specific location*/
    void topopoints(String pointname){
        String str1 = Integer.toString(point[0]++);
        if(pointname!=null || !pointname.equals("")){
            finalpoint =  pointname+str1;
        }else{
            finalpoint = "p"+str1;
        }
        int height = 50;
        int width = 50;
        @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.clc);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        LatLng india = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(india).title(finalpoint ).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        marker.showInfoWindow();
        points.add(india);
        String pointcode = pcode.getText().toString().trim();
        pname.setText(finalpoint);
        //*Database insertion part*//*
        boolean result = dbTask.insertTopo(TAG,finalpoint,pointcode,tskid,easting,northing,finalalti,Double.parseDouble(hAcc),Double.parseDouble(vAcc),antennaheight,StatusData,getlocale);
        if (result) {
            System.out.println("Data inserted");
            Toast.makeText(TopoMap.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TopoMap.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
        }
    }
    
    /*This thread regullarly update your current location according to your $GNGGA string*/
    /*CurrenLocation according to RTK data*/
    public void currentlocation() {
        try{
            if (marker != null) {
                marker.remove();
            }
            if (latitude != 0.0 || longitude != 0.0) {
                int height = 50;
                int width = 50;
                @SuppressLint("UseCompatLoadingForDrawables")
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.clc);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                LatLng india = new LatLng(latitude, longitude);
                marker = mMap.addMarker(new MarkerOptions().position(india).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
                points.add(india);

            } else {
                Toast.makeText(TopoMap.this, getString(R.string.null_value), Toast.LENGTH_SHORT).show();
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
            }else if(data.equalsIgnoreCase("Device is charging\r\n")){
                btrytopo.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
            }else if(data.contains("b562013c")){
                BasePositionParsing(data);
            }else{
                String[] somedata = data.split("\\r?\\n");
                int length=somedata.length;
                if(length>1){
                    for (String somedatum : somedata) {
                        normalparse(somedatum);
                    }
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


    void BasePositionParsing(String msg){
        LatLon2UTM latLon2UTM = new LatLon2UTM();
        double northinnggbase;
        double eastinggbase;
        double elevationbase;
        double distancebase;
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
          baseinfpstring= baseeasting +","+ basenorthingg +","+ distancebase +","+ baseelevation2 + "/" + pubxbase + "/" + elevationbase +","+ latiii +","+ longii;

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
                @SuppressLint("UseCompatLoadingForDrawables")
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

    public void lastparse(String val){
        if(val != null){
              String lines[] = val.split("\\r?\\n");
            for (String line : lines) {
                normalparse(line);
            }
        }

    }

    String hAcc;
    String HAE;
    String vAcc;
    String getlocale;
    double easting;
    double northing;
    String finalalti;
    String antennaheight;
    String StatusData = "";
    /*Parsing the normal data here*/
    void normalparse(String data){
        try{
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                try{
                    String accuracytime;
                    String lati = data.split(",")[2];
                    String longi = data.split(",")[4];
                    altitude = data.split(",")[9];
                    zvalt.setText(altitude);
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
                    LatLon2UTM latLon2UTM = new LatLon2UTM();
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
                    } else if (fix.equalsIgnoreCase("1") || fix.equalsIgnoreCase("2") ) {
                        StatusData =  getString(R.string.standalone_mode);
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
                        StatusData =  getString(R.string.not_applicable);
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
            }else if(data.equalsIgnoreCase("Device is charging\r\n")){
                btrytopo.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
            }else if(data.contains("$PUBX")) {
                try {
                    //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                    hAcc = data.split(",")[9];
                    HAE = data.split(",")[7];
                    vAcc = data.split(",")[10];
                    String numsvrs = data.split(",")[18];
                    hzpcn.setText(getString(R.string.h_prec)+" ="+" "+hAcc+"m");
                    vtpcn.setText(getString(R.string.v_prec)+" ="+" "+vAcc+"m");
                    st.setText(numsvrs);
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
                btrytopo.setImageResource(R.drawable.batteryone);
                stopSpeak();
            }else if(data.contains("Battery Status:3.")){
                btrytopo.setImageResource(R.drawable.batterytwo);
                stopSpeak();
            }else if(data.contains("Battery Status:4.")){
                btrytopo.setImageResource(R.drawable.batterythree);
                stopSpeak();
            }else if(data.contains("Battery Status:5.")){
                btrytopo.setImageResource(R.drawable.batteryfour);
                stopSpeak();
            }else if(data.contains("Battery Status:1.")){
                btrytopo.setImageResource(R.drawable.ic_battery_alert_black_24dp);
                delayBatteryStatus();
            }else if(data.contains("Battery Status: Charging")){
                btrytopo.setImageResource(R.drawable.ic_baseline_battery_charging_full_24);
                btrytopo.setColorFilter(getResources().getColor(R.color.black));
                stopSpeak();
            }else if(data.contains("Battery Status: Fully Charged")){
                btrytopo.setImageResource(R.drawable.batteryfive);
                stopSpeak();
            }else if(data.contains("Battery Status:6.")){
                btrytopo.setImageResource(R.drawable.batteryfive);
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
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Write whatever to want to do after delay specified (1 sec)
            Log.d("Handler", "Running Handler");
            speakOut(getString(R.string.battery_low));
            delayBatteryStatus();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopSpeak();
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

   public void antennatopo(View view){
        dialogantenna();
    }

    String antennadatacalculation(String msg){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(TopoMap.this);
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
                finaloutput = Math.sqrt(Math.pow(Double.parseDouble(height),2)-Math.pow(0.0675,2));
            }
        }
        return String.valueOf(finaloutput);
    }

    public void dialogantenna(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(TopoMap.this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogantenna, null);
        final String[] antennatype = new String[1];
        final EditText editText = dialogView.findViewById(R.id.nm);
        final EditText editText1 = dialogView.findViewById(R.id.rds);
        final ImageView imgvw = dialogView.findViewById(R.id.imgv);
        final Spinner spinner = dialogView.findViewById(R.id.antennatype);
        Button button1 = dialogView.findViewById(R.id.antnabtn);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(TopoMap.this);
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
        final ArrayAdapter<String> model_typeAdapter = new ArrayAdapter<>(TopoMap.this, android.R.layout.simple_spinner_item, typelist);
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

        button1.setOnClickListener(view -> {
            // DO SOMETHINGS
            double deefault = 25.3;
            double deefault2 = 0.09;
            String pointname = editText.getText().toString().trim();
            String height = editText1.getText().toString().trim();
            if(!pointname.equalsIgnoreCase("") && !height.equalsIgnoreCase("")){
                if(antennatype[0].equalsIgnoreCase(getString(R.string.vertical))){
                    editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.vertical));
                    editor.apply();
                    double finaloutput = deefault-(Double.parseDouble(height)+deefault2);
                    Toast.makeText(TopoMap.this, finaloutput+"M", Toast.LENGTH_SHORT).show();
                }else if(antennatype[0].equalsIgnoreCase(getString(R.string.slope))){
                    editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.slope));
                    double fianloutput = Math.sqrt(Math.pow(Double.parseDouble(height),2)-Math.pow(0.0675,2));
                    Toast.makeText(TopoMap.this, fianloutput+"M", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(TopoMap.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
            dialogBuilder.dismiss();
                antennaht.setText(height);
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onBackPressed() {
        t=new Thread();
        if(t.isAlive()){
            t.interrupt();
        }
      Intent intent = new Intent(TopoMap.this,HomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
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
}
