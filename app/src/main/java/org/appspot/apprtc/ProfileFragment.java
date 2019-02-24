package org.appspot.apprtc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

//口譯家自己看的介面
public class ProfileFragment extends Fragment {

    TextView tvName,tvId,tvGender,tvLocallang,tvPreterlang,tvIntro,tvScore,tvService,tvEmail,tvCard;
    ImageView ivPreter;

    private final static String PREFERENCES_NAME = "preterPreferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    String id;

    ProgressDialog pd;
    private static String PROFILE_URL = "https://shiaoyi.000webhostapp.com/profileSelf.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pd = new ProgressDialog(getContext());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);

        tvName = (TextView)view.findViewById(R.id.tvPreterSelfName);
        tvId = (TextView)view.findViewById(R.id.tvPreterSelfId);
        tvGender = (TextView)view.findViewById(R.id.tvSelfGender);
        tvLocallang = (TextView)view.findViewById(R.id.tvOriginalSelfLang);
        tvPreterlang = (TextView)view.findViewById(R.id.tvPreterSelfLang);
        tvIntro = (TextView)view.findViewById(R.id.tvPreterSelfIntro);
        tvScore = (TextView)view.findViewById(R.id.tvPreterSelfScore);
        tvService = (TextView)view.findViewById(R.id.tvPreterSelfService);
        tvEmail = (TextView)view.findViewById(R.id.tvPreterSelfEmail);
        tvCard = (TextView)view.findViewById(R.id.tvPreterSelfCard);
        ivPreter = (ImageView)view.findViewById(R.id.ivSelf);

        preterFetch();

        Button buttonSeeMore = (Button) view.findViewById(R.id.btnSeeSelfMore);
        buttonSeeMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putString("preter_id",id);
                CommentFragment commentFragment = new CommentFragment();
                commentFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.body_preter, commentFragment);
                fragmentTransaction.commit();
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
                            id = responseJSON.getString("id");
                            tvId.setText(id);
                            tvName.setText(responseJSON.getString("name"));
                            Picasso.with(getContext()).load(responseJSON.getString("image")).into(ivPreter);
                            tvGender.setText(responseJSON.getString("gender"));
                            tvLocallang.setText(responseJSON.getString("lang1"));
                            tvPreterlang.setText(responseJSON.getString("lang2"));
                            tvScore.setText(responseJSON.getString("score"));
                            tvService.setText(responseJSON.getString("service"));
                            tvIntro.setText(responseJSON.getString("intro"));
                            tvCard.setText(responseJSON.getString("card"));
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
