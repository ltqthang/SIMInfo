package com.alpha.siminfo.viettel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpha.siminfo.R;
import com.alpha.siminfo.util.Util;
import com.alpha.siminfo.view.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViettelCAFragment extends Fragment {
    private CircleButton registerButton;
    private CircleButton cancelButton;
    private CircleButton fastCreditButton;

    private SmsManager smsManager = SmsManager.getDefault();

    public ViettelCAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_viettel_ca, container, false);;
        registerButton = (CircleButton) viewRoot.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsManager.sendTextMessage("9118", null, "DK", null, null);
            }
        });

        cancelButton = (CircleButton) viewRoot.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsManager.sendTextMessage("9118", null, "HUY", null, null);
            }
        });

        fastCreditButton = (CircleButton) viewRoot.findViewById(R.id.ca3k_button);
        fastCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.sendUSSD(getActivity(), "911");
            }
        });

        int[] ids = {R.id.view1, R.id.view2, R.id.view3, R.id.view4, R.id.view5, R.id.view6};
        for (int i = 0; i < 6; i++) {
            final CircleButton button = (CircleButton) viewRoot.findViewById(ids[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smsManager.sendTextMessage("9118", null, "UT " + formatString(button.getText()), null, null);
                }
            });
        }

        return viewRoot;
    }

    private String formatString(String string) {
        StringBuilder sb = new StringBuilder();
        final int len = string.length();
        for (int i = 0; i < len; i++) {
            if (string.charAt(i) == ',') break;
            sb.append(string.charAt(i));
        }
        return new String(sb);
    }


}
