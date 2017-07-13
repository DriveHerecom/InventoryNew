package com.creadigol.drivehere.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.BasicResponse;
import com.creadigol.drivehere.Network.CarDetailResponse;
import com.creadigol.drivehere.Network.DataOneResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.ImageAdapter;
import com.creadigol.drivehere.dataone.model.BasicData;
import com.creadigol.drivehere.dataone.model.Engine;
import com.creadigol.drivehere.dataone.model.Query_Error;
import com.creadigol.drivehere.dialog.InputDialogListener;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.LotCodeDialogFragment;
import com.creadigol.drivehere.dialog.MakeDialogFragment;
import com.creadigol.drivehere.dialog.PinValidationDialogFragment;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.dialog.StageAddCarDialogFragment;
import com.creadigol.drivehere.ui.AddImagesActivity;
import com.creadigol.drivehere.ui.CarAddActivity;
import com.creadigol.drivehere.ui.GPSListActivity;
import com.creadigol.drivehere.ui.ScannerActivity;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.ItemDecorationGrid;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 13/6/17.
 */

public class CarAddBasicDetailFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_SCAN = 1001;
    public static final int REQUEST_GPS_LIST = 1004;
    public final String TAG_DIALOG_PIN_VALIDATE = "Pin validation";
    public final String TAG_DIALOG_LOT_CODE = "Lot code";
    public final String TAG_DIALOG_COLOR = "Color";
    private final int REQUEST_ADD_IMAGES = 1002;
    private final int REQUEST_ADD_TITLE_IMAGE = 1003;
    private final String TAG = CarAddBasicDetailFragment.class.getSimpleName();
    private View.OnClickListener onClickListenerScan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startScanning();
        }
    };
    private CarAddCallBack listener;
    private CarAddActivity.TYPE type;
    private String vinDataOne = "";
    private boolean isVin;
    private String vin, rfid;
    private EditText edtVin, edtRfid, edtMiles, edtStockNo, edtNote,edt_model,edt_year;
    private CarAddActivity carAddActivity;
    private TypeStage typeStage;
    private TextWatcher twVin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String vin = s.toString().trim();
            if (vin.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_vin_hint)).setText(getString(R.string.vin));

                if (vin.length() == Constant.LENGTH_VIN) {
                    if (vin.equalsIgnoreCase(CarAddBasicDetailFragment.this.vin)) {
                        // request data one info
                        getDataOneData(vin);
                        Log.e(TAG, "No need to check on server");
                    } else {
                        // check on server
                        isCarExist(vin, true);
                        Log.e(TAG, "Need to check on server");
                    }
                    CarAddBasicDetailFragment.this.vin = vin;
                }
                carAddActivity.getCarAdd().setVin(vin);
            } else {
                ((TextView) getView().findViewById(R.id.tv_vin_hint)).setText(getString(R.string.enter));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher twNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String note = s.toString().trim();
            if (note.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_note_hint)).setText(getString(R.string.note));
            } else {
                ((TextView) getView().findViewById(R.id.tv_note_hint)).setText(getString(R.string.enter));
            }
            carAddActivity.getCarAdd().setNote(note);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher twStockNo = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String stockNo = s.toString().trim();
            if (stockNo.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_stock_no_hint)).setText(getString(R.string.stock_no));
            } else {
                ((TextView) getView().findViewById(R.id.tv_stock_no_hint)).setText(getString(R.string.enter));
            }
            carAddActivity.getCarAdd().setStockNumber(stockNo);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher twMiles = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String miles = s.toString().trim();
            if (miles.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_miles_hint)).setText(getString(R.string.miles));
            } else {
                ((TextView) getView().findViewById(R.id.tv_miles_hint)).setText(getString(R.string.enter));
            }
            carAddActivity.getCarAdd().setMiles(miles);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher twRfid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String rfid = s.toString().trim();
            if (rfid.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_rfid_hint)).setText(getString(R.string.rfid));

                if (rfid.length() == Constant.LENGTH_RFID) {
                    if (!rfid.equalsIgnoreCase(CarAddBasicDetailFragment.this.rfid)) {
                        // TODO check on server
                        isCarExist(rfid, false);
                        Log.e(TAG, "Need to check on server");
                    } else {
                        Log.e(TAG, "No need to check on server");
                    }
                    CarAddBasicDetailFragment.this.rfid = rfid;
                }
                carAddActivity.getCarAdd().setRfid(rfid);
            } else {
                ((TextView) getView().findViewById(R.id.tv_rfid_hint)).setText(getString(R.string.enter));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    private TextWatcher twModel= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //for set model
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String model = s.toString().trim();
            if (model.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_model_hint)).setText(getString(R.string.model));
            } else {
                ((TextView) getView().findViewById(R.id.tv_model_hint)).setText(getString(R.string.enter));
            }
            carAddActivity.getCarAdd().setModel(model);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher twYear= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //for set model
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String year = s.toString().trim();
            if (year.trim().length() > 0) {
                ((TextView) getView().findViewById(R.id.tv_year_hint)).setText(getString(R.string.year));
            } else {
                ((TextView) getView().findViewById(R.id.tv_year_hint)).setText(getString(R.string.enter));
            }
            carAddActivity.getCarAdd().setModelYear(year);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView tvLotCode, tvStage, tvVacancy, tvHasTitle, tvTitleLocation, tvColor,tvMake;
    private ImageAdapter imageAdapter;
    View.OnClickListener imageDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            String path = carAddActivity.getCarAdd().getLocalImages().get(index);
            carAddActivity.getCarAdd().getLocalImages().remove(index);
            if (imageAdapter != null) {
                imageAdapter.removeItem(index);
            }

            if (path.startsWith(Constant.PREFIX_HTTPS)) {
                carAddActivity.getCarAdd().setDeletePhotoLink(path);
            }

        }
    };
    private RecyclerView rvImages;

    public static CarAddBasicDetailFragment getInstance(CarAddActivity.TYPE type, String code, boolean isVin) {
        CarAddBasicDetailFragment carAddBasicDetailFragment = new CarAddBasicDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CarAddActivity.EXTRA_KEY_TYPE, type);
        bundle.putString(CarAddActivity.EXTRA_KEY_CODE, code);
        bundle.putBoolean(CarAddActivity.EXTRA_KEY_IS_VIN, isVin);
        carAddBasicDetailFragment.setArguments(bundle);
        return carAddBasicDetailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CarAddCallBack) {
            listener = (CarAddCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CarAddCallBack");
        }

        if (getActivity() instanceof CarAddActivity) {
            carAddActivity = (CarAddActivity) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_car_add_basic_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        String code = "";
        if (bundle != null) {
            type = (CarAddActivity.TYPE) bundle.getSerializable(CarAddActivity.EXTRA_KEY_TYPE);
            code = bundle.getString(CarAddActivity.EXTRA_KEY_CODE, "");
            isVin = bundle.getBoolean(CarAddActivity.EXTRA_KEY_IS_VIN, false);
            if (isVin) {
                vin = code.trim();
//                vinDataOne = code.trim(); // TODO comment this statement
            } else {
                rfid = code.trim();
            }
        }
        Log.e(TAG, "type:" + type.toString());
        Log.e(TAG, "code:" + code.toString());
        Log.e(TAG, "isVin:" + isVin);

        getView().findViewById(R.id.tv_scan_vin).setOnClickListener(onClickListenerScan);
        getView().findViewById(R.id.tv_scan_rfid).setOnClickListener(onClickListenerScan);

        edtVin = (EditText) getView().findViewById(R.id.edt_vin);
        edtVin.addTextChangedListener(twVin);

        edtRfid = (EditText) getView().findViewById(R.id.edt_rfid);
        edtRfid.addTextChangedListener(twRfid);

        edtMiles = (EditText) getView().findViewById(R.id.edt_miles);
        edtStockNo = (EditText) getView().findViewById(R.id.edt_stock_no);
        edtNote = (EditText) getView().findViewById(R.id.edt_note);
        edt_model= (EditText) getView().findViewById(R.id.edt_model);
        edt_year= (EditText) getView().findViewById(R.id.edt_year);

        edtMiles.addTextChangedListener(twMiles);
        edtStockNo.addTextChangedListener(twStockNo);
        edtNote.addTextChangedListener(twNote);
        edt_model.addTextChangedListener(twModel);
        edt_year.addTextChangedListener(twYear);

        tvLotCode = (TextView) getView().findViewById(R.id.tv_lot_code);
        tvStage = (TextView) getView().findViewById(R.id.tv_stage);
        tvVacancy = (TextView) getView().findViewById(R.id.tv_vacancy);
        tvHasTitle = (TextView) getView().findViewById(R.id.tv_has_title);
        tvTitleLocation = (TextView) getView().findViewById(R.id.tv_title_location);
        tvColor = (TextView) getView().findViewById(R.id.tv_color);
        tvMake= (TextView) getView().findViewById(R.id.tv_make);

        getView().findViewById(R.id.tv_title_image).setOnClickListener(this);
        getView().findViewById(R.id.iv_title_image).setOnClickListener(this);

        getView().findViewById(R.id.cl_lot_code).setOnClickListener(this);
        getView().findViewById(R.id.cl_stage).setOnClickListener(this);
        getView().findViewById(R.id.cl_vacancy).setOnClickListener(this);
        getView().findViewById(R.id.cl_has_title).setOnClickListener(this);
        getView().findViewById(R.id.cl_title_location).setOnClickListener(this);
        getView().findViewById(R.id.cl_add_images).setOnClickListener(this);
        getView().findViewById(R.id.cl_color).setOnClickListener(this);
        getView().findViewById(R.id.cl_add_gps).setOnClickListener(this);
        getView().findViewById(R.id.cl_make).setOnClickListener(this);

        GridLayoutManager lLayout = new GridLayoutManager(getActivity().getBaseContext(),
                getResources().getInteger(R.integer.photo_list_preview_columns));

        rvImages = (RecyclerView) getView().findViewById(R.id.rv_images);
        rvImages.setNestedScrollingEnabled(false);
        rvImages.addItemDecoration(new ItemDecorationGrid(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(lLayout);

        getView().findViewById(R.id.cl_btn_add_car).setOnClickListener(this);

        if (vin != null && vin.trim().length() > 0)
            edtVin.setText(vin);

        if (rfid != null && rfid.trim().length() > 0)
            edtRfid.setText(rfid);

        String[] vacancies = getResources().getStringArray(R.array.Vacancy);
        setVacancy(vacancies[0]);

        setHasTitle("No");

        if (carAddActivity.isEdit && carAddActivity.carIdEdit.trim().length() > 0) {
            getCarDetail(carAddActivity.carIdEdit.trim(), null);
        }

    }

    public void showPinValidateDialog(final String values, final boolean isVacancy) {

        final InputDialogListener listener = new InputDialogListener() {

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                /*finish();*/
            }

            @Override
            public void onDialogPositiveClick(String pin) {
                if (pin.length() > 0 && pin.equals(Constant.ADMIN_PIN)) {
                    if (isVacancy)
                        setVacancy(values);
                    else {
                        setStage(values);
                        setStageType(values);
                    }
                }
            }
        };

        PinValidationDialogFragment dialog1 = new PinValidationDialogFragment(listener, TAG_DIALOG_PIN_VALIDATE);
        dialog1.show(getActivity().getSupportFragmentManager(), TAG_DIALOG_PIN_VALIDATE);
    }

    public void setCarDetail() {
        String vin = carAddActivity.getCarAdd().getVin().trim();
        if (vin.length() == Constant.LENGTH_VIN) {
            this.vin = vin;
            vinDataOne = vin;
            edtVin.setText(vin);
            edtVin.setEnabled(false);
            getView().findViewById(R.id.tv_scan_vin).setOnClickListener(null);
        }

        String rfid = carAddActivity.getCarAdd().getRfid().trim();
        if (rfid.length() == Constant.LENGTH_RFID) {
            this.rfid = rfid;
            edtRfid.setText(rfid);
        }

        String lotCode = carAddActivity.getCarAdd().getLotCode().trim();
        if (lotCode != null && lotCode.trim().length() > 0 && !lotCode.equalsIgnoreCase(Constant.NULL)) {
            setLotCode(lotCode);
        }

        String miles = carAddActivity.getCarAdd().getMiles().trim();
        if (miles != null && miles.trim().length() > 0 && !miles.equalsIgnoreCase(Constant.NULL)) {
            edtMiles.setText(miles);
        }

        String vacancy = carAddActivity.getCarAdd().getVacancy().trim();
        if (vacancy != null && vacancy.trim().length() > 0 && !vacancy.equalsIgnoreCase(Constant.NULL)) {
            setVacancy(vacancy);
        }

        String stage = carAddActivity.getCarAdd().getStage().trim();
        if (stage != null && stage.trim().length() > 0 && !stage.equalsIgnoreCase(Constant.NULL)) {
            // TODO
            TypeStage typeStage = getTypeStage(stage);
            if (this.typeStage == null || typeStage == this.typeStage) {
                setStage(stage);
                setStageType(stage);
            }
        }

        String stockNo = carAddActivity.getCarAdd().getStockNumber().trim();
        if (stockNo != null && stockNo.trim().length() > 0 && !stockNo.equalsIgnoreCase(Constant.NULL)) {
            edtStockNo.setText(stockNo);
        }

        String note = carAddActivity.getCarAdd().getNote().trim();
        if (note != null && note.trim().length() > 0 && !note.equalsIgnoreCase(Constant.NULL)) {
            edtNote.setText(note);
        }

        String hasTitle = carAddActivity.getCarAdd().getHasTitle().trim();
        if (hasTitle != null && hasTitle.trim().length() > 0 && !hasTitle.equalsIgnoreCase(Constant.NULL)) {
            setHasTitle(hasTitle);
        }

        String titleLocation = carAddActivity.getCarAdd().getTitleLocation().trim();
        if (titleLocation != null && titleLocation.trim().length() > 0 && !titleLocation.equalsIgnoreCase(Constant.NULL)) {
            setTitleLocation(titleLocation);
        }

        String color = carAddActivity.getCarAdd().getColor().trim();
        if (color.trim().length() > 0 && !color.equalsIgnoreCase(Constant.NULL) && !color.startsWith("#")) {
            setColor(color);
        }

        if (carAddActivity.getCarAdd().getTitleImages() != null && carAddActivity.getCarAdd().getTitleImages().size() > 0) {
            carAddActivity.getCarAdd().addTitleLocalImage(carAddActivity.getCarAdd().getTitleImages().get(carAddActivity.getCarAdd().getTitleImages().size() - 1).getImagePath());
            setTitleImage(carAddActivity.getCarAdd().getTitleLocalImages());
        }

        if (carAddActivity.getCarAdd().getImages() != null && carAddActivity.getCarAdd().getImages().size() > 0) {
            for (CarAdd.Image image : carAddActivity.getCarAdd().getImages()) {
                carAddActivity.getCarAdd().addLocalImage(image.getImagePath());
            }
            setImages();
        }

        if (carAddActivity.isEdit) {
            ((TextView) getView().findViewById(R.id.tv_add_car)).setText("UPDATE CAR");
        }

        if(carAddActivity.getCarAdd().getMake()!=null&&carAddActivity.getCarAdd().getMake().length()>0){
            setMake(carAddActivity.getCarAdd().getMake());
        }
        String model=carAddActivity.getCarAdd().getModel();
        if(model!=null&&model.length()>0){
            edt_model.setText(model);
        }
        String year=carAddActivity.getCarAdd().getModelYear();
        if(year!=null&&year.length()>0){
            edt_year.setText(year);
        }
        setGpsCount();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void startScanning() {
        Intent scanner = new Intent(getActivity().getBaseContext(),
                ScannerActivity.class);
        startActivityForResult(scanner, REQUEST_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            String scanCode = data.getStringExtra("code");
            int scanCodeLength = scanCode.length();
            Log.e("scanCode", scanCode);
            if (scanCodeLength == Constant.LENGTH_RFID) {
                edtRfid.setText(scanCode);
            } else if (scanCodeLength == 18
                    && (scanCode.startsWith("i") || scanCode
                    .startsWith("I"))) {
                scanCode = scanCode.substring(1, scanCode.length());

                Log.e("scanCode", scanCode);
                scanCodeLength = scanCode.length();

                if (scanCodeLength == Constant.LENGTH_VIN) {
                    edtVin.setText(scanCode);
                }
            } else if (scanCodeLength == Constant.LENGTH_VIN) {

                Log.e("scanCode", scanCode);
                edtVin.setText(scanCode);
            } else {
                CommonFunctions.showToast(getActivity().getBaseContext(), "Scanned code is not valid");
            }
        } else if (requestCode == REQUEST_ADD_IMAGES && resultCode == Activity.RESULT_OK) {
            carAddActivity.getCarAdd().setLocalImages(data.getStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES));

            if (carAddActivity.getCarAdd().getLocalImages() != null && carAddActivity.getCarAdd().getLocalImages().size() > 0) {
                // set images
                setImages();
            }
        } else if (requestCode == REQUEST_ADD_TITLE_IMAGE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> mImages = data.getStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES);
            String imgTitle = "";
            if (mImages != null && mImages.size() > 0) {
                // set images
                imgTitle = mImages.get(mImages.size() - 1);
                if (imgTitle.trim().length() > 0)
                    setTitleImage(mImages);
            }
        } else if (requestCode == REQUEST_GPS_LIST && resultCode == Activity.RESULT_OK) {
            Bundle args = data.getBundleExtra("BUNDLE");
            ArrayList<CarAdd.GPS> gpsList = (ArrayList<CarAdd.GPS>) args.getSerializable(GPSListActivity.EXTRA_KEY_GPS);
            carAddActivity.getCarAdd().setGpses(gpsList);
            setGpsCount();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cl_lot_code:
                showLotCodeDialog(true);
                break;

            case R.id.cl_stage:
                showStageDialog();
                break;

            case R.id.cl_vacancy:
                showVacancyDialog();
                break;

            case R.id.cl_color:
                // show color dialog
                showColorDialog();
                break;

            case R.id.cl_has_title:
                showHasTitleDialog();
                break;

            case R.id.cl_title_location:
                showLotCodeDialog(false);
                break;
            case R.id.cl_make:
                showMakeDialog();
                break;
            case R.id.tv_title_image:
            case R.id.iv_title_image:
                Intent intentAddTitleImage = new Intent(getActivity().getBaseContext(), AddImagesActivity.class);
                intentAddTitleImage.putExtra(AddImagesActivity.EXTRA_KEY_IS_SINGLE, true);
                startActivityForResult(intentAddTitleImage, REQUEST_ADD_TITLE_IMAGE);
                break;

            case R.id.cl_add_images:
                Intent intentAddImages = new Intent(getActivity().getBaseContext(), AddImagesActivity.class);
                intentAddImages.putStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES,
                        carAddActivity.getCarAdd().getLocalImages());
                startActivityForResult(intentAddImages, REQUEST_ADD_IMAGES);
                break;

            case R.id.cl_add_gps:
                // TODO add gps screen open with extra of gps detail
                Intent intentGpsList = new Intent(getActivity().getBaseContext(), GPSListActivity.class);
                // add put extra
                Bundle args = new Bundle();
                args.putSerializable(GPSListActivity.EXTRA_KEY_GPS, (Serializable) carAddActivity.getCarAdd().getGpses());
                args.putString(GPSListActivity.EXTRA_KEY_TITLE, carAddActivity.getCarAdd().getModelYear() + " "
                        + carAddActivity.getCarAdd().getMake() + " "
                        + carAddActivity.getCarAdd().getModel());
                intentGpsList.putExtra("BUNDLE", args);
                startActivityForResult(intentGpsList, REQUEST_GPS_LIST);

                break;

            case R.id.cl_btn_add_car:
                listener.onNext(type);
                break;

        }
    }

    // Get search car list from server
    private void getDataOneData(final String vin) {

        if (vin.equalsIgnoreCase(vinDataOne)) {
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait, Getting Dataone information...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_DATA_ONE;

        final StringRequest reqDataOneInfo = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqDataOneInfo", " Response:" + response.toString());
                //pDialog.hide();
                try {

                    vinDataOne = vin;
                    DataOneResponse dataOneResponse = DataOneResponse.parseJSON(response);
                    Query_Error queryError = dataOneResponse.query_responses.RequestSample.query_error;
                    carAddActivity.getCarAdd().setDataOneFound(true);

                    if (!queryError.error_code.equals("")) {
                        CommonFunctions.showToast(getActivity(), queryError.error_message);
                        Log.e("error_message", queryError.error_message);
                        CommonFunctions.showToast(getActivity().getBaseContext(), "Data one info is not found.");
                        carAddActivity.getCarAdd().setDataOneFound(false);
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        return;
                    }

                    BasicData basicData = dataOneResponse.query_responses.RequestSample.us_market_data.common_us_data.basic_data;

                    if (basicData != null) {
                        ((TextView) getView().findViewById(R.id.tv_make)).setText(basicData.make);
                        ((TextView) getView().findViewById(R.id.edt_model)).setText(basicData.model);
                        ((TextView) getView().findViewById(R.id.edt_year)).setText(basicData.year);
                        carAddActivity.getCarAdd().setMake(basicData.make);
                        carAddActivity.getCarAdd().setModel(basicData.model);
                        carAddActivity.getCarAdd().setModelNumber(basicData.model_number);
                        carAddActivity.getCarAdd().setModelYear(basicData.year);
                        carAddActivity.getCarAdd().setVehicleType(basicData.vehicle_type);
                        carAddActivity.getCarAdd().setDriveType(basicData.drive_type);

                    }

                    ArrayList<Engine> engines = dataOneResponse.query_responses.RequestSample.us_market_data.common_us_data.engines;

                    if (engines != null && engines.size() > 0) {

                        Engine engine = engines.get(0);
                        carAddActivity.getCarAdd().setMaxHp(engine.max_hp);
                        carAddActivity.getCarAdd().setMaxTorque(engine.max_torque);
                        carAddActivity.getCarAdd().setCylinder(engine.cylinders);
                        carAddActivity.getCarAdd().setOilCapacity(engine.oil_capacity);

                        if (!engine.fuel_type.equals("")) {
                            String[] sk = getResources().getStringArray(R.array.FuelTypeShortKey);
                            String[] ft = getResources().getStringArray(R.array.FuelType);
                            for (int i = 0; i < sk.length; i++) {
                                if (sk[i].equals(engine.fuel_type.trim())) {
                                    String fueltype = ft[i];
                                    carAddActivity.getCarAdd().setFuelType(fueltype);
                                    break;
                                }
                            }
                        }
                    }

                    carAddActivity.getCarAdd().setDataOneBase64(CommonFunctions.base64Encode(response));

                    CommonFunctions.showToast(getActivity().getBaseContext(), "Data one info is found successfully.");
                    CommonFunctions.hidekeyBoard(getActivity());

                } catch (Exception e) {
                    CommonFunctions.showToast(getActivity().getBaseContext(), "Data one info is not found.");
                    e.printStackTrace();
                    Log.e("reqDataOneInfo", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonFunctions.showToast(getActivity().getBaseContext(), "Data one info is not found.");
                Log.e("reqDataOneInfo", "Error Response: " + error.getMessage());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                //showTryAgainAlert("Info", "Network error, Please try again!");
            }

        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String decoder_query = getQueryParameters(vin);
                params.put(ParamsKey.DATA_ONE_CLIENT_ID, getString(R.string.dataone_client_id));
                params.put(ParamsKey.DATA_ONE_AUTH_CODE, getString(R.string.dataone_authorization_code));
                params.put(ParamsKey.DATA_ONE_DECODER_QUERY, decoder_query);
                Log.e("reqDataOneInfo", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqDataOneInfo, TAG);
    }

    private String getQueryParameters(String vin) {
        String decoder_query = getString(R.string.decoder_query_json);
        decoder_query = decoder_query.replace("xxxxx", vin);
        return decoder_query;
    }

    private void showLotCodeDialog(final boolean isLotCode) {

        final String[] lotList = getResources().getStringArray(R.array.Lotcode);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                if (isLotCode) {
                    setLotCode(lotList[position]);
                } else {
                    setTitleLocation(lotList[position]);
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, TAG_DIALOG_LOT_CODE);
        dialog1.show(getActivity().getSupportFragmentManager(), TAG_DIALOG_LOT_CODE);
    }

    private void showColorDialog() {

        final String[] colors = getResources().getStringArray(R.array.ColorName);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setColor(colors[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, TAG_DIALOG_COLOR);
        dialog1.show(getActivity().getSupportFragmentManager(), TAG_DIALOG_COLOR);
    }

    void showMakeDialog() {
        final CharSequence[] makeList = getResources().getStringArray(
                R.array.Make);
        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
//                tvMake.setText(makeList[position]);
                setMake((String) makeList[position]);

            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        MakeDialogFragment dialog1 = new MakeDialogFragment(listener, "Make");
        dialog1.show(getActivity().getSupportFragmentManager(), "Make");

    }
    private void showStageDialog() {

        ArrayList<String> stagesTemp = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Stage))); //(ArrayList<String>) Arrays.asList(getResources().getStringArray(R.array.Stage));

        stagesTemp.add("null");

        stagesTemp.addAll(Arrays.asList(getResources().getStringArray(R.array.Stage_sub)));

        final List<String> stages = stagesTemp;
        stagesTemp = null;
        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                // TODO
                TypeStage typeStage = getTypeStage(stages.get(position));
                if (CarAddBasicDetailFragment.this.typeStage == null
                        || typeStage == CarAddBasicDetailFragment.this.typeStage) {
                    setStage(stages.get(position));
                    setStageType(stages.get(position));
                } else {
                    // TODO check permission
                    showPinValidateDialog(stages.get(position), false);
                }

            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }
        };

        StageAddCarDialogFragment stageDialog = new StageAddCarDialogFragment(listener, stages, getString(R.string.stage), "Select " + getString(R.string.stage), null);
        stageDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.stage));

    }

    private void showVacancyDialog() {

        final String[] vacancies = getResources().getStringArray(R.array.Vacancy);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {

                if (!carAddActivity.getCarAdd().getVacancy().equalsIgnoreCase(vacancies[position])) {
//                    reqChangeVacancy(vacancy[position]);
                    showPinValidateDialog(vacancies[position], true);
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }
        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, vacancies, getString(R.string.vacancy), "Select " + getString(R.string.vacancy), null);
        stageDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.vacancy));
    }

    private void showHasTitleDialog() {

        final String[] option = new String[]{"Yes", "No"};
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                setHasTitle(option[position]);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, option, getString(R.string.has_title), "Select " + getString(R.string.has_title), null);
        stageDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.has_title));
    }

    private void setVacancy(String vacancy) {
        tvVacancy.setText(vacancy);
        tvVacancy.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_vacancy_hint)).setText(getString(R.string.vacancy));

        carAddActivity.getCarAdd().setVacancy(vacancy);
    }

    private void setHasTitle(String hasTitle) {
        tvHasTitle.setText(hasTitle);
        tvHasTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_has_title_hint)).setText(getString(R.string.has_title));

        carAddActivity.getCarAdd().setHasTitle(hasTitle);
    }

    private void setStage(String stage) {
        tvStage.setText(stage);
        tvStage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_stage_hint)).setText(getString(R.string.stage));

        carAddActivity.getCarAdd().setStage(stage);
    }

    private TypeStage getTypeStage(String stage) {
        ArrayList<String> stages = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Stage)));
        if (stages.contains(stage)) {
            return TypeStage.MAIN;
        }
        stages = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Stage_sub)));
        if (stages.contains(stage)) {
            return TypeStage.SUB;
        }
        return null;
    }

    private void setStageType(String stage) {
        typeStage = getTypeStage(stage);
    }

    private void setLotCode(String lotCode) {
        tvLotCode.setText(lotCode);
        tvLotCode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_lot_code_hint)).setText(getString(R.string.lot_code));

        carAddActivity.getCarAdd().setLotCode(lotCode);
    }

    private void setColor(String color) {
        tvColor.setText(color);
        tvColor.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_color_hint)).setText(getString(R.string.color));

        carAddActivity.getCarAdd().setColor(color);
    }

    private void setMake(String make) {
        tvMake.setText(make);
        tvMake.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_make_hint)).setText(getString(R.string.make));
        carAddActivity.getCarAdd().setMake(make);
    }
    private void setGpsCount() {
        TextView tvTotalGps = (TextView) getView().findViewById(R.id.tv_total_gps);
        if (carAddActivity.getCarAdd().getGpses() != null
                && carAddActivity.getCarAdd().getGpses().size() > 0) {
            tvTotalGps.setText("Total(" + carAddActivity.getCarAdd().getGpses().size() + ")");
        }
    }

    private void setTitleLocation(String lotCode) {
        tvTitleLocation.setText(lotCode);
        tvTitleLocation.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        ((TextView) getView().findViewById(R.id.tv_title_location_hint)).setText(getString(R.string.title_location));

        carAddActivity.getCarAdd().setTitleLocation(lotCode);
    }

    private void setTitleImage(final ArrayList<String> imagePaths) {

        String imagePath = "";
        if (imagePaths != null && imagePaths.size() > 0) {
            // set images
            imagePath = imagePaths.get(imagePaths.size() - 1);
        }

        final ImageView ivImage = (ImageView) getView().findViewById(R.id.iv_title_image);

        if (imagePath != null && imagePath.trim().length() > 0) {
            if (!imagePath.startsWith(Constant.PREFIX_HTTPS)) {
                imagePath = "file://" + imagePath;
            }
            MyApplication.getInstance().getImageLoader().displayImage(imagePath, ivImage, getDisplayImageOptions(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }

                    CommonFunctions.showToast(getActivity().getBaseContext(), message);
                    ivImage.setVisibility(View.GONE);
                    getView().findViewById(R.id.tv_title_image).setVisibility(View.VISIBLE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    carAddActivity.getCarAdd().setTitleLocalImages(imagePaths);
                }
            });

            ivImage.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.tv_title_image).setVisibility(View.GONE);
        }
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_car)
                .showImageOnFail(R.drawable.default_car)
                .showImageOnLoading(R.drawable.default_car).build();
        return options;
    }

    public void setImages() {
        if (carAddActivity.getCarAdd().getLocalImages() != null && carAddActivity.getCarAdd().getLocalImages().size() > 0) {

            getView().findViewById(R.id.cl_images).setVisibility(View.VISIBLE);
            if (imageAdapter == null) {
                imageAdapter = new ImageAdapter(getActivity().getBaseContext(), carAddActivity.getCarAdd().getLocalImages(), imageDeleteListener);
                rvImages.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged(carAddActivity.getCarAdd().getLocalImages());
            }
        } else {
            getView().findViewById(R.id.cl_images).setVisibility(View.GONE);
        }
    }

    void isCarExist(final String code, final boolean isVin) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        if (isVin)
            pDialog.setMessage("Please wait, checking VIN on server...");
        else
            pDialog.setMessage("Please wait, checking RFID on server...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_IS_CAR_EXIST;

        final StringRequest reqCarExist = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqCarExist", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    BasicResponse basicResponse = BasicResponse.parseJSON(response);

                    if (basicResponse.getStatusCode() == 1) {
                        // car not found on server
                        if (isVin) {
                            getDataOneData(code);
                        } else {
                            carAddActivity.setValidRfid(true);
                        }
                    } else if (basicResponse.getStatusCode() == 0) {
                        carAddActivity.setValidRfid(false);
                        CommonFunctions.showToast(getActivity().getBaseContext(), basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 3) {
                        carAddActivity.setValidRfid(false);
                        // TODO car found on server
                        CommonFunctions.showToast(getActivity().getBaseContext(), basicResponse.getMessage());
                    } else if (basicResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {
                        carAddActivity.setValidRfid(false);
                    }

                } catch (Exception e) {
                    carAddActivity.setValidRfid(false);
                    e.printStackTrace();
                    Log.e("reqCarExist", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carAddActivity.setValidRfid(false);
                Log.e("reqCarExist", "Error Response: " + error.getMessage());
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
                if (isVin)
                    params.put(ParamsKey.VIN, code);
                else
                    params.put(ParamsKey.RFID, code);
                Log.e("reqCarExist", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqCarExist, TAG);
    }

    void getCarDetail(final String carId, final String vin) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait, Getting car detail...");
        pDialog.show();
        pDialog.setCancelable(false);

        String url = AppUrl.URL_CAR_DETAIL;

        final StringRequest reqCarDetail = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqCarDetail", "Response:" + response.toString());
                //pDialog.hide();
                try {
                    CarDetailResponse carDetailResponse = CarDetailResponse.parseJSON(response);

                    if (carDetailResponse.getStatusCode() == 1) {
                        // TODO set car detail
                        carAddActivity.setCarAdd(carDetailResponse.getCarDetail());
                        setCarDetail();
                    } else if (carDetailResponse.getStatusCode() == 0) {
                        CommonFunctions.showToast(getActivity().getBaseContext(), carDetailResponse.getMessage());
                        getActivity().finish();
                    } else if (carDetailResponse.getStatusCode() == 3) {
                        CommonFunctions.showToast(getActivity().getBaseContext(), carDetailResponse.getMessage());
                        getActivity().finish();
                    } else if (carDetailResponse.getStatusCode() == 4) {
                        // TODO Block user by admin or user not valid
                    } else {
                        CommonFunctions.showToast(getActivity().getBaseContext(), "Error : Network " + carDetailResponse.getMessage());
                        getActivity().finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqCarDetail", "catch");
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqCarDetail", "Error Response: " + error.getMessage());
                CommonFunctions.showToast(getActivity().getBaseContext(), "Network error");
                getActivity().finish();
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
                Log.e("reqCarDetail", "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqCarDetail, TAG);
    }

    private enum TypeStage {MAIN, SUB}

}
