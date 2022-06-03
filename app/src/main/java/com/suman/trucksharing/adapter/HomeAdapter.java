package com.suman.trucksharing.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suman.trucksharing.DB.Data;
import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.UserDBHandler;
import com.suman.trucksharing.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{
    Context context;
    ArrayList<ItemModel> list;
    onItemClickListener onItemClickListener;
    Bitmap decodedByte;
    public HomeAdapter(Context context, ArrayList<ItemModel> list) {
        this.list = list;
        this.context = context;
        byte[] decodedString = Base64.decode(Data.image, Base64.DEFAULT);
        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    public interface onItemClickListener {
        void onitemClick(int position, String name, String date, String time, String weight, String type, String width, String height, String length);
    }

    public void setOnItemClickListener(HomeAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel itemModel = list.get(position);
        holder.sampleImage.setImageBitmap(decodedByte);
        holder.sampleTitle.setText(itemModel.getName());
        holder.sampleDesc.setText("Order at "+itemModel.getDate()+itemModel.getTime()+" Goods Type"+itemModel.getGoodstype());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION){
                    onItemClickListener.onitemClick(holder.getAdapterPosition(), itemModel.getName(), itemModel.getDate(), itemModel.getTime(), itemModel.getWeight(), itemModel.getGoodstype(), itemModel.getWidth(), itemModel.getHeight(), itemModel.getLength());
                }
            }
        });
        holder.shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, itemModel.getName()+ "\n"+itemModel.getDate()+itemModel.getTime()+"\nGoods Type "+itemModel.getGoodstype());
                view.getContext().startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView sampleImage, shareImg;
        TextView sampleTitle, sampleDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sampleImage = itemView.findViewById(R.id.sample_image);
            sampleTitle = itemView.findViewById(R.id.sample_title);
            sampleDesc = itemView.findViewById(R.id.sample_desc);
            shareImg = itemView.findViewById(R.id.sample_share);
        }
    }
}
