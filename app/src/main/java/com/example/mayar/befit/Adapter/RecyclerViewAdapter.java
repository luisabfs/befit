package com.example.mayar.befit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mayar.befit.R.color.colorAccent;

/**
 * Created by lylly on 16/05/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> mNomes;
    private ArrayList<String> mIconUrls;
    private Context mContext;
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public RecyclerViewAdapter( Context mContext, ArrayList<String> mNomes, ArrayList<String> mIconUrls) {
        this.mNomes = mNomes;
        this.mIconUrls = mIconUrls;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_modalidade_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mIconUrls.get(position))
                .into(holder.icon_modalidade_item);

        holder.nome_modalidade_item.setText(mNomes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNomes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView icon_modalidade_item;
        TextView nome_modalidade_item;
        RelativeLayout relativeLayout;
        int aux = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_modalidade_item = itemView.findViewById(R.id.id_icon_modalidade_item);
            nome_modalidade_item = itemView.findViewById(R.id.id_nome_modalidade_item);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            itemView.setOnClickListener(this);

        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {

            if(itemClickListener != null) {
                if(aux == 0) {
                    itemClickListener.onItemClick(getAdapterPosition());
                    itemView.setBackgroundColor(colorAccent);
                    aux = 1;
                }else{
                    itemView.setBackgroundColor(R.color.branca);
                    aux = 0;
                }
            }
        }
    }
}
