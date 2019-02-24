package org.appspot.apprtc;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    Button signinButton;
    Button signupButton;
    private static String URL  ="http://shiaoyi.000webhostapp.com/login.php";
    private Snackbar snackbar;
    private ProgressDialog pd;

    private final static String PREFERENCES_NAME = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        pd = new ProgressDialog(LoginActivity.this);

        signinButton = (Button) findViewById(R.id.email_sign_in_button);
        signinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        signupButton = (Button) findViewById(R.id.email_sign_up_button);
        signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginRequest();
        }

    }

    private void attemptRegister(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void loginRequest(){
        pd.setMessage("Signing In . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();
                        showSnackbar(response);

                        if(response.equals("First Time Login Successfully")) {

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle(R.string.action_log_in)
                                    .setMessage(R.string.first_login)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            SharedPreferences preferences =
                                                    getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

                                            String email = mEmailView.getText().toString();

                                            preferences.edit()
                                                    .putString("email", email)
                                                    .apply();

                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                                        }

                                    })
                                    .show();

                        }

                        else if(response.equals("Login Successfully")) {

                            SharedPreferences preferences =
                                    getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

                            String email = mEmailView.getText().toString();
                            preferences.edit()
                                    .putString("email", email)
                                    .apply();

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        }else{
                            Toast.makeText(getApplication(),"Login Failed",Toast.LENGTH_SHORT);
                        }


                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.hide();
                        Log.d("ErrorResponse", error.getMessage());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);

                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());
                params.put("date",year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
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

