package com.uhwaw.hanium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText ed1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1 = (EditText)findViewById(R.id.ed1);

        add_device();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),Select.class);
                startActivity(intent);
            }
        },2000);
    }
    // 단말기기 등록 메소드
    public void add_device() {
        //학교
        String url = "http://172.16.111.136:3000/process/adddevice";

//        String url = "http://192.168.137.98:3000/process/add_gps";

        //핫스팟
        //String url = "http://172.20.10.2:3000/process/adddevice";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("server", "서버는 들어옴");
                            //println("onResponse() 호출됨 : " + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("servererror", "error 스택 connectDevice, mainactivity");
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                DeviceInfo device = getDeviceInfo();
                params.put("mobile", device.getMobile());
                params.put("osVersion", device.getOsVersion());
                params.put("model", device.getModel());
                params.put("display", device.getDisplay());
                params.put("manufacturer", device.getManufacturer());
                params.put("macAdress", device.getMacAddress());
                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
        Log.d("adddevice", "device 서버단말기기 등록");
    }

    @SuppressLint("MissingPermission")
    public DeviceInfo getDeviceInfo() {
        DeviceInfo device = null;

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //1. 모바일(단말기기)

        @SuppressLint("HardwareIds") String mobile = telephonyManager.getLine1Number().replace("-", "").replace("+82", "0");
//        // 애뮬레이터(전화번호가 없으므로)
//        String mobile = "01076229705";

        //2. osVer
        String osVersion = Build.VERSION.RELEASE;

        //3. model
        String model = Build.MODEL;

        //4. display
        String display = getDisplay(this);

        //5. manufacturer
        String manufacturer = Build.MANUFACTURER;

        //6. macAddress
        String macAddress = getMacAddress(this);

        device = new DeviceInfo(mobile, osVersion, model, display, manufacturer, macAddress);

        return device;
    }

    private static String getDisplay(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        return deviceWidth + "x" + deviceHeight;
    }

    private static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        return info.getMacAddress();
    }
}
