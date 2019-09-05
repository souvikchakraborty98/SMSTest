package com.hfad.smstest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashSet;

import javax.crypto.AEADBadTagException;


public class recentSMS extends AppCompatActivity {
    //LinkedList<String> senderList = new LinkedList<String>();
    ArrayList<DataModel> senderList=new ArrayList<DataModel>();
    ListView listView;
    String simStat,oTpFlag,sndlist,phno;
    DataModel temp1;
    private RelativeLayout mRelativeLayout;
    private CustomAdapter arrayAdapter;
    ActionBar actBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        actBar=getSupportActionBar();
        actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1D1C1C")));
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

    public static ArrayList<DataModel> removeDuplicates(ArrayList<DataModel> list) {
        ArrayList<DataModel> noRepeat = new ArrayList<DataModel>();

        for (DataModel event : list) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (DataModel e : noRepeat) {
                if (e.name.equals(event.name) || (e.equals(event))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);
        }
        return noRepeat;
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

            if (cursor.moveToFirst()) {
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
                            senderList.add(new DataModel(senderID, false));
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
                                    senderList.add(new DataModel(senderID, false));
                                   // Log.e("messages sender v2", senderID);
                         /*   if (senderID.equals("A$AIRACT"))
                            {
                                f=1;
                            }*/
                                }
                            } catch (Exception e1) {
                                Log.e("still no sender", String.valueOf(e1));
                            }
                        }
                    }
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                Log.e("sms", "EOF");
            }
            cursor.close();

            senderList = removeDuplicates(senderList);

            return null;
        }

        protected void onPostExecute(Void param) {
            setContentView(R.layout.loading_circle);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.loadingCircle);
            mRelativeLayout.setVisibility(View.GONE);
            setContentView(R.layout.activity_recent_sms);
            //Log.e("at 12", senderList.get(12));
            listView = (ListView) findViewById(R.id.listview);
            arrayAdapter = new CustomAdapter(senderList, getApplicationContext());
            listView.setAdapter(arrayAdapter);
            /*listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setItemsCanFocus(false);*/

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    temp1 = senderList.get(position);
                    DataModel dataModel= senderList.get(position);
                    dataModel.checked = !dataModel.checked;
                    arrayAdapter.notifyDataSetChanged();
                    //  Log.e("sender at pos", temp1);
                    final ArrayList<String> selectedContacts=new ArrayList<>();
                    final Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                    final Bundle extras = new Bundle();
                    Button doneButton = (Button) findViewById(R.id.doneButton);
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(int i=0;i<senderList.size();i++)
                            {
                                DataModel d=senderList.get(i);
                                if(d.checked) {
                                    selectedContacts.add(d.name);
                                    Log.e("selectedContacts", d.name );
                                }
                            }
                            String[] selectedContactsArray = selectedContacts.toArray(new String[selectedContacts.size()]);
                            extras.putString("phonenumber", phno);
                            extras.putString("sendlist", sndlist);
                            extras.putStringArray("savedExtra", selectedContactsArray);
                            extras.putString("SIMSTATUS", simStat);
                            extras.putString("otpCheck", oTpFlag);
                            myIntent.putExtras(extras);
                            startActivity(myIntent);
                            finish();
                        }
                    });
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
