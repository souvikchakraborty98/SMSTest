package com.hfad.smstest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.LinkedList;


public class recentSMS extends AppCompatActivity {
    LinkedList<String> senderList = new LinkedList<String>();
    ListView listView;
    String simStat,oTpFlag,sndlist,phno,temp1;
    private RelativeLayout mRelativeLayout;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = recentSMS.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(recentSMS.this, R.color.stat_colour));
        setContentView(R.layout.loading_circle);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.loadingCircle);
        mRelativeLayout.setVisibility(View.VISIBLE);

        new PrepareData().execute();
    }


    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("phonenumber", phno);
        extras.putString("sendlist", sndlist);
        extras.putString("savedExtra", "null");
        extras.putString("SIMSTATUS",simStat);
        extras.putString("otpCheck",oTpFlag);
        myIntent.putExtras(extras);
        startActivity(myIntent);
        finish();

    }

    public static <T> LinkedList<T> removeDuplicates(LinkedList<T> list) {

        LinkedList<T> newList = new LinkedList<T>();

        for (T element : list) {

            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        return newList;
    }

    public class PrepareData extends AsyncTask<Void, Void, Void>
    {

        protected Void doInBackground(Void... param) {

            //int f=0;
            Bundle extras = getIntent().getExtras();
            try {
                phno = extras.getString("phonenumber");
                sndlist = extras.getString("sendlist");
                simStat = extras.getString("SIMSTATUS");
                oTpFlag = extras.getString("otpCheck");

            } catch (Exception e) {
                Log.e("Error ", "bundle data not found");
            }


            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            if (cursor.moveToFirst()) { //checking the result to prevent exception yo
                do {
                    String msgData = "";
                    String senderID = "";

                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        try {
                            msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                            //                              Log.e("original", senderID);
                            senderID = (msgData.substring((msgData.indexOf("address:") + 8), msgData.indexOf("person:"))).trim();
                            if ((Character.isDigit(senderID.charAt(1)) == false) && (Character.isUpperCase(senderID.charAt(1)) == true)&& (senderID.charAt(2)!='-')) {
                                senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
                            }
                            else if ((Character.isDigit(senderID.charAt(1)))==false && (Character.isLetter(senderID.charAt(1)))==false && (Character.isWhitespace(senderID.charAt(1)))==false && (senderID.charAt(2)!='-'))

                            {
                                senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
                            }
                            senderList.add(senderID);
                       /* if (senderID.equals("A$AIRACT"))
                        {
                            f=1;
                        }*/

                            //Log.e("messages sender", senderID);
                        } catch (Exception e) {
                            try {
                                if (msgData.indexOf("address:") >= 0) {
                                    senderID = (msgData.substring((msgData.indexOf("address:") + 8))).trim();
                                    if ((Character.isDigit(senderID.charAt(1)) == false) && (Character.isUpperCase(senderID.charAt(1)) == true) && (senderID.charAt(2)!='-'))
                                    {
                                        senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
                                    }
                                    else if ((Character.isDigit(senderID.charAt(1)))==false && (Character.isLetter(senderID.charAt(1)))==false && (Character.isWhitespace(senderID.charAt(1)))==false && (senderID.charAt(2)!='-'))
                                    {
                                        senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
                                    }
                                    senderList.add(senderID);
                                   // Log.e("messages sender v2", senderID);
                         /*   if (senderID.equals("A$AIRACT"))
                            {
                                f=1;
                            }*/
                                }
                            } catch (Exception e1) {
                                Log.e("still no sender", msgData);
                            }
                        }
                    }
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                Log.e("sms", "EOF");
            }
       /* if(f==1)
        {
            Log.e("found","it is there");
        }*/
            cursor.close();

            senderList = removeDuplicates(senderList);



           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });*/

            return null;
        }

        protected void onPostExecute(Void param) {
            setContentView(R.layout.loading_circle);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.loadingCircle);
            mRelativeLayout.setVisibility(View.GONE);
            setContentView(R.layout.activity_recent_sms);
            //Log.e("at 12", senderList.get(12));
            listView = (ListView) findViewById(R.id.listview);
            arrayAdapter = new ArrayAdapter<String>(recentSMS.this, R.layout.contact_listview, R.id.textView, senderList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    temp1 = senderList.get(position);
                    //  Log.e("sender at pos", temp1);
                    Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("phonenumber", phno);
                    extras.putString("sendlist", sndlist);
                    extras.putString("savedExtra", temp1);
                    extras.putString("SIMSTATUS",simStat);
                    extras.putString("otpCheck",oTpFlag);
                    myIntent.putExtras(extras);
                    startActivity(myIntent);
                    finish();
                }
            });
            //TODO IMPLEMENT SEARCH IN SENDERLIST
     /*   textBox=(EditText)findViewById(R.id.searchBox);
        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                arrayAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        }


    }
}
