package com.creadigol.drivehere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creadigol.drivehere.R;

import java.util.List;

public class StageAddCarDialogFragment extends DialogFragment implements View.OnClickListener {
    private List<String> options;
    private ListDialogListener mListener;
    private String tag;
    private RecyclerView recyclerView;
    private String title;
    private String description;

    public StageAddCarDialogFragment(ListDialogListener listener, List<String> options, String tag, String title, String description) {
        mListener = listener;
        this.options = options;
        this.tag = tag;
        this.title = title;
        this.description = description;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lotcode_choice, null);

        if (description != null && description.trim().length() > 0) {
            TextView tvDescription = (TextView) view.findViewById(R.id.tv_description);
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        CustomAdapter adapter = new CustomAdapter(recyclerView, options);
        recyclerView.setAdapter(adapter);
        builder.setView(view);

        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onDialogNegativeClick(StageAddCarDialogFragment.this);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        // Auto-generated method stub
        int position = recyclerView.getChildAdapterPosition(v);
        if (!options.get(position).equalsIgnoreCase("null")) {
            mListener.onItemClick(position, tag);
            dismiss();
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        List<String> options;
        LayoutInflater inflater;
        RecyclerView recyclerView;

        public CustomAdapter(RecyclerView recyclerView, List<String> options) {
            this.recyclerView = recyclerView;
            this.options = options;
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = inflater.inflate(R.layout.row_dialog_simple, viewGroup, false);
            v.setOnClickListener(StageAddCarDialogFragment.this);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            String stage = options.get(position);
            if (stage.equalsIgnoreCase("null")) {
                viewHolder.tvOption.setVisibility(View.GONE);
                viewHolder.viewRedLine.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvOption.setVisibility(View.VISIBLE);
                viewHolder.viewRedLine.setVisibility(View.GONE);
                viewHolder.tvOption.setText(stage);
                if(position > 6){
                    viewHolder.tvOption.setTextColor(getResources().getColor(R.color.red));
                } else {
                    viewHolder.tvOption.setTextColor(getResources().getColor(R.color.black));
                }
            }

        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOption;
            View viewRedLine;

            public ViewHolder(View v) {
                super(v);
                tvOption = (TextView) v.findViewById(R.id.tv_option);
                viewRedLine = (View) v.findViewById(R.id.view_red_line);
            }
        }
    }
}
