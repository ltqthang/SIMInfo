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

        return viewRoot;
    }


}
