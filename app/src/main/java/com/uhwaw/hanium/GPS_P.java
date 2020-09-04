package com.uhwaw.hanium;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;


import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
부모용 맵

*/
public class GPS_P extends AppCompatActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    String a, b, c, d;
    int orange;
    Double lat, lon;
    Double latt = 37.419740, lonn = 126.908476;
    Double child_lat = 37.0, child_lon = 127.0;
    Double clicklatitude, clicklongitude;
    String latitude,longitude;
    String greenzoneValue;
    EditText Ed3;
    EditText Ed4;
    TextView Ed_ChildLat, Ed_ChildLon;
    static final int SMS_SEND_PERMISSON = 1;
    int count = 0;
    LatLng home;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    private Marker currentMarker1 = null;
    public Marker currentMarkerChild = null;
    private Marker clickMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


//    Location mCurrentLocatiion;
    LatLng currentPosition;
    // TextView TvGPS;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    NetWorkSend2 netWorkSend = new NetWorkSend2();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person0);


//
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissonCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSON);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSON);
            }
        }
        //TvGPS = (TextView)findViewById(R.id.gpstv);

//         EtGPS = (EditText)findViewById(R.id.etgps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_g_p_s__p);

        mLayout = findViewById(R.id.layout_main);

        Log.d(TAG, "onCreate");

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        Ed_ChildLat = (TextView) findViewById(R.id.etChildLat);
        Ed_ChildLon = (TextView) findViewById(R.id.etChild_Lon);

        if(Ed_ChildLat.getText() ==null|| Ed_ChildLon.getText() == null)
        {
            Ed_ChildLat.setText("123");
            Ed_ChildLon.setText("345");
        }
        else {
            getPreferences3();
            getPreferences4();
        }
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(GPS_P.this);

        // Toast.makeText(this, gps, Toast.LENGTH_LONG).show();


        netWorkSend.execute();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            TextView TvGPs;
            TvGPs = (TextView) findViewById(R.id.gpstv);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());
////                String markerTitle = getCurrentAddress(currentPosition);
//                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());
//                String markerSnippet1 = "위도:" + latt.toString() + " 경도:" + lonn.toString();
//                String markerSnippetChild = "위도: (서버에서 넘겨주는 값 받아야함)  경도:";

                //Log.d(TAG, "onLocationResult : " + markerSnippet);
                //   gps = markerSnippet;
                //현재 위치에 마커 생성하고 이동
//                setCurrentLocation(location, markerTitle, markerSnippetChild, markerSnippet1);
//                mCurrentLocatiion = location;

            }
        }
    };
//
//    private void startLocationUpdates() {
//
//        if (!checkLocationServicesStatus()) {
//
//            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
//            showDialogForLocationServiceSetting();
//        } else {
//
//            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION);
//            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
//                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
//
//                //Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
//                return;
//            }
//            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
//
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//
//            if (checkPermission())
//                mGoogleMap.setMyLocationEnabled(true);
//
//        }

//    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");


        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if (clickMarker != null) clickMarker.remove();
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                clicklatitude = point.latitude; // 위도
                clicklongitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet("위도" + clicklatitude.toString() + ", " + "경도" + clicklongitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(clicklatitude, clicklongitude));
                mOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                // 마커(핀) 추가
                clickMarker = googleMap.addMarker(mOptions);

            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Ed3.setText(clicklatitude.toString());
                Ed4.setText(clicklongitude.toString());
                savePreferences(clicklatitude.toString());
                savePreferences1(clicklongitude.toString());

            }
        });

        //greenzone

        c = latt.toString();
        d = lonn.toString();
        Ed3 = (EditText) findViewById(R.id.etlat);
        Ed4 = (EditText) findViewById(R.id.etlon);


        Ed3.setText("37");
        Ed4.setText("126");
        getPreferences();
        getPreferences1();


        home = new LatLng(Double.parseDouble(Ed3.getText().toString()), Double.parseDouble(Ed4.getText().toString()));
        //home은 그린존의 중심이 되는 위치로 EditText 두개에 경도, 위도를 따로 저장한다.
        int myGreen = ContextCompat.getColor(this, R.color.MYGREEN);
        //그린존을 보기 쉽게 하기 위해 myGreen이라는 int형 변수에 연한 초록색 값을 넣는다.
        mGoogleMap.addCircle(new CircleOptions()
                .center(home)   //그린존의 중심을 변수 home으로 지정한다.
                .radius(3000)   //그린존 반지름크기를 3km로 지정한다.
                .strokeWidth(10)    //그린존의 테두리의 너비를 10으로 지정한다.
                .strokeColor(Color.GREEN)   //테두리의 색을 녹색으로 지정한다.
                .fillColor(myGreen)         //그린존 내부 색을 미리 설정해놓은 myGreen(연한 녹색)값으로 설정한다.
        );

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


//            startLocationUpdates(); // 3. 위치 업데이트 시작


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(GPS_P.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.c
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //  Toast.makeText(this, gps, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mGoogleMap != null)
                mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(final Location location, String markerTitle, String markerSnippetChild, String markerSnippet1) {
        Drawable drawable = getResources().getDrawable(R.drawable.child);
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        Ed_ChildLat = (TextView) findViewById(R.id.etChildLat);
        Ed_ChildLon = (TextView) findViewById(R.id.etChild_Lon);


        //위치를 받아와 그 위치에 marker를 찍는 매소드
        if (currentMarker != null) currentMarker.remove();
        if (currentMarker1 != null) currentMarker1.remove();
//        if (currentMarkerChild != null) currentMarkerChild.remove();

        //위치정보가 없는경우 currentMarker와 currentMarker1의 marker를 삭제한다.

        latt = Double.parseDouble(Ed3.getText().toString());
        lonn = Double.parseDouble(Ed4.getText().toString());


        child_lat = 37.2;
        child_lon = 127.0;

        Log.d("qwe","Ed_LAT = " +Ed_ChildLat.toString()+"Ed_LON = " +Ed_ChildLon.toString());

//            child_lat = Double.parseDouble(Ed_ChildLat.toString());
//            child_lon = Double.parseDouble(Ed_ChildLon.toString());
//

//
//        else {
//            child_lat = Double.parseDouble(Ed_ChildLat.getText().toString());
//            child_lon = Double.parseDouble(Ed_ChildLon.getText().toString());
//        }
        LatLng currentLatLng1 = new LatLng(latt, lonn);

        //currentLatLng1은 내가 지정한 (그린존 중심) 경도와 위도를 저장한다.
        LatLng ChildLatlng = new LatLng(child_lat, child_lon);
        //Log.d("lattt", latt.toString());
        //Log.d("lattt", lonn.toString());


        //markerOption1은 집이나 학원같은 아이가 자주가는 고정된 위치를 저장할 위치이다.
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(currentLatLng1);
        markerOptions1.title("지정장소");
        markerOptions1.snippet(markerSnippet1);
        markerOptions1.draggable(true);
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //현재위치와 구분하기 쉽게 하기 위해 marker의 색을 파란색으로 변경하였다.
        currentMarker1 = mGoogleMap.addMarker(markerOptions1);


    }


    public void setDefaultLocation() {

        //퍼미션을 받지 않았거나 위치가 활성화 되지 않았을때 Default로 보여지는 위치와 Marker를 설정하는매소드

        LatLng DEFAULT_LOCATION = new LatLng(Double.parseDouble(Ed3.getText().toString()), Double.parseDouble(Ed4.getText().toString()));
        //디폴트 위치를 home으로 설정해놓은 위도와 경도를 불러온다.

        String markerTitle = "위치정보 가져올 수 없음";
        //Marker에 Title을 저장할 변수 markerTitle에 Default값을 넣어준다.
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";
        //Marker의 위도 경도를 저장할 변수 markerSnippet에 Default값을 넣어준다.

        //if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        //MarkerOptions 새로운 객체를 설정한다.
        markerOptions.position(DEFAULT_LOCATION);
        //marker의 Default 위치를 서울로 설정한다.
        markerOptions.title(markerTitle);
        //marker를 누르면 나오는 마커의 title를 markerTitle변수의 값으로 설정한다.
        markerOptions.snippet(markerSnippet);
        //markerTitle 밑에 위도 경도값을 나타내는 변수 markerSnippet로 설정한다.
        markerOptions.visible(false);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //marker의 색을 빨간색으로 지정한다.
        currentMarker = mGoogleMap.addMarker(markerOptions);
        //지역변수 currentMarker에 Default로 지정한 현재 옵션을 넣는다.
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        //맵에서 Default 카메라 위치를 DEFAULT_LOCATION 변수에 저장한 서울로 하고 카메라 줌 값을 15로 설정한다.
        mGoogleMap.moveCamera(cameraUpdate);
    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
//                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GPS_P.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    private void getPreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String homeLat = pref.getString("lat", "37");
        Ed3.setText(homeLat);
    }

    private void getPreferences1() {

        SharedPreferences pref1 = getSharedPreferences("pref1", MODE_PRIVATE);
        String homeLon = pref1.getString("lon", "127");
        Ed4.setText(homeLon);
    }

    private void savePreferences(String s) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lat", s);
        editor.commit();
        finish();
    }

    private void savePreferences1(String s) {
        SharedPreferences pref1 = getSharedPreferences("pref1", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref1.edit();
        editor1.putString("lon", s);
        editor1.commit();
        finish();
    }

    private void getPreferences3() {
        SharedPreferences pref = getSharedPreferences("child_pref", MODE_PRIVATE);
        String childLat = pref.getString("child_lat", "37");
        Ed_ChildLat.setText(childLat);
    }

    private void getPreferences4() {

        SharedPreferences pref1 = getSharedPreferences("child_pref1", MODE_PRIVATE);
        String childLon = pref1.getString("child_lon", "127");
        Ed_ChildLon.setText(childLon);

    }

    private void savePreferences3(String s) {
        SharedPreferences pref = getSharedPreferences("child_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("child_lat", s);
        editor.commit();
        finish();
    }

    private void savePreferences4(String s) {
        SharedPreferences pref1 = getSharedPreferences("child_pref1", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref1.edit();
        editor1.putString("child_lon", s);
        editor1.commit();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferences();
        getPreferences1();
        latt = Double.parseDouble(Ed3.getText().toString());
        lonn = Double.parseDouble(Ed4.getText().toString());

        netWorkSend.cancel(true);
        Log.d("QWE","onDestroy");


    }
    class NetWorkSend2 extends AsyncTask {
        private Marker currentMarkerChild;
        public NetWorkSend2() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //Log.d("result", ""+ o.toString());
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            while (true) {
                if(isCancelled() == true) {
                    return 0;
                }
                try {
                    findLocation();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void findLocation() {
            Log.d("findlocation", "실행은 됐나?");
            String CONNECT_IP = "http://172.16.111.136:3000/process/findLocation";
//            String CONNECT_IP = "http://192.168.137.98:3000/process/add_gps";

            StringRequest request = new StringRequest(Request.Method.POST, CONNECT_IP, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("findlocation", "실행은 됐다");

                        JSONObject jsonObject = new JSONObject(response);
                        latitude = jsonObject.getString("latitude");
                        longitude = jsonObject.getString("longitude");
                        greenzoneValue = jsonObject.getString("greenZoneValue");
                        Toast.makeText(GPS_P.this, "db2조회 위도 : " + latitude + " 경도 : " + longitude + " GREENZONE" + greenzoneValue , Toast.LENGTH_SHORT).show();
                        Log.d("findlocation", "!@#") ;
                        if (currentMarkerChild != null)
                            currentMarkerChild.remove();

//                        latitude = String.valueOf(37.552);
//                        longitude = String.valueOf(127.003);

                        Log.d("findlocation", "!@#3") ;

                        LatLng ChildLatlng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                        MarkerOptions markerOptions123 = new MarkerOptions();
                        markerOptions123.position(ChildLatlng);
                        markerOptions123.title("아이의 위치");
                        markerOptions123.snippet("markerSnippetChild");
                        markerOptions123.draggable(true);
                        markerOptions123.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        currentMarkerChild = mGoogleMap.addMarker(markerOptions123);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(ChildLatlng);
                        orange = 1;
                        Ed_ChildLat.setText(latitude);
                        Ed_ChildLon.setText(longitude);
                        Log.d("findlocation", "!44@#") ;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @SuppressLint({"MissingPermission", "HardwareIds"})
                @Override
                protected Map<String, String> getParams() {
                    TelephonyManager telephonyManager
                            = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    Map<String, String> params = new HashMap<>();

                    params.put("parentsNumber", "01076229705");
                    //params.put("parentsNumber", "01011112222");//자녀
                    return params;
                }
            };

            request.setShouldCache(false);
            Request<String> add = Volley.newRequestQueue(GPS_P.this).add(request);
        }
    }
}