package org.appspot.apprtc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class AccountFragment extends Fragment {

    TextView tvName,tvNickname,tvScored,tvDollar,tvEmail,tvLang,tvLove;
    ImageView ivChoser;
    private Button btnViewList;

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    ProgressDialog pd;
    private static String PROFILE_URL = "https://shiaoyi.000webhostapp.com/choser.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        pd = new ProgressDialog(getActivity());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);


        tvName = (TextView)view.findViewById(R.id.tvChoserName);
        tvNickname = (TextView)view.findViewById(R.id.tvChoserNickname);
        tvScored = (TextView)view.findViewById(R.id.tvChoserScored);
        tvDollar = (TextView)view.findViewById(R.id.tvChoserBalance);
        tvEmail = (TextView)view.findViewById(R.id.tvChoserEmail);
        tvLang = (TextView)view.findViewById(R.id.tvChoserLang);
        tvLove = (TextView)view.findViewById(R.id.tvChoserLove);
        ivChoser = (ImageView)view.findViewById(R.id.ivPersonPic);

        choserFetch();

        btnViewList = (Button)view.findViewById(R.id.btnSeeLove);
        btnViewList.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                Fragment fragment=new FavoriateFragment();
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction ft=fragmentManager.beginTransaction();
                ft.replace(R.id.body,fragment);
                ft.addToBackStack(fragment.getClass().getName());
                ft.commit();
            }
        });
        return view;
    }


    private void choserFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Response
                        Log.d("accountFetch",response);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            tvName.setText(responseJSON.getString("name"));
                            Picasso.with(getContext()).load(responseJSON.getString("image")).into(ivChoser);
                            tvNickname.setText(responseJSON.getString("nickname"));
                            tvLang.setText(responseJSON.getString("lang"));
                            tvDollar.setText(responseJSON.getString("dollar"));
                            tvScored.setText(responseJSON.getString("scored"));
                            tvLove.setText(responseJSON.getString("love"));
                            tvEmail.setText(responseJSON.getString("email"));

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

                params.put("email",email);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}