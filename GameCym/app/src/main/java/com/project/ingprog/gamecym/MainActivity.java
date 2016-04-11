package com.project.ingprog.gamecym;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String mUserId;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mUserId = new String();
        mUserId = this.getIntent().getStringExtra("userid");


        mGoogleApiClient = GoogleAchievements.getGoogleApiClient(this);
        mGoogleApiClient.connect();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bio:
                modifyBioStats();
                return true;

            case R.id.action_logout:
                logout();
                return true;

            case R.id.action_viewschedule:
                goToScheduleActivity();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void logout()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void modifyBioStats()
    {

        Intent intent = new Intent(MainActivity.this, BioStats.class);
        intent.putExtra("userid", mUserId);
        intent.putExtra("modify", true);
        startActivity(intent);

        finish();
    }

    public void goToScheduleActivity()
    {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        intent.putExtra("userid", mUserId);

        startActivity(intent);

        finish();
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
}
