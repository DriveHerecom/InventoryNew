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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.AuctionReport;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.AuctionReportResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.AuctionReportAdapter;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.DividerItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionReportActivity extends AppCompatActivity {

    private final String TAG = AuctionReportActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private AuctionReportAdapter auctionReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_auction_list_main);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAuctionData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAuctionData() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);
        String url = AppUrl.URL_AUCTION_REPORT;
        Log.e("reqAuctionReport", url);
        final StringRequest reqAuctionReport = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqAuctionReport", "Response " + response.toString());
                //pDialog.hide();
                try {
                    AuctionReportResponse auctionReportResponse = AuctionReportResponse.parseJSON(response);

                    if (auctionReportResponse.getStatusCode() == 1) {
                        // set list of cars

                        //setCars(auctionReportResponse.getCarList());
                    } else if (auctionReportResponse.getStatusCode() == 0) {

                    } else if (auctionReportResponse.getStatusCode() == 2) {

                    } else if (auctionReportResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }
                    setAuction(auctionReportResponse.getAuctionReportList());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqAuctionReport", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqAuctionReport", "Error Response: " + error.getMessage());
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
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                Log.e("reqAuctionReport", "Posting params: " + params.toString());
                return params;
            }
        };

        reqAuctionReport.setRetryPolicy(new DefaultRetryPolicy(
                Constant.SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES + 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        reqAuctionReport.setShouldCache(false);

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqAuctionReport, TAG);
    }

    private void setAuction(List<AuctionReport> auctionReports) {

        if (auctionReports != null && auctionReports.size() > 0) {
            auctionReportAdapter = new AuctionReportAdapter(AuctionReportActivity.this, auctionReports, null);
            mRecyclerView.setAdapter(auctionReportAdapter);
        } else {
            // TODO display no auction found
            findViewById(R.id.cl_no_car_found).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


    }
    public void showTryAgainAlert(String title, String message) {
        CommonFunctions.showAlertWithNegativeButton(AuctionReportActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(AuctionReportActivity.this)) {
                    dialog.dismiss();
                    getAuctionData();
                } else
                    CommonFunctions.showToast(getApplicationContext(),"Please check your internet connection");
            }
        });
    }
}
