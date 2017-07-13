package com.creadigol.drivehere.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.creadigol.drivehere.util.CommonFunctions;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.CarAuctionListResponse;
import com.creadigol.drivehere.Network.CarListResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.Network.RepoCarListResponse;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.CarAdapter;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.LotCodeDialogFragment;
import com.creadigol.drivehere.util.DividerItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_PARAMS = "params_hash";
    public static final String EXTRA_KEY_LIST_TYPE = "list_type";
    public static final String EXTRA_KEY_SCAN_CHECK = "scan_check";
    private final String TAG_DIALOG_LOT_CODE = "Lot code";

    private final String TAG = CarListActivity.class.getSimpleName();
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private CarAdapter carAdapter;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;
    private boolean isLoading;
    private int pageIndex = 0;
    private List<Car> cars;
    private int selectedIndex;
    public String type,vin;
    private Button btnAddCar;

    View.OnClickListener onCarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Intent carDetail = new Intent(CarListActivity.this, CarDetailActivity.class);
            carDetail.putExtra(CarDetailActivity.EXTRA_KEY_CAR, cars.get(position));
            carDetail.putExtra(CarDetailActivity.EXTRA_KEY_CAR_DETAIL_TYPE, type);
            startActivity(carDetail);
        }
    };

    private boolean isLoadMore = true;
    private LIST_TYPE listType;
    private HashMap<String, String> hashParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cars");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        hashParams = (HashMap<String, String>) intent.getSerializableExtra(EXTRA_KEY_PARAMS);
        listType = (LIST_TYPE) intent.getSerializableExtra(EXTRA_KEY_LIST_TYPE);

        Log.e(TAG, "listType" + listType.toString());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (listType == LIST_TYPE.Search) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (isLoadMore) {
                        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            isLoading = true;
                            getSearchData(++pageIndex, hashParams);
                        }
                    }
                }
            });
        }

        if (listType == LIST_TYPE.Search)
            getSearchData(++pageIndex, hashParams);
        else if (listType == LIST_TYPE.Auction)
            getAuctionData(hashParams);
        else if (listType == LIST_TYPE.Repo)
            getRepoCars(hashParams);


        btnAddCar=(Button) findViewById(R.id.btnAddCar);
        btnAddCar.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.action_filter) {
            showLotCodeDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (listType == LIST_TYPE.Auction || listType == LIST_TYPE.Repo)
            getMenuInflater().inflate(R.menu.filter, menu);//Menu Resource, Menu
        return true;
    }

    // Get search car list from server
    private void getSearchData(final int pageIndex, final HashMap<String, String> hashSearch) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        if (pageIndex == 1) {
            pDialog.setMessage("Loading...");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        String url = AppUrl.URL_CAR_SEARCH;

        final StringRequest reqSearchCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqSearchCarList", " Response:" + response.toString());
                //pDialog.hide();
                try {
                    CarListResponse carListResponse = CarListResponse.parseJSON(response);

                    if (carListResponse.getStatusCode() == 1) {
                        // set list of cars
                        toolbar.setTitle("Cars (" + carListResponse.getCount() + ")");
                        //setCars(carListResponse.getCarList());
                    } else if (carListResponse.getStatusCode() == 0) {

                    } else if (carListResponse.getStatusCode() == 2) {

                    } else if (carListResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }
                    setSearchCars(carListResponse.getCarList());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqSearchCarList", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqSearchCarList", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network error", "please check your internet connection try again", pageIndex, hashSearch, "search");
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.PAGE_INDEX, String.valueOf(pageIndex));
                params.putAll(hashSearch);
                Log.e("reqSearchCarList", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, TAG);
    }

    // Set Search car list
    private void setSearchCars(List<Car> cars) {

        if (cars != null && cars.size() > 0) {
            cars.add(null);
            if (carAdapter == null) {
                this.cars = cars;
                type = "search";
                carAdapter = new CarAdapter(CarListActivity.this, this.cars, onCarClickListener, type);
                mRecyclerView.setAdapter(carAdapter);
            } else {
                if (this.cars.get(this.cars.size() - 1) == null) {
                    this.cars.remove(this.cars.size() - 1);
                    carAdapter.notifyItemRemoved(this.cars.size());
                }
                this.cars.addAll(cars);
                carAdapter.notifyDataSetChanged(this.cars);
            }
        } else {
            isLoadMore = false;
            if (this.cars == null || this.cars.size() <= 0) {
                // TODO display no car found
                viewNoData();
                mRecyclerView.setVisibility(View.GONE);
            } else if (this.cars.get(this.cars.size() - 1) == null) {
                this.cars.remove(this.cars.size() - 1);
                carAdapter.notifyItemRemoved(this.cars.size());
            }
        }
    }

    void viewNoData(){
        findViewById(R.id.cl_no_car_found).setVisibility(View.VISIBLE);
        vin = hashParams.get(ParamsKey.VIN).toString();
        if(vin.length()>0)
            btnAddCar.setVisibility(View.VISIBLE);
    }

    // Set Search car list
    private void setAuctionCars(List<Car> cars) {

        if (cars != null && cars.size() > 0) {
            findViewById(R.id.cl_no_car_found).setVisibility(View.GONE);
            btnAddCar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (carAdapter == null) {
                this.cars = cars;
                carAdapter = new CarAdapter(CarListActivity.this, this.cars, onCarClickListener, type);
                mRecyclerView.setAdapter(carAdapter);
            } else {
                this.cars = cars;
                carAdapter.notifyDataSetChanged(this.cars);
            }
        } else {
            // TODO display no car found
           viewNoData();
//            findViewById(R.id.cl_no_car_found).setVisibility(View.VISIBLE);
//            findViewById(R.id.cl_addCar).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    // Get auction car list from server

    /**
     * @param hashParams params for request auction car list from server
     */
    private void getAuctionData(final HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_AUCTION_CAR_LIST;

        final StringRequest reqAuctionCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqAuctionCarList", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    CarAuctionListResponse carAuctionListResponse = CarAuctionListResponse.parseJSON(response);

                    if (carAuctionListResponse.getStatusCode() == 1) {
                        // set list of cars
                        if (carAuctionListResponse.getCount() > 0)
                            toolbar.setTitle("Cars (" + carAuctionListResponse.getCount() + ")");
                        else
                            toolbar.setTitle("Cars");
                        //setCars(carAuctionListResponse.getCarList());
                        type = "auction";
                    } else if (carAuctionListResponse.getStatusCode() == 0) {

                    } else if (carAuctionListResponse.getStatusCode() == 2) {

                    } else if (carAuctionListResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }
                    setAuctionCars(carAuctionListResponse.getCarList());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqAuctionCarList", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqAuctionCarList", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network error", "please check your internet connection try again", 0, hashParams, "auction");
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.putAll(hashParams);
                Log.e("reqAuctionCarList", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqAuctionCarList, TAG);
    }


    // Get repo car list from server

    /**
     * @param hashParams params for request repo car list from server
     */
    private void getRepoCars(final HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_REPO_CAR_LIST;

        final StringRequest reqRepoCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqRepoCarList", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    RepoCarListResponse repoCarListResponse = RepoCarListResponse.parseJSON(response);

                    if (repoCarListResponse.getStatusCode() == 1) {
                        // set list of cars
                        if (repoCarListResponse.getCount() > 0)
                            toolbar.setTitle("Cars (" + repoCarListResponse.getCount() + ")");
                        else
                            toolbar.setTitle("Cars");
                        //setCars(repoCarListResponse.getCarList());
                        type = "repo";
                    } else if (repoCarListResponse.getStatusCode() == 0) {

                    } else if (repoCarListResponse.getStatusCode() == 2) {

                    } else if (repoCarListResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }
                    setAuctionCars(repoCarListResponse.getCarList());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqRepoCarList", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqRepoCarList", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network error", "please check your internet connection try again", 0, hashParams, "repo");
                if (pDialog.isShowing())
                    pDialog.dismiss();
                isLoading = false;
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.putAll(hashParams);
                Log.e("reqRepoCarList", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqRepoCarList, TAG);
    }

    public void showLotCodeDialog() {

        final String[] lotList = getResources().getStringArray(R.array.Lotcode);
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                // filter list by selected lot code
                if (listType == LIST_TYPE.Auction) {
                    if (hashParams.containsKey(ParamsKey.LOT_CODE))
                        hashParams.remove(ParamsKey.LOT_CODE);
                    hashParams.put(ParamsKey.LOT_CODE, lotList[position]);
                    getAuctionData(hashParams);
                } else if (listType == LIST_TYPE.Repo) {
                    if (hashParams.containsKey(ParamsKey.REPO_LOT_CODE))
                        hashParams.remove(ParamsKey.REPO_LOT_CODE);
                    hashParams.put(ParamsKey.REPO_LOT_CODE, lotList[position]);
                    getRepoCars(hashParams);
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, TAG_DIALOG_LOT_CODE);
        dialog1.show(getSupportFragmentManager(), TAG_DIALOG_LOT_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCar:
                Intent intentAddCar=new Intent(CarListActivity.this,CarAddActivity.class);
                intentAddCar.putExtra(CarAddActivity.EXTRA_KEY_CODE,vin);
                intentAddCar.putExtra(CarAddActivity.EXTRA_KEY_IS_VIN, true);
                startActivity(intentAddCar);
                finish();
                break;
        }
    }

    // Type of the list
    public enum LIST_TYPE {
        Search, Auction, Repo
    }

    public void showTryAgainAlert(String title, String message, final int pageIndex, final HashMap<String, String> hashSearch, final String type) {
        CommonFunctions.showAlertWithNegativeButton(CarListActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(CarListActivity.this)) {
                    dialog.dismiss();
                    if (type.equalsIgnoreCase("search")) {
                        getSearchData(pageIndex, hashSearch);
                    } else if (type.equalsIgnoreCase("auction")) {
                        getAuctionData(hashSearch);
                    } else if (type.equalsIgnoreCase("repo")) {
                        getRepoCars(hashSearch);
                    }
                } else
                    CommonFunctions.showToast(getApplicationContext(), "Please check your internet connection");
            }
        });
    }
}
