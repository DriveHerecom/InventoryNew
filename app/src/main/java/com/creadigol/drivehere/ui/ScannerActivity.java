package com.creadigol.drivehere.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.creadigol.drivehere.R;
import com.creadigol.drivehere.barcodescanner.zxing.ZXingScannerView;
import com.creadigol.drivehere.util.CommonFunctions;
import com.google.zxing.Result;

/**
 * Created by ADMIN on 26-05-2017.
 */

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String EXTRA_KEY_CODE = "code";

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    String code;

    String TAG_SCAN_RESULT_NOT_FOUND = "SCAN_RESULT_NOT_FOUND";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
        } else {
            mFlash = false;
            mAutoFocus = true;
        }

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        resumeCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem menuItem;
        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }

        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }

        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);

                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        code = "";
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }
        Log.e("Scan", "Barcode " + rawResult.getText() + " Type " + rawResult.getBarcodeFormat());
        CommonFunctions.showToast(getBaseContext(), "Barcode " + rawResult.getText() + " Type " + rawResult.getBarcodeFormat());
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_CODE, rawResult.getText());
        setResult(RESULT_OK,intent);
        finish();
    }

    // Resume the camera
    void resumeCamera() {
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    /*void findMatch(final String code) {
        this.code = code;
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("findMatch response", responseString);
                showToast("findMatch Error");
                resumeCamera();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("search Response", response.toString());
                Log.e("Scanner in search",response+" test");
                try {

                    FindMatch findmatch = Application.APP_JSON_PARSER.findMatch(response);

                    if (findmatch.match.trim().equals("1")) {
                        // TODO FINISH
                        showToast("Already Registered.");
                        Intent intent = new Intent();
                        intent.putExtra("code", code);
                        intent.putExtra("FOUND", true);
                        intent.putExtra("each_car", findmatch.cardetail);
                        setResult(RESULT_OK,intent);
                        finish();// finishing activity
                    } else {
                        // TODO set return data and finish activity
                        Intent intent = new Intent();
                        intent.putExtra("code", code);
                        intent.putExtra("FOUND", false);
                        setResult(RESULT_OK,intent);
                        finish();// finishing activity
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    CommonUtils.showAlertDialog(AddCarScannerActivity.this, Constant.ERR_TITLE, Constant.ERR_MESSAGE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (Application.isNetConnection())
                                findMatch(code);
                            else
                                Toast.makeText(getApplicationContext(),Constant.ERR_INTERNET , Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                showUpdateProgressDialog("Finding match...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }
        };

        String url = Application.APP_URL.URL_FIND_VEHICLE_NEW;
        RequestParams params = new RequestParams();
        if (code.length() == 17) {
            params.put("Vin", code.trim());
        } else if (code.length() == 7) {
            params.put("Rfid", code.trim());
        }

        params.put("flag", "2");
        params.put(Constant.KEY_TYPE, "3");

        if (getCurrentDate()!=null)
        {
            params.put("CreatedDate",getCurrentDate()+"");
        }

        Location location = Application.PLAY_MANAGER.getLastLocation();
        if(location!=null)
        {
            params.put("Latitude", location.getLatitude() + "");
            params.put("Longitude", location.getLongitude() + "");
            GetAddress getAddress = new GetAddress();
            params.put("address",""+getAddress.getAddressUsingLatLong(this,location.getLatitude(),location.getLongitude()));
        }
        else
        {
            params.put("address","");
            params.put("Latitude","");
            params.put("Longitude","");
        }

        RestClient.post(this, url, params, handler);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("code", "");
            //intent.putExtra("FOUND", false);
            setResult(RESULT_CANCELED,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

   /* @Override
    public void onDialogPositiveClick(MessageDialogFragment dialog) {
        // resumeCamera();

        if (dialog.getTag().equals(TAG_SCAN_RESULT_NOT_FOUND)) {
            Intent intent = new Intent(AddCarScannerActivity.this, AddNewCarActivity.class);
            intent.putExtra("code", code);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(MessageDialogFragment dialog) {
        resumeCamera();
    }

    @Override
    public void onDialogNeutralClick(MessageDialogFragment dialog) {

    }*/

    /*public String getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate ;
    }*/
}
