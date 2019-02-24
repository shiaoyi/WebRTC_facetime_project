package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/13.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class IndonesiaFragment extends Fragment {

    private static final int REMOVE_CONTACT_INDEX = 1;
    private static final int ADD_FAVORIATE_INDEX = 0;

    MemberAdapter memberAdapter;
    List<Member> roomList = new ArrayList<>();
    RecyclerView roomListView = null;
    //傳送口譯家id
    private final static String PREFERENCES_NAME = "preterPreference";
    //接收登入email
    private final static String PREFERENCES_NAME2 = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    String preter_id;

    Snackbar snackbar;
    ProgressDialog pd;
    private static String CALL_URL = "https://shiaoyi.000webhostapp.com/sendSinglePush.php";

    String id;//call傳過來的id

    private static String MEMBER_URL = "https://shiaoyi.000webhostapp.com/member.php";
    private static String FAVORITE_URL = "https://shiaoyi.000webhostapp.com/favoritePush.php";

    Member member;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        pd = new ProgressDialog(getActivity());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME2, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);

        memberFetch();

        roomListView = (RecyclerView) view.findViewById(R.id.room_listviewview);
        roomListView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
//        roomList.add(new Member("20","R.drawable.p01", "yee",5.0, "3年經驗", 7, 3));


        Log.d("onCreate","Hi!");
        return view;
    }

    private void memberFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, MEMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("memberFetch",response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("member");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    roomList.add(new Member(d.getString("id"),d.getString("image"),d.getString("name"),d.getDouble("score"),d.getString("experience"),d.getInt("service"),d.getInt("comment")));
                                }

                                memberAdapter = new MemberAdapter(getContext(), roomList);
                                roomListView.setAdapter(memberAdapter);

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

                params.put("language","2");

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        private Context context;
        private List<Member> roomList;

        public MemberAdapter(Context context, List<Member> roomList) {
            this.context = context;
            this.roomList = roomList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.translator_view, viewGroup, false);
            return new ViewHolder(itemView);
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvId, tvName, tvExContent, numService, numComment,tvScore;
            ImageButton ibVideo;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
                tvId = (TextView) itemView.findViewById(R.id.tvId);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvScore = (TextView) itemView.findViewById(R.id.tvScore);
                tvExContent = (TextView) itemView.findViewById(R.id.tvExContent);
                numService =(TextView) itemView.findViewById(R.id.numService);
                numComment =(TextView) itemView.findViewById(R.id.numComment);
                ibVideo = (ImageButton)itemView.findViewById(R.id.ibVideo);
            }
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            member = roomList.get(position);
            Picasso.with(getContext()).load(member.getImage()).into(viewHolder.ivImage);
            viewHolder.tvId.setText(String.valueOf(member.getId()));
            viewHolder.tvName.setText(member.getName());
            viewHolder.tvScore.setText(String.valueOf(member.getScore()));
            viewHolder.tvExContent.setText(member.getExperience());
            viewHolder.numService.setText(String.valueOf(member.getNumService()));
            viewHolder.numComment.setText(String.valueOf(member.getNumComment()));
            viewHolder.ibVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences =
                            getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

                    id = viewHolder.tvId.getText().toString();

                    preferences.edit()
                            .putString("id", id)
                            .apply();

                    callPush();
                    String roomId = viewHolder.tvId.getText().toString() + viewHolder.tvName.getText().toString();
                    Log.d("RoomId",roomId);
                    ConnectActivity room = (ConnectActivity) getActivity();
                    //Intent intent = new Intent(getContext(), ConnectActivity.class);
                    room.connectToRoom(roomId, false, false, false, 0);
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id",viewHolder.tvId.getText().toString());
                    ProfileChoserFragment profileChoserFragment = new ProfileChoserFragment();
                    profileChoserFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, profileChoserFragment);
                    fragmentTransaction.addToBackStack(profileChoserFragment.getClass().getName());
                    fragmentTransaction.commit();
                }
            });

            viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    menu.setHeaderTitle(viewHolder.tvName.getText().toString());
                    menu.add(Menu.NONE,0,0,"Add to Favoriate").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            //do what u want
                            if (item.getItemId() == ADD_FAVORIATE_INDEX) {
                                preter_id = viewHolder.tvId.getText().toString();
                                favoriteRequest();
                            }
                            return true;
                        }
                    });

                    menu.add(Menu.NONE,1,1,"Delete in Contacts").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            //do what u want
                            if (item.getItemId() == REMOVE_CONTACT_INDEX) {
                                roomList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                }


            });

        }

        private void favoriteRequest(){

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest postRequest = new StringRequest(Request.Method.POST, FAVORITE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();

                            if (response.equals("Successfully Add")) {

                                FavoriateFragment favoriateFragment = new FavoriateFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.body, favoriateFragment).commit();
                            }

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
                    params.put("preter_id", preter_id);

                    return params;
                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(postRequest);
        }


        private void callPush() {

            final String title = "Call";

            pd.setMessage("Calling");
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest postRequest = new StringRequest(Request.Method.POST, CALL_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();

                            Toast.makeText(getActivity(), "Calling Successfully", Toast.LENGTH_LONG).show();
                            Log.d("calling info",response);
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

                    return params;
                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(postRequest);
        }

    }
}