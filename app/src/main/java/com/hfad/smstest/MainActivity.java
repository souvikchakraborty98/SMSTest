package com.hfad.smstest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.net.Uri;
import android.content.Intent;
import android.widget.ListView;
import android.widget.EditText;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Button sendSMS;
    ListView listView ;
    ArrayList<String> contactsArray ;
    ArrayAdapter<String> arrayAdapter ;
    Button contactsButton;
    Cursor cursor ;
    String name, contactNumber,temp="";
    EditText textBox;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText phoneNumber = (EditText) findViewById(R.id.editText);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }

        listView = (ListView)findViewById(R.id.listview);
        contactsArray = new ArrayList<String>();
        contactsButton = (Button)findViewById(R.id.ldContacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);

                AddContactstoArray();

//Initialize ArrayAdapter and declare that we’re converting Strings into Views//

                arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,

//Specify the XML file where you’ve defined the layout for each item//

                        R.layout.contact_listview, R.id.textView,

//The array of data//

                        contactsArray
                );

//Set the Adapter to the ListView, using setAdapter()//

                listView.setAdapter(arrayAdapter);

            }

        });
        listView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String temp1=contactsArray.get(position);
                String temp2=temp1.substring(temp1.indexOf(':')+1,temp1.length());
                phoneNumber.setText(temp2);
            }
        });
        textBox=(EditText)findViewById(R.id.searchBox);
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
        });



        final EditText smsText = (EditText) findViewById(R.id.editText2);
        sendSMS = (Button) findViewById(R.id.btnSendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
                String sms = smsText.getText().toString();
                String phoneNum = phoneNumber.getText().toString();
                if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
                    if(checkPermission()) {

//Get the default SmsManager//
                        if((phoneNum.length()>=10)&&(phoneNum.length()<=16))
                        {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNum, null, sms, null, null);
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
            }
        });
    }
    public void AddContactstoArray(){

//Query the phone number table using the URI stored in CONTENT_URI/

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

//Get the display name for each contact//

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

//Get the phone number for each contact//

            contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//Add each display name and phone number to the Array//

            if(temp.equals(contactNumber)==false)
            {
                contactsArray.add(name + " " + ":" + contactNumber);
            }
            temp=contactNumber;
        }

        cursor.close();

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        if ((result == PackageManager.PERMISSION_GRANTED)&&(ContactPermissionResult==PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&  grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                    Button sendSMS = (Button) findViewById(R.id.btnSendSMS);
                    sendSMS.setEnabled(false);
                    contactsButton.setEnabled(false);

                }
                break;
        }
    }
    private static long back_pressed;

    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Tap once more and we will be out of your hair!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


}