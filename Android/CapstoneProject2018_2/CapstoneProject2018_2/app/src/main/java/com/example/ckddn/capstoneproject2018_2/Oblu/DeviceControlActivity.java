/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/*
* * Copyright (C) 2015 GT Silicon Pvt Ltd
 *
 * Licensed under the Creative Commons Attribution 4.0
 * International Public License (the "CCBY4.0 License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/legalcode
 *
 * Note that the CCBY4.0 license is applicable only for the modifications
made
 * by GT Silicon Pvt Ltd
 *
 * Modifications made by GT Silicon Pvt Ltd are within the following
comments:
 * // BEGIN - Added by GT Silicon - BEGIN //
 * {Code included or modified by GT Silicon}
 * // END - Added by GT Silicon - END //
*
* */


package com.example.ckddn.capstoneproject2018_2.Oblu;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ckddn.capstoneproject2018_2.PathItem;
import com.example.ckddn.capstoneproject2018_2.R;
import com.example.ckddn.capstoneproject2018_2.ServerInfo;
import com.skt.Tmap.MapUtils;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final String send = "0x34 0x00 0x34";
    private static final String sys_off = "0x32 0x00 0x32";
    private static final String pro_off = "0x22 0x00 0x22";
    private TextView mdis;
    private TextView dr_lat;
    private TextView mstepcount;
    private TextView dr_long;
    private TextView z;
    private Button mStartStopBtn;
    long timeSec3 = 0;
    private int counter = 0;
    private long StepD = 0;
    private Calendar t_origin;
    String finaltime;
    long timeSec;
    private int timer;
    double Avgspeed;
    double speednow = 0;
    private long timeprint;

    int bytes, i, j, step_counter, package_number, package_number_1, package_number_2, package_number_old = 0;
    int[] header = {0, 0, 0, 0};
    byte[] ack = new byte[5];

    double sin_phi, cos_phi;
    float[] payload = new float[14];
    double[] final_data = new double[3];

    double[] dx = new double[4];

    double[] x_sw = new double[4];

    byte[] temp = new byte[4];
    Vibrator vib;
    double[] delta = {0.0, 0.0, 0.0};
    double distance = 0.0;
    double distance1 = 0.0;
    private String TXDATA;
    long timeSec1 = 0;
    long timeSec2 = 0;
    double avg = 0;
    //variables for processing
    long timeSec6 = 0;
    DecimalFormat df1 = new DecimalFormat("0.00");
    Calendar c, filenameDate;
    SimpleDateFormat sdf;
    byte[] received_data;
    private String mDeviceName;
    private String mDeviceAddress;
    // private String mDeviceName_1;
    //private String mDeviceAddress_1;
    private BluetoothLeService mBluetoothLeService;
    private static BluetoothGattService mService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic mReadCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    /* User Activity */
    /* Extras and layout */
    private String uno;
    private String userId;
    private TMapPoint curPoint, destPoint;    //  curPoint in LocationListener, destPoint in SendLocTask.onPostExecute()
    private TextView textView;

    /* for FindPath */
    private LinearLayout linearLayoutTmap;
    private TMapView tMapView = null;
    private TextView pathtext;
    private StringBuilder pathPoint = new StringBuilder();
    private ArrayList<PathItem> pathlist = null;
    private TMapPolyLine polyLine;
    private int pathlistIdx = 0;

    /* Arduino */
    private BluetoothSPP bt;

    /*Dead Reckoning Variables*/
    double headingVectors[];
    double movementVectors[];
    double scalars[];
    private double dr_coordinates[];

    /*MapPoint Variables*/

    private TMapPoint finalPoint;
    private TMapPoint drPoint;
    private TMapPoint startPoint;



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
    BluetoothLeService mtu = new BluetoothLeService();

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        // Recevie
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 수신");
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "onReceive: ACTION_GATT_CONNECTED");
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "onReceive: ACTION_GATT_DISCONNECTED");
                mConnected = false;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "onReceive: ACTION_GATT_SERVICES_DISCOVERED");
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mtu.exchangeGattMtu(512);
                }
                // BEGIN - Added by GT Silicon - BEGIN //
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "onReceive: ACTION_DATA_AVAILABLE");
                //  displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                received_data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_TX_VALUE);    //   Oblu데이터 받기
                if (received_data != null && received_data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(received_data.length);
                    for (byte byteChar : received_data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    TXDATA = String.valueOf(stringBuilder.toString().trim());
                    System.out.print("swdr " + TXDATA);
                    // displayData(TXDATA);
                }
                byte[] buffer = received_data;
                //STEP WISE DATA HERE Receive in Buffer
                Log.e(TAG, "UART-data- " + TXDATA);
                int i = 0;
                int j;
                // writetofile( bytestring,byte2HexStr(buffer,64)+"\n" );
                Log.e(TAG, "b2h-data- " + byte2HexStr(buffer, buffer.length));
                for (j = 0; j < 4; j++) {
                    header[j] = buffer[i++] & 0xFF;          //HEADER ASSIGNED
                    Log.e(TAG, "h- " + header[j]);
                }
                for (j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++)
                        temp[k] = buffer[i++];
                    payload[j] = ByteBuffer.wrap(temp).getFloat();                //PAYLOAD ASSIGNED //
                }
                //Log.i(TAG, ""+ payload[0]+ "  "+ payload[2]);

                // ++i;++i;                                                // FOR SKIPPING CHECKSUM
                package_number_1 = header[1];
                package_number_2 = header[2];
                ack = createAck(ack, package_number_1, package_number_2);
                writeack(ack);
                package_number = package_number_1 * 256 + package_number_2;        //PACKAGE NUMBER ASSIGNED
                if (package_number_old != package_number) {
                    for (j = 0; j < 4; j++)
                        dx[j] = (double) payload[j];

                    stepwise_dr_tu();   //  default 오블루
                    run_pdr(); //custom pdr


                    // Log.e(TAG, "final data sent" + final_data[0] + " " + final_data[1] + " "+final_data[2]);
                    c = Calendar.getInstance();
                    sdf = new SimpleDateFormat("HHmmss");
                    // long timeSec= (c.getTimeInMillis()-filenameDate.getTimeInMillis());
                    if (timeSec != timeSec1) {
                        timeSec1 = timeSec;
                    }
                    if (distance1 >= 0.05) {
                        timeSec3 = timeSec1 - timeSec2;
                        timeSec6 = timeSec6 + timeSec3;
                        timeSec2 = timeSec1;
                        //  long timeSec5= (c.getTimeInMillis()-filenameDate.getTimeInMillis());
                        step_counter++;
                        DecimalFormat df1 = new DecimalFormat("0.00");
                        DecimalFormat df2 = new DecimalFormat("000");
                        avg = distance / step_counter;
                        speednow = (distance1 * 3.6) / (timeSec3 / 1000);
                        Avgspeed = distance * 3.6 / (timeSec6 / 1000);
                        StepD = timeSec6 / step_counter; //stepDuration

                        StepData stepData = new StepData(final_data[0], final_data[1], final_data[2], distance, step_counter);
                        stepData.setHeading(x_sw[3]);

                    }
                    package_number_old = package_number;
                }
                mstepcount.setText(" " + step_counter);
//                mdis.setText(" " + df1.format(distance));
//                x.setText(" " + df1.format(final_data[0]));///////////x
//                y.setText(" " + df1.format(final_data[1]));///////////y
                z.setText(" " + df1.format(final_data[2]));/////////Z
                //여기에 뷰 넣기

                dr_lat.setText("" +  dr_coordinates[0]);
                dr_long.setText("" + dr_coordinates[1]);


            }
            // END - Added by GT Silicon - END //
        }
    };


    /*  location attributes */
    private TextView longiText;
    private TextView latiText;
    private TextView altiText;
    private double longitude;
    private double latitude;
    private double altitude;

    /* Compass */
    private Compass compass;
    private float currentAzimuth;
    private TextView azimuthView;

    /* TMapGPSManager */
    TMapGpsManager gps;

    // BEGIN - Added by GT Silicon - BEGIN //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_chat);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mstepcount = (TextView) findViewById(R.id.stepcount);
        mdis = (TextView) findViewById(R.id.dis);
        // mDataField = (EditText) findViewById(R.id.connection_state);
        timerValue = (TextView) findViewById(R.id.timer);
        dr_lat = (TextView) findViewById(R.id.dr_lat);
        dr_long = (TextView)findViewById(R.id.dr_long);
        z = (TextView) findViewById(R.id.Z);
        mStartStopBtn = (Button) findViewById(R.id.start_stop_btn);

        /* initialize location attributes*/
        longiText = (TextView) findViewById(R.id.longitude_text);
        latiText = (TextView) findViewById(R.id.latitude_text);
        altiText = (TextView) findViewById(R.id.altitude_text);

        /* UserActivitiy init */
        /* get user info from login page */
        uno = intent.getStringExtra("no");
        userId = intent.getStringExtra("id");

        /* initialize default layouts */
        textView = (TextView) findViewById(R.id.user_location_result);
        textView.setText(userId + ": 위치정보 미수신중"); //DEFAULT
        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getApplicationContext());
        tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");
        pathtext = (TextView) findViewById(R.id.path_text); //  경로에 대한 포인트의 정보 출력 뷰
        pathtext.setMovementMethod(new ScrollingMovementMethod());
        polyLine = new TMapPolyLine();
        polyLine.setLineWidth(2);
        polyLine.setLineColor(Color.BLUE);

        /* tmapgpsmanager */
        gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(TMapGpsManager.GPS_PROVIDER);
        gps.OpenGps();

        /* initialize compass */
        azimuthView = (TextView) findViewById(R.id.azimuth_text);
        setupCompass();

        /* initialize bluetoothSPP for Arduino */
        bt = new BluetoothSPP(getApplicationContext());
        setupBluetoothWithArduino(bt);
        /* Location Manager */
//        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, ServerInfo.user_permissions, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
//        try {
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
//        } catch (Exception e) { e.printStackTrace();}

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String buttonText = btn.getText().toString();
                String startText = getResources().getString(
                        R.string.UART_START);
                String stopText = getResources().getString(
                        R.string.UART_STOP);

                if (buttonText.equalsIgnoreCase(startText)) {   //  start중일 때 누르면
                    btn.setText(stopText);
                    byte[] convertedBytes = convertingTobyteArray(send);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, convertedBytes);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    if (mNotifyCharacteristic != null) {
                        prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }

                } else {    //  stop 일때 누르면
                    btn.setText(startText);
                    byte[] pro = convertingTobyteArray(pro_off);
                    byte[] sys = convertingTobyteArray(sys_off);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, pro);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, sys);
                    stopBroadcastDataNotify(mReadCharacteristic);
                    timeSwapBuff = 0L;
                    customHandler.removeCallbacks(updateTimerThread);
                }

            }
        });

//        getActionBar().setTitle(mDeviceName);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private void setupBluetoothWithArduino(BluetoothSPP bt) {
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "블루투스 사용 불가", Toast.LENGTH_LONG).show();
            finish();
        }
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), name +"에 연결" /*+ " 주소: " + address*/ , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(),"연결이 끊어졌습니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent bIntent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(bIntent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentAzimuth = azimuth;
                        azimuthView.setText(Float.toString(azimuth));
                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        compass.start();
        if (bt != null) {
            if (!bt.isBluetoothEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {
                if (!bt.isServiceAvailable()) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_OTHER);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "UART RESUME");
        gps.OpenGps();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        compass.start();
    }

    /*
        @Override
        protected void onPause() {
            super.onPause();
            Log.e(TAG, "UART PAUSE");
            unregisterReceiver(mGattUpdateReceiver);
        }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "UART DESTROY");
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        lm.removeUpdates(mLocationListener);
        gps.CloseGps();
        unbindService(mServiceConnection);
        stopBroadcastDataNotify(mReadCharacteristic);
        try {
            if (mGattUpdateReceiver != null)
                unregisterReceiver(mGattUpdateReceiver);
        } catch (Exception e) {

        }
        mBluetoothLeService = null;
        compass.stop();
        /* HC-06 */
        bt.stopService();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Called... Type: " + data.getType());
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) { //  Result for HC-06 I-Stick
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // END - Added by GT Silicon - END //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // BEGIN - Added by GT Silicon - BEGIN //

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(    //  onCreate
                                        BluetoothGattCharacteristic gattCharacteristic) {
        Log.d(TAG, "prepareBroadcastDataNotify: UUID " + gattCharacteristic.getUuid());
        Log.d(TAG, "prepareBroadcastDataNotify: Properties " + gattCharacteristic.getProperties());

        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
        }
    }

    void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {

        Log.d(TAG, "stopBroadcastDataNotify: UUID " + gattCharacteristic.getUuid());
        Log.d(TAG, "stopBroadcastDataNotify: Properties " + gattCharacteristic.getProperties());
        final int charaProp = gattCharacteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (gattCharacteristic != null) {
                Log.d(TAG, "Stopped notification");
                BluetoothLeService.setCharacteristicNotification(
                        gattCharacteristic, false);
                mNotifyCharacteristic = null;
            }

        }

    }

    // END - Added by GT Silicon - END //


    //  GATT DATA Receive here
    //  initiate GATT Characterists that UUID equals to OBLU
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // BEGIN - Added by GT Silicon - BEGIN //
            if (uuid.equals(SampleGattAttributes.SERVER_UART)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
                    mReadCharacteristic = gattCharacteristic;
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.SERVER_UART_tx)) {
                        Log.e(TAG, "gatt- " + gattCharacteristic);
                        mNotifyCharacteristic = gattCharacteristic;
                        prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }
                }
            }

            // END - Added by GT Silicon - END //

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
// BEGIN - Added by GT Silicon - BEGIN //

    /**
     * Method to convert hex to byteArray
     */
    private byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) convertstringtobyte(trimmedByte);
            }

        }
        return valueByte;

    }

    /**
     * Convert the string to byte
     *
     * @param string
     * @return
     */

    private int convertstringtobyte(String string) {
        return Integer.parseInt(string, 16);
    }

    public byte[] createAck(byte[] ack, int package_number_1, int package_number_2) {
        ack[0] = 0x01;
        ack[1] = (byte) package_number_1;
        ack[2] = (byte) package_number_2;
        ack[3] = (byte) ((1 + package_number_1 + package_number_2 - (1 + package_number_1 + package_number_2) % 256) / 256);
        ack[4] = (byte) ((1 + package_number_1 + package_number_2) % 256);
        return ack;
    }

    /* x, y, z 계산 */
    public void stepwise_dr_tu() {
        Log.d(TAG, "stepwise_dr_tu: 걸음 계산 시작");
        sin_phi = (float) Math.sin(x_sw[3]);
        cos_phi = (float) Math.cos(x_sw[3]);
        //Log.i(TAG, "Sin_phi and cos_phi created");
        delta[0] = cos_phi * dx[0] - sin_phi * dx[1];
        delta[1] = sin_phi * dx[0] + cos_phi * dx[1];
        delta[2] = dx[2];
        x_sw[0] += delta[0];
        x_sw[1] += delta[1];
        x_sw[2] += delta[2];
        x_sw[3] += dx[3];
        final_data[0] = x_sw[0];
        final_data[1] = x_sw[1];
        final_data[2] = x_sw[2];
        distance1 = Math.sqrt((delta[0] * delta[0] + delta[1] * delta[1] + delta[2] * delta[2]));
        distance += Math.sqrt((delta[0] * delta[0] + delta[1] * delta[1]));

        /* longitude, latitude */
    }

    public void run_pdr(){ //oblu 센서값 ->
        headingVectors = new double[2];
        movementVectors = new double[2];
        scalars = new double [2];
        double[] delta_coor = new double[2];
        dr_coordinates = new double[2];

        for(int i = 0; i<2; i++){
            scalars[i] = dx[i];
        }

        //1. 현재 방향을 알아온다
        headingVectors = MapCalculator.getHeadingVectors(currentAzimuth); //sensor로 받은 raw Azimuth
        //headingVectors = MapCalculator.getHeadingVectors(f_Azimuth); //필터된 Azimuth 기준으로 방향 확인

        //2. 나의 움직임의 스칼라*방향
        movementVectors = MapCalculator.getMovementVectors(headingVectors, scalars);

        //3. 움직임벡터 -> 위도/경도 좌표로 변환 (델타 좌표)
        // delta_Coordinates = MapCalculator.TranslateCoordinates();
        delta_coor = MapCalculator.TranslateCoordinates(movementVectors, finalPoint.getLatitude());

        //4. 나중위치 = 델타 좌표 + 현재위치
        dr_coordinates = MapCalculator.CalculateMovement(delta_coor, finalPoint.getLatitude(), finalPoint.getLongitude());


    }

    ///WRITE ACK to uc
    void writeack(byte[] byteArray) {
        Log.e(TAG, "ackdata " + byte2HexStr(byteArray, 4));
        BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, byteArray);
    }

    //STOP watch Running
    long timeInMilliseconds = 0L;
    private long startTime = 0L;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;
    private Handler customHandler = new Handler();
    private TextView timerValue;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hr = mins / 60;
            secs = secs % 60;
            // int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText(" " + hr + ":" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }
    };

    public static String byte2HexStr(byte[] paramArrayOfByte, int paramInt) {
        StringBuilder localStringBuilder1 = new StringBuilder("");
        int i = 0;
        for (; ; ) {
            if (i >= paramInt) {
                String str1 = localStringBuilder1.toString().trim();
                Locale localLocale = Locale.US;
                return str1.toUpperCase(localLocale);
            }
            String str2 = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str2.length() == 1) {
                str2 = "0" + str2;
            }
            StringBuilder localStringBuilder2 = localStringBuilder1.append(str2);
            StringBuilder localStringBuilder3 = localStringBuilder1.append(" ");
            i += 1;
        }
    }



    /* FCM message */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("FCM_MESSAGE_RECEIVED", "onNewIntent: ");
        if (intent != null) {
            String longitude = intent.getStringExtra("longitude");
            String latitude = intent.getStringExtra("latitude");
            if (longitude.isEmpty() || latitude.isEmpty()) {
                Toast.makeText(getApplicationContext(), "목적지 설정 오류...", Toast.LENGTH_LONG).show();
                return;
            }
            destPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            new FindPathData().execute();
        }
    }

    @Override
    public void onLocationChange(Location location) {


        if (gps.getSatellite() < 6) { // GPS 위치가 신뢰할 수 없을 때]
            if(finalPoint == null){
                return;
            }
            drPoint = new TMapPoint(dr_coordinates[0],dr_coordinates[1]);
            finalPoint = drPoint;


        } else if (gps.getSatellite() >= 6) { //GPS 위치 신뢰 할 수 있을 때
            curPoint = new TMapPoint(location.getLatitude(), location.getLongitude());
            finalPoint = curPoint;
        }


        /* check num of satellites */
        Toast.makeText(getApplicationContext(), "num of satellites: " + gps.getSatellite(), Toast.LENGTH_LONG).show();

//        curPoint = new TMapPoint(location.getLatitude(), location.getLongitude());
        Log.d("test", "onLocationChanged, location:" + location);
        double clongitude = finalPoint.getLongitude(); //  경도
        double clatitude = finalPoint.getLatitude();   //  위도

        /* save user cur location */
        new SendLocTask().execute("http://" + ServerInfo.ipAddress + "/user", clongitude + "", clatitude + "");

        /* signal making algorithm...  */
        if (pathlist != null) {
            if (pathlistIdx < pathlist.size()) {
                double distance = MapUtils.getDistance(pathlist.get(pathlistIdx).getPoint(), finalPoint);

                if (distance < 7) { //  7m 이내면
                    Toast.makeText(getApplicationContext(), pathlist.get(pathlistIdx).getTurnType() + "", Toast.LENGTH_LONG).show();
                    /*  send turnType to Arduino    */
                    String sendMessage = pathlist.get(pathlistIdx).getTurnType() + "";//보낼 택스트
                    if (sendMessage.length() > 0) {
                        if (bt != null)
                            bt.send(sendMessage, true); //  send to IStick
//                            Toast.makeText(getApplicationContext(), "IStick에 " + sendMessage + "전송", Toast.LENGTH_LONG).show();
                    }
                    pathlistIdx++;
                }
            } else {
                Toast.makeText(getApplicationContext(), "목적지로 도착하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
        textView.setText("위도 : " + finalPoint.getLatitude() + "\n경도 : " + finalPoint.getLongitude());
    }

    /* LocationListener에서 위치가 바뀔때 마다 사용자의 현재위치에 대한 정보를 서버에 전송하는 작업 수행 */
    public class SendLocTask extends AsyncTask<String, String, String> {
        String TAG = "SendLocTask>>>";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start!");
            try {   //  json accumulate
                JSONObject locationInfo = new JSONObject();
                locationInfo.accumulate("uno", uno);
                locationInfo.accumulate("longitude", strings[1]);
                locationInfo.accumulate("latitude", strings[2]);

                Log.d(TAG, "doInBackground: create json");
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {   //  for HttpURLConnection
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");  //  POST방식
                    conn.setRequestProperty("Cache-Control", "no-cache");        // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text");       // text형식 수신
                    conn.setRequestProperty("Accept", "application/json");       // json형식 수신
                    conn.setDoOutput(true); //  OutputStream으로 POST데이터 전송
                    conn.setDoInput(true);  //  InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    //  버퍼생성
                    writer.write(locationInfo.toString());
                    writer.flush();
                    writer.close();
                    //  send Sign In Info to Server...
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        //  readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    //  이상한 URL일 때
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("ok")) {
                try {   //  새로운 목적지 정보가 도착한다면...
                    JSONObject jsonObject = new JSONObject(result);
                    destPoint = new TMapPoint(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")); //  도착 포인트
                    Toast.makeText(getApplicationContext(), destPoint.toString(), Toast.LENGTH_SHORT).show();
                    /*  경로 탐색 시작 */
//                    new FindPathData().execute(); //  FCM에게 위임한다.
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*  curPoint와 destPoint로 경로 탐색 후, turnType과 위치 array에 저장과 맵에 경로 표시해주는 작업 수행*/
    public class FindPathData extends AsyncTask<String, Void, Document> {
        String TAG = "FindPathData>>>";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pathlist = new ArrayList<PathItem>();

            /*  새로운 PloyLine을 위해 기존의 view제거*/
            linearLayoutTmap.removeView(tMapView);

            tMapView = new TMapView(getApplicationContext());
            tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");
            tMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());

            /*  기존의 polyLine제거*/
            tMapView.removeAllTMapPolyLine();
            polyLine = new TMapPolyLine();
            polyLine.setLineWidth(2);
            polyLine.setLineColor(Color.BLUE);
        }

        @Override
        protected Document doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start!");
            Document document = null;
            try {
                document = new TMapData().findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, curPoint, destPoint);  //  send find query to TMapServer..
                return document;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "경로탐색에 실패 하였습니다.", Toast.LENGTH_LONG).show();
            return null;    //  if failed...
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null) {
                /*  Parse by turntype and point information */
                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xPath = xPathFactory.newXPath();
                try {
                    XPathExpression expr = xPath.compile("//Placemark");
                    NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NodeList child = nodeList.item(i).getChildNodes();
                        int turnType = -1;
                        TMapPoint point;

                        for (int j = 0; j < child.getLength(); j++) {
                            Node node = child.item(j);
                            if (node.getNodeName().equals("tmap:turnType")) {
                                turnType = Integer.parseInt(node.getTextContent());
                            }
                            if (node.getNodeName().equals("Point")) {
                                String[] str = node.getTextContent().split(",");
                                point = new TMapPoint(Double.parseDouble(str[1]), Double.parseDouble(str[0]));
                                PathItem pathItem = new PathItem(turnType, point);
                                pathlist.add(pathItem);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pathPoint = new StringBuilder();
                for (int i = 0; i < pathlist.size(); i++) {
                    pathPoint.append(pathlist.get(i).toString() + "\n");
                }
                pathtext.setText(pathPoint.toString());

                /*  Parse and draw PloyLine on TMapView*/
                NodeList list = doc.getElementsByTagName("LineString");
                for (int i = 0; i < list.getLength(); i++) {

                    Element item = (Element) list.item(i);
                    String str = HttpConnect.getContentFromNode(item, "coordinates");
                    if (str != null) {
                        String[] str2 = str.split(" ");
                        for (int k = 0; k  < str2.length; k++) {
                            try {
                                String[] str3 = str2[k].split(",");
                                TMapPoint point = new TMapPoint(Double.parseDouble(str3[1]), Double.parseDouble(str3[0]));
                                polyLine.addLinePoint(point);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tMapView.addTMapPolyLine("path", polyLine);
                    }
                }
            }
            pathlistIdx = 0;

            linearLayoutTmap.addView(tMapView);
        }
    }
}
// END - Added by GT Silicon - END //