package com.example.mayar.befit.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mayar.befit.R;

import java.util.ArrayList;

/**
 * Created by lylly on 22/05/2018.
 */

public class DadosUsuarioAdapter extends RecyclerView.Adapter<com.example.mayar.befit.Adapter.DadosUsuarioAdapter.ViewHolder> {
        private ArrayList<String> mNomes;
        private ArrayList<String> mValores;


        private Context mContext;
        private static ItemClickListener itemClickListener;

        public void setOnItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public DadosUsuarioAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mValores){
            this.mContext = mContext;
            this.mNomes = mNomes;
            this.mValores = mValores;

        }

        @NonNull
        @Override
        public com.example.mayar.befit.Adapter.DadosUsuarioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dado_usuario, parent, false);

            return new com.example.mayar.befit.Adapter.DadosUsuarioAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.mayar.befit.Adapter.DadosUsuarioAdapter.ViewHolder holder, final int position) {
            holder.nome_atributo.setText(mNomes.get(position));
            holder.valor_atributo.setText(mValores.get(position));
        }

        @Override
        public int getItemCount() {
            return mNomes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView nome_atributo;
            TextView valor_atributo;

            public ViewHolder(View itemView) {
                super(itemView);
                nome_atributo = itemView.findViewById(R.id.id_nome_atributo);
                valor_atributo = itemView.findViewById(R.id.id_valor_atributo);

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
