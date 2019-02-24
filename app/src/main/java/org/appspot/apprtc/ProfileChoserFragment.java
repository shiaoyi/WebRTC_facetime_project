package org.appspot.apprtc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

//給顧客看的介面

public class ProfileChoserFragment extends Fragment {

    TextView tvName,tvId,tvGender,tvLocallang,tvPreterlang,tvIntro,tvScore,tvService;
    ImageView ivPreter;
    String preter_id;
    ProgressDialog pd;
    private static String PROFILE_URL = "https://shiaoyi.000webhostapp.com/profile.php";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        pd = new ProgressDialog(getActivity());

        tvName = (TextView)view.findViewById(R.id.tvPreterName);
        tvId = (TextView)view.findViewById(R.id.tvPreterId);
        tvGender = (TextView)view.findViewById(R.id.tvGender);
        tvLocallang = (TextView)view.findViewById(R.id.tvOriginalLang);
        tvPreterlang = (TextView)view.findViewById(R.id.tvPreterLang);
        tvIntro = (TextView)view.findViewById(R.id.tvPreterIntro);
        tvScore = (TextView)view.findViewById(R.id.tvPreterScore);
        tvService = (TextView)view.findViewById(R.id.tvPreterService);
        ivPreter = (ImageView)view.findViewById(R.id.ivPreterPic);

        Bundle bundle = getArguments();
        if (bundle != null) {
            preter_id = bundle.getString("id");
            tvId.setText(preter_id);
        }

        preterFetch();

        Button buttonSeeMore = (Button) view.findViewById(R.id.btnSeeMore);
        buttonSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("preter_id",preter_id);
                CommentFragment commentFragment = new CommentFragment();
                commentFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.body, commentFragment);
                fragmentTransaction.addToBackStack(commentFragment.getClass().getName());
                fragmentTransaction.commit();
            }
        });

        Button buttonBooking = (Button) view.findViewById(R.id.btnBooking);
        buttonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), BookingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void preterFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("preterFetch",response);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            tvName.setText(responseJSON.getString("name"));
                            Picasso.with(getContext()).load(responseJSON.getString("image")).into(ivPreter);
                            tvGender.setText(responseJSON.getString("gender"));
                            tvLocallang.setText(responseJSON.getString("lang1"));
                            tvPreterlang.setText(responseJSON.getString("lang2"));
                            tvScore.setText(responseJSON.getString("score"));
                            tvService.setText(responseJSON.getString("service"));
                            tvIntro.setText(responseJSON.getString("intro"));
                            tvId.setText(responseJSON.getString("id"));

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

                params.put("preter_id",preter_id);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}
