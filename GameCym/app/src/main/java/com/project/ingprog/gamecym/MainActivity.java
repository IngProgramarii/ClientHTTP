package com.project.ingprog.gamecym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        mEmail = new String();
        mEmail = this.getIntent().getStringExtra("email");
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

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("email", mEmail);
        intent.putExtra("modify", true);
        startActivity(intent);

        finish();
    }


    public void loginOrRegister(View view)
    {
    }

}
