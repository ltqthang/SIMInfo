package com.alpha.siminfo;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.alpha.siminfo.mobifone.MobifoneFragment;
import com.alpha.siminfo.viettel.ViettelFragment;
import com.alpha.siminfo.view.CircleButton;

public class MainActivity extends ActionBarActivity {
    public static String networkOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff009688));

        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        networkOperator = manager.getNetworkOperatorName();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (networkOperator) {
            case "VIETTEL" : {
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, new ViettelFragment()).commit();
                break;
            }

            case "MOBIFONE" : {
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, new MobifoneFragment()).commit();
                break;
            }

            default: {
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, new ViettelFragment()).commit();
                Toast.makeText(this, getString(R.string.not_supported), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onUTViettel(View view) {
        CircleButton circleButton = (CircleButton) view;
        String amount = circleButton.getText().replace(" ", "");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("9118", null, "UT " + amount, null, null);
    }
}
