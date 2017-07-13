package com.creadigol.drivehere.dialog;

import android.support.v4.app.DialogFragment;

/**
 * Created by ADMIN on 23-05-2017.
 */

public interface ListDialogListener {
/*
    void onItemClick(int position);*/
    void onItemClick(int position, String tag);
    void onDialogNegativeClick(DialogFragment dialog);
}
