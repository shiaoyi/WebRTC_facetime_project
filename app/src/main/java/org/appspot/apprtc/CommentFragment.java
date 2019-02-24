package org.appspot.apprtc;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 2018/4/9.
 */

public class CommentFragment extends Fragment {

    String date;
    CommentAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();
    ListView lvComment;

    String preter_id;
    ProgressDialog pd;
    private static String COMMENT_URL = "https://shiaoyi.000webhostapp.com/commentFetch.php";
    private static String PRETERID_URL = "https://shiaoyi.000webhostapp.com/idFetch.php";

    private final static String PREFERENCES_NAME = "preterPreferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        pd = new ProgressDialog(getActivity());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);
        idFetch();

//        commentList.add(new Comment(R.drawable.p01,"John",3,"服務態度良好",date));
//        commentList.add(new Comment(R.drawable.p02, "Jack",4,"hihi",date));

        lvComment = (ListView)view.findViewById(R.id.lvComment);
        return view;
    }

    private void idFetch() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, PRETERID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("idFetch",response);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            preter_id = responseJSON.getString("id");

                            Bundle bundle = getArguments();
                            if (bundle != null) {
                                preter_id = bundle.getString("preter_id");
                            }

                            commentFetch();
                        } catch (JSONException e) {
                            pd.hide();
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

    private void commentFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("commentFetch",response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("member");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    commentList.add(new Comment(d.getString("image"),d.getString("name"),d.getDouble("rating"),d.getString("comment"),d.getString("date")));
                                }

                                commentAdapter = new CommentAdapter(getContext(), commentList);
                                lvComment.setAdapter(commentAdapter);

                            }
                        } catch (JSONException e) {
                            pd.hide();
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


    private class CommentAdapter extends BaseAdapter {
        Context context;
        List<Comment> commentList;

        CommentAdapter(Context context, List<Comment> commentList) {
            this.context = context;
            this.commentList = commentList;
        }

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.comment_view, parent, false);
            }

            Comment comment = commentList.get(position);

            ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivChoserImage);
            Picasso.with(getContext()).load(comment.getImage()).into(ivImage);

            TextView tvComment = (TextView) itemView.findViewById(R.id.tvChoserComment);
            tvComment.setText(comment.getComments());

            TextView tvName = (TextView) itemView.findViewById(R.id.tvCommentName);
            tvName.setText(comment.getName());

            TextView tvDate = (TextView) itemView.findViewById(R.id.tvCommentDate);
            tvDate.setText(comment.getDate());

            RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            ratingBar.setRating((float)comment.getScore());

            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}

