package com.example.mayar.befit.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lylly on 22/05/2018.
 */

public class ModalidadeArrayAdapter extends RecyclerView.Adapter<com.example.mayar.befit.Adapter.ModalidadeArrayAdapter.ViewHolder> {
    private ArrayList<String> mIconUrls;
    private ArrayList<String> mNomes;
    private ArrayList<String> mDescricoes;


    private Context mContext;
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ModalidadeArrayAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mDescricoes, ArrayList<String> mIconUrls){
        this.mContext = mContext;
        this.mNomes = mNomes;
        this.mDescricoes = mDescricoes;
        this.mIconUrls = mIconUrls;

    }

    @NonNull
    @Override
    public com.example.mayar.befit.Adapter.ModalidadeArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_modalidade_item, parent, false);

        return new com.example.mayar.befit.Adapter.ModalidadeArrayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ModalidadeArrayAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mIconUrls.get(position))
                .into(holder.icon_modalidade);

        holder.nome_modalidade.setText(mNomes.get(position));
        holder.descricao_modalidade.setText(mDescricoes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNomes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView icon_modalidade;
        TextView nome_modalidade;
        TextView descricao_modalidade;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_modalidade = itemView.findViewById(R.id.id_icon_listar_atividade);
            nome_modalidade = itemView.findViewById(R.id.id_nome_listar_modalidade);
            descricao_modalidade = itemView.findViewById(R.id.id_descricao_listar_atividade);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}

