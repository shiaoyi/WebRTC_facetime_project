package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/5/24.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailActivity extends AppCompatActivity {
    TextView tvBookingName,tvBookingDate,tvBookingStartTime,tvBookingEndTime,tvBookingComment;
    ImageView ivPerson;
    Button refuse,accept;

    ProgressDialog pd;
    private static String ACCEPT_URL = "https://shiaoyi.000webhostapp.com/acceptBooking.php";
    Schedule schedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reservation);
        findViews();
        showEmail();
    }

    private void findViews() {
        pd = new ProgressDialog(EmailActivity.this);
        tvBookingName = (TextView)findViewById(R.id.tvBookingName);
        tvBookingDate = (TextView)findViewById(R.id.tvBookingDate);
        tvBookingStartTime = (TextView)findViewById(R.id.tvBookingStartTime);
        tvBookingEndTime = (TextView)findViewById(R.id.tvBookingEndTime);
        tvBookingComment = (TextView)findViewById(R.id.tvBookingComment);
        ivPerson = (ImageView)findViewById(R.id.ivBookingImage);
        refuse = (Button)findViewById(R.id.btnRefuse);
        accept = (Button)findViewById(R.id.btnAccept);
    }

    private void showEmail() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        schedule = (Schedule) bundle.getSerializable("schedule");
        if (schedule != null) {
            tvBookingName.setText(schedule.getName());
            tvBookingDate.setText(schedule.getDate());
            tvBookingStartTime.setText(schedule.getStartTime());
            tvBookingEndTime.setText(schedule.getEndTime());
            tvBookingComment.setText(schedule.getComments());
            Picasso.with(getBaseContext()).load(schedule.getImage()).into(ivPerson);

            accept.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
//                    scheduleList.add(new Schedule(schedule.getImage(),schedule.getName(),schedule.getComments(),schedule.getDate(),schedule.getStartTime(),schedule.getEndTime()));
                    Log.d("onclick","acceptclick");
                    acceptRequest();

                }
            });

            refuse.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.d("onclick","refuseclick");
                    startActivity(new Intent(EmailActivity.this, PreterActivity.class));
                    finish();
                }
            });

        }
    }

    private void acceptRequest() {
        pd.setMessage("Adding to Calender . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(EmailActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, ACCEPT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Response
//                        showSnackbar(response);

                        if (response.equals("Record updated successfully")) {

                            Toast.makeText(EmailActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EmailActivity.this, PreterActivity.class));
                            finish();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("comment", schedule.getComments());
                params.put("date", schedule.getDate());
                params.put("startTime", schedule.getStartTime());


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }
}
