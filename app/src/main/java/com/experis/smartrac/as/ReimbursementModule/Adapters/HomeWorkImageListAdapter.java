package com.experis.smartrac.as.ReimbursementModule.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.experis.smartrac.as.ReimbursementModule.Models.InQueueForUploadDataModel;
import com.experis.smartrac.as.R;

import java.util.ArrayList;


/**
 * Created by RajPrudhviMarella on 02/Dec/2021.
 */

public class HomeWorkImageListAdapter extends RecyclerView.Adapter {
    private Context context;
    ArrayList<InQueueForUploadDataModel> invoiceDataList;
    private static final int ITEM_TYPE_ADD = 0;
    private static final int ITEM_TYPE_IMAGE = 2;
    private onNewCameraClicked onNewCameraClicked;

    public HomeWorkImageListAdapter(Context context, ArrayList<InQueueForUploadDataModel> invoiceDataList, onNewCameraClicked onNewCameraClicked) {
        this.context = context;
        this.invoiceDataList = invoiceDataList;
        this.onNewCameraClicked = onNewCameraClicked;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_ADD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_grn_add_image_item, parent, false);
            return new ViewHolderAdder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_grn_image_item, parent, false);
            return new ViewHolderImage(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        InQueueForUploadDataModel item = invoiceDataList.get(position);
        if (item.isNewAdded()) {
            ViewHolderAdder viewHolder = (ViewHolderAdder) holder;
            viewHolder.lytRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNewCameraClicked.onNewCameraClicked();
                }
            });
        } else {
            final ViewHolderImage viewHolder = (ViewHolderImage) holder;
            String encodedImage = invoiceDataList.get(position).getByteCode().substring(invoiceDataList.get(position).getByteCode().indexOf(",") + 1);
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.imgGRN.setImageBitmap(bitmap);
            viewHolder.lytRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createImageFullscreenDialog(invoiceDataList.get(position), position);
                    onNewCameraClicked.onViewImageClicked(invoiceDataList.get(position));
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        InQueueForUploadDataModel item = invoiceDataList.get(position);
        if (item.isNewAdded()) {
            return ITEM_TYPE_ADD;
        } else {
            return ITEM_TYPE_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        return invoiceDataList.size();
    }

    public static class ViewHolderImage extends RecyclerView.ViewHolder {
        private ImageView imgGRN;
        private RelativeLayout lytRow;

        public ViewHolderImage(View itemView) {
            super(itemView);
            imgGRN = itemView.findViewById(R.id.grn_image);
            lytRow = itemView.findViewById(R.id.lyt_row);
        }
    }

    public class ViewHolderAdder extends RecyclerView.ViewHolder {
        private ImageView imgGRN;
        private RelativeLayout lytRow;

        public ViewHolderAdder(View v) {
            super(v);
            imgGRN = itemView.findViewById(R.id.grn_image);
            lytRow = itemView.findViewById(R.id.lyt_row);
        }


    }

    public interface onNewCameraClicked {
        void onNewCameraClicked();

        void onViewImageClicked(InQueueForUploadDataModel inQueueForUploadDataModel);
    }

    /**
     * return the rotated bitmap as per the rotation angle
     *
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * return the current rotation of and image
     *
     * @param exifOrientation
     * @return
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d("B4 sample size", inSampleSize + "*********");
        return inSampleSize;
    }

    private void createImageFullscreenDialog(final InQueueForUploadDataModel inQueueForUploadDataModel, final int position) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_full_screen);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageButton btnDelete = dialog.findViewById(R.id.img_btn_delete_invoice);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoiceDataList.remove(position);
                dialog.dismiss();
                notifyDataSetChanged();
            }
        });
        ImageView imageViewProduct = dialog.findViewById(R.id.image_product_product_details);
        String encodedImage = inQueueForUploadDataModel.getByteCode().substring(inQueueForUploadDataModel.getByteCode().indexOf(",") + 1);
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViewProduct.setImageBitmap(bitmap);
        dialog.show();
    }
}
