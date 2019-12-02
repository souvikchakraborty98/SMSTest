package com.hfad.smstest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.net.Uri;
import android.content.Intent;
import android.widget.CompoundButton;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    Button sendSMS,clearSendFromList,clearSendToList,ldSenders,contactsButton2,contactsButton;
    public String phone, name;
    public static String senderSelect;
    public static ArrayList<String> setNum=new ArrayList<>();
    public static ArrayList<String> nameList=new ArrayList<>();
    public static ArrayList<String> nameListToSend=new ArrayList<>();
    public static ArrayList<String> setNumToSend=new ArrayList<>();
    TextView smsText,sendList2,sendList,sentFrom,toSend,info,info2;
    boolean  sendto,sendfrom;
    private static final int RESULT_PICK_CONTACT = 1234;
    public static int ldSendersFlag=0;
    Switch sb,sc;
    TextView sim;
    static int simNum,otp_flag;
    SharedPreferences prefs;

    public String makenum(String s)
    {
        String seq1="+91";
        String seq2="91";
        String news;
        String newss="";
        if(s.contains(seq1))
        {
            news=s.substring(3,s.length());

            for(int i=0;i<news.length();i++)
            {
                if(Character.isDigit(news.charAt(i)))
                {
                    newss=newss+news.charAt(i);
                }
            }
        }
        else if(s.indexOf(seq2)==0)
        {
            news=s.substring(2,s.length());

            for(int i=0;i<news.length();i++)
            {
                if(Character.isDigit(news.charAt(i)))
                {
                    newss=newss+news.charAt(i);
                }
            }
        }
        else if(!s.contains(seq1))
        {
            news=s;
            for(int i=0;i<news.length();i++)
            {
                if(Character.isDigit(news.charAt(i)))
                {
                    newss=newss+news.charAt(i);
                }
            }
        }
        newss=seq1+newss;
       // Log.e("methodmakenum", newss );
        return newss;
    }

    public static void hideKeyboard(Activity activity) {                       //TO HIDE KEYBOARD DURING FOCUS CHANGE
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            sendto=false;
           sendfrom=false;
           // Log.e("sendsto onACT",String.valueOf(sendto) );
          // Log.e("sendsfrom onACT",String.valueOf(sendfrom) );
            Log.e("permission", "Failed to pick contact");

        }
    }

    public void clickPickContacts(View v)
    {
        Intent cp = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(cp, RESULT_PICK_CONTACT);
    }
    private void contactPicked(Intent data) {
        Cursor cursor;
        TinyDB tinydb = new TinyDB(getApplicationContext());
        try {
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                cursor.moveToFirst();
            }
            catch(Exception e)
            {
                sendto=false;
                sendfrom=false;
                Log.e("permission","e");
            }
            phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if(sendto==true) {
                if(!nameListToSend.contains(name))
                    nameListToSend.add(name);
                else
                    nameListToSend.remove(name);
                if(!setNumToSend.contains(makenum(phone)))
                    setNumToSend.add(makenum(phone));
                else
                    setNumToSend.remove(makenum(phone));
                try {
                    tinydb.putListString("setNumToSend", setNumToSend);
                    tinydb.putListString("nameListToSend", nameListToSend);
                }
                catch (Exception e)
                {
                    Log.e("contactPicked: ", e.toString());
                }

                if(setNumToSend.size()!=0)
                {
                    sendList.setText("Name[s] : " + nameListToSend + "\n\n" + "Phone No[s]. : " + setNumToSend);
                }
                else
                {
                    sendList.setText("");
                }
                sendto=false;
            }
            if(sendfrom==true)
            {
               // Log.e("phone",phone );
                if(!setNum.contains(makenum(phone)))
                    setNum.add(makenum(phone));
                else
                    setNum.remove(makenum(phone));

                if(!nameList.contains(name))
                    nameList.add(name);
                else
                    nameList.remove(name);

                setNum.removeAll(Collections.singleton(null));
                setNum.removeAll(Collections.singleton(""));
                nameList.removeAll(Collections.singleton(null));
                nameList.removeAll(Collections.singleton(""));

                tinydb.putListString("setNum", setNum);
                tinydb.putListString("nameList",nameList);

                if(setNum.size()!=0)
                {
                    sendList2.setText("Name[s] : " + nameList + "\n\n" + "Phone No[s]. : " + setNum);
                }
                else
                {
                    sendList2.setText("");
                }
               // Log.e("setnum",setNum);
                sendfrom=false;
            }
          /*Log.e("sendsto contktpickd",Boolean.toString(sendto) );
            Log.e("sendsfrom contktpickd",Boolean.toString(sendfrom) );
            Log.e("permission", "ContactPicked NAME: " + name);
            Log.e("permission", "ContactPicked NUMBER: " + phone);*/
        } catch (Exception e) {
            e.printStackTrace();
            sendto=false;
            sendfrom=false;
        }
    }
    ActionBar actBar;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendList=(TextView)findViewById(R.id.sentList);
        sentFrom=(TextView)findViewById(R.id.sentFrom);
        clearSendFromList=(Button)findViewById(R.id.clearSendFromList);
       // clearSendFromList.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clear_custom, 0, 0, 0);
        clearSendToList=(Button)findViewById(R.id.clearSendToList);
       // clearSendToList.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clear_custom, 0, 0, 0);
        Intent startBgSMS = new Intent(MainActivity.this, SMSBackgroundService.class);
        stopService(startBgSMS);
        final TinyDB tinydb = new TinyDB(getApplicationContext());
        setNum=tinydb.getListString("setNum");
        nameList=tinydb.getListString("nameList");
        setNumToSend=tinydb.getListString("setNumToSend");
        nameListToSend=tinydb.getListString("nameListToSend");
        sendList2=(TextView)findViewById(R.id.sendFrom);
        smsText = (TextView) findViewById(R.id.editText2);
        try {
            if(setNumToSend.size()!=0)
            {
                sendList.setText("Name[s] : " + nameListToSend + "\n\n" + "Phone No[s]. : " + setNumToSend);
            }
        }
        catch (Exception e) {
            Log.e("setNumToSend", e.toString() );
        }
        try {
            if(setNum.size()!=0)
            {
                sendList2.setText("Name[s] : " + nameList + "\n\n" + "Phone No[s]. : " + setNum);
            }
        }
        catch (Exception e) {
            Log.e("setNum", e.toString());
        }

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getSupportActionBar().hide();
       // getSupportActionBar().setDisplayShowTitleEnabled(false);
        actBar=getSupportActionBar();
        actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1D1C1C")));
        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.stat_colour));

        sendfrom=false;
        sendto=false;

        clearSendToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                final AlertDialog.Builder diagClear = new AlertDialog.Builder(MainActivity.this);
                diagClear.setTitle("Confirmation");
                diagClear.setMessage("Clear Send To List ? Press OK to clear.");
                diagClear.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setNumToSend.clear();
                        nameListToSend.clear();
                        tinydb.putListString("setNumToSend", setNumToSend);
                        tinydb.putListString("nameListToSend",nameListToSend);
                        sendList.setText("");
                    }
                });
                diagClear.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Toast.makeText(MainActivity.this, "List not cleared.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = diagClear.create();
                alertDialog.show();
            }
        });

        sendList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Toast.makeText(MainActivity.this,"Press \"Load Contacts\" above" ,Toast.LENGTH_LONG).show();
                Toast abvKeyBrd=Toast.makeText(MainActivity.this,"Press \"Load Contacts\" below" ,Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) abvKeyBrd.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                abvKeyBrd.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 200);
                abvKeyBrd.show();
                ((Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);

            }

        });



        sendList2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast abvKeyBrd=Toast.makeText(MainActivity.this,"Press \"Load Contacts\" or \"Recent Senders\" below" ,Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) abvKeyBrd.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                abvKeyBrd.show();
                ((Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);

            }

        });
        toSend=(TextView)findViewById(R.id.toSend);
        toSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast abvKeyBrd=Toast.makeText(MainActivity.this,"Press \"Load Contacts\" below" ,Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) abvKeyBrd.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                abvKeyBrd.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 200);
                abvKeyBrd.show();
                ((Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);

            }

        });
        smsText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast abvKeyBrd=Toast.makeText(MainActivity.this,"Will show the intercepted message" ,Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) abvKeyBrd.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                abvKeyBrd.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 200);
                abvKeyBrd.show();
                ((Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);

            }

        });

        sentFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast abvKeyBrd=Toast.makeText(MainActivity.this,"Press \"Load Contacts\" or \"Recent Senders\" below" ,Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) abvKeyBrd.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                abvKeyBrd.show();
                ((Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
            }

        });
        sim=(TextView)findViewById(R.id.SIM);
        info=(TextView)findViewById((R.id.infoBar));
        info2=(TextView)findViewById((R.id.infoBar2));
        info.setText("To remove contacts selected by \"Load Contacts\" select them again!");
        info.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_custom, 0, 0, 0);
        info2.setText("To remove contacts selected by \"Load Contacts\" select them again!");
        info2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_custom, 0, 0, 0);
        sb = (Switch) findViewById(R.id.sim_switch);
        sb.setTextOff("SIM 2");
        sb.setTextOn("SIM 1");
        sc=(Switch) findViewById(R.id.otp_switch);
        prefs = getSharedPreferences("com.hfad.smstest", 0);
        final String simVariable = prefs.getString("simSubID", "SIM 1");
        String checkOTP= prefs.getString("checkForOtp","0");
        Log.e("sim id", simVariable);

        prefs = getSharedPreferences("com.hfad.smstest", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        if(ldSendersFlag!=1)
        {
            if(simVariable.equals("SIM 1"))
            {
                sb.setChecked(true);
                sim.setText(simVariable);
            }
            else if(simVariable.equals("SIM 2"))
            {
                sb.setChecked(false);
                sim.setText(simVariable);
            }

            if(checkOTP.equals("1"))
            {
                sc.setChecked(true);
                otp_flag=1;
            }
            else if(checkOTP.equals("0"))
            {
                sc.setChecked(false);
                otp_flag=0;
            }
            final AlertDialog.Builder diag = new AlertDialog.Builder(MainActivity.this);
            diag.setTitle("WARNING !!!");
            diag.setMessage("PLEASE DO NOT REMOVE APP FROM \"RECENT APPS\" !!!\nDUE TO SYSTEM LIMITATIONS, THIS APP CANNOT WORK WHEN REMOVED FROM RECENT APPLICATIONS.");
            diag.setPositiveButton(android.R.string.ok, null);
            AlertDialog alertDialog = diag.create();
            alertDialog.show();

        }

        sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sim.setText("SIM 1");
                    editor.putString("simSubID", "SIM 1");
                    editor.commit();
                } else {
                    sim.setText("SIM 2");
                    editor.putString("simSubID", "SIM 2");
                    editor.commit();
                }
            }
        });
        sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    otp_flag=1;
                    editor.putString("checkForOtp", "1");
                    editor.commit();
                } else {
                    otp_flag=0;
                    editor.putString("checkForOtp", "0");
                    editor.commit();
                }
            }
        });
        ScrollView layout = (ScrollView) findViewById(R.id.scrollLayout);
      layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(MainActivity.this);
                return false;
            }
        });
      if(ldSendersFlag==1) {
          try {
              Bundle extras = getIntent().getExtras();
              String[] savedExtra = extras.getStringArray("savedExtra");
              /*String phno = extras.getString("phonenumber");*/
              String sndlist = extras.getString("sendlist");
              String simState = extras.getString("SIMSTATUS");
              String OTPFLAG = extras.getString("otpCheck");
             /* phoneNumber.setText(phno);*/
              if(simState.equals("SIM 1")) {
                  sb.setChecked(true);
                  sim.setText("SIM 1");
              }
              else if(simState.equals("SIM 2")) {
                  sb.setChecked(false);
                  sim.setText("SIM 2");
              }

              if(OTPFLAG.equals("true"))
              {
                  sc.setChecked(true);
                  otp_flag=1;
              }
              else if(OTPFLAG.equals("false"))
              {
                  sc.setChecked(false);
                  otp_flag=0;
              }
              sendList.setText(sndlist);
              //  Log.e("savedExtra",savedExtra );
              if(setNum.size()==0) {
                  sendList2.setText("");
                  setNum.add("");
              }
              else {
                  for(int i=0;i<savedExtra.length;i++)
                  {
                      if(!setNum.contains(savedExtra[i]))
                      setNum.add(savedExtra[i]);
                  }
                  for(int i=0;i<savedExtra.length;i++)
                  {
                      if(!nameList.contains((savedExtra[i])))
                      nameList.add(savedExtra[i]);
                  }
                  setNum.removeAll(Collections.singleton(null));
                  setNum.removeAll(Collections.singleton(""));
                  nameList.removeAll(Collections.singleton(null));
                  nameList.removeAll(Collections.singleton(""));
                  tinydb.putListString("setNum", setNum);
                  tinydb.putListString("nameList",nameList);
                  sendList2.setText("Name[s] : " + nameList + "\n\n" + "Phone No[s]. : "+setNum);

              }
             // phoneNumber2.requestFocus();
              ldSendersFlag = 0;
          }
          catch (Exception e)
          {
              ldSendersFlag = 0;
          }
      }

      SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                smsText.setText(messageText);
                String sms = "";
                int flagOTP = 0;
                //Log.e("getdata setnum","" + messageText);
                hideKeyboard(MainActivity.this);
                if (otp_flag == 1) {
                    String OTP_REGEX = "[0-9]{1,6}";
                    Pattern pattern = Pattern.compile(OTP_REGEX);
                    Matcher matcher = pattern.matcher(messageText);
                    String otp = "";
                    while (matcher.find()) {
                        otp = matcher.group();
                        sms = "Your OTP is : " + otp;
                        flagOTP = 1;

                    }
                }

                //  Toast.makeText(MainActivity.this,"OTP: "+ otp ,Toast.LENGTH_LONG).show();

                if (flagOTP != 1) {
                    sms = smsText.getText().toString();
                    flagOTP = 0;
                }


                String simSub = sim.getText().toString();
                if (simSub.equals("SIM 1"))
                    simNum = 0;
                else if (simSub.equals("SIM 2"))
                    simNum = 1;
                for (int i = 0; i < setNumToSend.size(); i++) {
                    if (!senderSelect.equals(setNumToSend.get(i))) {
                        if (!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(setNumToSend.get(i))) {
                            if (checkPermission()) {

                                if ((setNumToSend.get(i).length() >= 10) && (setNumToSend.get(i).length() <= 17)) {
                                    try {
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.getSmsManagerForSubscriptionId(simNum).sendTextMessage(setNumToSend.get(i), null, sms, null, null);
                                        Toast.makeText(MainActivity.this, "SMS(s) Sent", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this, "SMS NOT SENT. TRY AGAIN.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "DOMESTIC CALLS ONLY", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // Toast.makeText(MainActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "SENDING TO SAME NUMBER LOOP", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            }
        );

        sendList=(TextView) findViewById(R.id.sentList);
        sendList2=(TextView)findViewById(R.id.sendFrom);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }

        contactsButton = (Button)findViewById(R.id.ldContacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                sendto=true;
                clickPickContacts(v);


            }
        });

        contactsButton2= (Button) findViewById(R.id.ldContacts2);
        contactsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                sendfrom=true;
                clickPickContacts(v);


            }
        });

        ldSenders = (Button)findViewById(R.id.getSender);
        ldSenders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                Intent myIntent = new Intent(getBaseContext(),   recentSMS.class);
                Bundle extras = new Bundle();
               /* extras.putString("phonenumber",phoneNumber.getText().toString());*/
                extras.putString("sendlist",sendList.getText().toString());
                extras.putString("SIMSTATUS",sim.getText().toString());
                extras.putString("otpCheck",Boolean.toString(sc.isChecked()));
                myIntent.putExtras(extras);
                ldSendersFlag=1;
                startActivity(myIntent);
                finish();
            }
        });

        clearSendFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                final AlertDialog.Builder diagClear = new AlertDialog.Builder(MainActivity.this);
                diagClear.setTitle("Confirmation");
                diagClear.setMessage("Clear Send From List ? Press OK to clear.");
                diagClear.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setNum.clear();
                        nameList.clear();
                        tinydb.putListString("setNum", setNum);
                        tinydb.putListString("nameList",nameList);
                        sendList2.setText("");
                    }
                });
                diagClear.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Toast.makeText(MainActivity.this, "List not cleared.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = diagClear.create();
                alertDialog.show();
            }
        });



        /*
        sendSMS = (Button) findViewById(R.id.btnSendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
                if(!((phoneNumber.getText()).toString().equals((phoneNumber2.getText()).toString())))
                {
                String sms = smsText.getText().toString();
                String phoneNum = phoneNumber.getText().toString();
                    String simSub=sim.getText().toString();
                    if(simSub.equals("SIM 1"))
                        simNum=0;
                    else if(simSub.equals("SIM 2"))
                        simNum=1;
                if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
                    if(checkPermission()) {

                        if((phoneNum.length()>=10)&&(phoneNum.length()<=17))
                        {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            //int dip=smsManager.getSubscriptionId();
                            //Log.e("sub id",Integer.toString(dip));
                            smsManager.getSmsManagerForSubscriptionId(simNum).sendTextMessage(phoneNum, null, sms, null, null);
                            Toast.makeText(MainActivity.this, "SMS Sent", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this, "SMS NOT SENT. TRY AGAIN.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        {
                            Toast.makeText(MainActivity.this, "DOMESTIC CALLS ONLY", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }}
                else
                {
                    Toast.makeText(MainActivity.this, "SENDING TO SAME NUMBER LOOP", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.abouts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final TinyDB tinydb=new TinyDB(getApplicationContext());
        switch (item.getItemId()) {
            case R.id.item1:
                final AlertDialog.Builder diag = new AlertDialog.Builder(MainActivity.this);
                diag.setTitle("Confirmation");
                diag.setMessage("Refresh App?");
                diag.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        setNumToSend.clear();
                        setNum.clear();
                        nameListToSend.clear();
                        nameList.clear();
                        tinydb.putListString("setNum", setNum);
                        tinydb.putListString("nameList",nameList);
                        tinydb.putListString("setNumToSend", setNumToSend);
                        tinydb.putListString("nameListToSend",nameListToSend);

                        finish();
                        startActivity(intent);
                    }
                    });
                diag.setNegativeButton(android.R.string.no,null);
                AlertDialog alertDialog = diag.create();
                alertDialog.show();
                return true;
            case R.id.item2:
                final SpannableString s = new SpannableString(MainActivity.this.getText(R.string.dialog_message));
                Linkify.addLinks(s, Linkify.ALL);

                final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton(android.R.string.yes, null)
                        .setMessage( s )
                        .setTitle("About")
                        .create();

                d.show();
                ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance()); //only for URL handling
                return true;
            case R.id.item3:
                Intent newIntent = new Intent(getBaseContext(),   settings.class);
                startActivity(newIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int readsms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);
        int recsms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS);
        int vibrater=ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.VIBRATE);
        if ((result == PackageManager.PERMISSION_GRANTED)&&(ContactPermissionResult==PackageManager.PERMISSION_GRANTED)&&(readsms == PackageManager.PERMISSION_GRANTED)&&(recsms == PackageManager.PERMISSION_GRANTED)&& (vibrater==PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
    }
}

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&  grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                    sendSMS.setEnabled(false);
                    contactsButton.setEnabled(false);
                    contactsButton2.setEnabled(false);
                    ldSenders.setEnabled(false);

                }
                break;
        }
    }
    private static long back_pressed;

    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            final AlertDialog.Builder diag = new AlertDialog.Builder(MainActivity.this);
            diag.setTitle("WARNING !!!");
            diag.setMessage("PLEASE DO NOT REMOVE APP FROM \"RECENT APPS\" !!!\nDUE TO SYSTEM LIMITATIONS, THIS APP CANNOT WORK WHEN REMOVED FROM RECENT APPLICATIONS. PRESS OK TO EXIT.");
            diag.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent startBgSMS = new Intent(MainActivity.this, SMSBackgroundService.class);
                    startService(startBgSMS);
                    MainActivity.super.onBackPressed();

                }
            });
            AlertDialog alertDialog = diag.create();
            alertDialog.show();
        }
        else
            Toast.makeText(getBaseContext(), "Press Back Again To Confirm.", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

}