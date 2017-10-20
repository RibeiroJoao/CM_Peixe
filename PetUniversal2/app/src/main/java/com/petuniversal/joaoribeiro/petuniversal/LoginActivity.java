package com.petuniversal.joaoribeiro.petuniversal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //mEmailView.setText("joao.ribeiro@petuniversal.com");
        mEmailView.setText("peixe@clinicaPeixe.com");

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setText("a");

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
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
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
            try {
                if(mAuthTask.get()==null){
                    Log.i("ERROR@LOGIN","API returned null");
                    mAuthTask.cancel(true);
                    //showProgress(true);
                    //with Firebase
                    setContentForFirebase(email,password);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean setContentForFirebase(String email, String password) {
        // Write a message to the database
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        myDatabaseRef.child("users").child("email").setValue(email);

        //myDatabaseRef.child("clinics").child("name1").setValue("@login Clinic 1 firebase");
        //myDatabaseRef.child("clinics").child("name2").setValue("@login Clinic 2 firebase");

        return true;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String token = null;
        private String userID = null;
        private String email = null;
        private String URLParameters = null;
        private String returnado = null;        // Will contain the raw JSON response as a string.

        public UserLoginTask(String email, String password) {
            this.email = email;
            URLParameters = "grant_type=password&username="+email+"&password="+password;
            Log.i("StartBackg@Login","line352");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //showProgress(true);
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            byte[] postData = URLParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;

            try {
                // Construct the URL for the Login
                Log.i("PROGREEEESSSIING?","1....2....");
                URL url = new URL("http://dev.petuniversal.com/hospitalization/api/tokens");
                //URL url = new URL("https://pet-universal-app-id.firebaseio.com/");

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                Log.i("STEP0@LOGIN","with API");

                //Não percebo este pedaço, não funciona sem ele
                try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write( postData );
                    //Log.i("POSTDATA@LOGIN",postData.toString());
                } catch (IOException e) {
                    Log.i("CATCH@Login", String.valueOf(e));
                    e.printStackTrace();
                }
                Log.i("STEP1@LOGIN","with API");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.i("STEP2@LOGIN","with API");

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                Log.i("STEP3@LOGIN","with API");

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return false;
                }else{
                    returnado = buffer.toString();
                    Log.i("RETURNED",returnado);
                    return true;
                }
            } catch (IOException e) {
                Log.i("ERROR@Login", "Token not returned from API!");
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.i("CATCH@Login", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            //showProgress(true);

            if (success) {
                try {
                    if (returnado.contains("access_token") && returnado.contains("user_id")) {
                        JSONObject obj = new JSONObject(returnado);
                        token = obj.getString("access_token");
                        userID = obj.getString("user_id");

                        Context context = getApplicationContext();
                        CharSequence text = "Login Success!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent myIntent = new Intent(LoginActivity.this, ListClinicsActivity.class);
                        myIntent.putExtra("token", token);
                        myIntent.putExtra("userID", userID);
                        startActivity(myIntent);

                    } else {
                        Log.i("ERROR@Login", "Token JSONarray falhado!");
                    }
                } catch (JSONException e) {
                    Log.i("CATCH@Login", "PARSING token");
                    e.printStackTrace();
                }
            } else {
                Log.i("TOKEN@LOGIN","Starting Firebase");
                // Write a message to the database
                DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
                myDatabaseRef.child("users").child("email").setValue(this.email);
                //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                //myDatabaseRef.child("clinics").child("name1").setValue("@login Clinic 1 firebase");
                //myDatabaseRef.child("clinics").child("name2").setValue("@login Clinic 2 firebase");
                Context context = getApplicationContext();
                CharSequence text = "Login Success!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Intent myIntent = new Intent(LoginActivity.this, ListClinicsActivity.class);
                startActivity(myIntent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


