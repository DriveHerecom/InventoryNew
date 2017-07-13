package com.creadigol.drivehere.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.CarImageAddResponse;
import com.creadigol.drivehere.Network.CarListResponse;
import com.creadigol.drivehere.Network.MultipartRequest;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.dialog.InputDialogListener;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.PinValidationDialogFragment;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.util.CircleView;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.ItemDecorationGrid;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_CAR = "car_obj";
    public static final String EXTRA_KEY_CAR_DETAIL_TYPE = "type";
    public final String TAG_DIALOG_PIN_VALIDATE = "Pin validation";
    private final String TAG = CarDetailActivity.class.getSimpleName();
    public  static boolean isNeeded=false;
    private final int REQUEST_ADD_IMAGES = 1001;

    private Car car;
    private CarAdd carAdd;
    private TextView tvStage, tvVacancy;
    private String type;
    private ArrayList<String> mImages;
    private RecyclerView rvImages;
    private ImageAdapter imageAdapter;

    private ImageView ivCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        car = (Car) bundle.getSerializable(EXTRA_KEY_CAR);
        type = bundle.getString(EXTRA_KEY_CAR_DETAIL_TYPE);
       // Log.e("type", "" + type);
        if (car == null) {
            return;
        }
        setCarDetail();

        findViewById(R.id.cv_edit_stage).setOnClickListener(this);
        findViewById(R.id.cv_auction).setOnClickListener(this);
        findViewById(R.id.cv_repo).setOnClickListener(this);
        findViewById(R.id.cv_data_one).setOnClickListener(this);
        findViewById(R.id.cv_done_date).setOnClickListener(this);
        findViewById(R.id.cv_add_car_image).setOnClickListener(this);
        findViewById(R.id.cv_edit_vacancy).setOnClickListener(this);
        findViewById(R.id.cv_edit_problem).setOnClickListener(this);

        ivCar = (ImageView) findViewById(R.id.iv_car);
        ivCar.setOnClickListener(this);

        GridLayoutManager lLayout = new GridLayoutManager(CarDetailActivity.this, getResources().getInteger(R.integer.photo_list_preview_columns));

        rvImages = (RecyclerView) findViewById(R.id.rv_images);
        rvImages.addItemDecoration(new ItemDecorationGrid(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(lLayout);

        findViewById(R.id.cl_btn_send_image).setOnClickListener(this);

    }

    public void setCarDetail() {
        getSupportActionBar().setTitle(car.getModelYear() + " " + car.getMake() + " " + car.getModel());
        ((TextView) findViewById(R.id.tv_vin)).setText(car.getVin());
        ((TextView) findViewById(R.id.tv_rfid)).setText(car.getRfid());
        ((TextView) findViewById(R.id.tv_miles)).setText(car.getMiles());
        ((TextView) findViewById(R.id.tv_stock_no)).setText(car.getStockNumber());
        tvStage = ((TextView) findViewById(R.id.tv_stage));
        tvStage.setText(car.getStage());

        tvVacancy = ((TextView) findViewById(R.id.tv_vacancy));
        tvVacancy.setText(car.getVacancy()); // vacancy need it from api

        TextView tvLotCode = (TextView) findViewById(R.id.tv_lot_code);
        tvLotCode.setText(car.getLotCode());

        String hasTitle = car.getHasTitle().trim();
        if (hasTitle.length() > 0 && hasTitle.equalsIgnoreCase("yes")) {
            tvLotCode.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvLotCode.setTextColor(getResources().getColor(R.color.red));
        }

        ConstraintLayout layTagScanDays = (ConstraintLayout) findViewById(R.id.lay_tag_scan_days);
        TextView tvTagScanDays = (TextView) findViewById(R.id.tv_tag_scan_days);

        int scanDays = (int) CommonFunctions.daysDifferent(car.getScanDate());
        if (scanDays > 0) {
            layTagScanDays.setVisibility(View.VISIBLE);
            tvTagScanDays.setText("Scan " + scanDays + " days ago");
        } else if (scanDays == -1) {
            layTagScanDays.setVisibility(View.VISIBLE);
            tvTagScanDays.setText("Not Scanned yet");
        } else if (scanDays == 0) {
            layTagScanDays.setVisibility(View.VISIBLE);
            tvTagScanDays.setText("Scan today");
        }

        setCarImageCount();

        String color = car.getColor().trim();
        TextView tvColor = (TextView) findViewById(R.id.tv_color);
        CircleView circleView = (CircleView) findViewById(R.id.view_color);
        if (color.length() > 0) {
            tvColor.setText(color);
            List<String> colorNames = Arrays.asList(getResources().getStringArray(R.array.ColorName));
            List<String> colorValues = Arrays.asList(getResources().getStringArray(R.array.ColorValue));


            if (colorNames.contains(color)) {
                circleView.drawAgain(colorValues.get(colorNames.indexOf(color)));
            } else {
                circleView.setVisibility(View.GONE);
            }

        } else {
            tvColor.setText("no color");
            circleView.setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.tv_note)).setText(car.getNote().trim());

        ImageView ivCar = (ImageView) findViewById(R.id.iv_car);
        if (car.getImages() != null && car.getImages().size() > 0)
            MyApplication.getInstance().getImageLoader().displayImage(car.getImages().get(0).getImage(), ivCar, getDisplayImageOptions());
        else
            MyApplication.getInstance().getImageLoader().displayImage("", ivCar, getDisplayImageOptions());


        //check for color and pic
        if (type != null && type.equalsIgnoreCase("scan")) {
            if (car.getColor().equalsIgnoreCase("") && car.getImageCount() == 0) {
                Color_Pic_Dialog("Car color and image are not available please add");
            }else
            if (car.getColor().equalsIgnoreCase("")) {
                Color_Pic_Dialog("Car color not available please add");
            }else if(car.getImageCount()==0){
                Color_Pic_Dialog("Car image not available please add");
            }
        }
        ConstraintLayout cv_Repo = (ConstraintLayout) findViewById(R.id.cn_Repo);
        ConstraintLayout cv_Auction = (ConstraintLayout) findViewById(R.id.cn_auction);
        if (type != null && type.equalsIgnoreCase("repo")) {
            cv_Repo.setVisibility(View.VISIBLE);
            cv_Auction.setVisibility(View.GONE);
            TextView tv_voluntary = (TextView) findViewById(R.id.tvVoluntary);
            TextView tv_Note = (TextView) findViewById(R.id.tv_Note);
            TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
            TextView tv_Repo_company = (TextView) findViewById(R.id.tv_Repo_company);
            TextView tv_assigndate = (TextView) findViewById(R.id.tv_assigndate);
            TextView tv_deliverDate = (TextView) findViewById(R.id.tv_deliverDate);
            tv_voluntary.setText(car.getRepoDetail().getIsVoluntary());
            tv_Repo_company.setText(car.getRepoDetail().getCompany());
            tv_Note.setText(car.getRepoDetail().getNote());
            tvStatus.setText(car.getRepoDetail().getStatus());
            tv_assigndate.setText(CommonFunctions.getDate(Long.parseLong(car.getRepoDetail().getAssignedDate())));
            tv_deliverDate.setText(CommonFunctions.getDate(Long.parseLong(car.getRepoDetail().getDeliveredDate())));
        } else if (type != null && type.equalsIgnoreCase("auction")) {
            cv_Repo.setVisibility(View.GONE);
            cv_Auction.setVisibility(View.VISIBLE);
            TextView tv_auctionName = (TextView) findViewById(R.id.tvAuctionName);
            TextView tv_auctionNote = (TextView) findViewById(R.id.tvAuctionNote);
            TextView tv_auctionDate = (TextView) findViewById(R.id.tvAuctionDate);
            TextView tv_carReady = (TextView) findViewById(R.id.tvReady);
            TextView tvFloorPrice = (TextView) findViewById(R.id.tvFloorPrice);
            TextView tvAuctionMile = (TextView) findViewById(R.id.tvAuctionMile);
            TextView tvCarAtAuction = (TextView) findViewById(R.id.tvCarAtAuction);
            TextView tvCondition = (TextView) findViewById(R.id.tvCondition);
            tv_auctionName.setText(car.getAuctionDetails().getAuctionName());
            tv_auctionNote.setText(car.getAuctionDetails().getAuctionNote());
            tvFloorPrice.setText(car.getAuctionDetails().getFloorPrice());
            tvAuctionMile.setText(car.getAuctionDetails().getAuctionMile());
            tvCarAtAuction.setText(car.getAuctionDetails().getCarAtAuction());
            tvCondition.setText(car.getAuctionDetails().getCondition());
            tv_auctionDate.setText(CommonFunctions.getDate(Long.parseLong(car.getAuctionDetails().getAuctiondate())));
            tv_carReady.setText(car.getAuctionDetails().getCarReady());
        } else {
            cv_Repo.setVisibility(View.GONE);
            cv_Auction.setVisibility(View.GONE);
        }
    }

    public void setCarImageCount() {
        if (car.getImages() != null && car.getImages().size() > 0) {
            ((TextView) findViewById(R.id.tv_image_count)).setText("Count:" + String.valueOf(car.getImages().size()));
        } else
            findViewById(R.id.tv_image_count).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.action_edit) {
            // open car edit activity
            Intent intentCarEdit = new Intent(CarDetailActivity.this, CarAddActivity.class);
            intentCarEdit.putExtra(CarAddActivity.EXTRA_KEY_IS_EDIT, true);
            intentCarEdit.putExtra(CarAddActivity.EXTRA_KEY_CAR_ID, car.getCarId());
            startActivity(intentCarEdit);
        } else if (item.getItemId() == R.id.action_scan_history) {
            Intent intent = new Intent(CarDetailActivity.this, LocationScanHistoryActivity.class);
            intent.putExtra(LocationScanHistoryActivity.EXTRA_KEY_CARID, car.getCarId());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_edit_stage:
                showStageDialog();
                break;

            case R.id.cv_edit_vacancy:
                showVacancyDialog();
                break;

            case R.id.cv_edit_problem:
                // change problem dialog
                showProblemDialog();
                break;

            case R.id.cv_auction:
                Intent intentAuction = new Intent(CarDetailActivity.this, AddAuctionActivity.class);
                intentAuction.putExtra(AddAuctionActivity.EXTRA_KEY_CAR_ID, car.getCarId());
                startActivity(intentAuction);
                break;

            case R.id.cv_repo:
                Intent intentRepo = new Intent(CarDetailActivity.this, RepoAddActivity.class);
                intentRepo.putExtra(RepoAddActivity.EXTRA_KEY_CAR_ID, car.getCarId());
                startActivity(intentRepo);
                break;

            case R.id.cv_data_one:
                Intent intentDataOne = new Intent(CarDetailActivity.this, DataOneActivity.class);
                intentDataOne.putExtra(DataOneActivity.EXTRA_KEY_CAR_ID, car.getCarId());
                intentDataOne.putExtra(DataOneActivity.EXTRA_KEY_VIN, car.getVin());
                startActivity(intentDataOne);
                break;

            case R.id.cv_done_date:
                Intent intentChangeDoneDate = new Intent(CarDetailActivity.this, ChangeDoneDateActivity.class);
                intentChangeDoneDate.putExtra(ChangeDoneDateActivity.EXTRA_KEY_CAR_ID, car.getCarId());
                startActivity(intentChangeDoneDate);
                break;

            case R.id.cv_add_car_image:
                // show add images activity
                //CommonFunctions.showToast(this, "Click on add car images");
                Intent intentAddImages = new Intent(CarDetailActivity.this, AddImagesActivity.class);
                intentAddImages.putStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES, mImages);
                startActivityForResult(intentAddImages, REQUEST_ADD_IMAGES);
                break;

            case R.id.cl_btn_send_image:
                // send image to server
                // CommonFunctions.showToast(this, "Click on add car images");
                addImages(getSendImagesParams());
                break;

            case R.id.iv_car:
                Intent intentImagePager = new Intent(CarDetailActivity.this, ImagePagerActivity.class);
                // add put extra
                Bundle args = new Bundle();
                args.putSerializable(ImagePagerActivity.EXTRA_KEY_IMAGES, (Serializable) car.getImages());
                args.putString(ImagePagerActivity.EXTRA_KEY_TITLE, car.getModelYear() + " " + car.getMake() + " " + car.getModel());
                intentImagePager.putExtra("BUNDLE", args);

                //intentImagePager.putExtra(ImagePagerActivity.EXTRA_KEY_IMAGES, car.getLocalImages());
                startActivity(intentImagePager);
                break;
        }
    }

    public void showVacancyDialog() {

        final String[] vacancy = getResources().getStringArray(R.array.Vacancy);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                Log.e("Edit Vacancy", tag + " - " + vacancy[position]);
                if (!car.getVacancy().equalsIgnoreCase(vacancy[position])) {
//                    reqChangeVacancy(vacancy[position]);
                    showPinValidateDialog(vacancy[position]);
                }
                //tvStage.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, vacancy, getString(R.string.vacancy),
                "Select " + getString(R.string.vacancy), "Current vacancy is " + car.getVacancy());

        stageDialog.show(getSupportFragmentManager(), getString(R.string.vacancy));

    }

    public void showPinValidateDialog(final String values) {

        final InputDialogListener listener = new InputDialogListener() {

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                finish();
            }

            @Override
            public void onDialogPositiveClick(String pin) {
                if (pin.length() > 0 && pin.equals(Constant.ADMIN_PIN)) {
                    reqChangeVacancy(values);
                }
            }
        };

        PinValidationDialogFragment dialog1 = new PinValidationDialogFragment(listener, TAG_DIALOG_PIN_VALIDATE);
        dialog1.show(getSupportFragmentManager(), TAG_DIALOG_PIN_VALIDATE);
    }

    public void reqChangeVacancy(final String vacancy) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_EDIT_VACANCY;
        final StringRequest reqEditVacancy = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqEditVacancy Response", response.toString());
                //pDialog.hide();
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);

                    if (basicResponse.getStatusCode() == 1) {
                        // set list of cars
                        car.setVacancy(vacancy);
                        tvVacancy.setText(car.getVacancy());
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 9) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else {
                        CommonFunctions.showToast(CarDetailActivity.this, response);
                    }

                } catch (Exception e) {
                    CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
                    e.printStackTrace();
                    Log.e("reqEditVacancy Error_in", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqEditVacancy", "Error Response: " + error.getMessage());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() { // chang params of vacancy
                Map<String, String> params = new HashMap<>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.VACANCY, vacancy);
                params.put(ParamsKey.CAR_ID, car.getCarId());
                Log.e("reqEditVacancy", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqEditVacancy, TAG);
    }

    public void showProblemDialog() {

        final String[] problems = getResources().getStringArray(R.array.Problem);

        final ListDialogListener listener = new ListDialogListener() {
            @Override
            public void onItemClick(int position, String tag) {
                Log.e("Edit Problem", tag + " - " + problems[position]);
                if (!car.getProblem().equalsIgnoreCase(problems[position])) {
                    reqChangeProblem(problems[position]);
                }
                //tvStage.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }
        };

        String description;
        String currentProblem = car.getProblem().trim();
        if (currentProblem.length() > 0 && !currentProblem.equalsIgnoreCase(problems[0])) {
            description = "Current problem is " + car.getProblem();
        } else {
            description = "Currently car has no problem.";
        }

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, problems, getString(R.string.problem), "Select " + getString(R.string.problem), description);
        stageDialog.show(getSupportFragmentManager(), getString(R.string.problem));

    }

    public void reqChangeProblem(final String problem) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_EDIT_PROBLEM;
        final StringRequest reqProblem = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqProblem Response", response.toString());
                //pDialog.hide();
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);

                    if (basicResponse.getStatusCode() == 1) {
                        // set list of cars
                        car.setProblem(problem);
//                        tvStage.setText(car.getStage());
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 9) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else {
                        CommonFunctions.showToast(CarDetailActivity.this, response);
                    }

                } catch (Exception e) {
                    CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
                    e.printStackTrace();
                    Log.e("reqProblem Error_in", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqProblem", "Error Response: " + error.getMessage());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.PROBLEM, problem);
                params.put(ParamsKey.CAR_ID, car.getCarId());
                Log.e("reqProblem", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqProblem, TAG);
    }

    public void showStageDialog() {

        final String[] stages = getResources().getStringArray(R.array.Stage);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                Log.e("Edit Stage", tag + " - " + stages[position]);
                if (!car.getStage().equalsIgnoreCase(stages[position])) {
                    reqChangeStage(stages[position]);
                }
                //tvStage.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, stages, getString(R.string.stage), "Select " + getString(R.string.stage), "Current stage is " + car.getStage());
        stageDialog.show(getSupportFragmentManager(), getString(R.string.stage));

    }

    public void reqChangeStage(final String stage) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_EDIT_STAGE;
        final StringRequest myTagReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Car Response", response.toString());
                //pDialog.hide();
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);

                    if (basicResponse.getStatusCode() == 1) {
                        // set list of cars
                        car.setStage(stage);
                        tvStage.setText(car.getStage());
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 9) {
                        CommonFunctions.showToast(CarDetailActivity.this, basicResponse.getMessage());
                    } else {
                        CommonFunctions.showToast(CarDetailActivity.this, response);
                    }

                } catch (Exception e) {
                    CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
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
                CommonFunctions.showToast(CarDetailActivity.this, getString(R.string.network_error));
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.EDIT_STAGE, stage);
                params.put(ParamsKey.CAR_ID, car.getCarId());
                Log.e("MyTags", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(myTagReq, TAG);
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_car)
                .showImageOnFail(R.drawable.default_car)
                .showImageOnLoading(R.drawable.default_car).build();
        return options;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_IMAGES && resultCode == Activity.RESULT_OK) {
            mImages = data.getStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES);

            if (mImages != null && mImages.size() > 0) {
                // set images
                setImages();
            } else {
                findViewById(R.id.cl_images).setVisibility(View.GONE);
            }
        }
    }

    public void setImages() {
        if (mImages != null && mImages.size() > 0) {
            findViewById(R.id.cl_images).setVisibility(View.VISIBLE);
            if (imageAdapter == null) {
                imageAdapter = new ImageAdapter();
                rvImages.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        } else {
            findViewById(R.id.cl_images).setVisibility(View.GONE);
        }
    }

    private HashMap<String, String> getSendImagesParams() {
        HashMap<String, String> hashParams = new HashMap<>();

        hashParams.put(ParamsKey.CAR_ID, car.getCarId());
        hashParams.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
        hashParams.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);

        return hashParams;
    }

    void addImages(HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait, uploading images ...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_CAR_ADD_IMAGES;

        MultipartRequest reqAddCarNewImages = new MultipartRequest(url, hashParams, mImages,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reqAddCarNewImages", "Response:" + response.toString());
                        //pDialog.hide();
                        try {
                            CarImageAddResponse carImageAddResponse = CarImageAddResponse.parseJSON(response);

                            if (carImageAddResponse.getStatusCode() == 1) {
                                // set list of cars
                                CommonFunctions.showToast(CarDetailActivity.this, carImageAddResponse.getMessage());
                                if (carImageAddResponse.getImages() != null && carImageAddResponse.getImages().size() > 0) {
                                    car.addImages(carImageAddResponse.getImages());
                                    car.setImageCount(car.getImages().size());
                                }
                                setCarImageCount();
                                mImages = null;
                                setImages();
                            } else if (carImageAddResponse.getStatusCode() == 0) {
                                CommonFunctions.showToast(CarDetailActivity.this, carImageAddResponse.getMessage());
                            } else if (carImageAddResponse.getStatusCode() == 2) {
                                CommonFunctions.showToast(CarDetailActivity.this, carImageAddResponse.getMessage());
                            } else if (carImageAddResponse.getStatusCode() == 4) {
                                CommonFunctions.showToast(CarDetailActivity.this, carImageAddResponse.getMessage());
                                // TODO Block user by admin or user not valid
                            } else {
                                CommonFunctions.showToast(CarDetailActivity.this, carImageAddResponse.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("reqAddCarNewImages", "catch");
                        }
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("reqAddCarNewImages", "Error Response: " + error.getMessage());
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        //showTryAgainAlert("Info", "Network error, Please try again!");
                    }

                });

        MyApplication.getInstance().addToRequestQueue(reqAddCarNewImages, TAG);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (mImages == null || mImages.size() <= 0)
            this.finish();
        else {
            DialogInterface.OnClickListener onClickUpload = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addImages(getSendImagesParams());
                }
            };

            DialogInterface.OnClickListener onClickExit = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CarDetailActivity.this.finish();
                }
            };
            CommonFunctions.showAlert(this, "Info", "Are you sure, want to exit without upload new car images?", "UPLOAD", "EXIT", onClickUpload, onClickExit);
        }

    }

    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        public ImageAdapter() {
            this.context = CarDetailActivity.this;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, viewGroup, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
            // set default_car data
            String imagePath = mImages.get(i);

            if (imagePath != null && imagePath.trim().length() > 0)
                MyApplication.getInstance().getImageLoader().displayImage("file://" + imagePath, imageViewHolder.ivImage, getDisplayImageOptions());
            else
                MyApplication.getInstance().getImageLoader().displayImage("", imageViewHolder.ivImage, getDisplayImageOptions());

            imageViewHolder.clImage.setTag(i);

        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }

        public DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.default_car)
                    .showImageOnFail(R.drawable.default_car)
                    .showImageOnLoading(R.drawable.default_car).build();
            return options;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImage;
            public ConstraintLayout clImage;

            public ImageViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
                clImage = (ConstraintLayout) itemView.findViewById(R.id.cl_image);
            }
        }
    }

    void Color_Pic_Dialog(final String msg) {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("Car info");
        alert.setCancelable(false);
        alert.setMessage(""+msg);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNeeded==true){
            isNeeded=false;
            getCarDetail();

        }
    }


    private void getCarDetail() {
        final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            pDialog.setCancelable(false);

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
                        car=carListResponse.getCarList().get(0);
                        setCarDetail();
                    } else if (carListResponse.getStatusCode() == 0) {

                    } else if (carListResponse.getStatusCode() == 2) {

                    } else if (carListResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqSearchCarList", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqSearchCarList", "Error Response: " + error.getMessage());
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
                params.put(ParamsKey.PAGE_INDEX, "1");
                params.put(ParamsKey.VIN, car.getVin());
                Log.e("reqSearchCarList", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, TAG);
    }
}
