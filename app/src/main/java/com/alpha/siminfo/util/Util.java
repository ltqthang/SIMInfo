package com.alpha.siminfo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.View;

/**
 * Created by I Love Coding on 1/3/2015.
 */
public class Util {

    public static void call(Context context, String number) {
        number = "tel:" + number;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        context.startActivity(intent);
    }

    public static void sendUSSD(Context context, String USSD) {
        USSD = "tel:*" + USSD + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(USSD));
        context.startActivity(intent);
    }

    public static void sendMessager(Context context, String telephoneNumber, String stringText){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + telephoneNumber));
        sendIntent.putExtra("sms_body", stringText);
        context.startActivity(sendIntent);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return (phoneNumber.length() == 10 || phoneNumber.length() == 11);
    }

    public static void sendMessageOnClick(View button, final String desNumber, final String body) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager.getDefault().sendTextMessage(desNumber, null, body, null, null);
            }
        });

    }
}
