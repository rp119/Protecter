package com.uhwaw.hanium;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import androidx.annotation.NonNull;

public class myService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    Double lat,lon;
    Thread myThread;
    float batteryPct;
    int level,scale;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;
        public LocationListener(String provider)
        {
            //Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            //provider에게 제공받은 위치를 mLastLocation에 저장한다.
        }
        @Override
        public void onLocationChanged(Location location)
        {//위치가 변경되었을때 그 위치를 mLastLocation에 저장한다.
                    //Log.e(TAG, "onLocationChanged: " + location);
                    mLastLocation.set(location);
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    //lat은 mLastLocation의 위도를, lon은 경도를 저장한 변수이다.
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);  //배터리 퍼센트 72.0
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level / (float)scale;

        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
            myThread = new Thread() {
                public void run() {
                    Log.d(TAG, "경도 :  " + lat + "  위도 :  " + lon);
                    Log.d(TAG,"level = "+level +"  scale = "+scale+"  batteryPct =  "+batteryPct);

                    SystemClock.sleep(1000);
                }

            };
            myThread.start();
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        //Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            //Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
        } catch (IllegalArgumentException ex) {
        }
//        NetWorkSend netWorkSend = new NetWorkSend();
//        netWorkSend.execute();

    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "TEst");
        }
    };

//    class NetWorkSend extends AsyncTask {
//        public NetWorkSend() {
//            super();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            //Log.d("result", ""+ o.toString());
//        }
//
//        @Override
//        protected void onProgressUpdate(Object[] values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onCancelled(Object o) {
//            super.onCancelled(o);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            while (true) {
//                try {
//                    Thread.sleep(5000);
//                    //sendToServer();
//                    //findLocation();
//                    //connectChild();
//                    //Log.d("async_", "서버 발송  : " + lat.toString() + ", " + lon.toString());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//        public void sendToServer() {
//            HttpURLConnection httpURLConnection = null;
//            String CONNECT_IP = "http://172.16.111.136:3000/process/add_gps";
//
//            int count = 0;
//            try {
//                TelephonyManager telephonyManager
//                        = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                @SuppressLint({"MissingPermission", "HardwareIds"}) String mobile = telephonyManager.getLine1Number().replace("-", "").replace("+82", "0");
//                //String mobile = "01011112222";
//                String data = URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(mobile, "UTF-8");
//                data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat.toString(), "UTF-8");
//                data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lon.toString(), "UTF-8");
//
//
//                URL url = new URL(CONNECT_IP);
//
//                //Log.d("connectServer", "접속url : " + url);
//                //Log.d("connectServer", "send data : " + data);
//                //Log.d("connectServer", "동작횟수 : " + ++count);
//
//
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
//                wr.write(data);
//                wr.flush();
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//
//                StringBuilder sb = new StringBuilder();
//                //Log.d("async_gps", "" + lat.toString() + ", " + lon.toString());
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//
//                    httpURLConnection.disconnect();
//                }
//            } catch (Exception e) {
//                httpURLConnection.disconnect();
//            }
//        }
//
//        public void findLocation() {
//            String CONNECT_IP = "http://172.16.111.136:3000/process/findLocation";
//            StringRequest request = new StringRequest(Request.Method.POST, CONNECT_IP, new com.android.volley.Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        Log.d("findLocation", "서버는 들어옴");
//                        JSONObject jsonObject = new JSONObject(response);
//                        String latitude = jsonObject.getString("latitude");
//                        String longitude = jsonObject.getString("longitude");
//                        Log.d("findLocation", "서버에서 받은 위도 경도 " + latitude + " , " + longitude);
//                        Toast.makeText(myService.this, "db조회 위도 : " + latitude + " 경도 : " + longitude, Toast.LENGTH_SHORT).show();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("servererror", "error findLocation in myService");
//                            error.printStackTrace();
//                        }
//                    }
//            ) {
//                @SuppressLint({"MissingPermission", "HardwareIds"})
//                @Override
//                protected Map<String, String> getParams() {
//                    TelephonyManager telephonyManager
//                            = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                    Map<String, String> params = new HashMap<>();
//
//                    //임시로 내번호 받아오기
//                    params.put("parentsNumber", telephonyManager.getLine1Number().replace("-", "").replace("+82", "0"));
//                    //params.put("parentsNumber", "01011112222");//자녀
//                    return params;
//                }
//            };
//
//            request.setShouldCache(false);
//            //request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            Request<String> add = Volley.newRequestQueue(myService.this).add(request);
//            //println("웹 서버에 요청함 : " + url);
//            //Log.d("async_", "웹 서버에 접속 요청함");
//        }
//        public void connectChild() {
//            String CONNECT_IP = "http://172.16.111.136:3000/process/connectChild";
//            StringRequest request = new StringRequest(Request.Method.POST, CONNECT_IP, new com.android.volley.Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        Log.d("findLocation", "서버는 들어옴");
////                        JSONObject jsonObject = new JSONObject(response);
////                        String latitude = jsonObject.getString("latitude");
////                        String longitude = jsonObject.getString("longitude");
////                        Log.d("findLocation", "서버에서 받은 위도 경도 " + latitude + " , " + longitude);
////                        Toast.makeText(myService.this, "db조회 위도 : " + latitude + " 경도 : " + longitude, Toast.LENGTH_SHORT).show();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            },
//                    new com.android.volley.Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("servererror", "error 발생 myService, connectChild");
//                            error.printStackTrace();
//                        }
//                    }
//            ) {
//                @SuppressLint({"MissingPermission", "HardwareIds"})
//                @Override
//                protected Map<String, String> getParams() {
//                    TelephonyManager telephonyManager
//                            = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                    Map<String, String> params = new HashMap<>();
//
//                    //내번호 = 부모번호
//                    params.put("parentsNumber", "01076229705");
//                    //params.put("childNumber", "01011112222");
//                    return params;
//                }
//            };
//
//            request.setShouldCache(false);
//            //request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            Request<String> add = Volley.newRequestQueue(myService.this).add(request);
//            //println("웹 서버에 요청함 : " + url);
//            //Log.d("async_", "웹 서버에 접속 요청함");
//        }
//    }
}
