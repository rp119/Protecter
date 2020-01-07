package com.uhwaw.hanium;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProtectMode extends AppCompatActivity {


    String name[] = new String[4];
    String age[] = new String[4];
    String sex[] = new String[4];
    String phone[] = new String[4];
    String gps[] = new String[4];
    String spec[] = new String[4];
    Integer ycount[] = new Integer[4];
    Integer mcount[] = new Integer[4];
    Integer wcount[] = new Integer[4];
    Integer picture[] = new Integer[4];
    Double tem[] = new Double[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_mode);

        TextView tvname[] = new TextView[4];
        Integer nameID[] ={R.id.TVname0,R.id.TVname1,R.id.TVname2,R.id.TVname3};
        TextView tvage[] = new TextView[4];
        Integer ageID[] ={R.id.TVage0,R.id.TVage1,R.id.TVage2,R.id.TVage3};
        TextView tvsex[] = new TextView[4];
        Integer sexID[] ={R.id.TVgender0,R.id.TVgender1,R.id.TVgender2,R.id.TVgender3};
        TextView tvgps[] = new TextView[4];
        Integer gpsID[] ={R.id.TVgps0,R.id.TVgps1,R.id.TVgps2,R.id.TVgps3};
        TextView tvcount[] = new TextView[4];
        Integer countID[] ={R.id.TVcount0,R.id.TVcount1,R.id.TVcount2,R.id.TVcount3};
        TextView tvspec[] = new TextView[4];
        Integer specID[] ={R.id.TV0,R.id.TV1,R.id.TV2,R.id.TV3};
        TextView tvtem[] = new TextView[4];
        Integer temID[] ={R.id.TVtem0,R.id.TVtem1,R.id.TVtem2,R.id.TVtem3};
        ImageView imPicture[] = new ImageView[4];
        Integer pictureID[] ={R.id.ImagePicture0,R.id.ImagePicture1,R.id.ImagePicture2,R.id.ImagePicture3};
        ProgressBar pbTem[] = new ProgressBar[4];
        Integer PbtemID[] ={R.id.PB0,R.id.PB1,R.id.PB2,R.id.PB3};




            for(int i = 0 ; i<4; i++) {
                name[i] = "김태욱" + Integer.toString(i);
                age[i] = Integer.toString(20 + i);
                gps[i] = "광명시" + Integer.toString(i);
                sex[i] = "Male" + Integer.toString(i);
                spec[i] = "심근경색" + Integer.toString(i);

                ycount[i] = i;
                mcount[i] = i + 1;
                wcount[i] = i + 2;
                picture[i] = 0xFF0000 + 0xF * i;
                tem[i] = 36 + Double.valueOf(i);

                tvname[i] = findViewById(nameID[i]);
                tvage[i] = findViewById(ageID[i]);
                tvsex[i] = findViewById(sexID[i]);
                tvgps[i] = findViewById(gpsID[i]);
                tvspec[i] = findViewById(specID[i]);
                tvcount[i] = findViewById(countID[i]);
                imPicture[i] = findViewById(pictureID[i]);
                tvtem[i] = findViewById(temID[i]);
                pbTem[i] = findViewById(PbtemID[i]);
            }

            for(int i = 0; i<4; i++){
                tvname[i].setText("이름: " + name[i].toString());
                tvage[i].setText("나이: " + age[i].toString());
                tvsex[i].setText("성별: " + sex[i].toString());
                tvgps[i].setText("위치: " + gps[i].toString());
                tvspec[i].setText("특징: " + spec[i].toString());
                tvcount[i].setText("올해 " + Integer.toString(ycount[i]) + "번 이번 달 " + Integer.toString(mcount[i]) + "번이번 주: " + Integer.toString(wcount[i]) + "번");
                imPicture[i].setBackgroundColor(picture[i]);
                pbTem[i].setProgress(tem[i].intValue());
                tvtem[i].setText(Double.toString(tem[i]) + " ℃");
            }

        LinearLayout linear[] = new LinearLayout[4];
        Integer Lid[] ={R.id.Lin0,R.id.Lin1,R.id.Lin2,R.id.Lin3};


        for(int i=0; i<Lid.length;i++){
            final int index;
            index = i;
            linear[index] =(LinearLayout)findViewById(Lid[index]);
            Log.d("tem"+index,tem[index].toString());

            if(tem[index]<=35)
                pbTem[index].setProgressTintList(ColorStateList.valueOf(Color.BLUE));
            else if(tem[index]>=37)
                pbTem[index].setProgressTintList(ColorStateList.valueOf(Color.RED));
            else
                pbTem[index].setProgressTintList(ColorStateList.valueOf(Color.BLACK));

            linear[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Person0.class);
                intent.putExtra("name",name[index]);
                    intent.putExtra("age",age[index]);
                    intent.putExtra("sex",sex[index]);
                    intent.putExtra("phone",phone[index]);
                    intent.putExtra("gps",gps[index]);
                    intent.putExtra("ycount",ycount[index]);
                    intent.putExtra("mcount",mcount[index]);
                    intent.putExtra("wcount",wcount[index]);
                    intent.putExtra("picture",picture[index]);
                    intent.putExtra("tem",tem[index]);
                    startActivity(intent);
                }
            });

        }
    }
}
