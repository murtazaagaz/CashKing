package com.hackerkernel.cashking.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.activity.DetailOfferActivity;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.pojo.DealsListPojo;

import java.util.List;


public class DealsListAdapter extends RecyclerView.Adapter<DealsListAdapter.MyViewHolder> {
    private List<DealsListPojo> mList;
    private Context mContext;
    private String offerType;

    public DealsListAdapter(Context context,String offerType){
        this.mContext = context;

    }
    public void setDealList(List<DealsListPojo> list){
        this.mList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.deal_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DealsListPojo pojo = mList.get(position);
        holder.dealName.setText(pojo.getDealName());
        holder.dealDesription.setText(pojo.getDealDescription());
        holder.dealOffer.setText("GET "+mContext.getString(R.string.rupee_sign)+pojo.getDealAmount());

        //download image
        if (pojo.getImageUrl() != null){
            String imageUrl = EndPoints.IMAGE_BASE_URL+pojo.getImageUrl();
            Log.d("HUS","HUS: "+imageUrl);
            Glide.with(mContext)
                    .load(imageUrl)
                    .thumbnail(0.5f)
                    .into(holder.dealImage);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dealName, dealDesription, dealOffer;
        ImageView dealImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            dealName = (TextView) itemView.findViewById(R.id.deal_name);
            dealDesription = (TextView) itemView.findViewById(R.id.deal_desrciption);
            dealOffer = (TextView) itemView.findViewById(R.id.deal_offer);
            dealImage = (ImageView) itemView.findViewById(R.id.deal_image);

            //set on click on offer
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //set offer id
            int pos = getAdapterPosition();
            String id = mList.get(pos).getId();

            Intent i = new Intent(mContext, DetailOfferActivity.class);
            i.putExtra(Constants.COM_ID,id);
            mContext.startActivity(i);
        }
    }
}
