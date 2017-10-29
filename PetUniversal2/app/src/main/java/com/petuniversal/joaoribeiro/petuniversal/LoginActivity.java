package com.petuniversal.joaoribeiro.petuniversal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean connectedToInternet;
    final private ArrayList<String> emailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("net")) {
            if (extras.getString("net").equals("true")) {
                connectedToInternet = true;
            } else if (extras.getString("net").equals("false")) {
                connectedToInternet = false;
            }
        }else {
            Log.i("ERROR@LOGIN","Major failure from LogoActivity extras");
        }

        /**
         * //TODO Get list of users
        if(!connectedToInternet) {
            Log.i("ENTROU@LOGIN", "Not Connected");
        }

                FirebaseDatabase.getInstance().setPersistenceEnabled(true); //should be on the first time it is called

                DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

                myDatabaseRef.child("users").child("user1").child("email").setValue("testing");

                myDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                                String key = dataSnapshot2.getKey();
                                String value = dataSnapshot2.getValue(String.class);
                                if (key.contains("email")){
                                    emailList.add(value);
                                    Log.i("USERS@LOGIN", "through firebase= " + value);
                                }
                            }
                        }
                        Log.i("EMAILLIST@LOGIN", String.valueOf(emailList));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("FIREBASE@LOGIN", "Got canceled");
                    }
                });
            }
            Log.i("EMAILLIST2@LOGIN", String.valueOf(emailList));

            //TODO Check currentUser exists in list
            //TODO skipLogin if user registered, else {
            */
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //mEmailView.setText("joao.ribeiro@petuniversal.com");
        mEmailView.setText("joao@clinicavet.com");

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
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute();
            //try {
                //if(mAuthTask.get()==null){
                    Log.i("ERROR@LOGIN","API returned null");
                    //mAuthTask.cancel(true);
                    //with Firebase
                    setContentForFirebase(email,password);
            Intent myIntent = new Intent(LoginActivity.this, ListClinicsActivity.class);
            //myIntent.putExtra("fakeToken", fakeUser);
            startActivity(myIntent);
                //}
            //} catch (InterruptedException | ExecutionException e) {
              //  Log.i("CATCH@LOGIN", String.valueOf(e));
            //}
        }
    }

    private boolean isEmailValid(String email) {
        //Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //Replace this with your own logic
        return true;
    }

    private void setContentForFirebase(String email, String password) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //should be on the first time it is called

        // Write a message to the database
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //TODO check user size and add if new
        myDatabaseRef.child("users").child("user1").child("email").setValue(email);

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

    /**
     * Represents an asynchronous login/registration task used to authenticate the user. (with API)
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String token = null;
        private String userID = null;
        private String email = null;
        //private String fakeUser = null;
        private String URLParameters = null;
        private String returnado = null;        // Will contain the raw JSON response as a string.

        public UserLoginTask(String email, String password) {
            this.email = email;
            URLParameters = "grant_type=password&username="+email+"&password="+password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            byte[] postData = URLParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;

            try {
                // Construct the URL for the Login
                Log.i("PROGREEEESSSIING?","1....2....");
                URL url = new URL("http://dev.petuniversal.com:8080/hospitalization/api/tokens");

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

                    try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write( postData );
                } catch (IOException e) {
                    Log.i("CATCH@Login", String.valueOf(e));
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
                    return false;
                }else{
                    returnado = buffer.toString();
                    Log.i("RETURNED",returnado);
                    return true;
                }
            } catch (IOException e) {
                Log.i("ERROR@Login", "Token not returned from API!");
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.i("CATCH@Login", "Error closing stream "+e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;

            if (success) {
                try {
                    if (returnado.contains("access_token") && returnado.contains("user_id")) {
                        JSONObject obj = new JSONObject(returnado);
                        token = obj.getString("access_token");
                        userID = obj.getString("user_id");

                        Toast.makeText(getApplicationContext(), "Login Success! (API)", Toast.LENGTH_LONG).show();

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
                FirebaseDatabase.getInstance().setPersistenceEnabled(true); //should be on the first time it is called

                // from database instance get reference of 'users' node
                DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
                // Write a message to the database
                myDatabaseRef.child("users").child("user1").child("email").setValue(this.email);
                //A parte
                /* myDatabaseRef.child("users").child("user1").child("firstTimeLogin").setValue(0);
                myDatabaseRef.child("clinics").child("name1").setValue("Clinic 1 firebase");

                myDatabaseRef.child("animals").child("nomeAnimal1").setValue("ZeusF");
                myDatabaseRef.child("animals").child("tarefaAnimal1").setValue("Brufen");
                myDatabaseRef.child("animals").child("corTarefaAnimal1").setValue("colorOrange");
                myDatabaseRef.child("animals").child("especieAnimal1").setValue("Cão");
                myDatabaseRef.child("animals").child("idadeAnimal1").setValue("3 anos");
                myDatabaseRef.child("animals").child("imagemLink1").setValue("https://i.pinimg.com/originals/aa/21/8e/aa218e0d81d51178ab68f65ef759eb11.png");
                myDatabaseRef.child("animals").child("pesoAnimal1").setValue("44 kg");
                myDatabaseRef.child("animals").child("raçaAnimal1").setValue("Pastor Alemão");
                myDatabaseRef.child("animals").child("sexoAnimal1").setValue("Macho");

                myDatabaseRef.child("animals").child("nomeAnimal2").setValue("KikaF");
                myDatabaseRef.child("animals").child("tarefaAnimal2").setValue("Desparazitação");
                myDatabaseRef.child("animals").child("corTarefaAnimal2").setValue("colorOrange");
                myDatabaseRef.child("animals").child("especieAnimal2").setValue("Gato");
                myDatabaseRef.child("animals").child("idadeAnimal2").setValue("5 anos");
                myDatabaseRef.child("animals").child("imagemLink2").setValue("http://www.pngmart.com/files/1/Cat-PNG-HD.png");
                myDatabaseRef.child("animals").child("pesoAnimal2").setValue("3 kg");
                myDatabaseRef.child("animals").child("raçaAnimal2").setValue("Bosques Noruega");
                myDatabaseRef.child("animals").child("sexoAnimal2").setValue("Fêmea"); */

                    /*final ArrayList<String> clinicNames = new ArrayList<>();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("clinics");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                String value = dataSnapshot1.getValue(String.class);
                                clinicNames.add(value);
                                Log.i("CLINICS@MAIN", "through firebase: "+value);
                            }
                            Toast.makeText(getApplicationContext(), "Login Success! (Firebase)", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(LoginActivity.this, ListClinicsActivity.class);
                            //myIntent.putExtra("fakeToken", fakeUser);
                            myIntent.putExtra("clinics", clinicNames);
                            Log.i("SENT@MAIN", String.valueOf(clinicNames));
                            startActivity(myIntent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/


                Toast.makeText(getApplicationContext(), "Login Success! (Firebase)", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(LoginActivity.this, ListClinicsActivity.class);
                //myIntent.putExtra("fakeToken", fakeUser);
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


