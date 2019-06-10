package com.hfad.smstest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class recentSMS extends AppCompatActivity {
    ArrayList<String> senderList = new ArrayList<String>();
    ListView listView;
    String temp1;
    String phno;
    String sndlist;
    private RelativeLayout mRelativeLayout;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        extras.putString("savedExtra", "");
        myIntent.putExtras(extras);
        startActivity(myIntent);
        finish();

    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        ArrayList<T> newList = new ArrayList<T>();

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
                            //Log.e("messages", msgData);
                            senderID = (msgData.substring((msgData.indexOf("address:") + 8), msgData.indexOf("person:"))).trim();
                            if ((Character.isDigit(senderID.charAt(1)) == false) && (Character.isUpperCase(senderID.charAt(1)) == true))
                                senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
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
                                    if ((Character.isDigit(senderID.charAt(1)) == false) && (Character.isUpperCase(senderID.charAt(1)) == true))
                                        senderID = senderID.substring(0, 2) + "-" + senderID.substring(2);
                                    senderList.add(senderID);
                                    //Log.e("messages sender v2", senderID);
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
            // THIS WILL DISMISS CIRCLE
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
