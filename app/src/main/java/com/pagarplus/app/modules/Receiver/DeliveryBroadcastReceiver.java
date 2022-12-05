package com.pagarplus.app.modules.Receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class DeliveryBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "ON RECEIVE BROADCAST", Toast.LENGTH_LONG).show();
        Log.d("ON ", "RECEIVE");
        Bundle bundle = intent.getExtras();
        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        // Create messages for each incoming PDU
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }
        for (SmsMessage msg : sms) {
            Log.e("RECEIVED MSG", ":" + msg.getMessageBody());
            // Verify if the message came from our known sender

        }
    }
}
