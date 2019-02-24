package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/20.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookingActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView tvDate, tvStartTime, tvEndTime;
    private EditText etSay;
    private int mYear, mMonth, mDay, m1Hour, m1Minute, m2Hour, m2Minute;
    private boolean startclick = false;
    private Spinner spLanguage;
    private Spinner spId;
    private List<String> devices;

    Snackbar snackbar;
    ProgressDialog pd;
    private static String BOOKING_URL = "https://shiaoyi.000webhostapp.com/booking.php";
    private static String URL_FETCH_DEVICES = "https://shiaoyi.000webhostapp.com/GetRegisteredDevices.php";
    private static String PUSH_URL = "https://shiaoyi.000webhostapp.com/sendSinglePush.php";

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_booking);

        pd = new ProgressDialog(BookingActivity.this);
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        etSay = (EditText) findViewById(R.id.etSay);
        spLanguage = (Spinner) findViewById(R.id.spLanguage);
        spLanguage.setSelection(0, true);
        spId = (Spinner) findViewById(R.id.spTranslator);
        devices = new ArrayList<>();
        loadRegisteredDevices();
        showNow();
    }

    private void bookingUser() {

        // Reset errors.
        tvStartTime.setError(null);
        tvEndTime.setError(null);

        String startTime = tvStartTime.getText().toString();
        String endTime = tvEndTime.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (startTime.equals(endTime)) {
            tvEndTime.setError(getString(R.string.error_match_time));
            focusView = tvEndTime;
            cancel = true;
        }
        if (cancel) {

            focusView.requestFocus();
        } else {
            new AlertDialog.Builder(BookingActivity.this)
                    .setTitle(R.string.page_Booking)
                    .setMessage("Are you sure you want to submit the Reservation?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bookingRequest();

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }
    }

    private void loadRegisteredDevices() {
        pd.setMessage("Fetching Devices . . .");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(BookingActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL_FETCH_DEVICES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("devices");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    devices.add(d.getString("id"));
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        BookingActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        devices);

                                spId.setAdapter(arrayAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void bookingRequest() {
        pd.setMessage("Booking . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(BookingActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, BOOKING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Response
//                        showSnackbar(response);

                        if (response.equals("Successfully Booking")) {

                            sendSinglePush();


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
                params.put("preter_id", spId.getSelectedItem().toString());
                params.put("date", tvDate.getText().toString());
                params.put("comments", etSay.getText().toString());
                params.put("startTime", tvStartTime.getText().toString());
                params.put("endTime", tvEndTime.getText().toString());
                params.put("state", String.valueOf(0));

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    private void sendSinglePush() {

        final String title = "Reservation";
        final String comment = etSay.getText().toString();
        final String date = tvDate.getText().toString();
        final String startTime = tvStartTime.getText().toString();
        final String endTime = tvEndTime.getText().toString();
        final String id = spId.getSelectedItem().toString();

        pd.setMessage("Sending Push");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(BookingActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, PUSH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        Toast.makeText(BookingActivity.this, "Booking Successfully", Toast.LENGTH_LONG).show();
                        Log.d("booking info",response);
                        startActivity(new Intent().setClass(BookingActivity.this, HomeActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("preter_id", id);
                params.put("title", title);
                params.put("comment", comment);
                params.put("date", date);
                params.put("startTime", startTime);
                params.put("finishTime", endTime);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public void showSnackbar(String stringSnackbar) {
        snackbar.make(findViewById(android.R.id.content), stringSnackbar.toString(), Snackbar.LENGTH_SHORT)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            // 點擊 ActionBar 返回按鈕時 結束目前的 Activity
            case android.R.id.home:
                Log.d("backButton", "I clicked!");
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.item_Booking:
                Log.d("bookingButton", "You clicked!");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        m1Hour = calendar.get(Calendar.HOUR_OF_DAY);
        m1Minute = calendar.get(Calendar.MINUTE);
        m2Hour = calendar.get(Calendar.HOUR_OF_DAY);
        m2Minute = calendar.get(Calendar.MINUTE);
        updateDisplay();
    }

    private void updateDisplay() {
        tvDate.setText(new StringBuilder().append(mYear).append("-")
                .append(pad(mMonth + 1)).append("-").append(pad(mDay)));
        tvStartTime.setText(new StringBuilder().append(" ").append(pad(m1Hour)).append(":")
                .append(pad(m1Minute)));
        tvEndTime.setText(new StringBuilder().append(" ").append(pad(m2Hour)).append(":")
                .append(pad(m2Minute)));
    }

    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDisplay();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (startclick) {
            m1Hour = hour;
            m1Minute = minute;
        } else {
            m2Hour = hour;
            m2Minute = minute;
        }
        updateDisplay();
    }

    public static class DatePickerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            BookingActivity activity = (BookingActivity) getActivity();
            return new DatePickerDialog(
                    activity, activity, activity.mYear, activity.mMonth, activity.mDay);
        }
    }

    public static class TimePickerDialogFragment1 extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            BookingActivity activity = (BookingActivity) getActivity();
            return new TimePickerDialog(
                    activity, activity, activity.m1Hour, activity.m1Minute, false);
        }
    }

    public static class TimePickerDialogFragment2 extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            BookingActivity activity = (BookingActivity) getActivity();
            return new TimePickerDialog(
                    activity, activity, activity.m2Hour, activity.m2Minute, false);
        }
    }

    public void onDateClick(View view) {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }

    public void onTimeStartClick(View view) {
        startclick = true;
        TimePickerDialogFragment1 timePickerFragment = new TimePickerDialogFragment1();
        FragmentManager fm = getSupportFragmentManager();
        timePickerFragment.show(fm, "timeStartPicker");
    }

    public void onTimeEndClick(View view) {
        startclick = false;
        TimePickerDialogFragment2 timePickerFragment = new TimePickerDialogFragment2();
        FragmentManager fm = getSupportFragmentManager();
        timePickerFragment.show(fm, "timeEndPicker");
    }

    public void onSubmitClick(View view) {
        bookingUser();

    }

}
