package com.project.ingprog.gamecym;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivityClass
{
    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private SendUserIdToServer mSendUserIdTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mLoginFormView = (View)this.findViewById(R.id.login_form);
        mProgressView = (View)this.findViewById(R.id.login_progress);

        Utils.DebugLog("Before calling getGoogleApiClient");

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                mSignInClicked = true;
                if(mIsConnectToGoogle)
                    onConnected(null);
                else
                    signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        showProgress(true);

        //Send json to server with email and password
        mSendUserIdTask = new SendUserIdToServer(GoogleAchievements.getUniqueId(), this);
        mSendUserIdTask.execute();
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class SendUserIdToServer extends AsyncTask<Void, Void, String> {

        private final String mUserId;
        private final Context mContext;

        SendUserIdToServer(String userid, Context context) {
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
            try {
                JSONObject credentials = new JSONObject();
                credentials.put("userid", mUserId);
                credentials.put("action", "login");

                String key = mContext.getString(R.string.ENC_KEY);

                JSONObject sending = new JSONObject();
                sending.put("data",AESEncryption.encrypt(key, credentials.toString()) );


                String resp = post(LoginActivity.this.getString(R.string.SERVER_ADDRESS), sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                return jsonResp.getString("result");

            }
            catch (JSONException ex)
            {
                return "fail";
            }
            catch (IOException ex)
            {
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            mSendUserIdTask = null;
            showProgress(false);

            if (success.equals("success")) {


                Intent intent = new Intent(mContext, BioStats.class);
                intent.putExtra("userid", mUserId);
                startActivity(intent);
                finish();

            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mSendUserIdTask = null;
        }
    }
}

