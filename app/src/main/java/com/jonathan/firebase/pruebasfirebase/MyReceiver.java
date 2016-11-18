package com.jonathan.firebase.pruebasfirebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jonathan on 12/11/2016.
 */
public class MyReceiver extends BroadcastReceiver {

    private final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "INTENT RECEIVED");

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Toast.makeText(context, "INTENT RECEIVED by Receiver", Toast.LENGTH_LONG).show();

    }

}