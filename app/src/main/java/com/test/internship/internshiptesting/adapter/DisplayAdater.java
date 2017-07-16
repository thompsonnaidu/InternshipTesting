package com.test.internship.internshiptesting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.test.internship.internshiptesting.R;
import com.test.internship.internshiptesting.model.DataUsage;

import java.util.List;

/**
 * Created by Thompson on 16-07-2017.
 */

public class DisplayAdater extends RecyclerView.Adapter<DisplayAdater.MyViewHolder> {

    List<DataUsage> data;
    Context ctx;
    public DisplayAdater(List<DataUsage> data, Context ctx) {

        this.data=data;
        this.ctx=ctx;
    }

    @Override
    public DisplayAdater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DisplayAdater.MyViewHolder holder, int position) {

        DataUsage temp=data.get(position);
        holder.appdownload.setText(temp.getDownload()+" MB");
        holder.appupload.setText(temp.getUpload()+" MB");
        holder.apppack.setText(temp.getName());
        holder.appicon.setImageDrawable(temp.getIcon());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView appicon;
        TextView apppack,appupload,appdownload;

        public MyViewHolder(View itemView) {
            super(itemView);
            apppack= (TextView) itemView.findViewById(R.id.appname);
            appupload= (TextView) itemView.findViewById(R.id.appupload);
            appdownload= (TextView) itemView.findViewById(R.id.appdownload);
            appicon= (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}
