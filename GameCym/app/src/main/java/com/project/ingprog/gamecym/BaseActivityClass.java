package com.project.ingprog.gamecym;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class BaseActivityClass extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    protected String mUserId;
    protected GoogleApiClient mGoogleApiClient;

    protected static final int REQUEST_CODE_RESOLUTION = 1;

    protected boolean mResolvingConnectionFailure = false;
    protected boolean mAutoStartSignInFlow = true;
    protected boolean mSignInClicked = false;
    protected boolean mIsConnectToGoogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = GoogleAchievements.getGoogleApiClient(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        mAutoStartSignInFlow = false;
        mIsConnectToGoogle = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

        // Attempt to reconnect
        mIsConnectToGoogle = false;
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
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
}
