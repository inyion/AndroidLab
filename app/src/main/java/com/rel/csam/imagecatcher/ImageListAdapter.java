package com.rel.csam.imagecatcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by leechansaem on 2016. 11. 2..
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ArrayList<String> mImageList;
    private Context mContext;

    public ImageListAdapter(Context context,ArrayList<String> android) {
        this.mImageList = android;
        this.mContext = context;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.mImageList = imageList;
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageListAdapter.ViewHolder viewHolder, int i) {

//        viewHolder.title.setText(mImageList.get(i));
        Glide.with(mContext).load(mImageList.get(i)).thumbnail(0.1f).fitCenter().into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        if(mImageList != null) {
            return mImageList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private CustomImageView img;
        public ViewHolder(View view) {
            super(view);

//            title = (TextView)view.findViewById(R.id.title);
            img = (CustomImageView) view.findViewById(R.id.img);
        }
    }
}
