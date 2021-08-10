package com.apogee.surveydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.apogee.surveydemo.utility.BluetoothLeService;
import com.apogee.surveydemo.utility.DeviceControlActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;


import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class StakeMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, SensorEventListener {
    String TAG = "Stake out";
    // device sensor manager
    /*Graph section*/
    PointsGraphSeries<DataPoint> xySeries;
    PointsGraphSeries<DataPoint> xy2Series;
    PointsGraphSeries<DataPoint> xy3Series;
    PointsGraphSeries<DataPoint> xy4Series;
    LineGraphSeries<DataPoint> lineSeries;
    GraphView mScatterPlot;
    boolean graphcall = false;


    private SensorManager mSensorManager;
    /*Variables defined here*/
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Marker marker;
    boolean isMarkerRotating;
    ArrayList<LatLng> points = null;
    DeviceControlActivity dle = new DeviceControlActivity();
    double latiii = 0.0;
    double easting = 0.0;
    double eastingg = 0.0;
    double northingg = 0.0;
    double longii = 0.0;
    double northing = 0.0;
    FloatingTextButton distancekm, pointn, heading;
    Toolbar toolbar;
    double longitude = 0.0;
    double latitude = 0.0;
    List<Polyline> polylines = new ArrayList<>();
    List<Polyline> polylines2 = new ArrayList<>();

    private BluetoothLeService mBluetoothLeService;
    String payloadfinal;
    int pktno = 0;
    int totalnoofpkts = 0;
    ArrayList<String> datalist = new ArrayList<>();
    LatLon2UTM latLon2UTM = new LatLon2UTM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake_map);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mScatterPlot = findViewById(R.id.scatterPlot);
        mScatterPlot.setBackgroundColor(getResources().getColor(android.R.color.white));
        mScatterPlot.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        mScatterPlot.getGridLabelRenderer().setVerticalLabelsVisible(false);
        mScatterPlot.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        mScatterPlot.getViewport().setDrawBorder(false);
        mScatterPlot.setVisibility(View.INVISIBLE);
        xySeries = new PointsGraphSeries<>();
        xy2Series = new PointsGraphSeries<>();
        xy3Series = new PointsGraphSeries<>();
        xy4Series = new PointsGraphSeries<>();
        lineSeries = new LineGraphSeries<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distancekm = findViewById(R.id.distanceinkm);
        pointn = findViewById(R.id.stakepoint);
        heading = findViewById(R.id.heading);
        toolbar = findViewById(R.id.tool);
        setActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setTitle(getString(R.string.stake_survey));
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        isMarkerRotating = false;
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
        mMap.setMyLocationEnabled(false);
       // mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        /*84.984656, 71.342201
         * -84.190203, 74.244858*/

        points = new ArrayList<>();
        String lat_long = dle.lat_lang;
        if (lat_long != null) {
            currentlocation();
        }else{
            Toast.makeText(this, getString(R.string.rtk_data_not_found), Toast.LENGTH_SHORT).show();
        }
        String pointnm;
        String location;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            pointnm = null;
        } else {
            /*Easting Northing conversion and point placed on google maps*/
            pointnm = extras.getString("Point_nm");
            location = extras.getString("location");
            String est = location.split(",")[0];
            String eastng = est.split(":")[1];
            eastng = eastng.replace(" ","");
            easting = Double.parseDouble(eastng);
            String nrth = location.split(",")[1];
            String nrthng = nrth.split(":")[1];
            nrthng = nrthng.replace(" ","");
            northing = Double.parseDouble(nrthng);
            String zone = location.split(",")[3];
            String zone1 = zone.split(":")[1];
            zone1 = zone1.replace(" ","");
            int len=zone1.length()-1;
            zone1 = StringUtils.overlay(zone1," ", len, len);
            String converted = latLon2UTM.UTM2Deg(zone1+" "+eastng+" "+nrthng);
            String lat = converted.split(",")[0];
            String lon = converted.split(",")[1];
            latiii = Double.parseDouble(lat);
            longii = Double.parseDouble(lon);
            pointn.setTitle(pointnm);
        }
        // Add marker, wherever you want and move the camera
        LatLng india = new LatLng(latiii, longii);
        Marker mark = mMap.addMarker(new MarkerOptions().position(india).title(pointnm));
        mark.showInfoWindow();
        points.add(india);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        /*
         * for (Marker marker : markers) {
         * builder.include(marker.getPosition()); }
         */
        for (LatLng point : points) {
            builder.include(point);
        }
        final LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(() -> {
            int padding = 20; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                    padding);
            mMap.moveCamera(cu);
            mMap.animateCamera(cu, 2000, null);
        });
    }


    void graphcall(double east1, double north1, double east2, double north2){
        try{

            xySeries.resetData(new DataPoint[] {
                    new DataPoint(east1, north1)
            });

            xy2Series.resetData(new DataPoint[] {
                    new DataPoint(east2, north2)
            });

            xy3Series.resetData(new DataPoint[] {
                    new DataPoint(east1-5.180, north1-5.130),
            });

            xy4Series.resetData(new DataPoint[] {
                    new DataPoint(east2+5.180, north2+5.130)
            });
           /* lineSeries.resetData(new DataPoint[] {
                    new DataPoint(east1, north1),
                    new DataPoint(east2, north2)
            });*/


           // xySeries.appendData(new DataPoint(east1, north1), false, 10);
            xySeries.setColor(Color.parseColor("#F44336"));
           // xy3Series.appendData(new DataPoint(east1-5.180, north1-5.130), false, 10);
            // xy3Series.appendData(new DataPoint(2492763.708, 286029.271), false, 10);
           // xy2Series.appendData(new DataPoint(east2, north2), false, 10);
           // xy3Series.appendData(new DataPoint(east2+5.180, north2+5.130), false, 10);
            // xy3Series.appendData(new DataPoint(2492771.708, 286045.276), false, 10);
            xy2Series.setColor(Color.parseColor("#FF018786"));
            xy3Series.setColor(Color.parseColor("#FFFFFFFF"));
            xy4Series.setColor(Color.parseColor("#FFFFFFFF"));
            /*lineSeries.appendData(new DataPoint(east1, north1), false, 10);
            lineSeries.appendData(new DataPoint(east2, north2), false, 10);*/
            mScatterPlot.addSeries(xySeries);
            mScatterPlot.addSeries(xy2Series);
            mScatterPlot.addSeries(xy3Series);
            mScatterPlot.addSeries(xy4Series);
          //  mScatterPlot.addSeries(lineSeries);
           /* xySeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    String msg = "x: " + dataPoint.getX() + "\n y: " + dataPoint.getY();
                    Toast.makeText(StakeMap.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
            mScatterPlot.addSeries(xySeries);

            xy2Series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    String msg = "x: " + dataPoint.getX() + "\n y: " + dataPoint.getY();
                    Toast.makeText(StakeMap.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
            mScatterPlot.addSeries(xy2Series);*/

          /*  if(xy2Series.getSize()>0){
                mScatterPlot.removeAllSeries();
            }*/


            scrolllll();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void scrolllll(){
        //set Scrollable and Scaleable
        mScatterPlot.getViewport().setScalable(true);
        mScatterPlot.getViewport().setScalableY(true);
        mScatterPlot.getViewport().setScrollable(true);
        mScatterPlot.getViewport().setScrollableY(true);

    }

    /*This method is basically draws polyline between current location and stake out point*/
    public void polylinedraw(GoogleMap googleMap){

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(new LatLng(latiii, longii), new LatLng(latitude, longitude)));
        polylines.add(polyline1);
        // [END maps_poly_activity_add_polyline]
        // [START_EXCLUDE silent]
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // [END maps_poly_activity_add_polyline_set_tag]
        // Style the polyline.
        stylePolyline(polyline1);
        if(polylines.size()>1){
            for(Polyline line : polylines)
            {
                line.remove();
            }
            polylines.clear();
        }

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

    boolean iscircledraw;


    /*CurrenLocation method
    * This method updates currenlocation with precise data which is RTK data*/

    /*CurrenLocation according to RTK data*/
    public void currentlocation() {
        try{
            distancekm.setTitle("");

            String get = latLon2UTM.convertLatLonToUTM(latitude,longitude);
            String geteasting = get.split(" ")[1];
            eastingg = Double.parseDouble(geteasting);
            String getnorthing = get.split(" ")[2];
            northingg = Double.parseDouble(getnorthing);
            DecimalFormat newFormat = new DecimalFormat("####");
            double distance = latLon2UTM.calculateDistanceBetweenPoints(easting, northing, eastingg, northingg);
            if(distance<=1){
                distancekm.setTitle(newFormat.format(distance * 100) +" "+"Cm");
                mediaPlayer = MediaPlayer.create(StakeMap.this, R.raw.zxing_beep);
                mediaPlayer.start();
                if (!iscircledraw) {
                    drawCircle(new LatLng(latiii, longii));
                    iscircledraw = !iscircledraw;
                }
                int cmdistance = Integer.parseInt(newFormat.format(distance*100));
                if(cmdistance<=1){
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(StakeMap.this, R.raw.finishetask);
                    mediaPlayer.start();
                }
            }else{
                distancekm.setTitle(newFormat.format(distance) +"m");
            }

            bearing();
            if(graphcall){
                mScatterPlot.setVisibility(View.VISIBLE);
                graphcall(easting,northing,eastingg,northingg);
            }else{
                mScatterPlot.setVisibility(View.INVISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.show(mapFragment).commit();
                if (marker != null) {
                    marker.remove();
                }
                if (latitude != 0.0 || longitude != 0.0) {
                    int height = 70;
                    int width = 70;
                    @SuppressLint("UseCompatLoadingForDrawables")
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bluearrow);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    LatLng india = new LatLng(latitude, longitude);
                    marker = mMap.addMarker(new MarkerOptions().position(india).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    marker.showInfoWindow();
                    points.add(india);
                    polylinedraw(mMap);
                    drawPolyline();

                }
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

    public void lastparse(String val){
        if(val != null){
            String lines[] = val.split("\\r?\\n");
            for (String line : lines) {
                normalparse(line);
            }
        }

    }


    /*Parsing the normal data here*/
    void normalparse(String data){
        try{
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                try {
                    String accuracytime = null;
                    String lati = data.split(",")[2];
                    String longi = data.split(",")[4];
                    String vlll = latLon2UTM.latlngcnvrsn(lati, longi);
                    String latttllll = vlll.split("_")[0];
                    latitude = new Double(latttllll);
                    String lonngggll = vlll.split("_")[1];
                    longitude = new Double(lonngggll);
                    currentlocation();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            // final boolean result = mBluetoothLeService.connect(mDeviceAddress,CRS_sattelite.this,device_id,opid);
            // Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

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
    float bearTo;
    void bearing(){
        Location userLoc=new Location("service Provider");
        //get longitudeM Latitude and altitude of current location with gps class and  set in userLoc
        userLoc.setLongitude(longitude);
        userLoc.setLatitude(latitude);

        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(latiii); //kaaba latitude setting
        destinationLoc.setLongitude(longii); //kaaba longitude setting
        bearTo=userLoc.bearingTo(destinationLoc);
        if (bearTo < 0) {
            bearTo = bearTo + 360;
            heading.setTitle("Head to"+" "+ bearTo +"°");
        }else{
            heading.setTitle("Head to"+" "+ bearTo +"°");

        }
       // rotateMarker(marker,bearTo);
       // marker.setRotation(bearTo);
    }



    private void drawPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.argb(10, 0, 0, 100));
        polylineOptions.width(3.5f);
        polylineOptions.visible(true);
        polylineOptions.geodesic(true);
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(new LatLng(84.907230, 67.767142), new LatLng(latitude, longitude), new LatLng(-84.190203, 74.244858)));
        polyline1.setPattern(PATTERN_POLYLINE_DOTTED);
        this.polylines2.add(polyline1);
        if(polylines2.size()>1){
            for(Polyline line : polylines2)
            {
                line.remove();
            }
            polylines2.clear();
        }

       /* Polyline polyline = mMap.addPolyline(polylineOptions);
        this.polylines2.add(polyline);*/
    }



    /*Draw circle within 2m*/
    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(10);

        // Border color of the circle
        circleOptions.strokeColor(Color.GREEN);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
       Circle mycircle = mMap.addCircle(circleOptions);
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
                graphcall=false;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satview:
                graphcall=false;
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.noneview:
                graphcall=false;
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                return true;

            case R.id.graphview:
                  FragmentManager fm4 = getSupportFragmentManager();
                        FragmentTransaction ft4 = fm4.beginTransaction();
                        ft4.hide(mapFragment).commit();
                graphcall=true;
                return true;
        }
        return false;
    }



    // Method of SensorEventListner
    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree =bearTo - Math.round(event.values[0]);
        // Start the animation
        if(mMap!=null && bearTo!=0){
            marker.setRotation(bearTo);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // THis method is not in use.....
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
