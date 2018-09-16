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

public class AtividadePassadaAdapter extends RecyclerView.Adapter<com.example.mayar.befit.Adapter.AtividadePassadaAdapter.ViewHolder> {
    private ArrayList<String> mTitulos = new ArrayList<>();
    private ArrayList<String> mEnderecos = new ArrayList<>();
    private ArrayList<String> mDatas = new ArrayList<>();
    private ArrayList<String> mHorarios = new ArrayList<>();
    private ArrayList<Float> mAvaliacoes = new ArrayList<>();
    private ArrayList<String> mIconUrls = new ArrayList<>();

    private Context mContext;
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AtividadePassadaAdapter(Context mContext, ArrayList<String> mTitulos, ArrayList<String> mEnderecos, ArrayList<String> mDatas, ArrayList<String> mHorarios, ArrayList<Float> mAvaliacoes, ArrayList<String> mIconUrls) {
        this.mContext = mContext;
        this.mTitulos = mTitulos;
        this.mEnderecos = mEnderecos;
        this.mDatas = mDatas;
        this.mHorarios = mHorarios;
        this.mAvaliacoes = mAvaliacoes;
        this.mIconUrls = mIconUrls;

    }

    @NonNull
    @Override
    public com.example.mayar.befit.Adapter.AtividadePassadaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_atividade_passada_item, parent, false);

        return new com.example.mayar.befit.Adapter.AtividadePassadaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.mayar.befit.Adapter.AtividadePassadaAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mIconUrls.get(position))
                .into(holder.icon_atividade_item);

        holder.titulo_atividade_item.setText(mTitulos.get(position));
        holder.endereco_atividade_item.setText(mEnderecos.get(position));
        holder.data_atividade_item.setText(mDatas.get(position));
        holder.horario_atividade_item.setText(mHorarios.get(position));
        holder.avaliacao_atividade_item.setText(mAvaliacoes.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return mTitulos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView icon_atividade_item;
        TextView titulo_atividade_item;
        TextView endereco_atividade_item;
        TextView data_atividade_item;
        TextView horario_atividade_item;
        TextView avaliacao_atividade_item;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_atividade_item = itemView.findViewById(R.id.id_icon_atividade_item);
            titulo_atividade_item = itemView.findViewById(R.id.id_titulo_atividade_item);
            endereco_atividade_item = itemView.findViewById(R.id.id_endereco_atividade_item);
            data_atividade_item = itemView.findViewById(R.id.id_data_atividade_item);
            horario_atividade_item = itemView.findViewById(R.id.id_horario_atividade_item);
            avaliacao_atividade_item = itemView.findViewById(R.id.tv_avaliacao_atividade);

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