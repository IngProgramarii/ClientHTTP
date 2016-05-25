package com.project.ingprog.gamecym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends BaseActivityClass {

    String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mUserId = new String();
        mUserId = this.getIntent().getStringExtra("userid");
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

}
