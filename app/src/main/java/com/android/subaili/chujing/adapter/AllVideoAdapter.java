package com.android.subaili.chujing.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.activity.VideoToPhotoActivity;
import com.android.subaili.chujing.model.AllVideoModel;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AllVideoAdapter extends RecyclerView.Adapter<AllVideoAdapter.ViewHolder> {
    private ArrayList<AllVideoModel> mData;
    private Context mContext;

    public AllVideoAdapter(Context context, ArrayList<AllVideoModel> data) {
        this.mData = data;
        this.mContext = context;
    }

    public AllVideoAdapter(Context context, ArrayList<AllVideoModel> data, String path) {
        ArrayList<AllVideoModel> tempData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            AllVideoModel allvideomodel = new AllVideoModel();
            allvideomodel.path = data.get(i).path;
            File file = new File(allvideomodel.path).getParentFile();
            if (file.getAbsolutePath().equals(path)) {
                allvideomodel.name = data.get(i).name;
                allvideomodel.resolution = data.get(i).resolution;
                allvideomodel.size = data.get(i).size;
                allvideomodel.duration = data.get(i).duration;
                allvideomodel.modified = data.get(i).modified;
                tempData.add(allvideomodel);
            }
        }
        this.mData = tempData;
        this.mContext = context;
    }

    public void updateData(ArrayList<AllVideoModel> data, String path) {
        ArrayList<AllVideoModel> tempData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            AllVideoModel allvideomodel = new AllVideoModel();
            allvideomodel.path = data.get(i).path;
            File file = new File(allvideomodel.path).getParentFile();
            if (file.getAbsolutePath().equals(path)) {
                allvideomodel.name = data.get(i).name;
                allvideomodel.resolution = data.get(i).resolution;
                allvideomodel.size = data.get(i).size;
                allvideomodel.duration = data.get(i).duration;
                allvideomodel.modified = data.get(i).modified;
                tempData.add(allvideomodel);
            }
        }
        this.mData = tempData;
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<AllVideoModel> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        int parentWidth = parent.getWidth();
        parent.getHeight();
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.width =  (parentWidth/ UtilData.VIDEO_ITEM_COUNT);
        layoutParams.height = layoutParams.width;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mData.get(position).path)
                .placeholder(R.mipmap.ic_placeholder)
                .centerCrop()
                .dontAnimate()
                .into(holder.mImageView);
        final long lngtime = mData.get(position).duration;
        final String strPath = mData.get(position).path;
        String strTime = Tools.generateTime(lngtime);
        holder.mTextView.setText(strTime);
        holder.mImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(mContext, VideoToPhotoActivity.class);
            intent.putExtra("path", strPath);
            intent.putExtra("time", lngtime);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_image);
            mTextView = itemView.findViewById(R.id.tv_time);
        }
    }
}