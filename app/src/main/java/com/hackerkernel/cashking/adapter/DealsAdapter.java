package com.hackerkernel.cashking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.pojo.DealsListPojo;

import java.util.List;


public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.MyViewHolder> {
    private List<DealsListPojo> mList;
    private Context mContext;

    public DealsAdapter(Context context){
        this.mContext = context;

    }
    public void setDealList(List<DealsListPojo> list){
        this.mList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.deaa_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DealsListPojo pojo = mList.get(position);
        holder.dealName.setText(pojo.getDealName());
        holder.dealDesription.setText(pojo.getDealDescription());
        holder.dealOffer.setText(pojo.getDealOffer());
        Glide.with(mContext).load(pojo.getImageUrl()).into(holder.dealImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dealName, dealDesription, dealOffer;
        ImageView dealImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            dealName = (TextView) itemView.findViewById(R.id.deal_name);
            dealDesription = (TextView) itemView.findViewById(R.id.deal_desrciption);
            dealOffer = (TextView) itemView.findViewById(R.id.deal_offer);
            dealImage = (ImageView) itemView.findViewById(R.id.deal_image);
        }
    }
}
