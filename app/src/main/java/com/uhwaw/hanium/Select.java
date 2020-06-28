package com.uhwaw.hanium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.PublicKey;

public class Select extends AppCompatActivity {
    public ImageButton btnSon, btnPar;
    public int n = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        btnSon = (ImageButton) findViewById(R.id.btnSon);
        btnPar = (ImageButton) findViewById(R.id.btnPar);

        SharedPreferences pref = getSharedPreferences("Save",MODE_PRIVATE);
        n = pref.getInt("save",0);

        if(n==1) {

            Intent intent = new Intent(getApplicationContext(),Person0.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"부모로 접속완료",Toast.LENGTH_SHORT).show();
        }
        else if(n==2){

            Intent intent = new Intent(getApplicationContext(),Person0.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"자녀로 접속완료",Toast.LENGTH_SHORT).show();
        }

        btnPar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences
                        = getSharedPreferences("Save", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.clear();
                edit.commit();

                n = 1;

                edit.putInt("save",n);
                edit.commit();// 저장기능

                Intent intent = new Intent(getApplicationContext(),Person0.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"부모로 접속완료",Toast.LENGTH_SHORT).show();
            }
        });
        btnSon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences
                        = getSharedPreferences("Save", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.clear();
                edit.commit();
                n = 2;
                edit.putInt("save",n);
                edit.commit();
                Intent intent = new Intent(getApplicationContext(),Person0.class);

                startActivity(intent);
                Toast.makeText(getApplicationContext(),"자녀로 접속완료",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        Log.i("Destroy","종료");
        super.onDestroy();

        //프로그램이 종료 될때
        //SharedPreferences 객체에 저장.

        SharedPreferences sharedPreferences
                = getSharedPreferences("Save", Context.MODE_PRIVATE);
        //1.매개변수 -> 파일명
        //2.매개변수 -> 파일에대한 모드.. 0 아니면 Context.MODE_PRIVATE을 적는다..

        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt("save",n);

        edit.commit();// 저장기능. commit을 하지 않으면 저장되지 않는다.

    }
}
