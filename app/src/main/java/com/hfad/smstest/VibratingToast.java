package com.hfad.smstest;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class VibratingToast extends Toast {
    public VibratingToast(Context context, CharSequence text, int duration) {
        super(context);

        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);

        super.makeText(context, text, duration).show();
    }
}