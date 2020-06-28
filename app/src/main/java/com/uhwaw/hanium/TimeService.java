package com.uhwaw.hanium;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {
    public static final long NOTIFY_INTERVAL = 1000; // 1초
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        //이미 타이머가 있다면 그 타이머를 지우고
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // 아니라면 타이머를 새로 만든다.
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }
    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(), myService.class));
                    //10초에 한번씩 myService를 실행한다.
                }
            });
        }
    }
}
