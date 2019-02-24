package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/13.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RecordFragment extends Fragment {
    RecordAdapter recordAdapter;
    List<Record> recordList = new ArrayList<>();
    ListView lvRecord;
    String date;

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;
    ProgressDialog pd;
    private static String RECORD_URL = "https://shiaoyi.000webhostapp.com/recordFetch.php";
//    String time = "For" +min + ":" + sec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //未接或掛斷對方顯示紅色名字,沒有圖示沒有time
        //打過去但對方未接或掛斷顯示圖示，沒有time ˇ
        //打了有皆顯示通話時間10:10 ˇ

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        pd = new ProgressDialog(getContext());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);
        recordFetch();

//        recordList.add(new Record("John",date,"通話時間 3:26","TWD 65"));
//        recordList.add(new Record(R.drawable.facetime_call,"Jack",date));
//        recordList.add(new Record("Mark",date,"通話時間 1:30:10","TWD 1800"));
//        recordList.add(new Record("Ben",date,"通話時間 10:34","TWD 210"));
//        recordList.add(new Record(R.drawable.facetime_call,"James",date)); //打過去沒接顯示圖示
//        recordList.add(new Record("David",date,"通話時間 2:04","TWD 40"));

        lvRecord = (ListView)view.findViewById(R.id.lvRecord);

        return view;
    }

    private void recordFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("recordFetch",response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("record");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    //set Date
                                    String date = d.getString("finish_time");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date dt =sdf.parse(date);
                                    SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd");
                                    date=sdf5.format(dt);

                                    //set Time
                                    Date start =sdf.parse(d.getString("start_time"));
                                    Date finish =sdf.parse(d.getString("finish_time"));

                                    //取得兩個時間的Unix時間
                                    Long ut1=start.getTime();
                                    Long ut2=finish.getTime();

                                    //相減獲得兩個時間差距的毫秒
                                    Long timeP=ut2-ut1;
                                    Long sec=timeP/1000%60;//秒差
                                    Long min=timeP/1000/60;//分差
                                    Long hr=timeP/1000/60/60;//時差
                                    if (d.getString("state").equals("0")) {
                                        recordList.add(new Record(R.drawable.facetime_call,d.getString("name"), date,d.getString("lang")));
                                    } else {
                                        recordList.add(new Record(d.getString("name"),date, "通話時間 "+hr+":"+min+":"+sec, d.getString("cost")+" TWD",d.getString("lang")));
                                    }
                                }

                                recordAdapter = new RecordAdapter(getContext(), recordList);
                                lvRecord.setAdapter(recordAdapter);

                            }
                        } catch (JSONException e) {
                            pd.hide();
                            e.printStackTrace();
                        } catch (ParseException e) {
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

                params.put("email",email);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private class RecordAdapter extends BaseAdapter {
        Context context;
        List<Record> recordList;

        RecordAdapter(Context context, List<Record> recordList) {
            this.context = context;
            this.recordList = recordList;
        }

        @Override
        public int getCount() {
            return recordList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.record_view, parent, false);
            }

            Record record = recordList.get(position);

            ImageView ivImage = (ImageView)itemView.findViewById(R.id.ivRecordImage);
            ivImage.setImageResource(record.getImage());

            TextView tvName = (TextView) itemView.findViewById(R.id.tvRecordName);
            tvName.setText(record.getName());

            TextView tvLang = (TextView) itemView.findViewById(R.id.tvRecordlang);
            tvLang.setText(record.getLang());

            TextView tvTime = (TextView) itemView.findViewById(R.id.tvRecordTime);
            tvTime.setText(record.getTime());

            TextView tvDate = (TextView) itemView.findViewById(R.id.tvRecordDate);
            tvDate.setText(record.getDate());

            TextView tvDollar = (TextView) itemView.findViewById(R.id.tvRecordDollar);
            tvDollar.setText(record.getDollar());


            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
