package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/13.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class FavoriateFragment extends Fragment {

    private static final int REMOVE_CONTACT_INDEX = 1;
    //    ArrayAdapter<Member> adapter;
    MemberAdapter memberAdapter;
    List<Member> roomList = new ArrayList<>();;
    RecyclerView roomListView = null;
    TextView emptyView;

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    ProgressDialog pd;
    private static String FAVORITE_URL = "https://shiaoyi.000webhostapp.com/favorite.php";
    private static String DELETE_URL = "https://shiaoyi.000webhostapp.com/favoriteDelete.php";
    Member member;

    String preter_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favoriate, container, false);


        roomListView = (RecyclerView) view.findViewById(R.id.favorite_listview);
        emptyView=(TextView)view.findViewById(android.R.id.empty);

        pd = new ProgressDialog(getActivity());
        preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);

        fetchFavorite();

        return view;
    }

    private void fetchFavorite() {

        pd.setMessage("loading");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, FAVORITE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        JSONObject obj = null;
                        try {
                            Log.d("favFetch", response);
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("member");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    roomList.add(new Member(d.getString("id"), d.getString("image"), d.getString("name"), d.getString("language"), d.getDouble("score"), d.getInt("service"), d.getInt("comment")));
                                }

                                memberAdapter = new MemberAdapter(getContext(), roomList);
                                roomListView.setAdapter(memberAdapter);
                                roomListView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                                if (roomList.isEmpty()) {
                                    roomListView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    roomListView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }

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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);

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
            View itemView = inflater.inflate(R.layout.favorite_view, viewGroup, false);
            return new ViewHolder(itemView);
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvId, tvName, tvLang, numService, numComment, tvScore;
            ImageButton ibVideo;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.ivFavImage);
                tvId = (TextView) itemView.findViewById(R.id.tvFavId);
                tvName = (TextView) itemView.findViewById(R.id.tvFavName);
                tvScore = (TextView) itemView.findViewById(R.id.tvFavScore);
                tvLang = (TextView) itemView.findViewById(R.id.tvFavLang);
                numService = (TextView) itemView.findViewById(R.id.numFavService);
                numComment = (TextView) itemView.findViewById(R.id.numFavComment);
                ibVideo = (ImageButton) itemView.findViewById(R.id.ibFavVideo);
            }
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            member = roomList.get(position);
            viewHolder.tvId.setText(member.getId());
            Picasso.with(getContext()).load(member.getImage()).into(viewHolder.ivImage);
            viewHolder.tvName.setText(member.getName());
            viewHolder.tvScore.setText(String.valueOf(member.getScore()));
            viewHolder.tvLang.setText(member.getLanguage());
            viewHolder.numService.setText(String.valueOf(member.getNumService()));
            viewHolder.numComment.setText(String.valueOf(member.getNumComment()));
            viewHolder.ibVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String roomId = viewHolder.tvId.getText() + viewHolder.tvName.getText().toString();
                    Log.d("MyFragment", "getActivity(): " + getActivity());
                    ConnectActivity room = (ConnectActivity) getActivity();
                    room.connectToRoom(roomId, false, false, false, 0);
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileChoserFragment profileChoserFragment = new ProfileChoserFragment();
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

                    menu.setHeaderTitle(member.getName());

                    menu.add(Menu.NONE,1,1,"Delete in Contacts").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            //do what u want
                            if (item.getItemId() == REMOVE_CONTACT_INDEX) {
                                preter_id = viewHolder.tvId.getText().toString();
                                deleteFavorite();
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


        private void deleteFavorite() {

            pd.setMessage("loading");
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest postRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Log.d("Delete", response);
                            if (response.equals("Successfully Delete")) {
                                Toast.makeText(getActivity(), "Successfully Delete", Toast.LENGTH_SHORT).show();
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

    }
}