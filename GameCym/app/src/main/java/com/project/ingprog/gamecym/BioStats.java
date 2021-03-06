package com.project.ingprog.gamecym;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BioStats extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String mUserId;
    Spinner ageSpinner, sexSpinner;
    EditText weightTextBox, heightTextBox;
    View mProgressView, mBioView;
    UserSendBio mSendTask = null;
    UserGetBio mGetTask = null;

    boolean mModify = false;

    private GoogleApiClient mGoogleApiClient;

    protected static final int REQUEST_CODE_RESOLUTION = 1;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private boolean mIsConnectToGoogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_stats);

        mUserId = new String();
        mUserId = this.getIntent().getStringExtra("userid");

        mModify = this.getIntent().getBooleanExtra("modify", false);

        ageSpinner = (Spinner)findViewById(R.id.spinner_age);
        sexSpinner = (Spinner)findViewById(R.id.spinner_sex);
        weightTextBox = (EditText)findViewById(R.id.weight_edit_text);
        heightTextBox = (EditText)findViewById(R.id.height_edit_text);
        mProgressView = findViewById(R.id.send_progress);
        mBioView = findViewById(R.id.bio_view);

        mGoogleApiClient = GoogleAchievements.getGoogleApiClient(this);
        mGoogleApiClient.connect();

        //login achievement
        GoogleAchievements.unlockAchievement(GoogleAchievements.Achievements.TEST1);

        initAgeSpinner();
        initSexSpinner();

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mSignInClicked = false;
        mAutoStartSignInFlow = false;
        mIsConnectToGoogle = true;

        if(!mModify)
            checkIfProfileExists();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mIsConnectToGoogle = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Utils.DebugLog("Exception while starting resolution activity");
            }
            mResolvingConnectionFailure = false;
        }

        // Put code here to display the sign-in button
    }


    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mIsConnectToGoogle = false;
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mIsConnectToGoogle = false;
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        mIsConnectToGoogle = false;
    }

    private void initAgeSpinner()
    {
        Spinner ageSpinner = (Spinner)findViewById(R.id.spinner_age);
        ArrayList<Integer> items = new ArrayList<Integer>();

        for (int i = 1; i <= 120; i++)
        {
            items.add(i);
        }


        Integer[] arrItems = new Integer[items.size()];
        arrItems = items.toArray(arrItems);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, arrItems);
        ageSpinner.setAdapter(adapter);
        ageSpinner.setSelection(29);
    }

    private  void initSexSpinner()
    {
        Spinner sexSpinner = (Spinner)findViewById(R.id.spinner_sex);
        String[] sexes = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        sexSpinner.setAdapter(adapter);

    }

    public void checkIfProfileExists()
    {
        showProgress(true);

        mGetTask = new UserGetBio(mUserId, BioStats.this);
        mGetTask.execute((Void) null);
    }

    public void saveProfile(View view)
    {
        showProgress(true);

        if(areFieldsOk()) {
            //now send json to server
            mSendTask = new UserSendBio(mUserId, ageSpinner.getSelectedItem().toString(), weightTextBox.getText().toString(),
                    heightTextBox.getText().toString(), sexSpinner.getSelectedItem().toString(), BioStats.this);

            mSendTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mBioView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBioView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBioView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mBioView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean areFieldsOk()
    {
        if(weightTextBox.getText().toString().isEmpty())
        {
            weightTextBox.setError(getString(R.string.field_not_empty));
            weightTextBox.requestFocus();
            return false;
        }
        if(heightTextBox.getText().toString().isEmpty())
        {
            heightTextBox.setError(getString(R.string.field_not_empty));
            heightTextBox.requestFocus();
            return  false;
        }

        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserGetBio extends AsyncTask<Void, Void, String> {

        private final String mUserId;
        private final Context mContext;

        UserGetBio(String userid, Context context) {
            mUserId = userid;
            mContext = context;
        }
        public MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }


        @Override
        protected String doInBackground(Void... params) {

            try
            {
                JSONObject bio_info = new JSONObject();
                bio_info.put("userid", mUserId);
                bio_info.put("action", "profile_get");

                String key = BioStats.this.getString(R.string.ENC_KEY);
                String address = BioStats.this.getString(R.string.SERVER_ADDRESS);

                String allData = AESEncryption.encrypt(key, bio_info.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                return  jsonResp.getString("result");
            }
            catch (JSONException jsonEx)
            {
                return  "fail";
            }
            catch (IOException ioEx)
            {
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(final String success) {

            mGetTask = null;
            if(success.equals("fail"))
            {
                showProgress(false);
            }
            else
            {

                 Intent intent = new Intent(BioStats.this, MainActivity.class);
                 intent.putExtra("userid", mUserId);
                 startActivity(intent);

                finish();

            }

        }

        @Override
        protected void onCancelled() {
            mGetTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSendBio extends AsyncTask<Void, Void, String> {

        private final String mUserId;
        private final String mAge, mWeight, mHeight, mGender;
        private final Context mContext;

        UserSendBio(String userid, String age, String weight, String height, String gender, Context context) {
            mUserId = userid;
            mAge = age;
            mWeight = weight;
            mHeight = height;
            mGender = gender;
            mContext = context;
        }
        public MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }


        @Override
        protected String doInBackground(Void... params) {

            try
            {
                JSONObject bio_info = new JSONObject();
                bio_info.put("userid", mUserId);
                bio_info.put("age", mAge);
                bio_info.put("gender", mGender);
                bio_info.put("weight", mWeight);
                bio_info.put("height", mHeight);
                bio_info.put("action", "profile_set");

                String key = BioStats.this.getString(R.string.ENC_KEY);
                String address = BioStats.this.getString(R.string.SERVER_ADDRESS);


                String allData = AESEncryption.encrypt(key, bio_info.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                return  jsonResp.getString("result");
            }
            catch (JSONException jsonEx)
            {
                return  "fail";
            }
            catch (IOException ioEx)
            {
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(final String success) {

            mSendTask = null;
            if(success.equals("fail"))
            {
                showProgress(false);
            }
            else
            {

                Intent intent = new Intent(BioStats.this, MainActivity.class);
                intent.putExtra("userid", mUserId);
                startActivity(intent);

                finish();

            }

        }

        @Override
        protected void onCancelled() {
            mSendTask = null;
            showProgress(false);
        }
    }
}
