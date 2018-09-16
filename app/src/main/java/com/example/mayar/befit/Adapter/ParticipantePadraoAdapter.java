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
 * Created by lylly on 15/08/2018.
 */

public class ParticipantePadraoAdapter extends RecyclerView.Adapter<ParticipantePadraoAdapter.ViewHolder>{
    private ArrayList<String> mNomes;
    private ArrayList<String> mCidades;
    private ArrayList<String> mIdades;
    private ArrayList<String> mTelefones;
    private ArrayList<String> mFotosUrls;

    private Context mContext;
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ParticipantePadraoAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mCidades, ArrayList<String> mIdades, ArrayList<String> mTelefones, ArrayList<String> mFotosUrls) {
        this.mContext = mContext;
        this.mNomes = mNomes;
        this.mCidades = mCidades;
        this.mIdades = mIdades;
        this.mTelefones = mTelefones;
        this.mFotosUrls = mFotosUrls;

    }

    @NonNull
    @Override
    public ParticipantePadraoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_participante_padrao, parent, false);

        return new ParticipantePadraoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantePadraoAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mFotosUrls.get(position))
                .into(holder.foto_participante_item);

        holder.nome_participante_item.setText(mNomes.get(position));
        holder.cidade_participante_item.setText(mCidades.get(position));
        holder.idade_participante_item.setText(mIdades.get(position));
        holder.telefone_participante_item.setText(mTelefones.get(position));

    }

    @Override
    public int getItemCount() {
        return mNomes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView foto_participante_item;
        TextView nome_participante_item;
        TextView cidade_participante_item;
        TextView idade_participante_item;
        TextView telefone_participante_item;

        public ViewHolder(View itemView) {
            super(itemView);
            foto_participante_item = itemView.findViewById(R.id.id_foto_listar_parti_adm);
            nome_participante_item = itemView.findViewById(R.id.id_nome_listar_parti_adm);
            cidade_participante_item = itemView.findViewById(R.id.id_cidade_listar_parti_adm);
            idade_participante_item = itemView.findViewById(R.id.id_idade_listar_parti_adm);
            telefone_participante_item = itemView.findViewById(R.id.id_telefone_listar_parti_adm);

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
