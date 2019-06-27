package com.hfad.smstest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.net.Uri;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.security.spec.ECField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    Button contactsButton;
    Button contactsButton2;
    Button ldSenders;
    Button sendSMS;
    public String phone, name ;
    public static String setNum;
    EditText phoneNumber;
    public static EditText phoneNumber2;
    EditText sendList;
    public static EditText sendList2;
    EditText smsText;
    boolean  sendto,sendfrom;
    private static final int RESULT_PICK_CONTACT = 1234;
    public static int ldSendersFlag=0;
    Switch sb;
    TextView sim;
    int simNum;
    SharedPreferences prefs;

    public String makenum(String s)
    {
        String seq1="+91";
        String seq2="91";
        String news="";
        String newss="";
        if(s.contains(seq1)==true)
        {
            news=s.substring(3,s.length());

            for(int i=0;i<news.length();i++)
            {
                if(Character.isDigit(news.charAt(i))==true)
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
                if(Character.isDigit(news.charAt(i))==true)
                {
                    newss=newss+news.charAt(i);
                }
            }
        }
        else if(s.contains(seq1)==false)
        {
            news=s;
            for(int i=0;i<news.length();i++)
            {
                if(Character.isDigit(news.charAt(i))==true)
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
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
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
                phoneNumber.setText(phone);
                sendList.setText("Name : " + name + "\n" + "Phone No. : " + phone);
                sendto=false;
            }
            if(sendfrom==true)
            {
                phoneNumber2.setText(phone);
                sendList2.setText("Name : " + name + "\n" + "Phone No. : " + phone);
               // Log.e("phone",phone );
                setNum=makenum(phone);
               // Log.e("setnum",setNum);
                sendfrom=false;
            }
          /*  Log.e("sendsto contktpickd",Boolean.toString(sendto) );
            Log.e("sendsfrom contktpickd",Boolean.toString(sendfrom) );*/

           /* Log.e("permission", "ContactPicked NAME: " + name);
            Log.e("permission", "ContactPicked NUMBER: " + phone);*/
        } catch (Exception e) {
            e.printStackTrace();
            sendto=false;
            sendfrom=false;
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendfrom=false;
        sendto=false;
        phoneNumber = (EditText) findViewById(R.id.editText);
        phoneNumber2 = (EditText) findViewById(R.id.numToSendFrom);
        sendList=(EditText)findViewById(R.id.sentList);
        sendList2=(EditText)findViewById(R.id.sendFrom);
        sim=(TextView)findViewById(R.id.SIM);
        sb = (Switch) findViewById(R.id.sim_switch);
        sb.setTextOff("SIM 2");
        sb.setTextOn("SIM 1");
        prefs = getSharedPreferences("com.hfad.smstest", 0);
        String simVariable = prefs.getString("simSubID", "SIM 1");
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
              String savedExtra = extras.getString("savedExtra");
              String phno = extras.getString("phonenumber");
              String sndlist = extras.getString("sendlist");
              String simState = extras.getString("SIMSTATUS");
              phoneNumber.setText(phno);
              sendList.setText(sndlist);
              //  Log.e("savedExtra",savedExtra );
              phoneNumber2.setText(savedExtra);
              setNum = savedExtra;
              sendList2.setText("Name : " + savedExtra + "\n" + "Phone No. : null");
              if(simState.equals("SIM 1")) {
                  sb.setChecked(true);
                  sim.setText("SIM 1");
              }
              else if(simState.equals("SIM 2")) {
                  sb.setChecked(false);
                  sim.setText("SIM 2");
              }

              phoneNumber2.requestFocus();
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
                //Log.e("getdata setnum","" + messageText);
                hideKeyboard(MainActivity.this);
                if((phoneNumber.getText()).toString().equals((phoneNumber2.getText()).toString())==false)
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
                }
                //TODO OTP EXTRACTION
               // Toast.makeText(MainActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();

              /*  // If your OTP is six digits number, you may use the below code

                Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(messageText);
                String otp;
                while (matcher.find())
                {
                    otp = matcher.group();
                }

                Toast.makeText(MainActivity.this,"OTP: "+ otp ,Toast.LENGTH_LONG).show();*/

            }
            else
                {
                    Toast.makeText(MainActivity.this, "SENDING TO SAME NUMBER LOOP", Toast.LENGTH_SHORT).show();
                }
            }
        });



       phoneNumber = (EditText) findViewById(R.id.editText);
        phoneNumber2 = (EditText) findViewById(R.id.numToSendFrom);

        sendList=(EditText)findViewById(R.id.sentList);
        sendList2=(EditText)findViewById(R.id.sendFrom);

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
                extras.putString("phonenumber",phoneNumber.getText().toString());
                extras.putString("sendlist",sendList.getText().toString());
                extras.putString("SIMSTATUS",sim.getText().toString());
                myIntent.putExtras(extras);
                ldSendersFlag=1;
                startActivity(myIntent);
                finish();
            }
        });



        smsText = (EditText) findViewById(R.id.editText2);
        sendSMS = (Button) findViewById(R.id.btnSendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
                if((phoneNumber.getText()).toString().equals((phoneNumber2.getText()).toString())==false)
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
        });





    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int readsms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);
        int recsms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS);
        if ((result == PackageManager.PERMISSION_GRANTED)&&(ContactPermissionResult==PackageManager.PERMISSION_GRANTED)&&(readsms == PackageManager.PERMISSION_GRANTED)&&(recsms == PackageManager.PERMISSION_GRANTED)) {
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
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Stop Service? Press Back Again To Confirm.", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


}