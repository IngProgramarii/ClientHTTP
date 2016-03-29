package com.project.ingprog.gamecym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mEmail = new String();
        mEmail = this.getIntent().getStringExtra("email");
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void logout()
    {
        String appPrefName = MainActivity.this.getString(R.string.pref_file_name);
        SharedPreferences prefs = getSharedPreferences(appPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(MainActivity.this.getString(R.string.pref_username_key));
        editor.remove(MainActivity.this.getString(R.string.pref_password_key));

        editor.commit();


        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void modifyBioStats()
    {

        Intent intent = new Intent(MainActivity.this, BioStats.class);
        intent.putExtra("email", mEmail);
        intent.putExtra("modify", true);
        startActivity(intent);

        finish();
    }


    public void loginOrRegister(View view)
    {
    }

}
