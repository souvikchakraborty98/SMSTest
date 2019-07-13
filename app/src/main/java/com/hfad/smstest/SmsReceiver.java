package com.hfad.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
        String setnumm;
        public void getnumber()
        {
            MainActivity getData=new MainActivity();
            setnumm=getData.setNum;
           // Log.e("getdatasetnum our for","" + setnumm);
        }
    //interface
    private static SmsListener mListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        getnumber();
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            Log.e("Sender", sender);
            //Check the sender to filter messages which we require to read

            Log.e("getdata setnum","" + setnumm);
            //if ((sender.equals("+917550167668"))||(sender.equals("+919830093055"))||(sender.equals("+918335962289"))||(sender.equals("+919830519860")))
            if ((sender.equals(setnumm)))
            {

                String messageBody = smsMessage.getMessageBody();
                //Log.e("message","" + messageBody);

                //Pass the message text to interface
                mListener.messageReceived(messageBody);

            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}