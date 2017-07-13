package com.creadigol.drivehere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creadigol.drivehere.Model.AuctionReport;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.ui.CarListActivity;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class AuctionReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AuctionReport> auctionReports;
    private Context context;
    private View.OnClickListener clickListener;

    public AuctionReportAdapter(Context context, List<AuctionReport> auctionReports, View.OnClickListener clickListener) {

        if (auctionReports == null) {
            throw new IllegalArgumentException(
                    "auctionReports must not be null");
        }
        this.context = context;
        this.auctionReports = auctionReports;
        this.clickListener = clickListener;
        Log.e("tag", this.auctionReports.size() + "size");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_auction_list_main, viewGroup, false);
        return new AuctionMainViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        final AuctionReport auctionReport = auctionReports.get(i);
        final AuctionMainViewHolder auctionMainViewHolder = (AuctionMainViewHolder) viewHolder;
        // set default_car data
        Log.e("auctionReport total car", "auctionReport.getTotalCars() " + auctionReport.getTotalCars());
        auctionMainViewHolder.tvTotalCar.setText(String.valueOf(auctionReport.getTotalCars()));
        auctionMainViewHolder.tvAuctionName.setText(CommonFunctions.base64Decode(auctionReport.getAuctionName()));

        if (auctionReport.isDetailVisible())
            auctionMainViewHolder.clAuctionSubContent.setVisibility(View.VISIBLE);
        else
            auctionMainViewHolder.clAuctionSubContent.setVisibility(View.GONE);

        auctionMainViewHolder.clAuctionMainContent.setTag(i);

        auctionMainViewHolder.clAuctionMainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashParams = new HashMap<>();
                hashParams.put(ParamsKey.AUCTION_NAME, CommonFunctions.base64Decode(auctionReport.getAuctionName()));

                Intent search = new Intent(context, CarListActivity.class);
                search.putExtra(CarListActivity.EXTRA_KEY_PARAMS, hashParams);
                search.putExtra(CarListActivity.EXTRA_KEY_LIST_TYPE, CarListActivity.LIST_TYPE.Auction);
                context.startActivity(search);
            }
        });

    }

    @Override
    public int getItemCount() {
        return auctionReports.size();
    }


    class AuctionMainViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTotalCar, tvAuctionName;
        public RecyclerView rvAuctionSub;
        public ConstraintLayout clAuctionMainContent, clAuctionSubContent;

        public AuctionMainViewHolder(View itemView) {
            super(itemView);
            tvTotalCar = (TextView) itemView.findViewById(R.id.tv_total_car);
            tvAuctionName = (TextView) itemView.findViewById(R.id.tv_auction_name);
            rvAuctionSub = (RecyclerView) itemView.findViewById(R.id.rv_auction_list_sub);
            clAuctionMainContent = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_main_content);
            clAuctionSubContent = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_list_sub);
        }
    }

    public class AuctionReportSubAdapter extends RecyclerView.Adapter<AuctionReportSubAdapter.AuctionReportSubViewHolder> {

        List<AuctionReport.LotCodeAuction> lotCodeAuctions;
        String auctionName;

        public AuctionReportSubAdapter(List<AuctionReport.LotCodeAuction> prescriptionListProd, String auctionName) {

            if (prescriptionListProd == null) {
                throw new IllegalArgumentException(
                        "lotCodeRepo must not be null");
            }

            this.lotCodeAuctions = prescriptionListProd;
            this.auctionName = auctionName;
        }

        @Override
        public AuctionReportSubViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_auction_list_sub,
                            viewGroup,
                            false);
            return new AuctionReportSubViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                AuctionReportSubViewHolder viewHolder, int position) {
            AuctionReport.LotCodeAuction lotCodeAuction = lotCodeAuctions.get(position);

            viewHolder.tvLotCode.setText(lotCodeAuction.getLotCode() + ":");
            viewHolder.tvTotalCar.setText(String.valueOf(lotCodeAuction.getTotalCars()));
            viewHolder.clAuctionLotCode.setTag(auctionName + Constant.SEPARATOR_AUCTION_VIEW_TAG + lotCodeAuction.getLotCode());
            viewHolder.clAuctionLotCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = (String) v.getTag();
                    String[] s = tag.split("%20");
                    //CommonFunctions.showToast(context, "A:" + s[0] + " L:" + s[1]);
                    HashMap<String, String> hashParams = new HashMap<>();
                    hashParams.put(ParamsKey.AUCTION_NAME, s[0]);
                    hashParams.put(ParamsKey.LOT_CODE, s[1]);

                    Intent search = new Intent(context, CarListActivity.class);
                    search.putExtra(CarListActivity.EXTRA_KEY_PARAMS, hashParams);
                    search.putExtra(CarListActivity.EXTRA_KEY_LIST_TYPE, CarListActivity.LIST_TYPE.Auction);
                    context.startActivity(search);
                    
                }
            });
        }

        @Override
        public int getItemCount() {
            return lotCodeAuctions.size();
        }

        public final class AuctionReportSubViewHolder
                extends RecyclerView.ViewHolder {

            TextView tvLotCode;
            TextView tvTotalCar;
            ConstraintLayout clAuctionLotCode;

            public AuctionReportSubViewHolder(View itemView) {
                super(itemView);
                tvLotCode = (TextView) itemView.findViewById(R.id.tv_lot_code);
                tvTotalCar = (TextView) itemView.findViewById(R.id.tv_total_car);
                clAuctionLotCode = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_lot_code);
            }
        }
    }
}
