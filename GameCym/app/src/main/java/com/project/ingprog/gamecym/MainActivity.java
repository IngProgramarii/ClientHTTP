package com.project.ingprog.gamecym;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivityClass {

    String mUserId;

    private GetDayAvailableSchedule mGetDayScheduleTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mUserId = new String();
        mUserId = this.getIntent().getStringExtra("userid");

        mGetDayScheduleTask = new GetDayAvailableSchedule(mUserId, GetCurrentDay(), MainActivity.this);
        mGetDayScheduleTask.execute((Void) null);
    }

    private String GetCurrentDay()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
                // Current day is Sunday

            case Calendar.MONDAY:
                return "Monday";
                // Current day is Monday

            case Calendar.TUESDAY:
                return "Tuesday";
                // etc.

            case Calendar.WEDNESDAY:
                return "Wednesday";

            case Calendar.THURSDAY:
                return "Thursday";

            case Calendar.FRIDAY:
                return "Friday";

            case Calendar.SATURDAY:
                return "Saturday";

            default:
                return "err getting the day";
        }
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


    public class SendDoneSchedule extends AsyncTask<Void, Void, String> {

        private final Context mContext;
        private final String mUserId;
        private final String mDay;
        private final int mIndex;

        SendDoneSchedule(String userid, String day, int index, Context context) {
            mContext = context;
            mUserId = userid;
            mDay = day;
            mIndex = index;
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
                json.put("index", mIndex);
                json.put("action", "exercise_done");

                String key = MainActivity.this.getString(R.string.ENC_KEY);
                String address = MainActivity.this.getString(R.string.SERVER_ADDRESS);

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

            //TODO: save stuff to player prefs
            //TODO: award completed achievements
            //TODO: reload the page
            if(objects.isEmpty())
            {
                //((TextView)(findViewById(R.id.ScheduleText))).setText("");
                //showProgress(false);
            }
            else
            {
                //((TextView)(findViewById(R.id.ScheduleText))).setText("");
                //showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {
            //showProgress(false);
        }
    }

    public class GetDayAvailableSchedule extends AsyncTask<Void, Void, String> {

        private final Context mContext;
        private final String mUserId;
        private final String mDay;

        GetDayAvailableSchedule(String userid, String day, Context context) {
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
                json.put("action", "schedule_get_available");

                String key = MainActivity.this.getString(R.string.ENC_KEY);
                String address = MainActivity.this.getString(R.string.SERVER_ADDRESS);

                String allData = AESEncryption.encrypt(key, json.toString());
                JSONObject sending = new JSONObject();
                sending.put("data", allData);

                String resp = post(address, sending.toString());
                JSONObject jsonResp = new JSONObject(AESEncryption.decrypt(key, resp));

                if(jsonResp.getString("result").equals("success"))
                {
                    return jsonResp.getJSONArray("schedule").toString();
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

            LinearLayout ll = (LinearLayout)(findViewById(R.id.linearLayout));

            mGetDayScheduleTask = null;
            if(objects.isEmpty())
            {
                TextView tv = new TextView(mContext);
                tv.setText("No exercises for today");
                tv.setSingleLine(false);
                tv.setGravity(Gravity.CENTER);
                ll.addView(tv);

                //showProgress(false);
            }
            else
            {
                try {
                    JSONArray jsonArray = new JSONArray(objects);

                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        String newText = "";

                        JSONObject obj = jsonArray.getJSONObject(i);
                        newText += "Exercise: " + obj.getString("exercise_name") + "\n";
                        newText += "Reps:" + obj.getString("rep") + "\n";

                        if(!obj.getString("comment").isEmpty())
                            newText += "Comment: " + obj.getString("comment") + "\n\n";

                        TextView tv = new TextView(mContext);
                        tv.setText(newText);
                        tv.setSingleLine(false);
                        tv.setGravity(Gravity.CENTER);

                        Button bttn = new Button(mContext);
                        bttn.setText("Mark as Done");
                        bttn.setTag(i);
                        bttn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view)
                                   {
                                       Button bttn = (Button)view;
                                       //showProgress(true)

                                       SendDoneSchedule sds = new SendDoneSchedule(mUserId,
                                               mDay, Integer.parseInt(bttn.getTag().toString()),
                                               mContext);
                                       sds.execute((Void)null);
                                   }

                              }

                        );

                        ll.addView(tv);
                        ll.addView(bttn);
                    }

                    if(jsonArray.length() == 0)
                    {

                        TextView tv = new TextView(mContext);
                        tv.setText("No exercises for today");
                        tv.setSingleLine(false);
                        tv.setGravity(Gravity.CENTER);
                        ll.addView(tv);
                    }

                }
                catch (JSONException jsonEx)
                {

                    TextView tv = new TextView(mContext);
                    tv.setText("No exercises for today");
                    tv.setSingleLine(false);
                    tv.setGravity(Gravity.CENTER);
                    ll.addView(tv);
                }

                //showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {
            mGetDayScheduleTask = null;
            //showProgress(false);
        }
    }


}
