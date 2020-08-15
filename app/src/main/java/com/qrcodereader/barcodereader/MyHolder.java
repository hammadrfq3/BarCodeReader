package com.qrcodereader.barcodereader;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView indexTextView,contentTextView,formatTextView;
    ItemClickListener itemClickListener;

     MyHolder(@NonNull View itemView) {
        super(itemView);

        this.contentTextView=itemView.findViewById(R.id.contentTextView);
        this.indexTextView=itemView.findViewById(R.id.indexTextView);
        this.formatTextView=itemView.findViewById(R.id.formatTextView);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClickListener(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic){
         this.itemClickListener=ic;
    }
}
