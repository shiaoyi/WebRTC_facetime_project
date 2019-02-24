package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by hsushiaoyi on 2018/4/18.
 */

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
    }

    public void onChoerClick(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void onTranslatorClick(View view) {
        Intent intent = new Intent(this,TransLoginActivity.class);
        startActivity(intent);
    }
}
