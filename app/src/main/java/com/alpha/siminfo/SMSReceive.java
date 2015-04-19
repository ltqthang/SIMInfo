package com.alpha.siminfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by I Love Coding on 1/7/2015.
 */
public class SMSReceive extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME ="pdus";
    private static final String TAG = "SMSReceive";
    private final String[] viettelPromotions = {"CT1", "CT6", "CT16", "CT31", "CT32", "CT52", "CT53", "CT54", "CT55", "CT56",
            "CT57", "CT58", "CT59", "CT60"};
    private final String[] mobifonePromotions = {"KB", "CM100", "U5", "T10", "Y5", "Y10", "T29", "T199", "T699", "T59",
            "101", "T30", "CBHT"};

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] smsExtra = (Object[]) bundle.get(SMS_EXTRA_NAME);
            sharedPreferences =
                    context.getSharedPreferences(MainActivity.networkOperatorName, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putBoolean("promotionReceived", true);

            for (int i = 0; i < smsExtra.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                String body = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                if (address.equals("266")) {
                    editor.putBoolean("CT100", true);
                    for (int k = 0; k < viettelPromotions.length; k++) {
                        if (body.contains((viettelPromotions[k]) + ":")) {
                            editor.putBoolean(viettelPromotions[k], true);
                            Log.d(TAG, viettelPromotions[k]);
                        }
                    }
                } else if (address.equals("999")) {
                    editor.putBoolean("MIU", true);
                    for (int k = 0; k < mobifonePromotions.length; k++) {
                        if (body.contains((mobifonePromotions[k]) + ",")) {
                            editor.putBoolean(mobifonePromotions[k], true);
                            Log.d(TAG, mobifonePromotions[k]);
                        }
                    }
                }

            }
            editor.apply();
        }

    }


}
