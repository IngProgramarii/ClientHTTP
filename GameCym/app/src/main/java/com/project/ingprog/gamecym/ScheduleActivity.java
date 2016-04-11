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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String mUserId;
    private GoogleApiClient mGoogleApiClient;

    protected static final int REQUEST_CODE_RESOLUTION = 1;

    View mProgressView, mScheduleView;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private boolean mIsConnectToGoogle = false;

    GetExercisesAvailable mGetExercisesTask = null;
    GetDaySchedule mGetDayScheduleTask = null;
    SendDeleteSchedule mDeleteScheduleTask = null;

    private String[] days;

    ArrayList<String> allExercises;

    Spinner daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressView = findViewById(R.id.schedule_progress);
        mScheduleView = findViewById(R.id.schedule_view);

        days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allExercises = null;

        mUserId = new String();
        mUserId = this.getIntent().getStringExtra("userid");

        mGoogleApiClient = GoogleAchievements.getGoogleApiClient(this);
        mGoogleApiClient.connect();

        daySpinner = (Spinner)findViewById(R.id.spinner_day);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProgress(true);
                mGetDayScheduleTask = new GetDaySchedule(mUserId, days[position], ScheduleActivity.this);
                mGetDayScheduleTask.execute((Void) null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showProgress(true);

        mGetExercisesTask = new GetExercisesAvailable(ScheduleActivity.this);
        mGetExercisesTask.execute((Void) null);

        initDaySpinner();
    }

    public void DeleteScheduleAction(View view)
    {
        mDeleteScheduleTask = new SendDeleteSchedule(mUserId, days[daySpinner.getSelectedItemPosition()], ScheduleActivity.this);
        mDeleteScheduleTask.execute((Void) null);
    }

    public void EditScheduleAction(View view)
    {

    }


    private  void initDaySpinner()
    {
        Spinner daySpinner = (Spinner)findViewById(R.id.spinner_day);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, days);
        daySpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        intent.putExtra("userid", mUserId);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                onBackPressed();

                return(true);
        }

        return(super.onOptionsItemSelected(item));
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

            mScheduleView.setVisibility(show ? View.GONE : View.VISIBLE);
            mScheduleView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mScheduleView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mScheduleView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        mAutoStartSignInFlow = false;
        mIsConnectToGoogle = true;


        //biostats achievement
        GoogleAchievements.unlockAchievement(GoogleAchievements.Achievements.TEST2);
    }

    @Override
    public void onConnectionSuspended(int i) {

        // Attempt to reconnect
        mIsConnectToGoogle = false;
        mGoogleApiClient.connect();
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

    }

    public class GetExercisesAvailable extends AsyncTask<Void, Void, String> {

        private final Context mContext;

        GetExercisesAvailable(Context context) {
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
                JSONObject json = new JSONObject();
                json.put("action", "exercises_get");

                String key = ScheduleActivity.this.getString(R.string.ENC_KEY);
                String address = ScheduleActivity.this.getString(R.string.SERVER_ADDRESS);

                String allData = AESEncryption.encrypt(key, json.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));


                return  jsonResp.getString("exercises");
            }
            catch (JSONException jsonEx)
            {
                return  "" ;
            }
            catch (IOException ioEx)
            {
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String exercises) {

            mGetExercisesTask = null;
            if(exercises.isEmpty())
            {
                showProgress(false);
            }
            else
            {
                allExercises = new ArrayList<String>();
                allExercises.addAll(Arrays.asList(exercises.split(";")));
                showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {
            mGetExercisesTask = null;
            showProgress(false);
        }
    }


    public class GetDaySchedule extends AsyncTask<Void, Void, String> {

        private final Context mContext;
        private final String mUserId;
        private final String mDay;

        GetDaySchedule(String userid, String day, Context context) {
            mContext = context;
            mUserId = userid;
            mDay = day;
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
                JSONObject json = new JSONObject();
                json.put("userid", mUserId);
                json.put("day", mDay);
                json.put("action", "schedule_get");

                String key = ScheduleActivity.this.getString(R.string.ENC_KEY);
                String address = ScheduleActivity.this.getString(R.string.SERVER_ADDRESS);

                String allData = AESEncryption.encrypt(key, json.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                if(jsonResp.getString("result").equals("success"))
                {
                    return jsonResp.getJSONObject("schedule").toString();
                }
                else
                {
                    return "";
                }
            }
            catch (JSONException jsonEx)
            {
                return  "" ;
            }
            catch (IOException ioEx)
            {
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String objects) {

            mGetDayScheduleTask = null;
            if(objects.isEmpty())
            {
                ((TextView)(findViewById(R.id.ScheduleText))).setText("");
                showProgress(false);
            }
            else
            {
                String newText = "";
                try {
                    JSONArray jsonArray = new JSONArray(objects);

                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        newText += "Exercise: " + obj.getString("exercise") + "\n";
                        newText += "Reps:" + obj.getString("reps") + "\n";
                        newText += "Comment: " + obj.getString("comment") + "\n\n";
                    }

                    ((TextView)(findViewById(R.id.ScheduleText))).setText(newText);

                }
                catch (JSONException jsonEx)
                {
                    ((TextView)(findViewById(R.id.ScheduleText))).setText("");
                }

                showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {
            mGetDayScheduleTask = null;
            showProgress(false);
        }
    }

    public class SendDeleteSchedule extends AsyncTask<Void, Void, String> {

        private final Context mContext;
        private final String mUserId;
        private final String mDay;

        SendDeleteSchedule(String userid, String day, Context context) {
            mContext = context;
            mUserId = userid;
            mDay = day;
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
                JSONObject json = new JSONObject();
                json.put("userid", mUserId);
                json.put("day", mDay);
                json.put("action", "schedule_delete");

                String key = ScheduleActivity.this.getString(R.string.ENC_KEY);
                String address = ScheduleActivity.this.getString(R.string.SERVER_ADDRESS);

                String allData = AESEncryption.encrypt(key, json.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                return jsonResp.getString("success");

            }
            catch (JSONException jsonEx)
            {
                return  "fail" ;
            }
            catch (IOException ioEx)
            {
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(final String objects) {

            mDeleteScheduleTask = null;
            if(objects.isEmpty())
            {
                ((TextView)(findViewById(R.id.ScheduleText))).setText("");
                showProgress(false);
            }
            else
            {
                ((TextView)(findViewById(R.id.ScheduleText))).setText("");
                showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {
            mDeleteScheduleTask = null;
            showProgress(false);
        }
    }

}
