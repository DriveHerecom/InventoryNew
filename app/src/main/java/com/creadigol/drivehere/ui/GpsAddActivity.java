package com.creadigol.drivehere.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class GpsAddActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_GPS = "gps";
    private final int REQUEST_ADD_IMAGE = 1001;
    EditText edtGpsSerialNumber, edtTechnicianName;
    ImageView ivGps;
    String gpsImagePath = "";
    CarAdd.GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

        }

        edtGpsSerialNumber = (EditText) findViewById(R.id.edt_serial_number);
        edtTechnicianName = (EditText) findViewById(R.id.edt_technician_name);

        edtGpsSerialNumber.addTextChangedListener(twSerialNumber);
        edtTechnicianName.addTextChangedListener(twTechnicianName);

        ivGps = (ImageView) findViewById(R.id.iv_gps_image);
        ivGps.setOnClickListener(this);
        findViewById(R.id.cl_btn_add).setOnClickListener(this);

    }

    private TextWatcher twSerialNumber = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String miles = s.toString().trim();
            if (miles.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_serial_number_hint)).setText(getString(R.string.serial_number));
            } else {
                ((TextView) findViewById(R.id.tv_serial_number_hint)).setText(getString(R.string.enter));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher twTechnicianName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String miles = s.toString().trim();
            if (miles.trim().length() > 0) {
                ((TextView) findViewById(R.id.tv_technician_name_hint)).setText(getString(R.string.technician));
            } else {
                ((TextView) findViewById(R.id.tv_technician_name_hint)).setText(getString(R.string.enter));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cl_btn_add) {

            String serialNo = edtGpsSerialNumber.getText().toString();
            if (serialNo.trim().length() <= 0) {
                CommonFunctions.showToast(this, "Serial no is mandatory");
                return;
            }

            String technicianName = edtTechnicianName.getText().toString();

            if (gpsImagePath != null && gpsImagePath.trim().length() > 0) {

            } else {
                CommonFunctions.showToast(this, "Gps image is mandatory");
                return;
            }

            if (gps == null) {
                gps = new CarAdd.GPS();
            }

            gps.setImage(gpsImagePath);
            gps.setValue(serialNo);
            gps.setTechnicianName(technicianName);

            // return gps to parent activity
            done();
        } else if (v.getId() == R.id.iv_gps_image) {
            Intent intentAddTitleImage = new Intent(GpsAddActivity.this, AddImagesActivity.class);
            intentAddTitleImage.putExtra(AddImagesActivity.EXTRA_KEY_IS_SINGLE, true);
            startActivityForResult(intentAddTitleImage, REQUEST_ADD_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> mImages = data.getStringArrayListExtra(AddImagesActivity.EXTRA_KEY_IMAGES);
            //gpsImagePath = "";
            if (mImages != null && mImages.size() > 0) {
                // set images
                gpsImagePath = mImages.get(mImages.size() - 1);
                if (gpsImagePath.trim().length() > 0)
                    setTitleImage(mImages);
            }
        }
    }

    private void done() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_GPS, gps);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setTitleImage(final ArrayList<String> imagePaths) {

        String imagePath = "";
        if (imagePaths != null && imagePaths.size() > 0) {
            // set images
            imagePath = imagePaths.get(imagePaths.size() - 1);
        }

        if (imagePath != null && imagePath.trim().length() > 0) {
            if (!imagePath.startsWith(Constant.PREFIX_HTTPS)) {
                imagePath = "file://" + imagePath;
            }

            MyApplication.getInstance().getImageLoader().displayImage(imagePath, ivGps, getDisplayImageOptions(), new SimpleImageLoadingListener() {
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

                    CommonFunctions.showToast(GpsAddActivity.this, message);
                    ivGps.setVisibility(View.GONE);
                    //getView().findViewById(R.id.tv_title_image).setVisibility(View.VISIBLE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //carAddActivity.getCarAdd().setTitleLocalImages(imagePaths);
                }
            });

            //ivImage.setVisibility(View.VISIBLE);
            //getView().findViewById(R.id.tv_title_image).setVisibility(View.GONE);
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
}
