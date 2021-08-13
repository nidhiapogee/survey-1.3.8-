
package com.apogee.surveydemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.utility.BluetoothLeService;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.apogee.surveydemo.AutoMap.simpleSwitch;
import static com.apogee.surveydemo.Generic.taskGeneric.Name;
import static com.apogee.surveydemo.MeterActivity.TAG;
import static java.lang.Math.PI;
import static java.lang.String.valueOf;


public class StakeMapCustom extends Activity  implements View.OnTouchListener , SensorEventListener {


    ImageView imgSource1;
    EditText a1, a2, s1, s2;
    private Bitmap bmp;

    private Bitmap operation;
    // GPSTrack gpsTrack;
    private String latitude1, longitude1;
    ArrayList<Integer> xpixel = new ArrayList<>();
    ArrayList<Integer> ypixel = new ArrayList<>();
    ArrayList<String> latValue = new ArrayList<>();
    ArrayList<String> lngValue = new ArrayList<>();
    Handler timerHandler = new Handler();
    Handler timerHandler1 = new Handler();
    boolean isFromStake = false;

    double easting = 0.0;
    double northing = 0.0;
    LatLon2UTM latLon2UTM = new LatLon2UTM();
    String latiii;
    String longii;

    String payloadfinal;
    int pktno = 0;
    int totalnoofpkts = 0;
    ArrayList<String> datalist = new ArrayList<>();
    private BluetoothLeService mBluetoothLeService;

    double referenceX = 0.0;
    double referenceY = 0.0;
    boolean isFirstReference = true;
    ArrayList<Double> referValueX = new ArrayList<>();
    ArrayList<Double> referValueY = new ArrayList<>();

    Thread t;
    int[] point = {1};
    String finalpoint;
    DatabaseOperation dbTask = new DatabaseOperation(StakeMapCustom.this);
    int tskid;
    MediaPlayer mediaPlayer = new MediaPlayer();
    public String pointname;
    public int threadtime = 0;



    AppCompatImageButton addbtn;
    AppCompatImageButton minusbtn;
    AppCompatImageButton video;


    double PXmin = 0.0;
    double PYmin = 0.0;
    double PXmax = 0.0;
    double PYmax = 0.0;
    GestureDetector gestureDetector;

    double diffRefereX = 0.0;
    double diffRefereY = 0.0;
    int pointsPlus = 0;
    int pointsMinus = 0;
    ScaleGestureDetector scaleGestureDetector;

    String recordtype = "Auto Survey";
    // device sensor manager
    private SensorManager mSensorManager;
    // record the compass picture angle turned
    private float currentDegree = 0f;
    ImageView img_compass;
    String  getlocale;
    int count = 0;
    double Xs;
    double Ys;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_REQUEST = 1888;
    boolean isFirsttimeDistance = true;
    double distance;
    double estng2 =0.0;
    double northing2 = 0.0;
    boolean isForFirstPoint = true;
    float scaleBeginX;
    float scaleBeginY;
    RelativeLayout rldata;
    int pixelX;
    int pixelY;
    ArrayList<Double> referValueXdis = new ArrayList<>();
    ArrayList<Double> referValueYdis = new ArrayList<>();
    boolean isFirstReferencedis = true;

    ArrayList<Double> referValueX4 = new ArrayList<>();
    ArrayList<Double> referValueY4 = new ArrayList<>();
    boolean isFirstReference4 = true;

    ArrayList<Double> referValueX2 = new ArrayList<>();
    ArrayList<Double> referValueY2 = new ArrayList<>();
    boolean isFirstReference2 = true;

    ArrayList<Double> referValueXpoint2 = new ArrayList<>();
    ArrayList<Double> referValueYpoint2 = new ArrayList<>();
    boolean isFirstReferencepoint2 = true;
    File newfile;

    TextView tvdistance;

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        /*if (count == 0) {
                count++;
                abc(latValue.get(0), lngValue.get(0));
            } else if (count == 1) {
                count++;
                abc(latValue.get(1), lngValue.get(1));
            }else if (count == 2) {
                count++;
                abc(latValue.get(2), lngValue.get(2));
            }  else if (count == 3) {
                count++;
                abc(latValue.get(3), lngValue.get(3));
            }  else if(count == 4){
                count++;
                abc(latValue.get(4), lngValue.get(4));
            }else if(count == 5){
                count++;
                abc(latValue.get(5), lngValue.get(5));
            }  else if(count == 6){
                count++;
                abc(latValue.get(6), lngValue.get(6));
            }else if(count == 7){
                count++;
                abc(latValue.get(7), lngValue.get(7));
            } else if(count == 8){
                count++;
                abc(latValue.get(8), lngValue.get(8));
            }else if(count == 9){
                   count++;
                   abc(latValue.get(9), lngValue.get(9));
               }else if(count == 10){
                   count++;
                   abc(latValue.get(10), lngValue.get(10));
               }else if(count == 11){
                   count++;
                   abc(latValue.get(11), lngValue.get(11));
               }else if(count == 12){
                   count++;
                   abc(latValue.get(12), lngValue.get(12));
               }else if(count == 13){
                   count++;
                   abc(latValue.get(13), lngValue.get(13));
               }else if(count == 14){
                   count++;
                   abc(latValue.get(14), lngValue.get(14));
               }else if(count == 15){
                   count++;
                   abc(latValue.get(15), lngValue.get(15));
               }else if(count == 16){
                   count++;
                   abc(latValue.get(16), lngValue.get(16));
               }else if(count == 17){
                   count++;
                   abc(latValue.get(17), lngValue.get(17));
               }else if(count == 18){
                   count++;
                   abc(latValue.get(18), lngValue.get(18));
               }else if(count == 19){
                   count++;
                   abc(latValue.get(19), lngValue.get(19));
               }else if(count == 20){
                   count++;
                   abc(latValue.get(20), lngValue.get(20));
               }else if(count == 21){
                   count++;
                   abc(latValue.get(21), lngValue.get(21));
               }else if(count == 22){
                   count++;
                   abc(latValue.get(22), lngValue.get(22));
               }else if(count == 23){
                   count++;
                   abc(latValue.get(23), lngValue.get(23));
               }else if(count == 24){
                   count++;
                   abc(latValue.get(24), lngValue.get(24));
               }*/



           if(AutoMap.latitu != null && !AutoMap.latitu.isEmpty() && AutoMap.longti != null && !AutoMap.longti.isEmpty())
            abc(AutoMap.latitu, AutoMap.longti);
              if (simpleSwitch.isChecked()) {
                int tdtime = Integer.parseInt(AutoMap.vall.getText().toString());
                threadtime = tdtime * 1000;
                timerHandler.postDelayed(this, threadtime);
            }else {
                timerHandler.postDelayed(this, 5000);
            }

        }
    };

    Runnable timerRunnable1 = new Runnable() {

        @Override
        public void run() {
   /*  if (count == 0) {
                count++;
                abc(latValue.get(1), lngValue.get(1));
            } else if (count == 1) {
                count++;
                abc(latValue.get(2), lngValue.get(2));
            }  else if (count == 2) {
                count++;
                abc(latValue.get(3), lngValue.get(3));
            } else if (count == 3) {
                count++;
                abc(latValue.get(4), lngValue.get(4));
            }else if(count == 4){
                count++;
                abc(latValue.get(5), lngValue.get(5));
            }else if(count == 5){
                count++;
                abc(latValue.get(6), lngValue.get(6));
            }   else if(count == 6){
                count++;
                abc(latValue.get(7), lngValue.get(7));
            }   else if(count == 7){
                count++;
                abc(latValue.get(8), lngValue.get(8));
            }else if(count == 8){
                count++;
                abc(latValue.get(9), lngValue.get(9));
            } else if(count == 9){
                count++;
                abc(latValue.get(9), lngValue.get(9));
            }else if(count == 10){
                count++;
                abc(latValue.get(10), lngValue.get(10));
            }else if(count == 11){
                count++;
                abc(latValue.get(11), lngValue.get(11));
            }else if(count == 12){
                count++;
                abc(latValue.get(12), lngValue.get(12));
            }else if(count == 13){
                count++;
                abc(latValue.get(13), lngValue.get(13));
            }else if(count == 14){
                count++;
                abc(latValue.get(14), lngValue.get(14));
            }else if(count == 15){
             count++;
             abc(latValue.get(15), lngValue.get(15));
          }else if(count == 16){
            count++;
            abc(latValue.get(16), lngValue.get(16));
         }*/

            if(latitude1 != null && !latitude1.isEmpty() && longitude1 != null && !longitude1.isEmpty()){
                abc(latitude1, longitude1);
            }

            timerHandler1.postDelayed(timerRunnable1, 3000);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake_map_custom);
        imgSource1 = findViewById(R.id.source1);
        img_compass = findViewById(R.id.img_compass);
        a1 = findViewById(R.id.a1);

        a2 = findViewById(R.id.a2);

        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        tvdistance = findViewById(R.id.tvdistance);
        rldata = findViewById(R.id.rldata);
        rldata.setOnTouchListener(null);
        BitmapDrawable abmp = (BitmapDrawable) imgSource1.getDrawable();
        bmp = abmp.getBitmap();
        t = new Thread();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(StakeMapCustom.this);
        String taskname = sharedPreferences.getString(Name, "default value");

        dbTask.open();
        tskid = dbTask.gettaskid(taskname);



        latValue.add("732281.824");
        latValue.add("732345.619");
        latValue.add("732345.878");
        latValue.add("732346.002");
        latValue.add("732342.423");
        latValue.add("732340.848");
        latValue.add("732341.893");
        latValue.add("732342.973");
        latValue.add("732340.11");
        latValue.add("732331.657");
        latValue.add("732322.042");
        latValue.add("732314.02");
        latValue.add("732306.581");
        latValue.add("732298.091");
        latValue.add("732291.749");
        latValue.add("732285.906");
        latValue.add("732281.824");





        lngValue.add("3167390.499");
        lngValue.add("3167375.929");
        lngValue.add("3167376.013");
        lngValue.add("3167376.078");
        lngValue.add("3167382.067");
        lngValue.add("3167391.214");
        lngValue.add("3167400.552");
        lngValue.add("3167409.803");
        lngValue.add("3167417.232");
        lngValue.add("3167421.375");
        lngValue.add("3167422.013");
        lngValue.add("3167420.59");
        lngValue.add("3167416.748");
        lngValue.add("3167411.583");
        lngValue.add("3167406.066");
        lngValue.add("3167397.932");
        lngValue.add("3167390.499");



        final Intent intent = getIntent();
        if (intent != null) {
            isFromStake = intent.getBooleanExtra("FromStake", false);
            String location = intent.getStringExtra("location");

            if (location != null && !location.isEmpty()) {
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
                 latiii = converted.split(",")[0];
                longii = converted.split(",")[1];
               //abc(latValue.get(0), lngValue.get(0));
                a1.setText(eastng);
                a2.setText(nrthng);
                 pointplotStake();
                timerHandler1.postDelayed(timerRunnable1, 2000);
            }


        }




        addbtn = findViewById(R.id.add);
        minusbtn = findViewById(R.id.minus);
        video = findViewById(R.id.video);



        addbtn.setOnClickListener(v -> {
            pointsPlus = pointsPlus+2;
            pointplus();
        });

        minusbtn.setOnClickListener(v -> {
            pointsMinus = pointsMinus+2;
            pointMinus();

        });
        video.setOnClickListener(v -> {
            if (checkPermission()) {
                //main logic or main code

                // . write your main code to execute, It will execute if the permission is already given.

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, CAMERA_REQUEST);
                }

            } else {
                requestPermission();
            }
        });
        imgSource1.setOnTouchListener(this::onTouch);
       // scaleGestureDetector = new ScaleGestureDetector(this, new MyOnScaleGestureListener());
        gestureDetector = new GestureDetector(this, new OnSwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {

               pointScroll(direction);
                return true;
            }


        });

        if(!isFromStake){
            timerHandler.postDelayed(timerRunnable, 0);
        }

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


    }

    private   class MyOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        boolean isOut;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float scaleFactor = detector.getScaleFactor();
            // Don't let the object get too small or too large.
        //   mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            if (scaleFactor > 1) {
                //   scaleText.setText("Zooming Out");
                Log.d("checck=", "Zooming Out");
                isOut = true;


            } else {
                // scaleText.setText("Zooming In");
                Log.d("checck===","Zooming In");
                isOut = false;
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
          scaleBeginX = detector.getFocusX();
          scaleBeginY = detector.getFocusY();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {


             pixelX = (int) (detector.getFocusX() - scaleBeginX );
             pixelY   = (int) (detector.getFocusY() - scaleBeginY);
            Log.d("checck===", valueOf(pixelX));
            Log.d("checck====", valueOf(pixelY));
           /* if(pixelX < 0 && pixelY < 0){
                pixelY = Math.abs(pixelY);
            }else if(pixelX > 0 && pixelY > 0){
                pixelX = Math.abs(pixelX);
                pixelY = -pixelY;
            }else if(pixelX > 0 && pixelY < 0){
                pixelX = Math.abs(pixelX);
                pixelY = Math.abs(pixelY);
            }else if(pixelX < 0 && pixelY > 0){
                pixelX = - pixelX;
                pixelY = - pixelY;
            }*/
            pixelX = Math.abs(pixelX);
            pixelY = Math.abs(pixelY);
            if(isOut){
                pointZoom("Out");
            }else {
                pointZoom("in");
            }


        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

      //  tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        img_compass.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: ");

      //  scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        return true;
    }

    public void abc(String Lat, String Lon) {
        double Easting, Northing;
        String vlll = latLon2UTM.latlngcnvrsn(Lat,Lon);
        String latttllll = vlll.split("_")[0];
        double latitude = Double.parseDouble(latttllll);
       String lonngggll = vlll.split("_")[1];
        double  longitude = Double.parseDouble(lonngggll);
        String get = latLon2UTM.convertLatLonToUTM(latitude,longitude);
      //  String get = latLon2UTM.convertLatLonToUTM(Double.parseDouble(Lat),Double.parseDouble(Lon));
         getlocale = get.split(" ")[0];
        String geteasting = get.split(" ")[1];
        Easting = Double.parseDouble(geteasting);
        Easting = Double.parseDouble(new DecimalFormat("##.###").format(Easting));
        String getnorthing = get.split(" ")[2];
        Northing = Double.parseDouble(getnorthing);
        Northing = Double.parseDouble(new DecimalFormat("##.###").format(Northing));
        a1.setText(valueOf(Easting));
        a2.setText(valueOf(Northing));
        if (!isFromStake) {
            if (simpleSwitch.isChecked()) {
               timeSurveyNew(Easting, Northing);
                pointplot();
            }else {
                distance = Double.parseDouble( AutoMap.vall.getText().toString());
               distanceSurvey(Easting,Northing);
            }

        }else {
            pointplotStake();
        }

    }



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }



    private final ServiceConnection mServiceConnection = new ServiceConnection() {
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
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler1.removeCallbacks(timerRunnable1);
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
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

            if (data.contains("@@@@")) {
                try {
                    String packet_no = data.split(",")[1];
                    pktno = Integer.parseInt(packet_no);
                    String total_no_of_packets = data.split(",")[3];
                    totalnoofpkts = Integer.parseInt(total_no_of_packets);
                    datalist.add(data);
                    if (pktno == totalnoofpkts && pktno > 0) {
                        dataparse(datalist);
                        datalist.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                String[] somedata = data.split("\\r?\\n");
                int length = somedata.length;
                if (length > 1) {
                    for (String somedatum : somedata) {
                        normalparse(somedatum);
                    }
                }

            }
        }
    }

    public void dataparse(ArrayList<String> dataval) {
        String actualKey = null;
        String finalpayv = null;
        int totalnoofpkts = 0, datalenghth = 0, pktno = 0;
        for (int i = 0; i < dataval.size(); i++) {
            String val = dataval.get(i);
            if (val.contains("@@@@")) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if (payloadfinal != null) {
                payloadfinal = payloadfinal.concat(finalpayv);
            } else {
                payloadfinal = finalpayv;
            }
            if (pktno == totalnoofpkts && pktno > 0) {
                lastparse(payloadfinal);
                System.out.println(payloadfinal);
                payloadfinal = null;
            }

        }
    }

    public void lastparse(String val) {
        if (val != null) {
            String lines[] = val.split("\\r?\\n");
            for (String line : lines) {
                normalparse(line);
            }

        }

    }


    /*Parsing the normal data here*/
    void normalparse(String data) {
        try {
            //$GNGGA,065159.00,2231.67918,N,07255.16950,E,4,12,0.60,40.8,M,-56.7,M,1.0,0004*7F
            if (data.contains("$GNGGA")) {
                try {
                    String accuracytime = null;
                    String lati = data.split(",")[2];
                    String longi = data.split(",")[4];
                    String vlll = latLon2UTM.latlngcnvrsn(lati, longi);
                    String latttllll = vlll.split("_")[0];
                    latitude1 = latttllll;
                    String lonngggll = vlll.split("_")[1];
                    longitude1 = lonngggll;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (data.contains("$PUBX")) {
                try {
                    //$PUBX,00,052910.00,2231.67651,N,07255.16919,E,-15.520,G3,10,14,0.947,317.09,-0.075,,1.21,2.38,1.73,10,0,0*61
                    //$PUBX,00,052150.00,2231.67867,N,07255.16959,E,-11.305,D3,0.31,0.62,0.014,0.00,0.008,,0.62,1.06,0.82,26,0,0*40
                    String correction = data.split(",")[8];
                    String numsvrs = data.split(",")[18];
                    String pubxlatitude = data.split(",")[3];
                    String pubxlongitude = data.split(",")[5];
                    String pubxaltitude = data.split(",")[7];
                    /*double lat = Double.parseDouble(pubxlatitude) + 10000;
                    latitude1 = String.valueOf(lat);*/
                    latitude1 = pubxlatitude;
                    longitude1 = pubxlongitude;
                  /*  double log = Double.parseDouble(pubxlongitude) + 10000;
                    longitude1 = String.valueOf(log);*/


                 /*   if (isFirsttime && isFromStake) {
                        isFirsttime = false;
                          abc(latiii, longii);
                        timerHandler1.postDelayed(timerRunnable1, 600);


                    }*/


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*This method is basically for Time Auto Survey */

    public void timeSurveyNew(double eastingg, double northingg) {
        pointname = AutoMap.pname.getText().toString();
        String str1 = Integer.toString(point[0]++);
        if (pointname != null || !pointname.equals("")) {
            finalpoint = pointname + str1;
        } else {
            finalpoint = "p" + str1;
        }
        String pointcode = AutoMap.pcode.getText().toString().trim();
        //*Database insertion part*//*
        boolean result = dbTask.insertTopo(recordtype, finalpoint, pointcode, tskid, eastingg, northingg, AutoMap.finalalti,
                Double.parseDouble(AutoMap.hAcc), Double.parseDouble(AutoMap.vAcc), AutoMap.antennaheight, AutoMap.StatusData, getlocale);
        if (result) {
            mediaPlayer = MediaPlayer.create(StakeMapCustom.this, R.raw.beeppointtwo);
            mediaPlayer.start();
            Toast.makeText(StakeMapCustom.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StakeMapCustom.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
        }
    }

    public void distanceSurvey(double eastingg, double northingg){
        if(isFirsttimeDistance){
            isFirsttimeDistance = false;
            pointname = AutoMap.pname.getText().toString();
            String str1 = Integer.toString(point[0]++);
            if (pointname != null || !pointname.equals("")) {
                finalpoint = pointname + str1;
            } else {
                finalpoint = "p" + str1;
            }
            String pointcode = AutoMap.pcode.getText().toString().trim();
            //*Database insertion part*//*
            boolean result = dbTask.insertTopo(recordtype, finalpoint, pointcode, tskid, eastingg, northingg, AutoMap.finalalti,
                    Double.parseDouble(AutoMap.hAcc), Double.parseDouble(AutoMap.vAcc), AutoMap.antennaheight, AutoMap.StatusData, getlocale);
            if (result) {
                mediaPlayer = MediaPlayer.create(StakeMapCustom.this, R.raw.beeppointtwo);
                mediaPlayer.start();
                estng2=eastingg;
                northing2=northingg;
                Toast.makeText(StakeMapCustom.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StakeMapCustom.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
            }
            pointplot();
        }else {
            double distancee = latLon2UTM.calculateDistanceBetweenPoints(eastingg,northingg, estng2, northing2);
            Log.d("distancee" , String.valueOf(distancee));
            if(distancee >= distance){
                String str1 = Integer.toString(point[0]++);
                if (pointname !=null || !pointname.equalsIgnoreCase("")){
                    finalpoint = pointname + str1;
                }else{
                    finalpoint = "p"+str1;
                }

                String pointcode = AutoMap.pcode.getText().toString().trim();
                //*Database insertion part*//*
                boolean result = dbTask.insertTopo(recordtype, finalpoint, pointcode, tskid, eastingg, northingg, AutoMap.finalalti,
                        Double.parseDouble(AutoMap.hAcc), Double.parseDouble(AutoMap.vAcc), AutoMap.antennaheight, AutoMap.StatusData, getlocale);
                if (result) {
                    Toast.makeText(StakeMapCustom.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                    estng2=eastingg;
                    northing2=northingg;
                } else {
                    Toast.makeText(StakeMapCustom.this, getString(R.string.data_not_inserted), Toast.LENGTH_SHORT).show();
                }
                pointplot();
            }else {
                if(isForFirstPoint){
                    pointplot();
                }

            }
        }

    }

    //for stake plot rtk points
    private void pointplotStake() {
        String a4 = a1.getText().toString().trim();
        double value1 = Double.parseDouble(a4);
        String a5 = a2.getText().toString().trim();
        double value2 = Double.parseDouble(a5);

        if (isFirstReference) {
            isFirstReference = false;
            referenceX = value1;
            referenceY = value2;
        }

        referValueX.add(value1);
        referValueY.add(value2);
        double minX;
        double minY;

        double maxX;
        double maxY;

        double distancee = latLon2UTM.calculateDistanceBetweenPoints(referenceX,referenceY, value1, value2);
        tvdistance.setVisibility(View.VISIBLE);
        double distnce =  Double.parseDouble(new DecimalFormat("##.###").format(distancee));
        tvdistance.setText("Distance :" + distnce +"meters");
        if(distancee > 0.0 && distancee < 0.2){
            if(isFirstReferencepoint2){
                isFirstReferencepoint2 = false;
                referValueXpoint2.add(referenceX);
                referValueYpoint2.add(referenceY);
                Toast.makeText(StakeMapCustom.this,"distance between 0 to 20 centimeters", Toast.LENGTH_SHORT).show();
            }

            referValueXpoint2.add(value1);
            referValueYpoint2.add(value2);


            minX = Collections.min(referValueXpoint2);
            minY = Collections.min(referValueYpoint2);

            maxX = Collections.max(referValueXpoint2);
            maxY = Collections.max(referValueYpoint2);

            diffRefereX = Math.abs(maxX - minX);
            diffRefereY = Math.abs(maxY - minY);
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = minX - (diffRefereX / 4);
            PYmin = minY - (diffRefereY / 4);

            PXmax = maxX + (diffRefereX / 4);
            PYmax = maxY + (diffRefereY / 4);
        }else if(distancee > 0 && distancee < 2){
            if(isFirstReference2){
                isFirstReference2 = false;
                referValueX2.add(referenceX);
                referValueY2.add(referenceY);

                Toast.makeText(StakeMapCustom.this,"distance between 0 to 2 meters", Toast.LENGTH_SHORT).show();
            }

            referValueX2.add(value1);
            referValueY2.add(value2);


            minX = Collections.min(referValueX2);
            minY = Collections.min(referValueY2);

            maxX = Collections.max(referValueX2);
            maxY = Collections.max(referValueY2);

            diffRefereX = Math.abs(maxX - minX);
            diffRefereY = Math.abs(maxY - minY);
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = minX - (diffRefereX / 4);
            PYmin = minY - (diffRefereY / 4);

            PXmax = maxX + (diffRefereX / 4);
            PYmax = maxY + (diffRefereY / 4);
        } else if(distancee > 2 && distancee < 4){
            if(isFirstReference4){
                isFirstReference4 = false;
                referValueX4.add(referenceX);
                referValueY4.add(referenceY);
                Toast.makeText(StakeMapCustom.this,"distance between 2 to 4 meters", Toast.LENGTH_SHORT).show();
            }

            referValueX4.add(value1);
            referValueY4.add(value2);


            minX = Collections.min(referValueX4);
            minY = Collections.min(referValueY4);

            maxX = Collections.max(referValueX4);
            maxY = Collections.max(referValueY4);

            diffRefereX = Math.abs(maxX - minX);
            diffRefereY = Math.abs(maxY - minY);
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = minX - (diffRefereX / 4);
            PYmin = minY - (diffRefereY / 4);

            PXmax = maxX + (diffRefereX / 4);
            PYmax = maxY + (diffRefereY / 4);
        } else if(distancee > 4  &&   distancee < 6 ){

            if(isFirstReferencedis){
                isFirstReferencedis = false;
                referValueXdis.add(referenceX);
                referValueYdis.add(referenceY);
                Toast.makeText(StakeMapCustom.this,"distance between 4 to 6 centimeters", Toast.LENGTH_SHORT).show();
            }

            referValueXdis.add(value1);
            referValueYdis.add(value2);


            minX = Collections.min(referValueXdis);
            minY = Collections.min(referValueYdis);

            maxX = Collections.max(referValueXdis);
            maxY = Collections.max(referValueYdis);

            diffRefereX = Math.abs(maxX - minX);
            diffRefereY = Math.abs(maxY - minY);
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = minX - (diffRefereX / 4);
            PYmin = minY - (diffRefereY / 4);

            PXmax = maxX + (diffRefereX / 4);
            PYmax = maxY + (diffRefereY / 4);

        }else {
             minX = Collections.min(referValueX);
             minY = Collections.min(referValueY);

             maxX = Collections.max(referValueX);
             maxY = Collections.max(referValueY);


            diffRefereX = Math.abs(maxX - minX);
            diffRefereY = Math.abs(maxY - minY);
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = minX - (diffRefereX / 4);
            PYmin = minY - (diffRefereY / 4);

            PXmax = maxX + (diffRefereX / 4);
            PYmax = maxY + (diffRefereY / 4);

        }
        double factorX = 800 / Xs;
        double factorY = 800 / Ys;

        double firstplotX ;
        double firstplotY;

        double plotX;
        double plotY;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            for (int i = 1; i < referValueX.size(); i++) {
                plotX = (referValueX.get(i) - PXmin) * factorX;
                xpixel.add((int) plotX);
            }
            for (int i = 1; i < referValueY.size(); i++) {
                plotY = (referValueY.get(i) - PYmin) * factorY;
                plotY = 800 - plotY;
                ypixel.add((int) plotY);
            }

        }
        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
                for (int i = 0; i < bmp.getWidth(); i++) {
                    for (int j = 0; j < bmp.getHeight(); j++) {
                        if (i == 400  && j == 100) {
                         /*   paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);*/
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.navigation_s);
                            canvas.drawBitmap(bitmap, i, j, null);
                           /* icon.getIntrinsicWidth();
                            icon.getIntrinsicHeight();
                            icon.draw(canvas);*/
                        } else if (i == xpixel.get(k)  && j == ypixel.get(k) ) {
                            if(k == 0){
                                paint.setColor(Color.BLUE);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else if(k == xpixel.size() - 1) {
                                findtwoPointsDistance(referValueX.get(0),referValueY.get(0),referValueX.get(k), referValueY.get(k));
                                paint.setColor(Color.GREEN);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else {
                                paint.setColor(Color.RED);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }

                        }
                    }


                }
            }

        }else {
               for (int i = 0; i < bmp.getWidth(); i++) {
                    for (int j = 0; j < bmp.getHeight(); j++) {
                        if (i == 400  && j == 100) {
                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }
                    }


                }

        }
        imgSource1.setImageBitmap(operation);


    }

    //for plot rtk points
    private void pointplot() {
        String a4 = a1.getText().toString().trim();
        double value1 = Double.parseDouble(a4);
        String a5 = a2.getText().toString().trim();
        double value2 = Double.parseDouble(a5);



        if (isFirstReference) {
            isFirstReference = false;
            referenceX = value1;
            referenceY = value2;
        }

        referValueX.add(value1);
        referValueY.add(value2);
        double minX = Collections.min(referValueX);
        double minY = Collections.min(referValueY);

        double maxX = Collections.max(referValueX);
        double maxY = Collections.max(referValueY);

        diffRefereX = Math.abs(maxX - minX);
        diffRefereY = Math.abs(maxY - minY);
        double Xs;
        double Ys;

        Xs = diffRefereX * 1.5;
        Ys = diffRefereY * 1.5;

        PXmin = minX - (diffRefereX / 4);
        PYmin = minY - (diffRefereY / 4);

        PXmax = maxX + (diffRefereX / 4);
        PYmax = maxY + (diffRefereY / 4);



        double factorX = 800 / Xs;
        double factorY = 800 / Ys;

        double firstplotX ;
        double firstplotY ;

        double plotX ;
        double plotY;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            if(!isForFirstPoint){
                for (int i = 1; i < referValueX.size(); i++) {
                    plotX = (referValueX.get(i) - PXmin) * factorX;
                    xpixel.add((int) plotX);
                }
                for (int i = 1; i < referValueY.size(); i++) {
                    plotY = (referValueY.get(i) - PYmin) * factorY;
                    plotY = 800 - plotY;
                    ypixel.add((int) plotY);
                }
            }

            isForFirstPoint = false;

        }

        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int j = 0; j < bmp.getHeight(); j++) {
                    if (i == 400  && j == 400 ) {
                           paint.setColor(Color.GREEN);
                           paint.setStrokeWidth(5);
                          canvas.drawBitmap(operation, new Matrix(), null);
                          canvas.drawCircle(i, j, 8, paint);
                        } else if (i == xpixel.get(k)  && j == ypixel.get(k) ) {
                        if(k == 0){
                            paint.setColor(Color.BLUE);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }else if(k == xpixel.size() - 1) {
                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }else {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }

                        }
                    }
                }
            }


        }
        imgSource1.setImageBitmap(operation);


    }

    //for Zoom-in functionality
    public void pointplus(){
        double Xs;
        double Ys;
        diffRefereX = diffRefereX/2;
        diffRefereY = diffRefereY/2;
            Xs = diffRefereX * 1.5;
            Ys = diffRefereY * 1.5;
            PXmin = (PXmin + diffRefereX );
            PYmin = (PYmin + diffRefereY);
            PXmax = (PXmax - diffRefereX);
            PYmax = (PYmax - diffRefereY);

        double factorX = 800 / Xs;
        double factorY = 800 / Ys;

        double firstplotX;
        double firstplotY;

        double plotX;
        double plotY;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            for (int i = 1; i < referValueX.size(); i++) {
                plotX = (referValueX.get(i) - PXmin) * factorX;
                xpixel.add((int) plotX);
            }
            for (int i = 1; i < referValueY.size(); i++) {
                plotY = (referValueY.get(i) - PYmin) * factorY;
                plotY = 800 - plotY;
                ypixel.add((int) plotY);
            }

        }

        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int j = 0; j < bmp.getHeight(); j++) {
                    if (i == 400  && j == 400 ) {
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(5);
                        canvas.drawBitmap(operation, new Matrix(), null);
                        canvas.drawCircle(i, j, 8, paint);
                        } else if (i == xpixel.get(k) && j == ypixel.get(k) ) {
                        if(k == 0){
                            paint.setColor(Color.BLUE);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }else if(k == xpixel.size() - 1) {
                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }else {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }

                        }
                    }


                }
            }

        }

        imgSource1.setImageBitmap(operation);

    }

    //for zoom-out functionality
    public void pointMinus(){



        PXmin = (PXmin - diffRefereX );
        PYmin = (PYmin - diffRefereY);
        double Xs;
        double Ys;
        diffRefereX = diffRefereX*2;
        diffRefereY = diffRefereY*2;
        Xs = diffRefereX * 1.5;
        Ys = diffRefereY * 1.5 ;
        PXmax = (PXmax + diffRefereX );
        PYmax = (PYmax + diffRefereY);



        double factorX = 800 / Xs;

        double factorY = 800 / Ys;

        double firstplotX;
        double firstplotY;

        double plotX;
        double plotY;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            for (int i = 1; i < referValueX.size(); i++) {
                plotX = (referValueX.get(i) - PXmin) * factorX;
                xpixel.add((int) plotX);
            }
            for (int i = 1; i < referValueY.size(); i++) {
                plotY = (referValueY.get(i) - PYmin) * factorY;
                plotY = 800 - plotY;
                ypixel.add((int) plotY);
            }

        }

        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
                for (int i = 0; i < bmp.getWidth(); i++) {
                    for (int j = 0; j < bmp.getHeight(); j++) {
                        if (i == 400  && j == 400 ) {

                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        } else if (i == xpixel.get(k)  && j == ypixel.get(k)) {
                            if(k == 0){
                                paint.setColor(Color.BLUE);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else if(k == xpixel.size() - 1){
                                paint.setColor(Color.GREEN);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else {
                                paint.setColor(Color.RED);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }

                        }
                    }


                }
            }


        }

        imgSource1.setImageBitmap(operation);

    }


    // for scrolling left side
    public void pointScroll(OnSwipeListener.Direction direction){


        double Xs;
        double Ys;

        Xs = diffRefereX * 1.5;
        Ys = diffRefereY * 1.5;



        if(direction.equals(OnSwipeListener.Direction.left) || direction.equals(OnSwipeListener.Direction.up)){
            PXmin = (PXmin + diffRefereX/10);
            PYmin = (PYmin + diffRefereY/10);

            PXmax = (PXmax + diffRefereX /10) ;
            PYmax = (PYmax + diffRefereY /10) ;
        }else if(direction.equals(OnSwipeListener.Direction.right) || direction.equals(OnSwipeListener.Direction.down)) {
            PXmin = (PXmin - diffRefereX/10);
            PYmin = (PYmin - diffRefereY/10);

            PXmax = (PXmax - diffRefereX/10) ;
            PYmax = (PYmax - diffRefereY/10 ) ;
        }



        double factorX = 800 / Xs;
        double factorY = 800 / Ys;

        double firstplotX;
        double firstplotY;

        double plotX;
        double plotY;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            for (int i = 1; i < referValueX.size(); i++) {
                plotX = (referValueX.get(i) - PXmin) * factorX;
                xpixel.add((int) plotX);
            }
            for (int i = 1; i < referValueY.size(); i++) {
                plotY = (referValueY.get(i) - PYmin) * factorY;
                plotY = 800 - plotY;
                ypixel.add((int) plotY);
            }

        }

        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
                for (int i = 0; i < bmp.getWidth(); i++) {
                    for (int j = 0; j < bmp.getHeight(); j++) {
                        if (i == 400  && j == 400 ) {
                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        } else if (i == xpixel.get(k)  && j == ypixel.get(k) ) {
                            if(k == 0){
                                paint.setColor(Color.BLUE);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else if(k == xpixel.size() - 1){
                                paint.setColor(Color.GREEN);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }else {
                                paint.setColor(Color.RED);
                                paint.setStrokeWidth(5);
                                canvas.drawBitmap(operation, new Matrix(), null);
                                canvas.drawCircle(i, j, 8, paint);
                            }

                        }
                    }


                }
            }





        }
        imgSource1.setImageBitmap(operation);

    }


    // for scrolling left side
    public void pointZoom(String inOrOut){


        double Xs;
        double Ys;

        Xs = diffRefereX * 1.5;
        Ys = diffRefereY * 1.5;

        double factorX = Xs / 800 * pixelX ;
        double factorY = Ys / 800 * pixelY;

        if(inOrOut.equals("Out")){
            PXmin = (PXmin + diffRefereX/4 + factorX);
            PYmin = (PYmin + diffRefereY/4 + factorY);

            PXmax = (PXmax - diffRefereX/4 - factorX) ;
            PYmax = (PYmax - diffRefereY/4 - factorY) ;
        }else {
            PXmin = (PXmin + diffRefereX/4 - factorX);
            PYmin = (PYmin + diffRefereY/4 - factorY);

            PXmax = (PXmax - diffRefereX/4 + factorX) ;
            PYmax = (PYmax - diffRefereY/4 + factorY) ;
        }

        double firstplotX;
        double firstplotY;

        double plotX;
        double plotY ;

        if (referValueX.size() > 1) {
            xpixel.clear();
            ypixel.clear();
            firstplotX = (referValueX.get(0) - PXmin) * factorX;
            firstplotY = (referValueY.get(0) - PYmin) * factorY;
            firstplotY = 800 - firstplotY;
            xpixel.add((int) firstplotX);
            ypixel.add((int) firstplotY);
            for (int i = 1; i < referValueX.size(); i++) {
                plotX = (referValueX.get(i) - PXmin) * factorX;
                xpixel.add((int) plotX);
            }
            for (int i = 1; i < referValueY.size(); i++) {
                plotY = (referValueY.get(i) - PYmin) * factorY;
                plotY = 800 - plotY;
                ypixel.add((int) plotY);
            }

        }

        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(operation);
        if (xpixel.size() != 0 && ypixel.size() != 0) {
            for (int k = 0; k < xpixel.size(); k++) {
                for (int i = 0; i < bmp.getWidth(); i++) {
                    for (int j = 0; j < bmp.getHeight(); j++) {
                        if (i == 400  && j == 400 ) {
                            paint.setColor(Color.GREEN);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        } else if (i >= xpixel.get(k) && i <= xpixel.get(k) + 10 && j >= ypixel.get(k) && j <= ypixel.get(k) + 10) {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(5);
                            canvas.drawBitmap(operation, new Matrix(), null);
                            canvas.drawCircle(i, j, 8, paint);
                        }
                    }


                }
            }





        }
        imgSource1.setImageBitmap(operation);

    }


    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();

                // main logic
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        showMessageOKCancel(getString(R.string.you_need_to_allow_access_permissions), (DialogInterface.OnClickListener) (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission();
                            }
                        });
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(StakeMapCustom.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            saveVideoData(videoUri);

        }
    }

    public void saveVideoData(Uri videoUri){
        try {

            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(videoUri, "r");
            FileInputStream in = videoAsset.createInputStream();

            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath() + "/" +"Auto Survey" + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            newfile = new File(dir, "save_"+System.currentTimeMillis()+".mp4");
            long result = dbTask.updatevideopoint(newfile.getAbsolutePath(),String.valueOf(tskid),recordtype);

            if (newfile.exists()) newfile.delete();



            OutputStream out = new FileOutputStream(newfile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            Log.v("", "Copy file successful.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void findtwoPointsDistance(double x1 , double y1 , double x2 , double y2){
       final double x = 0, y;
        final double
                v = (y2 - y1) / (x2 - x1) ;
        y = v - v*x1 + y1;
        double atan = Math.atan(v);
        atan =  Double.parseDouble(new DecimalFormat("##.###").format(atan));
        Log.d("check---", String.valueOf(atan));
        double degree = (PI / 180) * atan;
        Log.d("degree---", String.valueOf(atan));
    }


}
