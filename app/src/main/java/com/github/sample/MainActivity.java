package com.github.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.github.gongw.VerifyCodeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerifyCodeView underLine = findViewById(R.id.vc_underLine);
        VerifyCodeView centerLine = findViewById(R.id.vc_centerLine);
        VerifyCodeView square = findViewById(R.id.vc_square);
        VerifyCodeView circle = findViewById(R.id.vc_circle);
        underLine.setOnAllFilledListener(listener);
        centerLine.setOnAllFilledListener(listener);
        square.setOnAllFilledListener(listener);
        circle.setOnAllFilledListener(listener);

    }

    private VerifyCodeView.OnAllFilledListener listener = new VerifyCodeView.OnAllFilledListener() {
        @Override
        public void onAllFilled(String text) {
            Toast.makeText(MainActivity.this, "filled by "+text, Toast.LENGTH_SHORT).show();
        }
    };
}
