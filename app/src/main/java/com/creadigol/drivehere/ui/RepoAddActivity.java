package com.creadigol.drivehere.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.Repo;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.MultipartRequest;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.Network.RepoDetailResponse;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.ImageAdapter;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.LotCodeDialogFragment;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.ItemDecorationGrid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepoAddActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_CAR_ID = "car_id";
    private final String TAG = RepoAddActivity.class.getSimpleName();
    private final String TAG_DIALOG_REPO_COMPANY = "RepoReport company";
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
    private Status repoStatus;
    private TextView tvRepoCompany, tvIsVoluntary, tvAssignedDate, tvDeliveredDate, tvRepoStatusOpen, tvRepoStatusClose, tvLotCode;
    private EditText edtNote;
    private ArrayList<String> mImages;
    private ImageAdapter imageAdapter;
    View.OnClickListener imageDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            String path = mImages.get(index);
            mImages.remove(index);
            if (imageAdapter != null) {
                imageAdapter.removeItem(index);
            }

            if (path.startsWith(Constant.PREFIX_HTTPS)) {
                // TODO set deleted image
            }

        }
    };
    private String carId = "";

    public void selectRepoStatus(Status status) {

        if (status == repoStatus) {
            return;
        }

        ConstraintLayout clLotCode = (ConstraintLayout) findViewById(R.id.cl_lot_code);

        if (status == Status.open) {
            tvRepoStatusOpen.setTextColor(getResources().getColor(R.color.white));
            tvRepoStatusOpen.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_selected));

            tvRepoStatusClose.setTextColor(getResources().getColor(R.color.black));
            tvRepoStatusClose.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));
            clLotCode.setVisibility(View.GONE);
        } else if (status == Status.close) {
            tvRepoStatusClose.setTextColor(getResources().getColor(R.color.white));
            tvRepoStatusClose.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_selected));

            tvRepoStatusOpen.setTextColor(getResources().getColor(R.color.black));
            tvRepoStatusOpen.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));
            clLotCode.setVisibility(View.VISIBLE);
        }

        repoStatus = status;

    }

    public void setRepoCompanyName(String companyName) {
        tvRepoCompany.setText(companyName);
        tvRepoCompany.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_repo_company_hint)).setText(getString(R.string.repo_company));
    }

    public void setLotCode(String lotCode) {
        tvLotCode.setText(lotCode);
        tvLotCode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) findViewById(R.id.tv_lot_code_hint)).setText(getString(R.string.lot_code));
    }

    public void showRepoCompanyDialog() {

        final String[] auctionList = getResources().getStringArray(R.array.auction_name);
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setRepoCompanyName(auctionList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, auctionList,
                TAG_DIALOG_REPO_COMPANY, "Select " + TAG_DIALOG_REPO_COMPANY, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_REPO_COMPANY);
    }

    public void showIsVoluntaryDialog() {

        final String[] optionList = new String[]{"Yes", "No"};
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setIsVoluntary(optionList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, optionList, TAG_DIALOG_REPO_COMPANY, "Select ", null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_REPO_COMPANY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        carId = bundle.getString(EXTRA_KEY_CAR_ID, "");

        tvRepoCompany = (TextView) findViewById(R.id.tv_repo_company);
        tvIsVoluntary = (TextView) findViewById(R.id.tv_is_voluntary);
        tvAssignedDate = (TextView) findViewById(R.id.tv_assigned_date);
        tvDeliveredDate = (TextView) findViewById(R.id.tv_delivered_date);
        tvRepoStatusOpen = (TextView) findViewById(R.id.tv_repo_status_open);
        tvRepoStatusClose = (TextView) findViewById(R.id.tv_repo_status_close);
        tvLotCode = (TextView) findViewById(R.id.tv_lot_code);

        edtNote = (EditText) findViewById(R.id.edt_note);

        edtNote.addTextChangedListener(twNote);

        findViewById(R.id.cl_repo_company).setOnClickListener(this);
        findViewById(R.id.cl_lot_code).setOnClickListener(this);
        findViewById(R.id.cl_is_voluntary).setOnClickListener(this);
        findViewById(R.id.cl_add_images).setOnClickListener(this);
        findViewById(R.id.cl_btn_add_repo).setOnClickListener(this);
        findViewById(R.id.cl_assigned_date).setOnClickListener(this);
        findViewById(R.id.cl_delivered_date).setOnClickListener(this);
        tvRepoStatusOpen.setOnClickListener(this);
        tvRepoStatusClose.setOnClickListener(this);

        GridLayoutManager lLayout = new GridLayoutManager(RepoAddActivity.this, getResources().getInteger(R.integer.photo_list_preview_columns));

        rvImages = (RecyclerView) findViewById(R.id.rv_images);
        rvImages.addItemDecoration(new ItemDecorationGrid(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(lLayout);

        selectRepoStatus(Status.open);

        getRepoDetail(carId);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLotCodeDialog() {

        final String[] lotList = getResources().getStringArray(R.array.Lotcode);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setLotCode(lotList[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, getString(R.string.lot_code));
        dialog1.show(getSupportFragmentManager(), getString(R.string.lot_code));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_repo_company:
                showRepoCompanyDialog();
                break;

            case R.id.cl_lot_code:
                showLotCodeDialog();
                break;

            case R.id.cl_is_voluntary:
                showIsVoluntaryDialog();
                break;


            case R.id.cl_add_images:
                Intent intentAddImages = new Intent(RepoAddActivity.this, AddImagesActivity.class);
                intentAddImages.putStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES, mImages);
                startActivityForResult(intentAddImages, REQUEST_ADD_IMAGES);
                break;

            case R.id.cl_btn_add_repo:
                // TODO send repo detail to server
                addRepo(getRepoParams());
                //Log.e("add repo", "Posting params: " + getRepoParams().toString());
                break;

            case R.id.cl_assigned_date:
                // show dialog for assigned date
                openDatePicker(true);
                break;

            case R.id.cl_delivered_date:
                // show dialog for delivered date
                openDatePicker(false);
                break;

            case R.id.tv_repo_status_open:
                selectRepoStatus(Status.open);
                break;

            case R.id.tv_repo_status_close:
                selectRepoStatus(Status.close);
                break;
        }
    }

    void openDatePicker(final boolean isAssignedDate) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog dpd = new DatePickerDialog(RepoAddActivity.this,
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

                        if (isAssignedDate) {
                            setAssignedDate(year + "-" + month + "-" + day);
                        } else {
                            setDeliveredDate(year + "-" + month + "-" + day);
                        }

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.show();
    }

    public void setAssignedDate(String assignedDate) {
        tvAssignedDate.setText(assignedDate);
        ((TextView) findViewById(R.id.tv_assigned_date_hint)).setText(getString(R.string.assigned_date));
    }

    public void setDeliveredDate(String deliveredDate) {
        tvDeliveredDate.setText(deliveredDate);
        ((TextView) findViewById(R.id.tv_delivered_date_hint)).setText(getString(R.string.delivered_date));
    }

    private HashMap<String, String> getRepoParams() {
        HashMap<String, String> hashParams = new HashMap<>();

        hashParams.put(ParamsKey.CAR_ID, carId);
        hashParams.put(ParamsKey.USER_ID, MyApplication.getInstance().getPreferenceSettings().getUserId());
        hashParams.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);

        String repoName = tvRepoCompany.getText().toString().trim();
        if (repoName != null && repoName.length() > 0
                && !repoName.equalsIgnoreCase(getResources().getString(R.string.repo_company))) {
            hashParams.put(ParamsKey.REPO_COMPANY, repoName.trim());
        }

        String status = repoStatus.toString();
        if (status != null && status.length() > 0) {
            hashParams.put(ParamsKey.REPO_STATUS, status.trim());

            if(status.equalsIgnoreCase(Status.close.toString())){
                String lotCode = tvLotCode.getText().toString().trim();

                if (lotCode != null && lotCode.length() > 0
                        && !lotCode.equalsIgnoreCase(getResources().getString(R.string.lot_code))) {
                    hashParams.put(ParamsKey.LOT_CODE, lotCode.trim());
                }
            }
        }

        String note = edtNote.getText().toString().trim();
        if (note != null && note.length() > 0
                && !note.equalsIgnoreCase(getResources().getString(R.string.note))) {
            hashParams.put(ParamsKey.REPO_NOTE, note.trim());
        }

        String isVoluntary = tvIsVoluntary.getText().toString().trim();
        if (isVoluntary != null && isVoluntary.length() > 0) {
            hashParams.put(ParamsKey.REPO_VOLUNTARY, isVoluntary.trim());
        }

        String assignedDate = tvAssignedDate.getText().toString().trim();
        if (assignedDate != null && assignedDate.length() > 0
                && !assignedDate.equalsIgnoreCase(getResources().getString(R.string.assigned_date))) {
            assignedDate = String.valueOf(CommonFunctions.getMilliseconds(assignedDate, "yyyy-MM-dd"));
            hashParams.put(ParamsKey.REPO_ASSIGNED_DATE, assignedDate.trim());// need to change
        }

        String deliveredDate = tvDeliveredDate.getText().toString().trim();
        if (deliveredDate != null && deliveredDate.length() > 0
                && !deliveredDate.equalsIgnoreCase(getResources().getString(R.string.delivered_date))) {
            deliveredDate = String.valueOf(CommonFunctions.getMilliseconds(deliveredDate, "yyyy-MM-dd"));
            hashParams.put(ParamsKey.REPO_DELIVERED_DATE, deliveredDate.trim());// need to change
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
                imageAdapter = new ImageAdapter(RepoAddActivity.this, mImages); // TODO add image delete listener
                rvImages.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        } else {
            findViewById(R.id.cl_images).setVisibility(View.GONE);
        }
    }

    void addRepo(HashMap<String, String> hashParams) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_REPO_ADD;

        MultipartRequest reqAddRepo = new MultipartRequest(url, hashParams, mImages,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reqAddRepo Response", response.toString());
                        //pDialog.hide();
                        try {
                            BasicResponse basicResponse = BasicResponse.parseJSON(response);

                            if (basicResponse.getStatusCode() == 1) {
                                // set list of cars
                                CommonFunctions.showToast(RepoAddActivity.this, basicResponse.getMessage());
                                finish();
                            } else if (basicResponse.getStatusCode() == 0) {
                                CommonFunctions.showToast(RepoAddActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 2) {
                                CommonFunctions.showToast(RepoAddActivity.this, basicResponse.getMessage());
                            } else if (basicResponse.getStatusCode() == 4) {
                                CommonFunctions.showToast(RepoAddActivity.this, basicResponse.getMessage());
                                // TODO Block user by admin or user not valid
                            } else {
                                CommonFunctions.showToast(RepoAddActivity.this, basicResponse.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("reqAddRepo Error_in", "catch");
                        }
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("reqAddRepo", "Error Response: " + error.getMessage());
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        //showTryAgainAlert("Info", "Network error, Please try again!");
                    }

                });

        MyApplication.getInstance().addToRequestQueue(reqAddRepo, TAG);
    }

    void getRepoDetail(final String carId) {
        final ProgressDialog pDialog = new ProgressDialog(RepoAddActivity.this);
        pDialog.setMessage("Please wait, Getting repo detail...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_REPO_DETAIL;

        final StringRequest reqRepoDetail = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqRepoDetail", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    RepoDetailResponse repoDetailResponse = RepoDetailResponse.parseJSON(response);

                    if (repoDetailResponse.getStatusCode() == 1) {
                        // set car detail
                        setRepoDetail(repoDetailResponse.getRepo());
                    } else if (repoDetailResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(RepoAddActivity.this, repoDetailResponse.getMessage());
                    } else if (repoDetailResponse.getStatusCode() == 2) {
                        CommonFunctions.showToast(RepoAddActivity.this, repoDetailResponse.getMessage());
                    } else if (repoDetailResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {
                        CommonFunctions.showToast(RepoAddActivity.this, "Error : Network " + repoDetailResponse.getMessage());
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
                CommonFunctions.showToast(RepoAddActivity.this, "Network error");
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

    public void setIsVoluntary(String isVoluntary) {
        tvIsVoluntary.setText(isVoluntary);
        tvIsVoluntary.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public void setRepoDetail(Repo repoDetail) {
        // set detail

        ((TextView) findViewById(R.id.tv_add_repo_button)).setText("UPDATE REPO");

        String isVoluntary = repoDetail.getIsVoluntary();
        if (isVoluntary != null && isVoluntary.trim().length() > 0 && !isVoluntary.equalsIgnoreCase(Constant.NULL)) {
            setIsVoluntary(isVoluntary);
        }

        String assignedDate = repoDetail.getAssignedDate();
        if (assignedDate != null && assignedDate.trim().length() > 0 && !assignedDate.equalsIgnoreCase(Constant.NULL)) {
            assignedDate = CommonFunctions.getDate(Long.parseLong(assignedDate), "yyyy-MM-dd");
            setAssignedDate(assignedDate);
        }

        String deliveredDate = repoDetail.getDeliveredDate();
        if (deliveredDate != null && deliveredDate.trim().length() > 0 && !deliveredDate.equalsIgnoreCase(Constant.NULL)) {
            deliveredDate = CommonFunctions.getDate(Long.parseLong(deliveredDate), "yyyy-MM-dd");
            setDeliveredDate(deliveredDate);
        }

        String repoCompany = repoDetail.getRepoCompany();
        if (repoCompany != null && repoCompany.trim().length() > 0 && !repoCompany.equalsIgnoreCase(Constant.NULL)) {
            setRepoCompanyName(repoCompany);
        }

        String note = repoDetail.getNote();
        if (note != null && note.trim().length() > 0 && !note.equalsIgnoreCase(Constant.NULL)) {
            edtNote.setText(note);
        }

        String status = repoDetail.getStatus();
        if (status != null && status.trim().length() > 0 && !status.equalsIgnoreCase(Constant.NULL)) {
            if (status.equalsIgnoreCase(Status.close.toString())){
                selectRepoStatus(Status.close);

                String lotCode = repoDetail.getLotCode();
                if (lotCode != null && lotCode.trim().length() > 0 && !lotCode.equalsIgnoreCase(Constant.NULL)) {
                    setLotCode(lotCode);
                }
            }
            else
                selectRepoStatus(Status.open);
        }

        List<Repo.Image> images = repoDetail.getImages();
        if (images != null && images.size() > 0) {
            mImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                mImages.add(images.get(i).getImagePath());
            }
            setImages();
        }

    }

    private enum Status {open, close}

    /*public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        public ImageAdapter() {
            this.context = RepoAddActivity.this;
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
}
