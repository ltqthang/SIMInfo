package com.alpha.siminfo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alpha.siminfo.util.Util;
import com.alpha.siminfo.view.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferMoneyFragment extends Fragment {
    private final int PICK_CONTACT_REQUEST = 1;
    // Controls in register form
    EditText registerPassword;
    CircleButton registerButton;

    // Controls in transfer form
    private EditText phoneNumber;
    private EditText password;
    private EditText money;
    private ImageButton selectContactButton;
    private CircleButton transferButton;

    public TransferMoneyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_tranfer_money, container, false);

        setupRegisterForm(viewRoot);

        setupTransferForm(viewRoot);

        // Show soft keyboard
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(0, 0);

        return viewRoot;
    }

    private void setupRegisterForm(View viewRoot) {
        registerPassword = (EditText) viewRoot.findViewById(R.id.password_register);
        registerButton = (CircleButton) viewRoot.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psswd = registerPassword.getText().toString();
                if (psswd.isEmpty()) {
                    Toast.makeText(getActivity(), "Mật khẩu không được trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (MainActivity.networkOperator) {
                    case "VIETTEL": {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("136", null, psswd, null, null);
                        Toast.makeText(getActivity(), "Đăng ký thành công, vui lòng đợi xác nhận", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case "MOBIFONE": {
                        Util.sendUSSD(getActivity(), "117*" + psswd + "*" + psswd);
                        Toast.makeText(getActivity(), "Đăng ký thành công, vui lòng đợi xác nhận", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void setupTransferForm(View viewRoot) {
        phoneNumber = (EditText) viewRoot.findViewById(R.id.phone_number);
        phoneNumber.requestFocus();


        selectContactButton = (ImageButton) viewRoot.findViewById(R.id.select_contact_button);
        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.toggleSoftInput(0, 0);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        password = (EditText) viewRoot.findViewById(R.id.password);
        money = (EditText) viewRoot.findViewById(R.id.money);
        transferButton = (CircleButton) viewRoot.findViewById(R.id.OK_button);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber.getText().toString();
                String psswd = password.getText().toString();
                String amount = money.getText().toString();
                if (!isValid(number, psswd, amount)) return;
                Toast.makeText(getActivity(), number + " " + psswd + " " + amount, Toast.LENGTH_SHORT).show();

                switch (MainActivity.networkOperator) {
                    case "VIETTEL" : {
                        Util.sendUSSD(getActivity(), "136*" + psswd + "*" + number + "*" + amount);
                        break;
                    }
                    case "MOBIFONE" : {
                        Util.sendUSSD(getActivity(), "119*" + number + "*" + amount + "*" + psswd);
                    }
                }

                Toast.makeText(getActivity(), getString(R.string.send_success), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String number, String psswd, String amount) {
        if (!Util.isValidPhoneNumber(number)) {
            Toast.makeText(getActivity(), getString(R.string.invalid_phone_number),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (psswd.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.invalid_password),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amount.isEmpty() || Integer.parseInt(amount) % 1000 != 0 || Integer.parseInt(amount) == 0) {
            Toast.makeText(getActivity(), getString(R.string.invalid_amount),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        if (requestCode == PICK_CONTACT_REQUEST) {
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
            }
        }
    }

}
