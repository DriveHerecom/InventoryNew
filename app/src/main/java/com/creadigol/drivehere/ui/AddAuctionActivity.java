package com.creadigol.drivehere.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.Auction;
import com.creadigol.drivehere.Model.Repo;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.AuctionDetailResponse;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.MultipartRequest;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.Network.RepoDetailResponse;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.ImageAdapter;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.ItemDecorationGrid;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAuctionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_CAR_ID = "car_id";
    private final String TAG = AddAuctionActivity.class.getSimpleName();
    private final String TAG_DIALOG_AUCTION_NAME = "Auction name";
    private final String TAG_DIALOG_CONDITION = "Condition";

    private final int REQUEST_ADD_IMAGES = 1001;
    RecyclerView rvImages;
    TextWatcher twNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String note = s.toString().trim();
            if (note.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_note_hint)).setText(getString(R.string.note));
            } else {
                ((TextView) findViewById(R.id.tv_note_hint)).setText("Enter");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher twMiles = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String note = s.toString().trim();
            if (note.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_miles_hint)).setText(getString(R.string.miles));
            } else {
                ((TextView) findViewById(R.id.tv_miles_hint)).setText("Enter");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher twFloorPrice = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String note = s.toString().trim();
            if (note.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_floor_price_hint)).setText(getString(R.string.floor_price));
            } else {
                ((TextView) findViewById(R.id.tv_floor_price_hint)).setText("Enter");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView tvAuctionName, tvCondition, tvCarReadyForAuction, tvCarAtAuction, tvAuctionDate;
    /*private TextView tvAuctionNameHint, tvConditionHint, tvCarReadyForAuctionHint,
            tvCarAtAuctionHint, tvAuctionDateHint, tvFloorPriceHint, tvMilesHint, tvNoteHint;*/
    private EditText edtFloorPrice, edtMiles, edtNote;
    private ArrayList<String> mImages;
    private ImageAdapter imageAdapter;
    private String carId = "";

    public void showAuctionNameDialog() {

        final String[] auctionList = getResources().getStringArray(R.array.auction_name);
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setAuctionName(auctionList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, auctionList, TAG_DIALOG_AUCTION_NAME, "Select " + TAG_DIALOG_AUCTION_NAME, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_AUCTION_NAME);
    }

    public void showConditionDialog() {

        final String[] list = new String[]{"1", "2", "3", "4", "5"};
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setCondition(list[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, list, TAG_DIALOG_CONDITION, "Select " + TAG_DIALOG_CONDITION, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_CONDITION);
    }

    public void showCarReadyForAuctionDialog() {

        final String[] auctionList = new String[]{"Yes", "No"};
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setCarReadyForAuction(auctionList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, auctionList, TAG_DIALOG_CONDITION, "Select " + TAG_DIALOG_CONDITION, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_CONDITION);
    }

    public void showCarAtAuctionDialog() {

        final String[] auctionList = new String[]{"Yes", "No"};
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setCarAtAuction(auctionList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, auctionList, TAG_DIALOG_CONDITION, "Select " + TAG_DIALOG_CONDITION, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_CONDITION);
    }

    void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog dpd = new DatePickerDialog(AddAuctionActivity.this,
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

                        setAuctionDate(year + "-" + month + "-" + day);

                    }
                }, mYear, mMonth, mDay);
        //dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        carId = bundle.getString(EXTRA_KEY_CAR_ID, "");

        tvAuctionName = (TextView) findViewById(R.id.tv_auction_name);
        tvCondition = (TextView) findViewById(R.id.tv_condition);
        tvCarReadyForAuction = (TextView) findViewById(R.id.tv_ready_for_auction);
        tvCarAtAuction = (TextView) findViewById(R.id.tv_car_at_auction);
        tvAuctionDate = (TextView) findViewById(R.id.tv_auction_date);


        edtFloorPrice = (EditText) findViewById(R.id.edt_floor_price);
        edtMiles = (EditText) findViewById(R.id.edt_miles);
        edtNote = (EditText) findViewById(R.id.edt_note);

        edtNote.addTextChangedListener(twNote);
        edtMiles.addTextChangedListener(twMiles);
        edtFloorPrice.addTextChangedListener(twFloorPrice);

        findViewById(R.id.cl_auction_name).setOnClickListener(this);
        findViewById(R.id.cl_floor_price).setOnClickListener(this);
        findViewById(R.id.cl_condition).setOnClickListener(this);
        findViewById(R.id.cl_miles).setOnClickListener(this);
        findViewById(R.id.cl_note).setOnClickListener(this);
        findViewById(R.id.cl_auction_date).setOnClickListener(this);
        findViewById(R.id.cl_car_at_auction).setOnClickListener(this);
        findViewById(R.id.cl_ready_for_auction).setOnClickListener(this);
        findViewById(R.id.cl_add_images).setOnClickListener(this);
        findViewById(R.id.cl_btn_add_auction).setOnClickListener(this);

        GridLayoutManager lLayout = new GridLayoutManager(AddAuctionActivity.this, getResources().getInteger(R.integer.photo_list_preview_columns));

        rvImages = (RecyclerView) findViewById(R.id.rv_images);
        rvImages.addItemDecoration(new ItemDecorationGrid(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(lLayout);

        getAuctionDetail(carId);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_auction_name:
                showAuctionNameDialog();
                break;

            case R.id.cl_condition:
                showConditionDialog();
                break;

            case R.id.cl_auction_date:
                openDatePicker();
                break;

            case R.id.cl_ready_for_auction:
                showCarReadyForAuctionDialog();
                break;

            case R.id.cl_car_at_auction:
                showCarAtAuctionDialog();
                break;

            case R.id.cl_add_images:
                Intent intentAddImages = new Intent(AddAuctionActivity.this, AddImagesActivity.class);
                intentAddImages.putStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES, mImages);
                startActivityForResult(intentAddImages, REQUEST_ADD_IMAGES);
                break;

            case R.id.cl_btn_add_auction:
                // send auction detail to server
                addAuction(getAuctionParams());
                break;
        }
    }

    private HashMap<String, String> getAuctionParams() {
        HashMap<String, String> hashParams = new HashMap<>();

        hashParams.put(ParamsKey.CAR_ID, carId);
        hashParams.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
        hashParams.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);

        String floorPrice = edtFloorPrice.getText().toString();
        if (floorPrice.trim().length() > 0) {
            hashParams.put(ParamsKey.FLOOR_PRICE, floorPrice);
        }

        String auctionName = tvAuctionName.getText().toString().trim();
        if (auctionName != null && auctionName.length() > 0
                && !auctionName.equalsIgnoreCase(getResources().getString(R.string.auction_name))) {
            hashParams.put(ParamsKey.AUCTION_NAME, auctionName.trim());
        }

        String condition = tvCondition.getText().toString().trim();
        if (condition != null && condition.length() > 0
                && !condition.equalsIgnoreCase(getResources().getString(R.string.condition))) {
            hashParams.put(ParamsKey.CONDITION, condition.trim());
        }

        String miles = edtMiles.getText().toString().trim();
        if (miles != null && miles.length() > 0) {
            hashParams.put(ParamsKey.MILES, miles.trim());
        }

        String note = edtNote.getText().toString().trim();
        if (note != null && note.length() > 0
                && !note.equalsIgnoreCase(getResources().getString(R.string.note))) {
            hashParams.put(ParamsKey.AUCTION_NOTE, note.trim());
        }

        String auctionDate = tvAuctionDate.getText().toString().trim();
        if (auctionDate != null && auctionDate.length() > 0
                && !auctionDate.equalsIgnoreCase(getResources().getString(R.string.auction_date))) {
            long date = CommonFunctions.getMilliseconds(auctionDate.trim(), "yyyy-MM-dd");
            if (date > 0)
                hashParams.put(ParamsKey.AUCTION_DATE, String.valueOf(date));
        }

        String carReadyForAuction = tvCarReadyForAuction.getText().toString().trim();
        if (carReadyForAuction != null && carReadyForAuction.length() > 0) {
            hashParams.put(ParamsKey.CAR_READY_FOR_AUCTION, carReadyForAuction.trim());
        }

        String carAtAuction = tvCarAtAuction.getText().toString().trim();
        if (carAtAuction != null && carAtAuction.length() > 0) {
            hashParams.put(ParamsKey.CAR_AT_AUCTION, carAtAuction.trim());
        }

        return hashParams;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_IMAGES && resultCode == Activity.RESULT_OK) {
            mImages = data.getStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES);

            if (mImages != null && mImages.size() > 0) {
                // set images
                setImages();
            }
        }
    }

    public void setImages() {
        if (mImages != null && mImages.size() > 0) {

            findViewById(R.id.cl_images).setVisibility(View.VISIBLE);
            if (imageAdapter == null) {
                imageAdapter = new ImageAdapter(AddAuctionActivity.this, mImages);
                rvImages.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        } else {
            findViewById(R.id.cl_images).setVisibility(View.GONE);
        }
    }

    void addAuction(HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_AUCTION_ADD;

        MultipartRequest reqAddAuction = new MultipartRequest(url, hashParams, mImages,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reqAddAuction Response", response.toString());
                        //pDialog.hide();
                        try {
                            BasicResponse basicResponse = BasicResponse.parseJSON(response);

                            if (basicResponse.getStatusCode() == 1) {
                                // set list of cars
                                CommonFunctions.showToast(AddAuctionActivity.this, basicResponse.getMessage());
                                finish();
                            } else if (basicResponse.getStatusCode() == 0) {
                                CommonFunctions.showToast(AddAuctionActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 2) {
                                CommonFunctions.showToast(AddAuctionActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 4) {
                                CommonFunctions.showToast(AddAuctionActivity.this, basicResponse.getMessage());
                                // TODO Block user by admin or user not valid
                            } else {
                                CommonFunctions.showToast(AddAuctionActivity.this, basicResponse.getMessage());
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

    /*public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        public ImageAdapter() {
            this.context = AddAuctionActivity.this;
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
    }*/

    void getAuctionDetail(final String carId) {
        final ProgressDialog pDialog = new ProgressDialog(AddAuctionActivity.this);
        pDialog.setMessage("Please wait, Getting auction detail...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_AUCTION_DETAIL;

        final StringRequest reqRepoDetail = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqRepoDetail", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    AuctionDetailResponse auctionDetailResponse = AuctionDetailResponse.parseJSON(response);

                    if (auctionDetailResponse.getStatusCode() == 1) {
                        // set car detail
                        setAuctionDetail(auctionDetailResponse.getAuction());
                    } else if (auctionDetailResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(AddAuctionActivity.this, auctionDetailResponse.getMessage());
                    } else if (auctionDetailResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(AddAuctionActivity.this, auctionDetailResponse.getMessage());
                    } else if (auctionDetailResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {
                        CommonFunctions.showToast(AddAuctionActivity.this, "Error : Network " + auctionDetailResponse.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqRepoDetail", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqRepoDetail", "Error Response: " + error.getMessage());
                CommonFunctions.showToast(AddAuctionActivity.this, "Network error");
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                // params.put(ParamsKey.VIN, vin);
                params.put(ParamsKey.CAR_ID, carId);
                Log.e("reqRepoDetail", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqRepoDetail, TAG);
    }

    private void setAuctionName(String auctionName){
        tvAuctionName.setText(auctionName);
        tvAuctionName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_auction_name_hint)).setText(getString(R.string.auction_name));
    }

    private void setAuctionDate(String auctionDate){
        tvAuctionDate.setText(auctionDate);
        tvAuctionDate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_auction_date_hint)).setText(getString(R.string.auction_date));
    }

    private void setCarAtAuction(String carAtAuction){
        tvCarAtAuction.setText(carAtAuction);
        tvCarAtAuction.setHint("");
        tvCarAtAuction.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_car_at_auction_hint)).setText(getString(R.string.car_at_the_auction));
    }

    private void setCarReadyForAuction(String carReadyForAuction){
        tvCarReadyForAuction.setText(carReadyForAuction);
        tvCarReadyForAuction.setHint("");
        tvCarReadyForAuction.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_car_ready_for_auction_hint)).setText(getString(R.string.car_ready_for_auction));
    }

    private void setCondition(String condition){
        tvCondition.setText(condition);
        tvCondition.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_condition_hint)).setText(getString(R.string.condition));
    }

    private void setAuctionDetail(Auction auctionDetail){
            // images

        ((TextView) findViewById(R.id.tv_add_auction_button)).setText("UPDATE AUCTION");

        String auctionName = auctionDetail.getAuctionName();
        if (auctionName != null && auctionName.trim().length() > 0
                && !auctionName.equalsIgnoreCase(Constant.NULL)) {
            setAuctionName(auctionName);
        }

        String floorPrice = auctionDetail.getFloorPrice();
        if (floorPrice != null && floorPrice.trim().length() > 0
                && !floorPrice.equalsIgnoreCase(Constant.NULL) && !floorPrice.equalsIgnoreCase("0")) {
            edtFloorPrice.setText(floorPrice);
        }

        String miles = auctionDetail.getMiles();
        if (miles != null && miles.trim().length() > 0
                && !miles.equalsIgnoreCase(Constant.NULL) && !miles.equalsIgnoreCase("0")) {
            edtMiles.setText(miles);
        }

        String note = auctionDetail.getAuctionNote();
        if (note != null && note.trim().length() > 0
                && !note.equalsIgnoreCase(Constant.NULL)) {
            edtNote.setText(note);
        }

        String auctionDate = auctionDetail.getAuctionDate();
        if (auctionDate != null && auctionDate.trim().length() > 0
                && !auctionDate.equalsIgnoreCase(Constant.NULL) && !miles.equalsIgnoreCase("0")) {
            auctionDate = CommonFunctions.getDate(Long.parseLong(auctionDate), "yyyy-MM-dd");
            setAuctionDate(auctionDate);
        }

        String carAtAuction = auctionDetail.getCarAtAuction();
        if (carAtAuction != null && carAtAuction.trim().length() > 0
                && !carAtAuction.equalsIgnoreCase(Constant.NULL)) {
            setCarAtAuction(carAtAuction);
        }

        String carReadyForAuction = auctionDetail.getCarReady();
        if (carReadyForAuction != null && carReadyForAuction.trim().length() > 0
                && !carReadyForAuction.equalsIgnoreCase(Constant.NULL)) {
            setCarReadyForAuction(carReadyForAuction);
        }

        String condition = auctionDetail.getCondition();
        if (condition != null && condition.trim().length() > 0
                && !condition.equalsIgnoreCase(Constant.NULL) && !condition.equalsIgnoreCase("0")) {
            setCondition(condition);
        }

        List<Auction.Image> images = auctionDetail.getImages();
        if(images!= null && images.size()>0){
            mImages = new ArrayList<>();
            for (int i = 0; i<images.size(); i++){
                mImages.add(images.get(i).getImagePath());
            }
            setImages();
        }
    }
}
