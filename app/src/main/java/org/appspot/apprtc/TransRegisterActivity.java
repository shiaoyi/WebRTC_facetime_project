package org.appspot.apprtc;

import android.app.ProgressDialog;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsushiaoyi on 2018/4/21.
 */

public class TransRegisterActivity extends AppCompatActivity {

    EditText edit_username;
    EditText edit_creditcard;
    EditText edit_email;
    EditText edit_pass;
    EditText edit_repass;
    EditText edit_experience;
    Spinner sp_lang;
    Spinner sp_preterlang;
    RadioGroup rg_gender;
    Button btn_image;
    ImageView iv_image;
    Bitmap bitmap;
    Button btn_sign;
    Button btn_login;
    CheckBox checkBoxRemember;
    Snackbar snackbar;
    ProgressDialog pd;
    private static String REGISTER_URL="https://shiaoyi.000webhostapp.com/preterRegister.php";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        pd = new ProgressDialog(TransRegisterActivity.this);
        edit_username = (EditText) findViewById(R.id.etPreterName);
        edit_email = (EditText) findViewById(R.id.etPreterEmail);
        edit_pass = (EditText) findViewById(R.id.etPreterPass);
        edit_repass = (EditText) findViewById(R.id.etPreterRepass);
        edit_creditcard = (EditText) findViewById(R.id.etCreditcard);
        edit_experience = (EditText) findViewById(R.id.etExperience);
        sp_lang = (Spinner) findViewById(R.id.spLang1);
        sp_preterlang = (Spinner) findViewById(R.id.spPreterLang);
        rg_gender = (RadioGroup) findViewById(R.id.rgPreterGender);
        btn_image = (Button) findViewById(R.id.btnPreterImage);
        btn_sign = (Button) findViewById(R.id.btnPreterSignUp);
        checkBoxRemember = (CheckBox)findViewById(R.id.checkboxRemember);
        iv_image = (ImageView)findViewById(R.id.ivPreterImage);

        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        btn_login=(Button)findViewById(R.id.btnPreterSignIn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransRegisterActivity.this,TransLoginActivity.class);
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
        edit_creditcard.setError(null);


        String username = edit_username.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_pass.getText().toString();
        String repassword = edit_repass.getText().toString();
        String creditcard = edit_creditcard.getText().toString();
        String lang1 = sp_lang.getSelectedItem().toString();
        String lang2 = sp_preterlang.getSelectedItem().toString();


        boolean cancel = false;
        View focusView = null;

        if(bitmap == null){
            showSnackbar(getString(R.string.error_photo_required));
            focusView = btn_image;
            cancel = true;
        }

        if(lang1.equals(lang2)){
            showSnackbar(getString(R.string.error_lang));
            focusView = sp_lang;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            edit_username.setError(getString(R.string.error_field_required));
            focusView = edit_username;
            cancel = true;
        }

        if (TextUtils.isEmpty(creditcard)) {
            edit_creditcard.setError(getString(R.string.error_field_required));
            focusView = edit_creditcard;
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

        RequestQueue queue = Volley.newRequestQueue(TransRegisterActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        showSnackbar(response);

                        if(response.equals("Successfully Signed In!")) {

                            startActivity(new Intent(getApplicationContext(), TransLoginActivity.class));

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


                int genderid = rg_gender.getCheckedRadioButtonId();
                int gender;

                if(genderid == R.id.radio_male) {
                    gender = 1;
                }
                else
                    gender = 2;



                params.put("email", edit_email.getText().toString());
                params.put("password", edit_pass.getText().toString());
                params.put("creditcard", edit_creditcard.getText().toString());
                params.put("name", edit_username.getText().toString());
                params.put("experience", edit_experience.getText().toString());
                params.put("gender", String.valueOf(gender));
                params.put("lang", sp_lang.getSelectedItem().toString());
                params.put("preterlang", sp_preterlang.getSelectedItem().toString());
                params.put("image",getStringImage(bitmap));
                params.put("token", token);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    public void showSnackbar(String stringSnackbar){
        snackbar.make(findViewById(android.R.id.content), stringSnackbar.toString(), Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }


    public void onPreterLoginClick(View view) {
        Intent intent = new Intent(this,TransLoginActivity.class);
        startActivity(intent);
    }
}
