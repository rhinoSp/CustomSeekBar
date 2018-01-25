package com.rhino.customseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rhino.customseekbar.view.CustomSeekBar;

public class MainActivity extends AppCompatActivity {

    private TextView t1, t2, t3, t4;
    private CustomSeekBar c1, c2, c3, c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = findViewById(R.id.TextView1);
        c1 = findViewById(R.id.CustomSeekBarSection1);
        c1.setOnProgressChangedListener(new CustomSeekBar.OnProgressChangedListener() {
            @Override
            public void onChanged(CustomSeekBar seekBar, boolean fromUser, boolean isFinished) {
                t1.setText("fromUser = " + fromUser + ", isFinished = " + isFinished + ", " + seekBar.getProgress());
            }
        });

        t2 = findViewById(R.id.TextView2);
        c2 = findViewById(R.id.CustomSeekBarSection2);
        c2.setOnProgressChangedListener(new CustomSeekBar.OnProgressChangedListener() {
            @Override
            public void onChanged(CustomSeekBar seekBar, boolean fromUser, boolean isFinished) {
                t2.setText("fromUser = " + fromUser + ", isFinished = " + isFinished + ", " + seekBar.getProgress());
            }
        });

        t3 = findViewById(R.id.TextView3);
        c3 = findViewById(R.id.CustomSeekBarSection3);
        c3.setOnProgressChangedListener(new CustomSeekBar.OnProgressChangedListener() {
            @Override
            public void onChanged(CustomSeekBar seekBar, boolean fromUser, boolean isFinished) {
                t3.setText("fromUser = " + fromUser + ", isFinished = " + isFinished + ", " + seekBar.getProgress());
            }
        });

        t4 = findViewById(R.id.TextView4);
        c4 = findViewById(R.id.CustomSeekBarSection4);
        c4.setOnProgressChangedListener(new CustomSeekBar.OnProgressChangedListener() {
            @Override
            public void onChanged(CustomSeekBar seekBar, boolean fromUser, boolean isFinished) {
                t4.setText("fromUser = " + fromUser + ", isFinished = " + isFinished + ", " + seekBar.getProgress());
            }
        });

        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c4.setProgress(99, true, false);
            }
        });
    }

}
