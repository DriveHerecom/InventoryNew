package com.creadigol.drivehere.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class CarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Car> cars;
    private Context context;
    private View.OnClickListener clickListener;
    private String type;

    public CarAdapter(Context context, List<Car> cars, View.OnClickListener clickListener,String type ) {
        this.context = context;
        this.cars = cars;
        this.clickListener = clickListener;
        this.type=type;
        Log.e("tag", this.cars.size() + "size");
    }

    @Override
    public int getItemViewType(int position) {
        return cars.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_car, viewGroup, false);
            return new CarViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof CarViewHolder) {
            Car car = cars.get(i);
            CarViewHolder carViewHolder = (CarViewHolder) viewHolder;
            // set default_car data
            carViewHolder.tvTitle.setText(car.getModelYear() + " " + car.getMake() + " " + car.getModel());
            carViewHolder.tvVin.setText(car.getVin().toUpperCase());
            carViewHolder.tvRfid.setText(car.getRfid().toUpperCase());
            carViewHolder.tvNote.setText(car.getNote());
            carViewHolder.tvMiles.setText(car.getMiles());
            carViewHolder.tvLotCode.setText(car.getLotCode());

            if(type.equalsIgnoreCase("repo")){
                carViewHolder.cn_auction.setVisibility(View.VISIBLE);
                carViewHolder.cn_search.setVisibility(View.GONE);
                carViewHolder.textAuctionName.setText("Repo Company");
                carViewHolder.textAuctionDate.setText("Delivered Date");
                carViewHolder.textAuctionMile.setText("Status");
                carViewHolder.textCondition.setText("Voluntary");
                carViewHolder.textFloorPrice.setText("Assigned Date");

                carViewHolder.tvCondition.setText(car.getRepoDetail().getIsVoluntary());
                carViewHolder.tvAuctionName.setText(car.getRepoDetail().getCompany()); // problem need it from api
                carViewHolder.tvAuctionMile.setText(car.getRepoDetail().getStatus()); // problem need it from api
                carViewHolder.tvFloorPrice.setText(CommonFunctions.getDate(Long.parseLong(car.getRepoDetail().getAssignedDate()))); // vacancy need it from api
                carViewHolder.tvAuctionDate.setText(CommonFunctions.getDate(Long.parseLong(car.getRepoDetail().getDeliveredDate())));

            }else if(type.equalsIgnoreCase("search")){
                carViewHolder.cn_auction.setVisibility(View.GONE);
                carViewHolder.cn_search.setVisibility(View.VISIBLE);
                carViewHolder.tvStage.setText(car.getStage());
                carViewHolder.tvStockNo.setText(car.getStockNumber().toUpperCase());
                carViewHolder.tvProblem.setText(car.getProblem()); // problem need it from api
                carViewHolder.tvVacancy.setText(car.getVacancy()); // vacancy need it from api
            }else if(type.equalsIgnoreCase("auction")){
                carViewHolder.cn_auction.setVisibility(View.VISIBLE);
                carViewHolder.cn_search.setVisibility(View.GONE);
                carViewHolder.tvAuctionName.setText(car.getAuctionDetails().getAuctionName());
                carViewHolder.tvFloorPrice.setText(car.getAuctionDetails().getFloorPrice());
                carViewHolder.tvAuctionDate.setText(CommonFunctions.getDate(Long.parseLong(car.getAuctionDetails().getAuctiondate()))); // problem need it from api
                carViewHolder.tvAuctionMile.setText(car.getAuctionDetails().getAuctionMile()); // vacancy need it from api
                carViewHolder.tvCondition.setText(car.getAuctionDetails().getCondition()); // vacancy need it from api
            }

            if (car.getAtAuction().equalsIgnoreCase("yes")) {
                carViewHolder.layTagAtAuction.setVisibility(View.VISIBLE);
            }

            int scanDays = (int) CommonFunctions.daysDifferent(car.getScanDate());
            if (scanDays > 0) {
                carViewHolder.layTagScanDays.setVisibility(View.VISIBLE);
                carViewHolder.tvTagScanDays.setText("Scan " + scanDays + " days ago");
            } else if (scanDays == -1) {
                carViewHolder.layTagScanDays.setVisibility(View.VISIBLE);
                carViewHolder.tvTagScanDays.setText("Not Scanned yet");
            } else if (scanDays == 0){
                carViewHolder.layTagScanDays.setVisibility(View.VISIBLE);
                carViewHolder.tvTagScanDays.setText("Scan today");
            }

            if (car.getImages() != null && car.getImages().size() > 0)
                MyApplication.getInstance().getImageLoader().displayImage(car.getImages().get(0).getImage(), carViewHolder.ivCar, getDisplayImageOptions());
            else
                MyApplication.getInstance().getImageLoader().displayImage("", carViewHolder.ivCar, getDisplayImageOptions());

            carViewHolder.layMain.setTag(i);
            carViewHolder.layMain.setOnClickListener(clickListener);
        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void notifyDataSetChanged(List<Car> cars) {
        this.cars = cars;
        this.notifyDataSetChanged();
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvVin, tvRfid, tvMiles, tvLotCode,
                tvStage, tvVacancy, tvProblem, tvStockNo, tvNote, tvTagScanDays;
        public TextView textAuctionName,textFloorPrice,textAuctionMile,textAuctionDate,textCondition;
        public  TextView tvAuctionName,tvFloorPrice,tvAuctionMile,tvAuctionDate,tvCondition;
        public ImageView ivCar;
        public ConstraintLayout layMain, layTagScanDays, layTagAtAuction,cn_search,cn_auction;

        public CarViewHolder(View itemView) {
            super(itemView);
            textAuctionName= (TextView) itemView.findViewById(R.id.textAuctionName);
            textFloorPrice= (TextView) itemView.findViewById(R.id.textFloorPrice);
            textAuctionMile= (TextView) itemView.findViewById(R.id.textAuctionMile);
            textAuctionDate= (TextView) itemView.findViewById(R.id.textAuctionDate);
            textCondition= (TextView) itemView.findViewById(R.id.textCondition);
            tvAuctionName= (TextView) itemView.findViewById(R.id.tvAuctionName);
            tvAuctionDate= (TextView) itemView.findViewById(R.id.tvAuctionDate);
            tvAuctionMile= (TextView) itemView.findViewById(R.id.tvAuctionMile);
            tvFloorPrice= (TextView) itemView.findViewById(R.id.tvFloorPrice);
            tvCondition= (TextView) itemView.findViewById(R.id.tvCondition);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvVin = (TextView) itemView.findViewById(R.id.tv_vin);
            tvRfid = (TextView) itemView.findViewById(R.id.tv_rfid);
            tvMiles = (TextView) itemView.findViewById(R.id.tv_miles);
            tvLotCode = (TextView) itemView.findViewById(R.id.tv_lot_code);

            tvStage = (TextView) itemView.findViewById(R.id.tv_stage);
            tvVacancy = (TextView) itemView.findViewById(R.id.tv_vacancy);
            tvProblem = (TextView) itemView.findViewById(R.id.tv_problem);
            tvStockNo = (TextView) itemView.findViewById(R.id.tv_stock_no);
            tvNote = (TextView) itemView.findViewById(R.id.tv_note);
            tvTagScanDays = (TextView) itemView.findViewById(R.id.tv_tag_scan_days);

            ivCar = (ImageView) itemView.findViewById(R.id.iv_car);
            layMain = (ConstraintLayout) itemView.findViewById(R.id.lay_main);
            layTagAtAuction = (ConstraintLayout) itemView.findViewById(R.id.lay_tag_at_auction);
            layTagScanDays = (ConstraintLayout) itemView.findViewById(R.id.lay_tag_scan_days);
            cn_search= (ConstraintLayout) itemView.findViewById(R.id.cn_search);
            cn_auction= (ConstraintLayout) itemView.findViewById(R.id.cn_auction);

        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
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
