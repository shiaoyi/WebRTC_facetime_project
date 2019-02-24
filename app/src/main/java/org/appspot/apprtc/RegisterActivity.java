package org.appspot.apprtc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.Map;


/**
 * Created by hsushiaoyi on 2018/4/18.
 */

public class RegisterActivity extends AppCompatActivity{

    EditText edit_username;
    EditText edit_nickname;
    EditText edit_email;
    EditText edit_pass;
    EditText edit_repass;
    Spinner sp_lang;
    RadioGroup rg_gender;
    Button btn_image;
    ImageView iv_image;
    Bitmap bitmap;
    Button btn_sign;
    Button btn_login;
    CheckBox checkBoxTerms;
    Snackbar snackbar;
    ProgressDialog pd;
    private static String REGISTER_URL="https://shiaoyi.000webhostapp.com/register.php";
    private static String DEFAULT_IMAGE="https://shiaoyi.000webhostapp.com/upload/default.png";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);

        pd = new ProgressDialog(RegisterActivity.this);
        edit_username = (EditText) findViewById(R.id.etUsername);
        edit_email = (EditText) findViewById(R.id.etEmail);
        edit_pass = (EditText) findViewById(R.id.etPass);
        edit_repass = (EditText) findViewById(R.id.etRePass);
        edit_nickname = (EditText) findViewById(R.id.etNickname);
        sp_lang = (Spinner) findViewById(R.id.spLang);
        rg_gender = (RadioGroup) findViewById(R.id.rgGender);
        btn_image = (Button) findViewById(R.id.btnChooseImage);
        btn_sign = (Button) findViewById(R.id.btn_signUp);
        checkBoxTerms = (CheckBox)findViewById(R.id.checkBoxTerms);
        iv_image = (ImageView)findViewById(R.id.ivUserImage);

        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        btn_login=(Button)findViewById(R.id.btn_signIn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),999);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode== RESULT_OK && data !=null){
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                iv_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }

    private void registerUser() {

        // Reset errors.
        edit_username.setError(null);
        edit_email.setError(null);
        edit_pass.setError(null);
        edit_nickname.setError(null);


        String username = edit_username.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_pass.getText().toString();
        String repassword = edit_repass.getText().toString();
        String nickname = edit_nickname.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if(bitmap == null){
            showSnackbar(getString(R.string.error_photo_required));
            focusView = btn_image;
            cancel = true;
        }

        if(checkBoxTerms.isChecked() != true){
            Toast.makeText(getApplicationContext(),"Please Accept Terms & Services",Toast.LENGTH_SHORT).show();
            focusView = checkBoxTerms;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            edit_username.setError(getString(R.string.error_field_required));
            focusView = edit_username;
            cancel = true;
        }

        if (TextUtils.isEmpty(nickname)) {
            edit_nickname.setError(getString(R.string.error_field_required));
            focusView = edit_nickname;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edit_email.setError(getString(R.string.error_invalid_email));
            focusView = edit_email;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            edit_pass.setError(getString(R.string.error_invalid_password));
            focusView = edit_pass;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)){
            edit_pass.setError(getString(R.string.error_field_required));
            focusView = edit_pass;
            cancel = true;
        }

        if (!password.equals(repassword)) {
            edit_repass.setError(getString(R.string.error_dismatch_password));
            focusView = edit_repass;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {
            signupRequest();
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void signupRequest(){
        pd.setMessage("Signing Up . . .");
        pd.show();
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();

        if (token == null) {
            pd.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        showSnackbar(response);

                        if(response.equals("Successfully Signed In")) {

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

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

                String lang = sp_lang.getSelectedItem().toString();
                int genderid = rg_gender.getCheckedRadioButtonId();
                int gender;

                if(genderid == R.id.radioMale) {
                    gender = 1;
                }
                else
                    gender = 2;
                String image = getStringImage(bitmap);


                params.put("email", edit_email.getText().toString());
                params.put("password", edit_pass.getText().toString());
                params.put("nickname", edit_nickname.getText().toString());
                params.put("username", edit_username.getText().toString());
                params.put("gender", String.valueOf(gender));
                params.put("lang", lang);
                params.put("image",image);
                params.put("token", token);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    public void showSnackbar(String stringSnackbar){
        snackbar.make(findViewById(android.R.id.content), stringSnackbar.toString(), Snackbar.LENGTH_SHORT)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }



}
