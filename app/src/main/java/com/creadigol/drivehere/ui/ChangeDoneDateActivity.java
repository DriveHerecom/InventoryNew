package com.creadigol.drivehere.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.dialog.InputDialogListener;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.LotCodeDialogFragment;
import com.creadigol.drivehere.dialog.PinValidationDialogFragment;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChangeDoneDateActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = ChangeDoneDateActivity.class.getSimpleName();
    public final String TAG_DIALOG_LOT_CODE = "Lot Code";
    public final String TAG_DIALOG_PIN_VALIDATE = "Pin validation";
    public static final String EXTRA_KEY_CAR_ID = "car_id";

    private TextView tvSelectDoneDate, tvSelectLotCode;
    private EditText edtQCDoneBy;
    private String carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_done_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        carId = bundle.getString(EXTRA_KEY_CAR_ID, "");

        tvSelectDoneDate = (TextView) findViewById(R.id.tv_select_done_date);
        tvSelectLotCode = (TextView) findViewById(R.id.tv_select_done_lot_code);

        edtQCDoneBy = (EditText) findViewById(R.id.edt_qc_done_by);
        edtQCDoneBy.addTextChangedListener(twQCDoneBy);

        tvSelectDoneDate.setOnClickListener(this);
        tvSelectLotCode.setOnClickListener(this);

        findViewById(R.id.btn_submit_done_date).setOnClickListener(this);

    }

    private TextWatcher twQCDoneBy = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String miles = s.toString().trim();
            if (miles.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_qc_done_by_hint)).setText(getString(R.string.qc_done_by));
            } else {
                ((TextView) findViewById(R.id.tv_qc_done_by_hint)).setText(getString(R.string.enter));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_done_date:
                openDatePicker();
                break;

            case R.id.tv_select_done_lot_code:
                showLotCodeDialog();
                break;

            case R.id.btn_submit_done_date:
                validateInputs();
                break;
        }
    }

    void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog dpd = new DatePickerDialog(ChangeDoneDateActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String month, day;

                        if (monthOfYear < 10) {
                            month = "0" + monthOfYear;
                        } else {
                            month = "" + monthOfYear;
                        }

                        if (dayOfMonth < 10) {
                            day = "0" + dayOfMonth;
                        } else {
                            day = "" + dayOfMonth;
                        }

                        tvSelectDoneDate.setText(year + "-" + month + "-" + day);
                        tvSelectDoneDate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.show();
    }

    public void showLotCodeDialog() {

        final String[] lotList = getResources().getStringArray(R.array.Lotcode);
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                tvSelectLotCode.setText(lotList[position]);
                tvSelectLotCode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, TAG_DIALOG_LOT_CODE);
        dialog1.show(getSupportFragmentManager(), TAG_DIALOG_LOT_CODE);
    }

    public void showPinValidateDialog(final Map<String, String> values) {

        final InputDialogListener listener = new InputDialogListener() {

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                finish();
            }

            @Override
            public void onDialogPositiveClick(String pin) {
                if (pin.length() > 0 && pin.equals(Constant.ADMIN_PIN)) {
                    reqChangeDoneDate(values);
                }
            }
        };

        PinValidationDialogFragment dialog1 = new PinValidationDialogFragment(listener, TAG_DIALOG_PIN_VALIDATE);
        dialog1.show(getSupportFragmentManager(), TAG_DIALOG_PIN_VALIDATE);
    }

    public void validateInputs(){
        Map<String, String> values = new HashMap<>();
        boolean isValidLotCode = false, isValidDate = false;

        String QCDoneBy = edtQCDoneBy.getText().toString();
        values.put(ParamsKey.EDIT_DONE_QC_DONE_BY, QCDoneBy);

        String doneDate = tvSelectDoneDate.getText().toString();

        if (doneDate.trim().length() > 0 && !doneDate.trim().equalsIgnoreCase(getString(R.string.select_date))) {
            doneDate = String.valueOf(CommonFunctions.getMilliseconds(doneDate, "yyyy-MM-dd"));
            values.put(ParamsKey.EDIT_DONE_DATE, doneDate);
            isValidDate = true;
        } else {
            isValidDate = false;
        }

        String doneLotCode = tvSelectLotCode.getText().toString();

        if (doneLotCode.trim().length() > 0 && !doneLotCode.trim().equalsIgnoreCase(getString(R.string.select_lot_code))) {
            values.put(ParamsKey.EDIT_DONE_LOT_CODE, doneLotCode);
            isValidLotCode = true;
        } else {
            isValidLotCode = false;
        }

        if (isValidDate && isValidLotCode) {
            values.put(ParamsKey.CAR_ID, String.valueOf(carId));
            showPinValidateDialog(values);
        } else {
            if (isValidDate && !isValidLotCode) {
                CommonFunctions.showToast(ChangeDoneDateActivity.this, "Please select valid Done Lot code.");
            } else if (!isValidDate && isValidLotCode) {
                CommonFunctions.showToast(ChangeDoneDateActivity.this, "Please select valid Done date.");
            } else {
                CommonFunctions.showToast(ChangeDoneDateActivity.this, "Please select valid Done date and Lot code.");
            }
        }
    }

    public void reqChangeDoneDate(final Map<String, String> values) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_EDIT_DONE_DATE;
        final StringRequest myTagReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Car Response", response.toString());
                //pDialog.hide();
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);

                    if (basicResponse.getStatusCode() == 1) {
                        CommonFunctions.showToast(ChangeDoneDateActivity.this, basicResponse.getMessage());
                        ChangeDoneDateActivity.this.finish();
                    } else if (basicResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(ChangeDoneDateActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(ChangeDoneDateActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 9) {
                        CommonFunctions.showToast(ChangeDoneDateActivity.this, basicResponse.getMessage());
                    } else {
                        CommonFunctions.showToast(ChangeDoneDateActivity.this, response);
                    }

                } catch (Exception e) {
                    CommonFunctions.showToast(ChangeDoneDateActivity.this, getString(R.string.network_error));
                    e.printStackTrace();
                    Log.e("Error_in", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Mytag", "Error Response: " + error.getMessage());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                CommonFunctions.showToast(ChangeDoneDateActivity.this, getString(R.string.network_error));
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.putAll(values);
                Log.e("MyTags", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(myTagReq, TAG);
    }

}
