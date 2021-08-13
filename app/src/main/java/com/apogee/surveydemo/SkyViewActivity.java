package com.apogee.surveydemo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.apogee.surveydemo.Sattelite.Skymodel;
import com.apogee.surveydemo.barchart.BarChartActivity;
import com.apogee.surveydemo.utility.BluetoothLeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class SkyViewActivity extends AppCompatActivity {
    private final static String TAG = SkyViewActivity.class.getSimpleName();
    EditText e1,e21,e332;
    Button b1;
    Spinner s1;
    ImageView imageView;
    Canvas canvas;
    Paint paint;
    Paint paint1;
    FloatingTextButton sats,position,hzpcn,vtpcn,hdop,vdop,listing, graph;
    public  ArrayList<String> drawList = new ArrayList<>();
     boolean mConnected = false;
    int pktno=0;
    int totalnoofpkts=0;
    public ArrayList<String> datalist = new ArrayList<>();
    String payloadfinal;
    private BluetoothLeService mBluetoothLeService;

    public List<String> newCommandList = new ArrayList<>();
    public List<String> delaylist = new ArrayList<>();
    public List<String> newCommandFormatList = new ArrayList<>();
    String item;
    TextView stgps,stglonass,stsbas,stgalieleo,stbeidou;
    public ArrayList<Skymodel> mDataSet = new ArrayList<>();

    String StatusData = "";

    int SBAS=0;
    int gps=0;
    int galileo=0;
    int beidou=0;
    int glonass=0;


    /*Broadcast Reciever for getting Action Data*/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(context, getString(R.string.your_connection_request_successfully), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(context, getString(R.string.your_connection_request_fail), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_view);
        e1=findViewById(R.id.e1);
        e21=findViewById(R.id.e2);
        e332=findViewById(R.id.e3);
        b1=findViewById(R.id.b1);
        stgps = findViewById(R.id.stgps);
        stglonass = findViewById(R.id.stglonass);
        stsbas = findViewById(R.id.stsbas);
        stgalieleo = findViewById(R.id.stgalieleo);
        stbeidou = findViewById(R.id.stbeidou);

        sats = findViewById(R.id.sats);
        position = findViewById(R.id.postn);
        hzpcn = findViewById(R.id.hzpcn);
        vtpcn = findViewById(R.id.vtpcn);
        hdop = findViewById(R.id.hdop);
        vdop = findViewById(R.id.vdop);
        listing = findViewById(R.id.listing);
        graph = findViewById(R.id.graph);
        imageView = findViewById(R.id.imageView);
        s1=findViewById(R.id.s1);
        imageView.setOnTouchListener(imgSourceOnTouchListener);
        Bitmap bitmap= Bitmap.createBitmap(500,500, Bitmap.Config.ARGB_8888);
         canvas=new Canvas(bitmap);
         paint=new Paint();
         paint1=new Paint();
         paint.setColor(Color.BLACK);
         paint.setStrokeWidth(5);
         paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(250,200,180,paint);

        canvas.drawCircle(250,200,120,paint);
         canvas.drawCircle(250,200,60,paint);
         canvas.drawLine(250,20,250,380,paint);
         canvas.drawLine(377,72,123,327,paint);
         canvas.drawLine(123,73,377,327,paint);
         canvas.drawLine(70,200,430,200,paint);
         imageView.setImageBitmap(bitmap);
        b1.setOnClickListener(v -> {
          //plot();

        });

        graph.setOnClickListener(v -> {
            Intent intent = new Intent(SkyViewActivity.this, BarChartActivity.class);
            intent.putStringArrayListExtra("drawList", (ArrayList<String>) drawList);
            startActivity(intent);
        });

        listing.setOnClickListener(v -> {
            Intent intent = new Intent(SkyViewActivity.this, CRSList.class);
            intent.putStringArrayListExtra("drawList", (ArrayList<String>) drawList);
            startActivity(intent);
        });

        try{
            handlerrequest();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.seems_you_are_not_connected), Toast.LENGTH_SHORT).show();
        }

    }

    /*Binding service*/
    protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    void handlerrequest(){
        delaylist.add("100");
        newCommandList.add("B562068A090000010000C50091200111FE");
        newCommandFormatList.add("hex");

        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> mBluetoothLeService.send( item, SkyViewActivity.this, false, false, newCommandList, delaylist,newCommandFormatList), 2000);
    }
    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener imgSourceOnTouchListener
            = (view, event) -> {

                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[] {eventX, eventY};

                Matrix invertMatrix = new Matrix();
                ((ImageView)view).getImageMatrix().invert(invertMatrix);

                invertMatrix.mapPoints(eventXY);
                int x = Integer.valueOf((int)eventXY[0]);
                int y = Integer.valueOf((int)eventXY[1]);
                int z= Math.abs((int)eventX-400);
                int z1= Math.abs((int)eventY-334);
    //            int x1=732382;
    //            int y1=3169094;
    //            double scale1=0.253258845;
    //            double scale2=0.221518987;
                Log.e("touch position x", String.valueOf(x));
                Log.e("touch position y", String.valueOf(y));

                return true;
            };
    public void plot(String azimath, String elevation, String sattelite, String id)
    {

        int e2= Integer.parseInt(azimath);
        int e22= Integer.parseInt(elevation);

        double xtry,ytry;
        int e3= Math.abs(270+e2);
        xtry=250+180* Math.cos(Math.toRadians(e3));
        ytry=(200+180* Math.sin(Math.toRadians(e3)));
        Log.e("x and y is", String.valueOf(xtry)+","+ String.valueOf(ytry));
        double xsc= Math.sin(Math.toRadians(e2))*200;
        double ysc=(200*200)-(xsc*xsc);
        Log.e("x try is", String.valueOf(xtry));
        Log.e("y try is", String.valueOf(ytry));
        double e24=2;
        double r23=e22*e24;
        Log.e("r23", String.valueOf(r23));
        Log.e("e24", String.valueOf(e24));
        double r24=0;
        double r25=0;
        double splox=0;
        double sploy=0;
        if(e2>270&& e2<=360)
        {
            r24= Math.abs(r23* Math.sin(Math.toRadians(270+e2)));
            r25= Math.sqrt((r23*r23)-(r24*r24));
            splox=r25+xtry;
            sploy=r24+ytry;

        }
        else   if(e2>180&& e2<=270)
        {
            r24= Math.abs(r23* Math.sin(Math.toRadians(270-e2)));
            r25= Math.sqrt((r23*r23)-(r24*r24));
            splox=r25+xtry;
            sploy= Math.abs(r24-ytry);
            Log.e("splo", String.valueOf(sploy));

        }
        else      if(e2>90&& e2<=180)
        {

            r24=r23* Math.sin(Math.toRadians(90+e2));
            r25= Math.sqrt((r23*r23)-(r24*r24));
            splox= Math.abs(r25-xtry);
            sploy= Math.abs(r24+ytry);
        }
        else    if(e2>0&& e2<=90)
        {
            r24=r23* Math.sin(Math.toRadians(90-e2));
            r25= Math.sqrt((r23*r23)-(r24*r24));
            splox= Math.abs(r25-xtry);
            sploy= Math.abs(r24+ytry);

        }
        Log.e("r25 and r24 is", String.valueOf(r24)+","+ String.valueOf(r25));
        int satno = Integer.parseInt(id);
        Rect bounds = new Rect();
        switch ((sattelite)){
            case "GPGSV":
                if (satno >= 1 && satno < 33) {//GPS
                    paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.colorgreen));
                    paint1.setColor(Color.BLACK);
                    paint1.setTextSize(18f);
                    paint1.setAntiAlias(true);
                    paint1.setTextAlign(Paint.Align.CENTER);
                    paint.setStyle(Paint.Style.FILL);
                    paint1.getTextBounds(id, 0, id.length(), bounds);
                } else if (satno >= 120 && satno < 159) {//SBAS
                    paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.color_blued));
                    paint1.setColor(Color.BLACK);
                    paint1.setTextSize(18f);
                    paint1.setAntiAlias(true);
                    paint1.setTextAlign(Paint.Align.CENTER);
                    paint.setStyle(Paint.Style.FILL);
                    paint1.getTextBounds(id, 0, id.length(), bounds);

                } else if (satno >= 193 && satno < 197) {//QZSS
                    paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.colorPrimary1));
                    paint1.setColor(Color.BLACK);
                    paint1.setTextSize(18f);
                    paint1.setAntiAlias(true);
                    paint1.setTextAlign(Paint.Align.CENTER);
                    paint.setStyle(Paint.Style.FILL);
                    paint1.getTextBounds(id, 0, id.length(), bounds);
                }

                break;

            case  "GLGSV":
                paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.coloryellow));
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(18f);
                paint1.setAntiAlias(true);
                paint1.setTextAlign(Paint.Align.CENTER);
                paint.setStyle(Paint.Style.FILL);
                paint1.getTextBounds(id, 0, id.length(), bounds);
                break;

            case "GAGSV":
                paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.colorlightblue));
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(18f);
                paint1.setAntiAlias(true);
                paint1.setTextAlign(Paint.Align.CENTER);
                paint.setStyle(Paint.Style.FILL);
                paint1.getTextBounds(id, 0, id.length(), bounds);
                break;

            case "GBGSV":
                paint.setColor(ContextCompat.getColor(SkyViewActivity.this, R.color.coloret));
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(18f);
                paint1.setAntiAlias(true);
                paint1.setTextAlign(Paint.Align.CENTER);
                paint.setStyle(Paint.Style.FILL);
                paint1.getTextBounds(id, 0, id.length(), bounds);
                break;
        }

        Log.e("splox and sploy is", String.valueOf(splox)+","+ String.valueOf(sploy));
        canvas.drawCircle((int)splox,(int)sploy,12,paint);
        canvas.drawText(id, (int)splox,(int)sploy+4, paint1);


    }


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
                      /*  if(mRadarCustom!=null){
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
         //   canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawview(aList);
        /*    if(isFirstTime){
                isFirstTime = false;

            }*/



        }

    }

    public void drawview(ArrayList<String> view) {
        mDataSet.clear();
        gps=0;
        SBAS=0;
        galileo=0;
        beidou=0;
        glonass=0;
  //      LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //    View mCenterView = inflater.inflate(R.layout.layout_center, null);
      //  TextView textView = mCenterView.findViewById(R.id.mTVText);

        for(int i=0; i<view.size() ;i++) {
            String data = view.get(i);
            if (data.contains("$GPGSV")) {//GPS, SBAS, QZSS
                try {
                    String totalsat = data.split(",")[2];
                    String noofsat = data.split(",")[4];
                    int satno = Integer.parseInt(noofsat);
                    String selvtn = data.split(",")[5];
                    int elvtn = Integer.parseInt(selvtn);
                    String sazmth = data.split(",")[6];
                    int azmth = Integer.parseInt(sazmth);
                    plot(sazmth,selvtn,"GPGSV",noofsat);

                    if (satno >= 1 && satno < 33) {//GPS
                        gps++;

                    } else if (satno >= 120 && satno < 159) {//SBAS
                        SBAS++;
                    }
                }catch (Exception e){
                    gps--; SBAS--;

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
                    plot(sazmth,selvtn,"GLGSV",noofsat);
                    glonass++;
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
                    plot(sazmth,selvtn,"GAGSV",noofsat);
                    galileo++;
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
                    plot(sazmth,selvtn,"GBGSV",noofsat);
                    beidou++;
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
        }
        stgps.setText(getString(R.string.gps)+" "+ gps);
        stglonass.setText(getString(R.string.glonass)+" "+ glonass);
        stsbas.setText(getString(R.string.sbas)+" "+ SBAS);
        stgalieleo.setText(getString(R.string.galileo)+" "+ galileo);
        stbeidou.setText(getString(R.string.beidou)+" "+ beidou);
        //mRadarCustom.setupData(90, mDataSet, azimElev, mCenterView);
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
    protected void onDestroy() {
        super.onDestroy();
      /*  if(mBluetoothLeService!=null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }*/
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

        mBluetoothLeService.send( item,SkyViewActivity.this, false, false, newCommandList, delaylist,newCommandFormatList);
        unregisterReceiver(mGattUpdateReceiver);
        super.onBackPressed();
    }



}