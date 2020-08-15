package com.qrcodereader.barcodereader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context context;
    ArrayList<Model> models;

    public MyAdapter(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        try{
            holder.contentTextView.setText(models.get(position).getContent());
            holder.formatTextView.setText(models.get(position).getHeading());
            holder.indexTextView.setText(models.get(position).getIndex());
        }
        catch (Exception ex){
            ex.getCause();
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Intent intent=new Intent(context,WebViewActivity.class);
                String content=models.get(position).getContent();
                String searchableText;
                if(content.contains("http") || content.contains(".com")){
                    searchableText=content;
                }else {
                    searchableText=MainActivity.url+content;
                }
                intent.putExtra("URL",searchableText);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
