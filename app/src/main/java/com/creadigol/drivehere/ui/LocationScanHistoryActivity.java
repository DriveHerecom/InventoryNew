package com.creadigol.drivehere.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.ScanHistoryItem;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.Network.ScanHistoryListResponse;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.LocationScanAdapter;
import com.creadigol.drivehere.util.DividerItemDecoration;
import com.creadigol.drivehere.util.CommonFunctions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationScanHistoryActivity extends AppCompatActivity {
    public static final String EXTRA_KEY_CARID = "carId";
    RecyclerView rvLocationHistory;
    LocationScanAdapter locationScanAdapter;
    String carId;
    TextView tvNoDataFound;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Scan history");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvLocationHistory = (RecyclerView) findViewById(R.id.rvLocationHistory);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);
        try {
            Bundle extras = getIntent().getExtras();
            carId = extras.getString(EXTRA_KEY_CARID);
            Log.e("", "" + carId);
            getSearchData();
        } catch (Exception e) {
            Log.e("Exception", " " + e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    public void setResultView(List<ScanHistoryItem> resultView) {
        if (resultView != null && resultView.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvLocationHistory.setVisibility(View.VISIBLE);
            setHistoryList(resultView);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            rvLocationHistory.setVisibility(View.GONE);
            if (resultView != null) {
                locationScanAdapter.modifyDataSet(resultView);
            }
        }
    }

    void setHistoryList(List<ScanHistoryItem> scanList) {
        if (locationScanAdapter == null) {
            rvLocationHistory.setVisibility(View.VISIBLE);
            rvLocationHistory.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
            rvLocationHistory.setHasFixedSize(true);
            rvLocationHistory.setItemAnimator(new DefaultItemAnimator());
            rvLocationHistory.setLayoutManager(new LinearLayoutManager(this));
            locationScanAdapter = new LocationScanAdapter(this, scanList);
            rvLocationHistory.setAdapter(locationScanAdapter);
        } else {
            locationScanAdapter.notifyDataSetChanged();
        }
    }


    private void getSearchData() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_SCAN_HISTORY;

        final StringRequest reqSearchCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqList", " Response:" + response.toString());
                try {
                    ScanHistoryListResponse scanHistoryListResponse = ScanHistoryListResponse.parseJSON(response);

                    if (scanHistoryListResponse.getStatusCode() == 1) {
                    } else if (scanHistoryListResponse.getStatusCode() == 0) {
                    } else if (scanHistoryListResponse.getStatusCode() == 2) {
                    } else if (scanHistoryListResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {
                    }
                    pDialog.dismiss();
                    setResultView(scanHistoryListResponse.getScanList());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqSearchCarList", "catch");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqSearchCarList", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network error", "please check your internet connection try again");
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
//                params.put(ParamsKey.USER_ID, "34");
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.CAR_ID, carId);
//                params.put(ParamsKey.CAR_ID, "5941");
                Log.e("reqSearchCarList", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, "carScanList");
    }
    public void showTryAgainAlert(String title, String message) {
        CommonFunctions.showAlertWithNegativeButton(LocationScanHistoryActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(LocationScanHistoryActivity.this)) {
                    dialog.dismiss();
                    getSearchData();
                } else
                    CommonFunctions.showToast(getApplicationContext(),"Please check your internet connection");
            }
        });
    }
}
