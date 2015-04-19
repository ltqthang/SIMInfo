package com.alpha.siminfo.viettel;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.siminfo.Promotion;
import com.alpha.siminfo.R;
import com.alpha.siminfo.TransferMoneyFragment;
import com.alpha.siminfo.provider.ViettelPromotionProvider;
import com.alpha.siminfo.util.Util;
import com.alpha.siminfo.view.CircleButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViettelFragment extends Fragment {
    private final String TAG = "ViettelFragment";
    private final String[] promotionNames = {"CT1", "CT6", "CT16", "CT31", "CT32", "CT52", "CT53", "CT54", "CT55", "CT56",
            "CT57", "CT58", "CT59", "CT60", "CT100"};
    private CircleButton balanceButton;
    private CircleButton transferMoneyButton;
    private CircleButton recallButton;
    private CircleButton ca10kButton;
    private ImageButton refreshButton;

    // Store all possible promotions
    private List<Promotion> allPromotions;
    // Store current promotions which appreciate to this user
    private List<Promotion> promotions = new ArrayList<>();

    private SmsManager smsManager = SmsManager.getDefault();

    // Object to animate refreshButton while waiting respond SMS
    private ObjectAnimator objectAnimator;
    private LinearLayout layoutContainer;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private View viewInstructor;
    private boolean isAddToUI = false;

    AlertDialog alertDialog;

    public ViettelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_viettel, container, false);

        setupButons(viewRoot);

        // Create all possible promotions
        allPromotions = ViettelPromotionProvider.createAllPromotion(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("VIETTEL", Context.MODE_PRIVATE);

        layoutContainer = (LinearLayout) viewRoot.findViewById(R.id.container);
        if (!sharedPreferences.getBoolean("promotionReceived", false)) {
            viewInstructor = View.inflate(getActivity(), R.layout.view_instructor, null);
            layoutContainer.addView(viewInstructor);
            refreshButton = (ImageButton) viewRoot.findViewById(R.id.refreshButton);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshButton.setClickable(false);
                    refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
                    smsManager.sendTextMessage("266", null, "KM", null, null);
                    Toast.makeText(getActivity(), getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        objectAnimator = ObjectAnimator.ofFloat(refreshButton, "rotation", 0, 360);
                        objectAnimator.setDuration(1000);
                        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                        objectAnimator.start();
                    }
                }
            });
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (isAddToUI) return;
                Log.d(TAG, "OnSharedPreferenceChangeListener " + key);
                isAddToUI = true;
                layoutContainer.removeView(viewInstructor);
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.load_promotions));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    objectAnimator.setRepeatCount(0);
                }
                addPromotionUI(sharedPreferences, inflater, layoutContainer);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        addPromotionUI(sharedPreferences, inflater, layoutContainer);

        return viewRoot;
    }

    private void addPromotionUI(SharedPreferences sharedPreferences, LayoutInflater inflater, LinearLayout layoutContainer) {
        // Get promotions from preference
        promotions = parsePreferences(sharedPreferences);
        for (int i = 0; i < promotions.size(); i++) {
            View view = inflater.inflate(R.layout.viettel_row_promotion, null);
            setupView(view, promotions.get(i));
            layoutContainer.addView(view);
        }
    }

    private void setupButons(View viewRoot) {
        balanceButton = (CircleButton) viewRoot.findViewById(R.id.balance_button);
        balanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.sendUSSD(getActivity(), "101");
            }
        });


        transferMoneyButton = (CircleButton) viewRoot.findViewById(R.id.transfer_money_button);
        transferMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, new TransferMoneyFragment()).addToBackStack(null);
                transaction.commit();
            }
        });

        recallButton = (CircleButton) viewRoot.findViewById(R.id.request_recall_button);
        recallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, new RecallFragment()).addToBackStack(null);
                transaction.commit();
            }
        });

        ca10kButton = (CircleButton) viewRoot.findViewById(R.id.ca10k_button);
        ca10kButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, new ViettelCAFragment()).addToBackStack(null);
                transaction.commit();
            }
        });


    }

    private List<Promotion> parsePreferences(SharedPreferences sharedPreferences) {
        List<Promotion> promotions = new ArrayList<>();

        for (int i = 0; i < promotionNames.length; i++) {
            if (sharedPreferences.getBoolean(promotionNames[i], false)) {
                promotions.add(allPromotions.get(i));
            }
        }

        return promotions;
    }


    private void setupView(View view, final Promotion promotion) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView description = (TextView) view.findViewById(R.id.description);
        CircleButton registerButton = (CircleButton) view.findViewById(R.id.register_button);
        CircleButton checkButton = (CircleButton) view.findViewById(R.id.check_button);
        CircleButton cancelButton = (CircleButton) view.findViewById(R.id.cancel_button);

        name.setText(promotion.getName() + ": " + promotion.getMessageRegister());
        description.setText(promotion.getDescription());
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(getActivity());

                builder.setTitle(getString(R.string.confirm_register))
                       .setMessage(getString(R.string.confirm_message))
                       .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               smsManager.sendTextMessage(promotion.getDesNumber(), null, promotion.getMessageRegister(), null, null);
                               Toast.makeText(getActivity(), getString(R.string.registered_toast), Toast.LENGTH_LONG).show();
                           }
                       })
                       .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               alertDialog.dismiss();
                           }
                       });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsManager.sendTextMessage(promotion.getDesNumber(), null, promotion.getMessageCheck(), null, null);
                Toast.makeText(getActivity(), getString(R.string.checked_toast), Toast.LENGTH_LONG).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsManager.sendTextMessage(promotion.getDesNumber(), null, promotion.getMessageCancel(), null, null);
                Toast.makeText(getActivity(), getString(R.string.cancel_toast), Toast.LENGTH_LONG).show();
            }
        });
    }
}
