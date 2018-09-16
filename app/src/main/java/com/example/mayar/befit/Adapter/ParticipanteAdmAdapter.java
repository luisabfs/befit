package com.example.mayar.befit.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lylly on 15/08/2018.
 */

public class ParticipanteAdmAdapter extends RecyclerView.Adapter<ParticipanteAdmAdapter.ViewHolder>{
    //Firebase
    private ArrayList<String> mNomes;
    private ArrayList<String> mCidades;
    private ArrayList<String> mIdades;
    private ArrayList<String> mTelefones;
    private ArrayList<String> mFotosUrls;

    private Context mContext;
    private static ItemClickListener itemClickListener;
    private final OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ParticipanteAdmAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mCidades, ArrayList<String> mIdades, ArrayList<String> mTelefones, ArrayList<String> mFotosUrls, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mNomes = mNomes;
        this.mCidades = mCidades;
        this.mIdades = mIdades;
        this.mTelefones = mTelefones;
        this.mFotosUrls = mFotosUrls;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ParticipanteAdmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_participante_adm, parent, false);
        return new ParticipanteAdmAdapter.ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteAdmAdapter.ViewHolder holder, final int position) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView foto_participante_item;
        TextView nome_participante_item;
        TextView cidade_participante_item;
        TextView idade_participante_item;
        TextView telefone_participante_item;
        CustomImageView aprovar_participante_item;
        CustomImageView recusar_participante_item;
        private WeakReference<OnItemClickListener> listenerWeakReference;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            listenerWeakReference = new WeakReference<>(onItemClickListener);
            foto_participante_item = itemView.findViewById(R.id.id_foto_listar_parti_adm);
            nome_participante_item = itemView.findViewById(R.id.id_nome_listar_parti_adm);
            cidade_participante_item = itemView.findViewById(R.id.id_cidade_listar_parti_adm);
            idade_participante_item = itemView.findViewById(R.id.id_idade_listar_parti_adm);
            telefone_participante_item = itemView.findViewById(R.id.id_telefone_listar_parti_adm);
            aprovar_participante_item = itemView.findViewById(R.id.id_aprovar_parti);
            recusar_participante_item = itemView.findViewById(R.id.id_recusar_parti);

            aprovar_participante_item.setOnClickListener(this);
            recusar_participante_item.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == aprovar_participante_item.getId()){
                Log.i("participanteAdapter:", "onClick: aprovar");
            }else if(v.getId() == recusar_participante_item.getId()){
                Log.i("participanteAdapter:", "onClick: recusar");
            }else if (itemClickListener != null) {
                Log.i("participanteAdapter:", "onClick: item");
            }

            listenerWeakReference.get().onClick(getAdapterPosition(), (long) v.getId());
        }
    }
}
