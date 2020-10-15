package com.android.subaili.chujing.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.utils.UtilData;

import java.util.ArrayList;

public class VideoToPhotoAdapter extends RecyclerView.Adapter<VideoToPhotoAdapter.ViewHolder> {
    private ArrayList<Bitmap> mData;

    public VideoToPhotoAdapter(ArrayList<Bitmap> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videotophoto, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        int parentWidth = parent.getWidth();
        parent.getHeight();
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.width =  (parentWidth/ UtilData.CUTVIDEO_DEFAULT_LENGTH);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImageView.setImageBitmap(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_image);
        }
    }
}