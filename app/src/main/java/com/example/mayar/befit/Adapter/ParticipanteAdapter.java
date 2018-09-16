package com.example.mayar.befit.Adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lylly on 15/08/2018.
 */

public class ParticipanteAdapter extends RecyclerView.Adapter<ParticipanteAdapter.ViewHolder>{
    private ArrayList<String> mIds;
    private ArrayList<String> mNomes;
    private ArrayList<String> mCidades;
    private ArrayList<String> mIdades;
    private ArrayList<String> mTelefones;
    private ArrayList<String> mFotosUrls;

    //Firebase
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private DatabaseReference atividadeReference = databaseReference.child("atividades");

    private int q_parti;

    private Context mContext;
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ParticipanteAdapter(Context mContext, ArrayList<String> mNomes, ArrayList<String> mCidades, ArrayList<String> mIdades, ArrayList<String> mTelefones, ArrayList<String> mFotosUrls, ArrayList<String> mIds) {
        this.mContext = mContext;
        this.mNomes = mNomes;
        this.mCidades = mCidades;
        this.mIdades = mIdades;
        this.mTelefones = mTelefones;
        this.mFotosUrls = mFotosUrls;
        this.mIds = mIds;

    }

    @NonNull
    @Override
    public ParticipanteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_participante, parent, false);

        return new ParticipanteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParticipanteAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mFotosUrls.get(position))
                .into(holder.foto_participante_item);

        holder.nome_participante_item.setText(mNomes.get(position));
        holder.cidade_participante_item.setText(mCidades.get(position));
        holder.idade_participante_item.setText(mIdades.get(position));
        holder.telefone_participante_item.setText(mTelefones.get(position));

        holder.menu_participante_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu(holder.menu_participante_item, holder.nome_participante_item.getText().toString(), mIds.get(position));
            }
        });

    }

    public void showPopUpMenu(View view, String titulo, String id){
        //creating a popup menu
        PopupMenu popup = new PopupMenu(mContext, view, R.style.AlertDialog);
        //inflating menu from xml resource
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_participante_item, popup.getMenu());
        //adding click listener
        popup.setOnMenuItemClickListener(new ParticipanteAdapter.MyHomeMenuItemClickListener(titulo, id));
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
                case R.id.item_remover_participante:
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    participanteReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            atividadeReference.child(dataSnapshot.getValue(Participante.class).getCod_atv_parti())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    q_parti = Integer.parseInt(dataSnapshot.child("qparticipante_atv").getValue().toString())-1;
                                                    atividadeReference.child(dataSnapshot.getValue(Participante.class).getCod_atv_parti()).child("qparticipante_atv").setValue(q_parti);
                                                    participanteReference.child(id).removeValue();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(mContext, "Solicitação não cancelada!", Toast.LENGTH_SHORT);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage("Remover participante?").setPositiveButton("Sim", dialogClickListener)
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
        CircleImageView foto_participante_item;
        TextView nome_participante_item;
        TextView cidade_participante_item;
        TextView idade_participante_item;
        TextView telefone_participante_item;
        TextView menu_participante_item;

        public ViewHolder(View itemView) {
            super(itemView);
            foto_participante_item = itemView.findViewById(R.id.id_foto_listar_parti_adm);
            nome_participante_item = itemView.findViewById(R.id.id_nome_listar_parti_adm);
            cidade_participante_item = itemView.findViewById(R.id.id_cidade_listar_parti_adm);
            idade_participante_item = itemView.findViewById(R.id.id_idade_listar_parti_adm);
            telefone_participante_item = itemView.findViewById(R.id.id_telefone_listar_parti_adm);
            menu_participante_item = itemView.findViewById(R.id.id_menu_listar_participante_adm);

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
