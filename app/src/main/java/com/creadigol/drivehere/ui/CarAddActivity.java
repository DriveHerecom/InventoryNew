package com.creadigol.drivehere.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.MultipartRequest;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.ui.fragment.CarAddBasicDetailFragment;
import com.creadigol.drivehere.ui.fragment.CarAddCallBack;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;

import java.util.HashMap;

public class CarAddActivity extends AppCompatActivity implements CarAddCallBack {

    public static final String EXTRA_KEY_TYPE = "type";
    public static final String EXTRA_KEY_IS_VIN = "is_vin";
    public static final String EXTRA_KEY_CODE = "code";

    public static final String EXTRA_KEY_IS_EDIT = "is_edit";
    public static final String EXTRA_KEY_CAR_ID = "car_id";
    public static final String EXTRA_KEY_VIN = "car_vin";

    private final String TAG = CarAddActivity.class.getSimpleName();
    public boolean isEdit = false;
    public String carIdEdit = "";
    private TYPE selectedType;
    private CarAdd carAdd;
    private boolean isValidVin, isValidRfid;

    public CarAdd getCarAdd() {
        if (carAdd == null) {
            carAdd = new CarAdd();
        }
        return carAdd;
    }

    public void setCarAdd(CarAdd carAdd) {
        this.carAdd = carAdd;
    }

    public void setValidVin(boolean validVin) {
        isValidVin = validVin;
    }

    public void setValidRfid(boolean validRfid) {
        isValidRfid = validRfid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        boolean isVin = false;
        String code = null;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString(EXTRA_KEY_CODE, "");
            isVin = bundle.getBoolean(EXTRA_KEY_IS_VIN, false);

            isEdit = bundle.getBoolean(EXTRA_KEY_IS_EDIT, false);
            carIdEdit = bundle.getString(EXTRA_KEY_CAR_ID, "");
        }
        setFragmentContainer(TYPE.BASIC, code, isVin);

        if (isEdit) {
            getSupportActionBar().setTitle("Edit Car");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onNext(TYPE type) {
        if (type == TYPE.BASIC) {
            HashMap<String, String> hasParams = getHasParams();
            if (hasParams != null)
                addCar(hasParams);
        }
        Log.e(TAG, "type:" + type.toString());
    }

    public void setFragmentContainer(TYPE type, String code, boolean isVin) {
        if (selectedType == type) {
            return;
        }

        Fragment fragment = null;
        switch (type) {
            case BASIC:
                fragment = CarAddBasicDetailFragment.getInstance(TYPE.BASIC, code, isVin);
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }


    // TODO need to add gps data to request
    void addCar(HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait, Adding car to the server...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_CAR_ADD;

        MultipartRequest reqAddAuction = new MultipartRequest(url, hashParams, getCarAdd().getLocalImages(),
                getCarAdd().getTitleLocalImages(), getCarAdd().getGpses(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reqAddAuction Response", response.toString());
                        //pDialog.hide();
                        try {
                            BasicResponse basicResponse = BasicResponse.parseJSON(response);

                            if (basicResponse.getStatusCode() == 1) {
                                // set list of cars
                                CommonFunctions.showToast(CarAddActivity.this, basicResponse.getMessage());
                                CarDetailActivity.isNeeded=true;
                                finish();
                            } else if (basicResponse.getStatusCode() == 0) {
                                CommonFunctions.showToast(CarAddActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 2) {
                                CommonFunctions.showToast(CarAddActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 4) {
                                CommonFunctions.showToast(CarAddActivity.this, basicResponse.getMessage());
                                // TODO Block user by admin or user not valid
                            } else {
                                CommonFunctions.showToast(CarAddActivity.this, basicResponse.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("reqAddAuction Error_in", "catch");
                        }
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("reqAddAuction", "Error Response: " + error.getMessage());
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        //showTryAgainAlert("Info", "Network error, Please try again!");
                    }

                });

        MyApplication.getInstance().addToRequestQueue(reqAddAuction, TAG);
    }

    public HashMap<String, String> getHasParams() {
        HashMap<String, String> hashParams = new HashMap<>();

        String vin = getCarAdd().getVin().trim();
        if (vin.length() != Constant.LENGTH_VIN) {
            CommonFunctions.showToast(this, "Please enter valid vin.");
            return null;
        }
        hashParams.put(ParamsKey.VIN, vin);

        hashParams.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
        hashParams.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);

        if (isEdit) {
            hashParams.put(ParamsKey.CAR_ID, getCarAdd().getCarId());
            hashParams.put(ParamsKey.DELETE_PHOTO_LINK, getCarAdd().getDeletePhotoLink());
        }

        String rfid = getCarAdd().getRfid().trim();
        if (rfid.length() > 0 && rfid.length() != Constant.LENGTH_RFID) {
            CommonFunctions.showToast(this, "Please enter valid RFID or make it blank.");
            return null;
        }
        hashParams.put(ParamsKey.RFID, rfid);

        hashParams.put(ParamsKey.LOT_CODE, getCarAdd().getLotCode());
        hashParams.put(ParamsKey.MILES, getCarAdd().getMiles());
        hashParams.put(ParamsKey.VACANCY, getCarAdd().getVacancy());
        hashParams.put(ParamsKey.STAGE, getCarAdd().getStage());
        hashParams.put(ParamsKey.STOCK_NUMBER, getCarAdd().getStockNumber());
        hashParams.put(ParamsKey.HAS_TITLE, getCarAdd().getHasTitle());
        hashParams.put(ParamsKey.LOT_CODE_TITLE, getCarAdd().getTitleLocation());
        hashParams.put(ParamsKey.DATA_ONE_INFO, getCarAdd().getDataOneBase64());
        hashParams.put(ParamsKey.COLOR, getCarAdd().getColor());
        hashParams.put(ParamsKey.NOTE, getCarAdd().getNote());
        hashParams.put(ParamsKey.MAKE, getCarAdd().getMake());
        hashParams.put(ParamsKey.MODEL, getCarAdd().getModel());
        hashParams.put(ParamsKey.MODEL_YEAR, getCarAdd().getModelYear());

        if (!isEdit && carAdd.isDataOneFound()) {
//            hashParams.put(ParamsKey.MAKE, getCarAdd().getMake());
//            hashParams.put(ParamsKey.MODEL, getCarAdd().getModel());
            hashParams.put(ParamsKey.MODEL_NUMBER, getCarAdd().getModelNumber());
//            hashParams.put(ParamsKey.MODEL_YEAR, getCarAdd().getModelYear());
            hashParams.put(ParamsKey.VEHICAL_TYPE, getCarAdd().getVehicleType());
            hashParams.put(ParamsKey.DRIVE_TYPE, getCarAdd().getDriveType());
            hashParams.put(ParamsKey.MAX_HP, getCarAdd().getMaxHp());
            hashParams.put(ParamsKey.MAX_TORQUE, getCarAdd().getMaxTorque());
            hashParams.put(ParamsKey.OIL_CAPACITY, getCarAdd().getOilCapacity());
            hashParams.put(ParamsKey.CYLINDERS, getCarAdd().getCylinder());
            hashParams.put(ParamsKey.FUEL_TYPE, getCarAdd().getFuelType());
        }
        return hashParams;
    }

    public static enum TYPE {BASIC, MORE, DATAONE}

}
