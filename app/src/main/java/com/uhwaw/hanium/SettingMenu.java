package com.uhwaw.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class SettingMenu extends AppCompatActivity {

    FrameLayout frameLayout;
    EditText et_num;
    Button btnOk;
    ImageButton imgBlue,imgNum;
    int check;
    String numCheck;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_menu);

        frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        et_num = (EditText)findViewById(R.id.Et_number);
        btnOk = (Button)findViewById(R.id.btnOK);
        imgBlue = (ImageButton)findViewById(R.id.btnBluetooth);
        imgNum = (ImageButton)findViewById(R.id.btnPhone);




        Intent intent14 = getIntent();
        check = intent14.getIntExtra("select",0);
        getPreferences();

        imgBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Setting.class);
                startActivity(intent);
            }
        });

        Log.d("qwe","et_num="+et_num.getText().toString());

        String Sibal = et_num.getText().toString();
        Log.d("QWE","sibal ="+Sibal);
        if(Sibal=="123")
            Log.d("QWE","Test");

        else
            Log.d("QWE","Test Fail");


        numCheck = et_num.getText().toString();
        Log.d("QWE", "SSSS"+numCheck);
        imgNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QWE", numCheck);

                if(check == 1)
                    et_num.setHint("자녀 번호를 입력하세요.");
                else if(check == 2)
                    et_num.setHint("부모 번호를 입력하세요.");

                if (numCheck.equals("") ) {
                    frameLayout.setVisibility(View.VISIBLE);
                }
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SettingMenu.this);
                    dialog.setTitle("Message");
                    dialog.setMessage("이미 등록된 번호("+numCheck+")가 있습니다. 새로 등록하시겠습니까?");
                    dialog.setCancelable(false);

                    dialog.setPositiveButton(
                            "네",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int
                                        id) {

                                    et_num.setText("");
                                    frameLayout.setVisibility(View.VISIBLE);
                                }
                            });

                    dialog.setNegativeButton(
                            "아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    frameLayout.setVisibility(View.INVISIBLE);
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = dialog.create();
                    alert.show();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences(et_num.getText().toString());
            }
        });
    }

    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String number = pref.getString("number", "");
        et_num.setText(number);
    }

    private void savePreferences(String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("number", s);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(),Person0.class);
        intent.putExtra("number",et_num.getText().toString());
        startActivity(intent);
    }


    public void connectChild(final String childNum) {
        String CONNECT_IP = "http://172.16.111.136:3000/process/connectChild";
//        String CONNECT_IP = "http://192.168.137.98:3000/process/add_gps";


        StringRequest request = new StringRequest(Request.Method.POST, CONNECT_IP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("connetChild", "서버는 들어옴");
                        Toast.makeText(SettingMenu.this, "" + childNum + " 자녀 등록 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("servererror", "error 스택, fincLocation in SettingMenu.JAVA");
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

                Log.d("childconnect", "childconnect 성공");
                //내번호 = 부모번호
                //params.put("parentsNumber", telephonyManager.getLine1Number().replace("-", "").replace("+82", "0"));
                params.put("parentsNumber", "01076229705");
                return params;
            }
        };

        request.setShouldCache(false);
        //request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Request<String> add = Volley.newRequestQueue(this).add(request);
        //println("웹 서버에 요청함 : " + url);
        Log.d("async_", "웹 서버에 접속 요청함");
    }

    class NetWorkSend extends AsyncTask<String, Void, Void> {
        public NetWorkSend() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            while(true) {
                try {
                    //findLocation();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.d("findLocation", "findLocation 에러, gPS_P");
                    e.printStackTrace();
                }
            }
        }

        public void findLocation() {
            String CONNECT_IP = "http://172.16.111.136:3000/process/findLocation";
            StringRequest request = new StringRequest(Request.Method.POST, CONNECT_IP, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("taewook", "서버는 들어옴");
                        JSONObject jsonObject = new JSONObject(response);
                        String latitude = jsonObject.getString("latitude");
                        String longitude = jsonObject.getString("longitude");
                        Log.d("taewook", "값 : " + latitude + ", " + longitude);
                        Log.d("taewook", "서버에서 받은 위도 경도 " + latitude + " , " + longitude);
                        Toast.makeText(SettingMenu.this, "db회 위도 : " + latitude + " 경도 : " + longitude, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("servererror", "error 스택, findLocation int GPS_P");
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
            request.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Request<String> add = Volley.newRequestQueue(SettingMenu.this).add(request);
            //println("웹 서버에 요청함 : " + url);
            Log.d("async_", "웹 서버에 접속 요청함");
        }
    }
}
