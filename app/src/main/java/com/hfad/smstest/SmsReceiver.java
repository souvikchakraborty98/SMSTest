package com.hfad.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            Log.e("Permission", sender);
            //Check the sender to filter messages which we require to read

            if (sender.equals("755-016-7668")|| sender.equals("+91 75501 67668")||sender.equals("7550167668")||sender.equals("+91 75501 67668")||sender.equals("+91 75 5016 7668")||(sender.equals("+917550167668"))||(sender.equals("+919830093055"))||(sender.equals("+918335962289")))
            {

                String messageBody = smsMessage.getMessageBody();

                //Pass the message text to interface
                mListener.messageReceived(messageBody);

            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}