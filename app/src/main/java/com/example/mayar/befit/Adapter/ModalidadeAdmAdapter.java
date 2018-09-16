package com.example.mayar.befit.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Modalidade.AlterarModalidadeActivity;
import com.example.mayar.befit.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lylly on 22/05/2018.
 */

public class ModalidadeAdmAdapter extends RecyclerView.Adapter<com.example.mayar.befit.Adapter.ModalidadeAdmAdapter.ViewHolder> {
    private ArrayList<String> mIds;
    private ArrayList<String> mIconUrls;
    private ArrayList<String> mNomes;
    private ArrayList<String> mDescricoes;


    private Context mContext;
    private static ItemClickListener itemClickListener;

    //Firebase
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference modalidadeReference = databaseReference.child("modalidades");

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ModalidadeAdmAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mDescricoes, ArrayList<String> mIconUrls, ArrayList<String> mIds){
        this.mContext = mContext;
        this.mNomes = mNomes;
        this.mDescricoes = mDescricoes;
        this.mIconUrls = mIconUrls;
        this.mIds = mIds;
    }

    @NonNull
    @Override
    public com.example.mayar.befit.Adapter.ModalidadeAdmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_modalidade_item_adm, parent, false);

        return new com.example.mayar.befit.Adapter.ModalidadeAdmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ModalidadeAdmAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mIconUrls.get(position))
                .into(holder.icon_modalidade);

        holder.nome_modalidade.setText(mNomes.get(position));
        holder.descricao_modalidade.setText(mDescricoes.get(position));

        holder.menu_modalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showPopUpMenu(holder.menu_modalidade, holder.nome_modalidade.getText().toString(), mIds.get(position));
            }
        });
    }

    public void showPopUpMenu(View view, String titulo, String id){
        //creating a popup menu
        PopupMenu popup = new PopupMenu(mContext, view, R.style.AlertDialog);
        //inflating menu from xml resource
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_modalidade_item, popup.getMenu());
        //adding click listener
        popup.setOnMenuItemClickListener(new MyHomeMenuItemClickListener(titulo, id));
        //displaying the popup
        popup.show();

    }

    class MyHomeMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        private String titulo;
        private String id;

        public MyHomeMenuItemClickListener(String titulo, String id){
            this.setTitulo(titulo);
            this.setId(id);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item_alterar_mod:
                    Intent alterarIntent = new Intent(mContext, AlterarModalidadeActivity.class);
                    alterarIntent.putExtra("modalidade", this.getTitulo());
                    mContext.startActivity(alterarIntent);
                    Toast.makeText(mContext, "Alterar", Toast.LENGTH_SHORT);
                case R.id.item_excluir_mod:
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    modalidadeReference.child(id).removeValue();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(mContext, "Solicitação não cancelada!", Toast.LENGTH_SHORT);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage("Excluir modalidade?").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();
                    builder.setCancelable(false);
                    break;
            }
            return false;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getId(){
            return id;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public void setId(String id){
            this.id = id;
        }
    }

    @Override
    public int getItemCount() {
        return mNomes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView icon_modalidade;
        TextView nome_modalidade;
        TextView descricao_modalidade;
        TextView menu_modalidade;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_modalidade = itemView.findViewById(R.id.id_icon_listar_atividade_adm);
            nome_modalidade = itemView.findViewById(R.id.id_nome_listar_modalidade_adm);
            descricao_modalidade = itemView.findViewById(R.id.id_descricao_listar_atividade_adm);
            menu_modalidade = itemView.findViewById(R.id.id_menu_listar_modalidade_adm);

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

