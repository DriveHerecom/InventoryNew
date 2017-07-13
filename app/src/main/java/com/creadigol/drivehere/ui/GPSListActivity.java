package com.creadigol.drivehere.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.adapter.GpsAdapter;
import com.creadigol.drivehere.util.DividerItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;

public class GPSListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_GPS = "gps_array";
    public static final String EXTRA_KEY_TITLE = "title";
    private final int REQUEST_ADD_GPS = 1001;
    private ArrayList<CarAdd.GPS> gpsList;
    private RecyclerView mRecyclerView;
    private GpsAdapter gpsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        gpsList = (ArrayList<CarAdd.GPS>) args.getSerializable(EXTRA_KEY_GPS);
        String title = args.getString(EXTRA_KEY_TITLE, "");

        if(gpsList == null){
            gpsList = new ArrayList<>();
        }

        if (getSupportActionBar() != null) {
            if (title.trim().length() > 0) {
                getSupportActionBar().setTitle(title);
            } else {
                getSupportActionBar().setTitle(getString(R.string.title_activity_gps_list));
            }
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.cl_add_new_gps).setOnClickListener(this);
        findViewById(R.id.cl_btn_done).setOnClickListener(this);

        setGpsList();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    // Set GPS list
    private void setGpsList() {
        if (gpsList != null && gpsList.size() > 0) {
            if (gpsAdapter == null) {
                gpsAdapter = new GpsAdapter(GPSListActivity.this, this.gpsList, null);
                mRecyclerView.setAdapter(gpsAdapter);
            } else {
                gpsAdapter.notifyDataSetChanged(this.gpsList);
            }
            findViewById(R.id.cl_no_gps_found).setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.cl_no_gps_found).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_add_new_gps:
                // open add gps
                Intent intentAddImages = new Intent(GPSListActivity.this, GpsAddActivity.class);
                startActivityForResult(intentAddImages, REQUEST_ADD_GPS);
                break;

            case R.id.cl_btn_done:
                // send change data to parent activity
                done();
                break;
        }
    }

    public void done() {
        Intent intent = new Intent();
        // add put extra
        Bundle args = new Bundle();
        args.putSerializable(GPSListActivity.EXTRA_KEY_GPS, (Serializable) gpsList);
        intent.putExtra("BUNDLE", args);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_GPS && resultCode == Activity.RESULT_OK) {
            CarAdd.GPS gps = (CarAdd.GPS) data.getSerializableExtra(GpsAddActivity.EXTRA_KEY_GPS);

            if (gps != null) {
                // set images

                gpsList.add(gps);
                if (gpsAdapter != null)
                    gpsAdapter.notifyInsertItem(gpsList);
                else
                    setGpsList();
            }
        }
    }
}
