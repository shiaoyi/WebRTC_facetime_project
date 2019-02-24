package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/13.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static android.content.Context.MODE_PRIVATE;


public class DepositFragment extends Fragment {

    private Button button_opay;
    private Button button_credit;
    private Button button_coupon;
    private Button coupon_del;
    private EditText coupon;
    private TextView tv_rest;
    ProgressDialog pd;
    Snackbar snackbar;
    private static String COUPON_URL="https://shiaoyi.000webhostapp.com/coupon.php";
    private static String DEPOSIT_URL="https://shiaoyi.000webhostapp.com/deposit.php";

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    int state = 0;
    String deposit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);
        pd = new ProgressDialog(getActivity());
        preferences = this.getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);
        depositFetch();

        coupon_del= (Button) view.findViewById(R.id.coupon_del);
        coupon = (EditText)view.findViewById(R.id.etCoupon);

        coupon_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupon.setText("");
            }
        });

        button_opay = (Button)view.findViewById(R.id.button_opay);
        button_opay.setOnClickListener(listener_opay);
        button_credit = (Button)view.findViewById(R.id.button_credit);
        button_credit.setOnClickListener(listener_credit);
        button_coupon = (Button)view.findViewById(R.id.button_coupon);
        button_coupon.setOnClickListener(listener_coupon);
        tv_rest = (TextView)view.findViewById(R.id.tvRest);
        return view;
    }


    private Button.OnClickListener listener_opay = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0){
            state = 3;
            Intent intent =new Intent();
            intent.setClass(getActivity(), OpayLogin.class);
            startActivity(intent);
        }
    };

    private Button.OnClickListener listener_credit = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0){
            state = 4;
            Intent intent =new Intent();
            intent.setClass(getActivity(), CreditCard.class);
            startActivity(intent);
        }
    };

    private Button.OnClickListener listener_coupon = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0){
                state = 2;
                couponRequest();

        }
    };

    private void depositFetch() {
        pd.setMessage("loading . . .");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, DEPOSIT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Response

                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            deposit = responseJSON.getString("deposit");
                            tv_rest.setText(deposit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.dismiss();
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


    private void couponRequest(){
        pd.setMessage("Check coupon . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, COUPON_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response

                        if(response.equals("Successfully Get Coupon")) {
                            depositFetch();
                            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                            coupon.setText("");

                        }
                        else if(response.equals("Wrong Code Number!")){
                            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.x_mark)
                                    .setTitle("代碼錯誤！")
                                    .setMessage("請再嘗試一次")
                                    .setPositiveButton("close",new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            coupon.setText("");
                                        }
                                    })
                                    .show();

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                Log.d("Wrong email", email);

                Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);

                params.put("code_num", coupon.getText().toString());
                params.put("state", String.valueOf(state));
                params.put("user_email",email);
                params.put("date",year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

}









