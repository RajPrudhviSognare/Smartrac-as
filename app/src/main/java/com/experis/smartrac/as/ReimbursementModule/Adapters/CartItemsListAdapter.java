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

import com.experis.smartrac.as.ReimbursementModule.Models.ClaimData;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementDailyTravelAllowanceFormActivity;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementLodgingFormActivity;
import com.experis.smartrac.as.ReimbursementModule.ReimbursementOthersFormActivity;
import com.experis.smartrac.as.R;
import com.experis.smartrac.as.Utils;

import java.util.List;

/**
 * Created by RajPrudhviMarella on 13/Mar/2022.
 */
public class CartItemsListAdapter extends RecyclerView.Adapter<CartItemsListAdapter.ViewHolder> {
    private Context context;
    private List<ClaimData> lstClaimData;

    public CartItemsListAdapter(Context context, List<ClaimData> lstClaimData) {
        this.context = context;
        this.lstClaimData = lstClaimData;
    }

    @NonNull
    @Override
    public CartItemsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lyt_cart_item_row, viewGroup, false);
        CartItemsListAdapter.ViewHolder holder = new CartItemsListAdapter.ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemsListAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.txtAmount.setText(lstClaimData.get(i).getAmount());
        viewHolder.txtDate.setText("Claim date:" + lstClaimData.get(i).getBillDate());
        viewHolder.txtSubHeader.setText(lstClaimData.get(i).getOthersHead());
        viewHolder.txtHeader.setText(lstClaimData.get(i).getHead());
        viewHolder.lytRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lstClaimData.get(i).getHead().equalsIgnoreCase("Daily Travel Allowance")) {
                    Intent intent = new Intent(context, ReimbursementDailyTravelAllowanceFormActivity.class);
                    intent.putExtra("CLAIM_DATA", Utils.gsonExposeExclusive.toJson(lstClaimData.get(i)));
                    intent.putExtra("REIMBURSEMENT_HEADER", lstClaimData.get(i).getHead());

                    context.startActivity(intent);
                } else if (lstClaimData.get(i).getHead().equalsIgnoreCase("Lodging and Boarding")) {
                    Intent intent = new Intent(context, ReimbursementLodgingFormActivity.class);
                    intent.putExtra("CLAIM_DATA", Utils.gsonExposeExclusive.toJson(lstClaimData.get(i)));
                    intent.putExtra("REIMBURSEMENT_HEADER", lstClaimData.get(i).getHead());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ReimbursementOthersFormActivity.class);
                    intent.putExtra("CLAIM_DATA", Utils.gsonExposeExclusive.toJson(lstClaimData.get(i)));
                    intent.putExtra("REIMBURSEMENT_HEADER", lstClaimData.get(i).getHead());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstClaimData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHeader;
        private TextView txtSubHeader;
        private TextView txtAmount;
        private TextView txtDate;
        private RelativeLayout lytRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHeader = itemView.findViewById(R.id.txt_assignment_name);
            txtSubHeader = itemView.findViewById(R.id.txt_assignment_description);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtAmount = itemView.findViewById(R.id.txt_amount);
            lytRow = itemView.findViewById(R.id.lyt_row);
        }
    }
}
