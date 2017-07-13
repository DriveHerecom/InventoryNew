package com.creadigol.drivehere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CircleView;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;

public class PinValidationDialogFragment extends DialogFragment {

    InputDialogListener mListener;
    RecyclerView recyclerView;
    String tag;

    public PinValidationDialogFragment(InputDialogListener listener, String tag) {
        mListener = listener;
        this.tag = tag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the XML view for the help dialog fragment
        View view = inflater.inflate(R.layout.dialog_pin_validatio, container);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(tag);

        final EditText edtPin = (EditText) view.findViewById(R.id.edt_pin);

        view.findViewById(R.id.btn_validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = edtPin.getText().toString().trim();
                if (pin.length() > 0 && pin.equals(Constant.ADMIN_PIN)) {
                    mListener.onDialogPositiveClick(pin);
                    dismiss();
                } else {
                    CommonFunctions.showToast(getActivity().getApplicationContext(), "Please enter valid pin.");
                }
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        /*setStyle(STYLE_NO_TITLE, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Pin Validation");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pin_validatio, null);


        //builder.setView(view);
        dialog.setContentView(view);
        //return builder.create();*/
        return dialog;
    }
}
