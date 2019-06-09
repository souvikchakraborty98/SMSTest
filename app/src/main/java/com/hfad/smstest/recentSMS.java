package com.hfad.smstest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class recentSMS extends AppCompatActivity {
    ArrayList<String> senderList = new ArrayList<String>();
    String temp1;
    String phno;
    String sndlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_sms);
        //int f=0;
        Bundle extras = getIntent().getExtras();
        phno = extras.getString("phonenumber");
        sndlist= extras.getString("sendlist");
        ListView listView ;
        ArrayAdapter<String> arrayAdapter;

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                String senderID= "";

                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    try {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                        //Log.e("messages", msgData);
                        senderID = (msgData.substring((msgData.indexOf("address:") + 8), msgData.indexOf("person:"))).trim();
                        if(Character.isDigit(senderID.charAt(1))==false)
                        senderID = senderID.substring(0,2)+"-"+senderID.substring(2);
                        senderList.add(senderID);
                       /* if (senderID.equals("A$AIRACT"))
                        {
                            f=1;
                        }*/

                        //Log.e("messages sender", senderID);
                    }
                    catch (Exception e)
                    {
                        try{
                            if(msgData.indexOf("address:")>=0) {
                                senderID = (msgData.substring((msgData.indexOf("address:") + 8))).trim();
                                if(Character.isDigit(senderID.charAt(1))==false)
                                senderID = senderID.substring(0,2)+"-"+senderID.substring(2);
                                senderList.add(senderID);
                                //Log.e("messages sender v2", senderID);
                         /*   if (senderID.equals("A$AIRACT"))
                            {
                                f=1;
                            }*/
                            }
                        }
                        catch (Exception e1)
                        {
                            Log.e("still no sender", msgData);
                        }
                    }
                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
       /* if(f==1)
        {
            Log.e("found","it is there");
        }*/
      cursor.close();

        senderList=removeDuplicates(senderList);
        //Log.e("at 12", senderList.get(12));
        listView = (ListView)findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<String>(recentSMS.this, R.layout.contact_listview, R.id.textView, senderList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                temp1=senderList.get(position);
              //  Log.e("sender at pos", temp1);
                Intent myIntent = new Intent(getBaseContext(),   MainActivity.class);
                Bundle extras = new Bundle();
                extras.putString("phonenumber",phno);
                extras.putString("sendlist",sndlist);
                extras.putString("savedExtra",temp1);
                myIntent.putExtras(extras);
                startActivity(myIntent);
                finish();
            }
        });

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

  @Override
    public void onBackPressed()
    {
        Intent myIntent = new Intent(getBaseContext(),   MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("phonenumber",phno);
        extras.putString("sendlist",sndlist);
        extras.putString("savedExtra","");
        myIntent.putExtras(extras);
        startActivity(myIntent);
        finish();

    }
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) {

            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        return newList;
    }
}
