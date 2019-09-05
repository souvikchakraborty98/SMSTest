package com.hfad.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {
        ArrayList<String> setnumm;
        public void getnumber()
        {
            MainActivity getData=new MainActivity();
            /*LinkedList<String> setnumm=new LinkedList<>();
            setnumm=getData.setNum;*/
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

        for(int i=0;i<pdus.length;i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //Log.e("Sender", sender);
            for (String s : setnumm) {
                Log.e("getDataSetNum", "" + setnumm);
                if ((sender.equals(s)) || (sender.equals(s.toLowerCase()))) {

                    String messageBody = smsMessage.getMessageBody();
                    //Log.e("message","" + messageBody);

                    //Pass the message text to interface
                    mListener.messageReceived(messageBody);

                }
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}