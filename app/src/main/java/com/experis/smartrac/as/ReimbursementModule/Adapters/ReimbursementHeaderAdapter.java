package com.experis.smartrac.as.ReimbursementModule.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.experis.smartrac.as.ReimbursementModule.Models.ReimbursementHeaderModel;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementDailyTravelAllowanceFormActivity;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementLodgingFormActivity;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementOthersFormActivity;
import com.experis.smartrac.as.R;

import java.util.List;

/**
 * Created by RajPrudhviMarella on 03/Dec/2021.
 */

public class ReimbursementHeaderAdapter extends RecyclerView.Adapter<ReimbursementHeaderAdapter.ViewHolder> {
    private Context context;
    private List<ReimbursementHeaderModel> lstHeader;


    public ReimbursementHeaderAdapter(Context context, List<ReimbursementHeaderModel> lstHeader) {
        this.context = context;
        this.lstHeader = lstHeader;
    }

    @NonNull
    @Override
    public ReimbursementHeaderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lyt_reimbursement_header, viewGroup, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReimbursementHeaderAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.txtName.setText(lstHeader.get(i).getName());
        viewHolder.lytRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstHeader.get(i).getName().equalsIgnoreCase("Daily Travel Allowance")) {
                    Intent intent = new Intent(context, ReimbursementDailyTravelAllowanceFormActivity.class);
                    intent.putExtra("REIMBURSEMENT_HEADER", lstHeader.get(i).getName());
                    context.startActivity(intent);
                } else if (lstHeader.get(i).getName().equalsIgnoreCase("Lodging and Boarding")) {
                    Intent intent = new Intent(context, ReimbursementLodgingFormActivity.class);
                    intent.putExtra("REIMBURSEMENT_HEADER", lstHeader.get(i).getName());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ReimbursementOthersFormActivity.class);
                    intent.putExtra("REIMBURSEMENT_HEADER", lstHeader.get(i).getName());
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstHeader.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private RelativeLayout lytRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_assignment_name);
            lytRow = itemView.findViewById(R.id.lyt_row);
        }
    }
}
