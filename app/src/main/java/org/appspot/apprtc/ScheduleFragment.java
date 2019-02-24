package org.appspot.apprtc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class ScheduleFragment extends Fragment{

//    int mYear,mMonth,mDay,startHour,startMin,endHour,endMin;


    ScheduleAdapter scheduleAdapter;
    private Toolbar toolbar;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    CompactCalendarView compactCalendar;
    List<Schedule> scheduleList = new ArrayList<>();
    Schedule schedule;

    TextView tvName,tvDate,tvStartTime,tvEndTime,tvComment;
    ImageView ivImage;
    ListView lvSchedule;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formattedDate = null;

    private final static String PREFERENCES_NAME = "preterPreferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    Snackbar snackbar;
    ProgressDialog pd;
    private static String SCHEDULE_URL = "https://shiaoyi.000webhostapp.com/schedule.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        pd = new ProgressDialog(getContext());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);

        lvSchedule = (ListView)view.findViewById(R.id.lvSchedule);

        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);


//        Go to a website: www.epochconverter.com to chang the time to timeinmills and the last must add a "L",cuz it is a Long type


//        Event ev19 = new Event(Color.BLUE,  1527033600000L, "Beginning of Term for Students");
//        compactCalendar.addEvent(ev19);

        actionBar.setTitle(dateFormatForMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));
        scheduleRequest();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {


            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity().getApplicationContext();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String currDateString = sf.format(dateClicked);

                boolean reserve = false;

                for(int i = 0; i < scheduleList.size();i++) {
                    if (currDateString.equals(scheduleList.get(i).getDate())) {
                        Toast.makeText(context, currDateString, Toast.LENGTH_SHORT).show();
                        lvSchedule.setSelection(i);
                        lvSchedule.requestFocus();
                        reserve = true;

                    } else {
//                        Toast.makeText(context, scheduleList.get(i).getDate(), Toast.LENGTH_SHORT).show();

                    }
                }
                    if(!reserve){
                        Toast.makeText(context, R.string.no_booking, Toast.LENGTH_SHORT).show();
                    }
                }



            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                 Changes toolbar title on monthChange
                actionBar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));

            }

        });

        gotoToday();


        return view;
    }


    private class ScheduleAdapter extends BaseAdapter {
        Context context;
        List<Schedule> scheduleList;

        ScheduleAdapter(Context context, List<Schedule> scheduleList) {
            this.context = context;
            this.scheduleList = scheduleList;
        }

        @Override
        public int getCount() {
            return scheduleList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.schedule_view, parent, false);
            }

            schedule = scheduleList.get(position);

            ivImage = (ImageView) itemView.findViewById(R.id.ivBookingImage);
            Picasso.with(getContext()).load(schedule.getImage()).into(ivImage);


            tvComment = (TextView) itemView.findViewById(R.id.tvBookingComment);
            tvComment.setText(schedule.getComments());

            tvName = (TextView) itemView.findViewById(R.id.tvBookingName);
            tvName.setText(schedule.getName());

            tvDate = (TextView) itemView.findViewById(R.id.tvBookingDate);
            tvDate.setText(schedule.getDate());

            tvStartTime = (TextView) itemView.findViewById(R.id.tvBookingStartTime);
            tvStartTime.setText(schedule.getStartTime());

            tvEndTime = (TextView) itemView.findViewById(R.id.tvBookingEndTime);
            tvEndTime.setText(schedule.getEndTime());

            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return scheduleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    public void gotoToday() {

        // Set any date to navigate to particular date
        compactCalendar.setCurrentDate(Calendar.getInstance(Locale.getDefault()).getTime());

    }

    private void scheduleRequest() {
        pd.setMessage("Fetching Reservation . . .");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, SCHEDULE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.d("schFetch",response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("sch");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    scheduleList.add(new Schedule(d.getString("image"),d.getString("name"),d.getString("comment"),d.getString("date"),d.getString("startTime"),d.getString("finishTime")));
                                }

                                scheduleAdapter = new ScheduleAdapter(getContext(), scheduleList);
                                lvSchedule.setAdapter(scheduleAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("schFetch",error.getMessage());
                    }
                }) {
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