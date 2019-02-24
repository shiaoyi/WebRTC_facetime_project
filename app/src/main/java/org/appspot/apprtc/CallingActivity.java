package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/5/24.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CallingActivity extends AppCompatActivity{
    TextView tvName;
    ImageView ivPerson;
    ImageButton refuse,accept;

    ProgressDialog pd;
    private static String CALLING_URL = "https://shiaoyi.000webhostapp.com/acceptCalling.php";
    Call calling;

    private final static String PREFERENCES_NAME = "preterPreferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    String id,name,roomid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calling);
        findViews();
        showEmail();
    }

    private void findViews() {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);//this is room name
        pd = new ProgressDialog(CallingActivity.this);
        tvName = (TextView)findViewById(R.id.tvCallingName);
        ivPerson = (ImageView)findViewById(R.id.ivCallingImage);
        accept = (ImageButton)findViewById(R.id.btnPickup);
        refuse = (ImageButton)findViewById(R.id.btnHangout);
    }

    private void showEmail() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.d("coming call","call is null");
            return;
        }
        calling = (Call) bundle.getSerializable("calling");
        if (calling != null) {
            tvName.setText(calling.getName());
            Picasso.with(getBaseContext()).load(calling.getImage()).into(ivPerson);

            accept.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    acceptRequest();

                }
            });

            refuse.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.d("onclick","refuseclick");
                    startActivity(new Intent(CallingActivity.this, PreterActivity.class));
                    finish();
                }
            });

        }
    }

    private void acceptRequest() {

        RequestQueue queue = Volley.newRequestQueue(CallingActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, CALLING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Response

                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            id = responseJSON.getString("id");
                            name = responseJSON.getString("name");
                            roomid = id + name;
                            //do sth to connect to the preter's room,email is room name
                            Log.d("RoomId", roomid);
                            Intent intent = new Intent(CallingActivity.this,ConnectActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("RoomId",roomid);
                            bundle.putBoolean("Apreter",true);
                            intent.putExtras(bundle);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
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

                params.put("email", email);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

}
