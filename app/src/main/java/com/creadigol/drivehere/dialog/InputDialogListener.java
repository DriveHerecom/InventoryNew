package com.creadigol.drivehere.dialog;

import android.support.v4.app.DialogFragment;

/**
 * Created by ADMIN on 23-05-2017.
 */

public interface InputDialogListener {

    void onDialogNegativeClick(DialogFragment dialog);
    void onDialogPositiveClick(String input);
}
