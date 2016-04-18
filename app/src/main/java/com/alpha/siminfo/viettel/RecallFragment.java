package com.alpha.siminfo.viettel;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alpha.siminfo.R;
import com.alpha.siminfo.util.Util;
import com.alpha.siminfo.view.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecallFragment extends Fragment {
    private final int PICK_CONTACT_REQUEST_CODE = 1;

    private EditText phoneNumber;
    private EditText senderName;
    private ImageButton selectContactButton;
    private CircleButton OKButton;
    private SharedPreferences sharedPreferences;

    public RecallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_recall, container, false);;
        phoneNumber = (EditText) viewRoot.findViewById(R.id.phone_number);
        senderName = (EditText) viewRoot.findViewById(R.id.sender_name);

        selectContactButton = (ImageButton) viewRoot.findViewById(R.id.select_contact_button);
        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, PICK_CONTACT_REQUEST_CODE);
            }
        });

        OKButton = (CircleButton) viewRoot.findViewById(R.id.OK_button);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                String number = phoneNumber.getText().toString();
                String name = senderName.getText().toString();
                if (!checkValid(number, name)) return;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone", number);
                editor.putString("name", name);
                editor.apply();
                smsManager.sendTextMessage("9119", null, number + " " + name, null, null);
                Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            }
        });

        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String lastUsedNumber = sharedPreferences.getString("phone", "");
        if (!lastUsedNumber.equals("")) {
            phoneNumber.setText(lastUsedNumber);
        }
        String lastUsedName = sharedPreferences.getString("name", "");
        if (!lastUsedName.equals("")) {
            senderName.setText(lastUsedName);
        }
    }

    private boolean checkValid(String number, String name) {
        if (!Util.isValidPhoneNumber(number)) {
            Toast.makeText(getActivity(), getString(R.string.invalid_phone_number),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.contains(" ")) {
            Toast.makeText(getActivity(), getString(R.string.invalid_name),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(android.content.Intent, int)}.  This follows the
     * related Activity API as described there in
     *
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(columnIndex);
                number = number.replaceAll(" ", "");
                number = number.replaceAll("\\+", "");
                number = number.replaceAll("84", "0");
                phoneNumber.setText(number);

                cursor.close();
            }
        }
    }
}
