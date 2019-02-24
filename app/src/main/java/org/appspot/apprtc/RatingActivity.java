package org.appspot.apprtc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.Toast;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.graphics.Color;

/**
 * Created by hsushiaoyi on 2018/4/19.
 */

public class RatingActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_score);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.dialog_ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String text = rating + " star(s)";
                Toast.makeText(RatingActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
