package com.hfad.smstest;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
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
    String phone, name;
    EditText textBox;
    EditText phoneNumber;
    private static final int RESULT_PICK_CONTACT = 1234;



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
            Log.e("permission", "Failed to pick contact");
        }
    }
    public void clickPickContacts(View v)
    {
        Intent cp = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(cp, RESULT_PICK_CONTACT);
    }
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber.setText(phone);
           /* Log.e("permission", "ContactPicked NAME: " + name);
            Log.e("permission", "ContactPicked NUMBER: " + phone);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = (EditText) findViewById(R.id.editText);

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

                clickPickContacts(v);


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