package com.creadigol.drivehere.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.CarFindResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.PreferenceSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_SCAN = 1001;
    public boolean isNedded = false;
    boolean flag = false;

    private final String TAG = MainActivity.class.getSimpleName();


@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // handle arrow click here
    if (item.getItemId() == android.R.id.home) {
        onBackPressed(); // close this activity and return to preview activity (if there is any)
    } else if (item.getItemId() == R.id.action_logout) {
        // open car edit activity
        logoutAlert();
    }

    return super.onOptionsItemSelected(item);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardSearch = (CardView) findViewById(R.id.card_search);
        cardSearch.setOnClickListener(this);

        CardView cardScanCheck = (CardView) findViewById(R.id.card_scan_check);
        cardScanCheck.setOnClickListener(this);

        CardView cardGaugeClock = (CardView) findViewById(R.id.card_gauge_clock);
        cardGaugeClock.setOnClickListener(this);

        CardView cardAuctionReport = (CardView) findViewById(R.id.card_auction);
        cardAuctionReport.setOnClickListener(this);

        CardView cardRepoReport = (CardView) findViewById(R.id.card_repo);
        cardRepoReport.setOnClickListener(this);

        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            getSupportActionBar().setTitle(getString(R.string.app_name) + " " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int version = info.versionCode;
        Log.e("version", " =" + version);
        if (MyApplication.getInstance().getPreferenceSettings().getServerAppVersion() > version) {
            updateVersionDialog();
        } else if (CommonFunctions.isNetworkConnected(getApplicationContext())) {
            checkVersionUpdate(version);
        }
        checkPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.getInstance().getLocationTracker().connectGoogleApiClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getInstance().getLocationTracker().disconnectGoogleApiClient();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;

            case R.id.card_scan_check:
                startScanning();
                break;

            case R.id.card_gauge_clock:
               startActivity(new Intent(MainActivity.this, GaugeClockActivity.class));
                break;

            case R.id.card_auction:
                startActivity(new Intent(MainActivity.this, AuctionReportActivity.class));
                break;

            case R.id.card_repo:
                startActivity(new Intent(MainActivity.this, RepoReportActivity.class));
                break;
        }
    }

    void startScanning() {

        Intent scannerVin = new Intent(MainActivity.this,
                ScannerActivity.class);
        startActivityForResult(scannerVin, REQUEST_SCAN);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isNedded == true) {
            isNedded = false;
            checkPermission();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            // set Scan VIN
            String scanCode = data.getStringExtra("code");
            scanCode = scanCode.toUpperCase();
            int scanCodeLength = scanCode.length();
            if (scanCodeLength == 18
                    && (scanCode.startsWith("i") || scanCode
                    .startsWith("I"))) {
                scanCode = scanCode.substring(1, scanCode.length());
            }
            Log.e("scanCode", scanCode);
            scanCodeLength = scanCode.length();

            if (scanCodeLength == Constant.LENGTH_VIN) {
                // get vin from scanning
                if (MyApplication.getInstance().getLocationTracker().getmLocation() != null) {
                    getAddress(MyApplication.getInstance().getLocationTracker().getmLocation(), scanCode, true);
                } else {
                    reqFindCar(scanCode, true, "", null);
                }
            } else if (scanCodeLength == Constant.LENGTH_RFID) {
                // get rfid from scanning
                if (MyApplication.getInstance().getLocationTracker().getmLocation() != null) {
                    getAddress(MyApplication.getInstance().getLocationTracker().getmLocation(), scanCode, false);
                } else {
                    reqFindCar(scanCode, false, "", null);
                }
            }
        }
    }

    /**
     * @param code  vin or rfid code
     * @param isVin true if code is a vin
     */
    private void reqFindCar(final String code, final boolean isVin, final String address, final Location location) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Searching car on server...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_CAR_FIND;
        final StringRequest myTagReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqFindCar Response", response.toString());
                //pDialog.hide();
                try {
                    CarFindResponse carFindResponse = CarFindResponse.parseJSON(response);

                    if (carFindResponse.getStatusCode() == 1) {
                        // car found
                        startCarDetailActivity(carFindResponse.getCar());
                    } else if (carFindResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(MainActivity.this, carFindResponse.getMessage());
                    } else if (carFindResponse.getStatusCode() == 2) {
                        // Car not found
                        if (!isVin) {
                            // CommonFunctions.showToast(MainActivity.this, "Rfid is not in System scan Vin.");
                            DialogInterface.OnClickListener onClickScanVin = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // open scanning activity
                                    startScanning();
                                }
                            };

                            CommonFunctions.showAlert(MainActivity.this, "Info", "Rfid is not in System, Scan Vin.", "Scan VIN", "No", onClickScanVin);
                        } else {
                            Intent intentAddCar = new Intent(MainActivity.this, CarAddActivity.class);
                            intentAddCar.putExtra(CarAddActivity.EXTRA_KEY_CODE, code);
                            intentAddCar.putExtra(CarAddActivity.EXTRA_KEY_IS_VIN, isVin);
                            startActivity(intentAddCar);
                        }

                    } else if (carFindResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqFindCar Error", e.toString());
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqFindCar", "Error Response: " + error.getMessage());
                CommonFunctions.showToast(MainActivity.this, getString(R.string.network_error));
                if (pDialog.isShowing())
                    pDialog.dismiss();
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                if (isVin)
                    params.put(ParamsKey.VIN, code);
                else
                    params.put(ParamsKey.RFID, code);

                if (location != null) {
                    params.put(ParamsKey.LATITUDE, String.valueOf(location.getLatitude()));
                    params.put(ParamsKey.LONGITUDE, String.valueOf(location.getLongitude()));
                    params.put(ParamsKey.ADDRESS, address);
                    String lotCode = CommonFunctions.getLotCodeByLocation(location.getLatitude(), location.getLongitude());
                    if (lotCode != null && lotCode.trim().length() > 0)
                        params.put(ParamsKey.LOT_CODE, lotCode);
                }
                Log.e("reqFindCar", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(myTagReq, TAG);
    }

    public void startCarDetailActivity(Car car) {
        Intent carDetail = new Intent(MainActivity.this, CarDetailActivity.class);
        carDetail.putExtra(CarDetailActivity.EXTRA_KEY_CAR, car);
        carDetail.putExtra(CarDetailActivity.EXTRA_KEY_CAR_DETAIL_TYPE, "scan");
        startActivity(carDetail);
    }

    private void getAddress(final Location location, final String code, final boolean isVin) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Address...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + ","
                + location.getLongitude() + "&sensor=true";

        final StringRequest reqAddress = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("reqAddress Response", response.toString());
                //pDialog.hide();
                String address = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    String Status = jsonObj.getString("status");
                    if (Status.equalsIgnoreCase("OK")) {
                        JSONArray results = jsonObj.getJSONArray("results");
                        if (results != null && results.length() > 0) {
                            JSONObject zero = results.getJSONObject(0);
                            address = zero.getString("formatted_address");
                        }
                    }
                    Log.e(TAG, "address: " + address);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqAddress Error", e.toString());
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
                reqFindCar(code, isVin, address, location);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqAddress", "Error Response: " + error.getMessage());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                reqFindCar(code, isVin, "", location);
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqAddress, TAG);
    }

    private void updateVersionDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.version_check_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);
        // if decline button is clicked, close the custom dialog
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                finish();
            }
        });
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        // if decline button is clicked, close the custom dialog
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(AppUrl.URL_STORE_APP));
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void checkVersionUpdate(final int version) {
        String url = AppUrl.URL_CHECK_VERSION;

        final StringRequest reqSearchCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqList", " Response:" + response.toString());
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);
                    PreferenceSettings preferenceSettings = MyApplication.getInstance().getPreferenceSettings();
                    if (basicResponse.getStatusCode() == 1) {
                        Log.e("version", "" + basicResponse.getCurrent_version_code());
                        preferenceSettings.setServerAppVersion(basicResponse.getCurrent_version_code());
                        if (preferenceSettings.getServerAppVersion() > version) {
                            updateVersionDialog();
                        }
                    } else if (basicResponse.getStatusCode() == 2) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqSearchCarList", "catch");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqSearchCarList", "Error Response: " + error.getMessage());
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("reqSearchCarList", "Posting params: " + params.toString());
                params.put(ParamsKey.APP_TYPE, "drivehereinventory");
                params.put(ParamsKey.VERSION_CODE, String.valueOf(version));
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, "carScanList");
    }
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.CAMERA);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (permissionsList != null && permissionsList.size() > 0) {
                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
                alert.setTitle("Need Multiple Permissions");
                alert.setCancelable(false);
                alert.setMessage("This app needs Camera and Location permissions, Write external storage.");
                alert.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (flag == true) {
                            isNedded = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            Toast.makeText(MainActivity.this, "Go to permissions to grant all permissions", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            dialog.cancel();
                            dialog.dismiss();
                        } else {
                            dialog.cancel();
                            dialog.dismiss();
                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 100);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
                alert.show();
            } else {

            }
        } else {

        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (grantResults != null && grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.e("In Permission", " BottomTabActivity " + permissions[i] + " result " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("sss", "fdg" + grantResults[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsList.add(permissions[i]);
                    }
                }
            } else {

            }
            if (permissionsList != null && permissionsList.size() > 0) {
                for (int i = 0; i < permissionsList.size(); i++) {
                    if (!shouldShowRequestPermissionRationale(permissionsList.get(i))) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
                checkPermission();

            }
        } else {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            alert.setTitle("Sorry");
            alert.setCancelable(false);
            alert.setMessage("Please give all permision ");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            });
            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finishAffinity();
                }
            });
            alert.show();
        }
    }

    void logoutAlert(){
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("Logout");
        alert.setCancelable(false);
        alert.setMessage("Are you sure you want to logout.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                    MyApplication.getInstance().getPreferenceSettings().clearSession();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        alert.show();
    }
}
