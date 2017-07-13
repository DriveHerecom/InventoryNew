package com.creadigol.drivehere.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.support.constraint.ConstraintLayout;

import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.LotCodeDialogFragment;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.dialog.StageAddCarDialogFragment;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.creadigol.drivehere.ui.SearchActivity.ScanType.RFID;
import static com.creadigol.drivehere.ui.SearchActivity.ScanType.VIN;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_SCAN_VIN = 1001;
    public static final int REQUEST_SCAN_RFID = 1002;
    public static final int REQUEST_PERMISSION_CAMERA = 1003;

    public final String TAG_DIALOG_LOT_CODE = "Lot Code";
    public final String TAG_DIALOG_STAGE = "Stage";
    public final String TAG_DIALOG_HAS_RFID = "has RFID?";
    public final String TAG_DIALOG_HAS_TITLE = "has Title?";

    private TextView tvHasTitle, tvStage, tvLotCode, tvProblem,
            tvHasRfid, tvScanVin, tvScanRfid, tvSearchVacancyAvailable, tvSearchVacancySold, tvSearchVacancyAll;
    private EditText edtVin, edtRfid, edtScanDaysFrom, edtScanDaysTo;
    private ScanType scanType;
    private CheckBox checkBox;

    private String vin = "", rfid = "";
    TextWatcher twVin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            vin = s.toString();
            if (vin.trim().length() > 0 || rfid.trim().length() > 0) {
                findViewById(R.id.cl_search_disable_more_option).setVisibility(View.VISIBLE);
                findViewById(R.id.cl_other_search_options).setVisibility(View.GONE);
            } else {
                findViewById(R.id.cl_search_disable_more_option).setVisibility(View.GONE);
                findViewById(R.id.cl_other_search_options).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher twRfid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            rfid = s.toString();
            if (rfid.trim().length() > 0 || vin.trim().length() > 0) {
                findViewById(R.id.cl_search_disable_more_option).setVisibility(View.VISIBLE);
                findViewById(R.id.cl_other_search_options).setVisibility(View.GONE);
            } else {
                findViewById(R.id.cl_search_disable_more_option).setVisibility(View.GONE);
                findViewById(R.id.cl_other_search_options).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private Vacancy mVacancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtVin = (EditText) findViewById(R.id.edt_vin);
        edtRfid = (EditText) findViewById(R.id.edt_rfid);
        edtScanDaysFrom = (EditText) findViewById(R.id.edt_days_from);
        edtScanDaysTo = (EditText) findViewById(R.id.edt_days_to);

        checkBox = (CheckBox) findViewById(R.id.checkBox_Yes);
        edtVin.addTextChangedListener(twVin);
        edtRfid.addTextChangedListener(twRfid);

        tvScanVin = (TextView) findViewById(R.id.tv_scan_vin);
        tvScanRfid = (TextView) findViewById(R.id.tv_scan_rfid);
        tvHasRfid = (TextView) findViewById(R.id.tv_has_rfid);
        tvHasTitle = (TextView) findViewById(R.id.tv_has_title);
        tvStage = (TextView) findViewById(R.id.tv_stage);
        tvProblem = (TextView) findViewById(R.id.tv_problem);
        tvLotCode = (TextView) findViewById(R.id.tv_lot_code);
        tvSearchVacancyAvailable = (TextView) findViewById(R.id.tv_search_available);
        tvSearchVacancySold = (TextView) findViewById(R.id.tv_search_sold);
        tvSearchVacancyAll = (TextView) findViewById(R.id.tv_search_vacancy_all);

        findViewById(R.id.lay_btn_search).setOnClickListener(this);
        tvScanVin.setOnClickListener(this);
        tvScanRfid.setOnClickListener(this);
        tvLotCode.setOnClickListener(this);
        tvStage.setOnClickListener(this);
        tvProblem.setOnClickListener(this);
        tvHasRfid.setOnClickListener(this);
        tvHasTitle.setOnClickListener(this);
        tvSearchVacancyAll.setOnClickListener(this);
        tvSearchVacancySold.setOnClickListener(this);
        tvSearchVacancyAvailable.setOnClickListener(this);

        CommonFunctions.getMillisecondsForDaysPast(4);

        selectVacancySwitch(Vacancy.AVAILABLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConstraintLayout clScanDays = (ConstraintLayout) findViewById(R.id.cl_scan_days);
                View view = (View) findViewById(R.id.view_has_title);
                if (isChecked == true) {
                    clScanDays.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                } else if (isChecked == false) {
                    clScanDays.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
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
    protected void onResume() {
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_has_rfid:
                showSimpleDialog(TAG_DIALOG_HAS_RFID);
                break;

            case R.id.tv_has_title:
                showSimpleDialog(TAG_DIALOG_HAS_TITLE);
                break;

            case R.id.tv_stage:
                showStageDialog();
                break;

            case R.id.tv_problem:
                showProblemDialog();
                break;

            case R.id.tv_lot_code:
                showLotCodeDialog();
                break;

            case R.id.tv_scan_vin:
                scanType = VIN;
                if (MyApplication.getInstance().needPermissionCheck()) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        startScanning();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    startScanning();
                }
                break;

            case R.id.tv_scan_rfid:
                scanType = RFID;
                if (MyApplication.getInstance().needPermissionCheck()) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        startScanning();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    startScanning();
                }
                break;

            case R.id.lay_btn_search:

                HashMap<String, String> hashSearch = getSearchHash();

                if (hashSearch != null) {
                    // make search request to server
                    Intent search = new Intent(this, CarListActivity.class);
                    search.putExtra(CarListActivity.EXTRA_KEY_PARAMS, hashSearch);
                    search.putExtra(CarListActivity.EXTRA_KEY_LIST_TYPE, CarListActivity.LIST_TYPE.Search);
                    startActivity(search);
                }
                // this.finish();
                break;

            case R.id.tv_search_vacancy_all:
                selectVacancySwitch(Vacancy.ALL);
                break;

            case R.id.tv_search_available:
                selectVacancySwitch(Vacancy.AVAILABLE);
                break;

            case R.id.tv_search_sold:
                selectVacancySwitch(Vacancy.SOLD);
                break;
        }
    }

    public void selectVacancySwitch(Vacancy vacancy) {

        if (vacancy == mVacancy) {
            return;
        }

        if (vacancy == Vacancy.AVAILABLE) {
            tvSearchVacancyAvailable.setTextColor(getResources().getColor(R.color.white));
            tvSearchVacancyAvailable.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_selected));

            tvSearchVacancySold.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancySold.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));

            tvSearchVacancyAll.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancyAll.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));
        } else if (vacancy == Vacancy.SOLD) {
            tvSearchVacancySold.setTextColor(getResources().getColor(R.color.white));
            tvSearchVacancySold.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_selected));

            tvSearchVacancyAll.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancyAll.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));

            tvSearchVacancyAvailable.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancyAvailable.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));
        } else if (vacancy == Vacancy.ALL) {
            tvSearchVacancyAll.setTextColor(getResources().getColor(R.color.white));
            tvSearchVacancyAll.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_selected));

            tvSearchVacancySold.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancySold.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));

            tvSearchVacancyAvailable.setTextColor(getResources().getColor(R.color.black));
            tvSearchVacancyAvailable.setBackground(getResources().getDrawable(R.drawable.bg_search_switch_normal));
        }

        mVacancy = vacancy;

    }

    public HashMap<String, String> getSearchHash() {
        HashMap<String, String> hashSearch = new HashMap<>();


        String vin = edtVin.getText().toString();
        String rfid = edtRfid.getText().toString().trim();

        if (vin.trim().length() > 0 || rfid.trim().length() > 0) {
            if (vin != null && vin.trim().length() > 0) {
                hashSearch.put(ParamsKey.VIN, vin);
            }


            if (rfid != null && rfid.length() > 0) {
                hashSearch.put(ParamsKey.RFID, rfid);
            }
        } else {
            if (checkBox.isChecked()) {
                hashSearch.put(ParamsKey.NOT_SCANNED_YET,"Yes");
            } else {
                String strDaysFrom = edtScanDaysFrom.getText().toString().trim();
                String strDaysTo = edtScanDaysTo.getText().toString().trim();

                if (strDaysFrom.length() > 0 || strDaysTo.length() > 0) {
                    int daysFrom = -1, daysTo = -1;
                    try {
                        daysFrom = Integer.parseInt(strDaysFrom);
                    } catch (NumberFormatException e) {
                        daysFrom = -1;
                    }

                    try {
                        daysTo = Integer.parseInt(strDaysTo);
                    } catch (NumberFormatException e) {
                        daysTo = -1;
                    }

                    if (daysFrom > -1 && daysTo > -1) {
                        if (daysTo < daysFrom) {
                            CommonFunctions.showToast(SearchActivity.this, "For search by scan days 'To' days can't be smaller then 'From' days.");
                            return null;
                        }
                    }

                    if (daysFrom > -1) {
                        hashSearch.put(ParamsKey.SEARCH_SCAN_DAYS_FROM,
                                String.valueOf(CommonFunctions.getMillisecondsForDaysPast(daysFrom)));
                        if (daysTo > -1) {
                            hashSearch.put(ParamsKey.SEARCH_SCAN_DAYS_TO,
                                    String.valueOf(CommonFunctions.getMillisecondsForDaysPast(daysTo)));
                        } else {
                            hashSearch.put(ParamsKey.SEARCH_SCAN_DAYS_TO,
                                    String.valueOf(CommonFunctions.getMillisecondsForDaysPast(9999)));
                        }
                    } else if (daysTo > -1) {
                        hashSearch.put(ParamsKey.SEARCH_SCAN_DAYS_FROM,
                                String.valueOf(CommonFunctions.getMillisecondsForDaysPast(0)));
                        hashSearch.put(ParamsKey.SEARCH_SCAN_DAYS_TO,
                                String.valueOf(CommonFunctions.getMillisecondsForDaysPast(daysTo)));
                    }
                }
            }
            if (mVacancy != Vacancy.ALL)
                hashSearch.put(ParamsKey.VACANCY, mVacancy.toString());

            String hasRfid = tvHasRfid.getText().toString().trim();
            try {
                if (hasRfid != null && hasRfid.length() > 0
                        && !hasRfid.equalsIgnoreCase(getResources().getString(R.string.has_rfid))) {
                    hasRfid = hasRfid.replace(TAG_DIALOG_HAS_RFID, "");
                    hashSearch.put(ParamsKey.HAS_RFID, hasRfid.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String hasTitle = tvHasTitle.getText().toString().trim();
            try {
                if (hasTitle != null && hasTitle.length() > 0
                        && !hasTitle.equalsIgnoreCase(getResources().getString(R.string.has_title))) {
                    //if (hasTitle.contains(getResources().getString(R.string.has_title)))
                    hasTitle = hasTitle.replace(TAG_DIALOG_HAS_TITLE, "");
                    hashSearch.put(ParamsKey.HAS_TITLE, hasTitle.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String stage = tvStage.getText().toString().trim();
            try {
                if (stage != null && stage.length() > 0
                        && !stage.equalsIgnoreCase(getResources().getString(R.string.stage))) {
                    stage = stage.replace(TAG_DIALOG_STAGE + " - ", "");
                    hashSearch.put(ParamsKey.VEHICLE_STAGE, stage.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String problem = tvProblem.getText().toString().trim();
            String tagProblem = getResources().getString(R.string.problem);
            try {
                if (problem != null && problem.length() > 0
                        && !problem.equalsIgnoreCase(tagProblem)) {
                    problem = problem.replace(tagProblem + " - ", "");
                    hashSearch.put(ParamsKey.PROBLEM, problem.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String lotCode = tvLotCode.getText().toString().trim();
            try {
                if (lotCode != null && lotCode.length() > 0
                        && !lotCode.equalsIgnoreCase(getResources().getString(R.string.lot_code))) {
                    lotCode = lotCode.replace(TAG_DIALOG_LOT_CODE + " - ", "");
                    hashSearch.put(ParamsKey.LOT_CODE, lotCode.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return hashSearch;
    }

    public void showSimpleDialog(String tag) {

        final String[] options = new String[]{"Yes", "No"};

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                if (tag.equals(TAG_DIALOG_HAS_RFID)) {
                    tvHasRfid.setText(tag + " " + options[position]);
                    tvHasRfid.setTextColor(getResources().getColor(R.color.search));
                } else if (tag.equals(TAG_DIALOG_HAS_TITLE)) {
                    tvHasTitle.setText(tag + " " + options[position]);
                    tvHasTitle.setTextColor(getResources().getColor(R.color.search));
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        SingleChoiceDialogFragment dialog = new SingleChoiceDialogFragment(listener, options, tag, "Select " + tag, null);
        dialog.show(getSupportFragmentManager(), tag);

    }

    public void showStageDialog() {

        ArrayList<String> stagesTemp = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Stage))); //(ArrayList<String>) Arrays.asList(getResources().getStringArray(R.array.Stage));

        stagesTemp.add("null");

        stagesTemp.addAll(Arrays.asList(getResources().getStringArray(R.array.Stage_sub)));

        final List<String> stages = stagesTemp;
        stagesTemp = null;

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                tvStage.setText(tag + " - " + stages.get(position));
                tvStage.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }
        };

        StageAddCarDialogFragment stageDialog = new StageAddCarDialogFragment(listener, stages, TAG_DIALOG_STAGE, "Select " + TAG_DIALOG_STAGE, null);
        stageDialog.show(getSupportFragmentManager(), TAG_DIALOG_STAGE);

    }


    public void showProblemDialog() {

        final String[] problems = getResources().getStringArray(R.array.Problem);

        final ListDialogListener listener = new ListDialogListener() {
            @Override
            public void onItemClick(int position, String tag) {
                tvProblem.setText(tag + " - " + problems[position]);
                tvProblem.setTextColor(getResources().getColor(R.color.search));
                //tvStage.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }
        };

        SingleChoiceDialogFragment stageDialog = new SingleChoiceDialogFragment(listener, problems, getString(R.string.problem), "Select " + getString(R.string.problem), "");
        stageDialog.show(getSupportFragmentManager(), getString(R.string.problem));

    }


    public void showLotCodeDialog() {

        final String[] lotList = getResources().getStringArray(R.array.Lotcode);
        //final String[] colorValueList = getResources().getStringArray(R.array.LotCodeColorValue);

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                tvLotCode.setText(tag + " - " + lotList[position]);
                //lotCode.setTag(colorValueList);
//                tvLotCode.setTextColor(Color.parseColor(colorValueList[position]));
                tvLotCode.setTextColor(getResources().getColor(R.color.search));
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
            }

        };

        LotCodeDialogFragment dialog1 = new LotCodeDialogFragment(listener, TAG_DIALOG_LOT_CODE);
        dialog1.show(getSupportFragmentManager(), TAG_DIALOG_LOT_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanning();
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);

                builder.setTitle("");
                builder.setMessage("Camera Permission Needed For Scanning,Allow It?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(getApplicationContext(), "Camera Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void startScanning() {
        if (scanType == RFID) {
            Intent scannerRfid = new Intent(SearchActivity.this,
                    ScannerActivity.class);
            startActivityForResult(scannerRfid, REQUEST_SCAN_RFID);
        } else if (scanType == VIN) {
            Intent scannerVin = new Intent(SearchActivity.this,
                    ScannerActivity.class);
            startActivityForResult(scannerVin, REQUEST_SCAN_VIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN_RFID && resultCode == Activity.RESULT_OK) {
            String scanCode = data.getStringExtra("code");
            int scanCodeLength = scanCode.length();
            Log.e("scanCode", scanCode);
            if (scanCodeLength == Constant.LENGTH_RFID) {
                edtRfid.setText(scanCode);
            }
        } else if (requestCode == REQUEST_SCAN_VIN && resultCode == Activity.RESULT_OK) {
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
                edtVin.setText(scanCode);
            }
        }
    }

    public enum ScanType {VIN, RFID}

    private enum Vacancy {AVAILABLE, SOLD, ALL}
}


