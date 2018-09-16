package com.example.mayar.befit.Atividade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayar.befit.Adapter.ItemClickListener;
import com.example.mayar.befit.Adapter.RecyclerViewAdapter2;
import com.example.mayar.befit.Classes.Atividade;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuscarAtividadeActivity extends AppCompatActivity {
    //Tela
    private Toolbar toolbar;
    private TextView mSearchText;
    private ImageView ic_clear;
    private ImageView botao_voltar;
    private ImageView marca;

    //aux
    private String selectedItem;
    private ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private FirebaseUser firebaseUser;

    //Atividade
    private ArrayList<String> mCods = new ArrayList<>();
    private ArrayList<String> mTitulos = new ArrayList<>();
    private ArrayList<String> mIconUrls = new ArrayList<>();
    private ArrayList<String> mEnderecos = new ArrayList<>();
    private ArrayList<String> mDatas = new ArrayList<>();
    private ArrayList<String> mHorarios = new ArrayList<>();

    Atividade atividade = new Atividade();

    private static final String TAG = "BuscarAtividadeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_atividade);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Buscar Atividade");
        setSupportActionBar(toolbar);

        mSearchText = findViewById(R.id.input_search);
        ic_clear = findViewById(R.id.ic_clear);
        botao_voltar = findViewById(R.id.id_voltar);
        marca = findViewById(R.id.marca_toolbar);

        ic_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        botao_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        marca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuscarAtividadeActivity.this, MainActivity.class));
            }
        });

        progressDialog = new ProgressDialog(BuscarAtividadeActivity.this, R.style.AlertDialog);
        progressDialog.setTitle("Carregando...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        atividadeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    final Atividade atividade = dados.getValue(Atividade.class);

                    if(atividade.getStatus_atv() == 0){
                        mCods.add(atividade.getCod_atv());
                        mTitulos.add(atividade.getTitulo_atv());
                        mEnderecos.add(atividade.getEndereco_atv());
                        mDatas.add(atividade.getData_atv());
                        mHorarios.add(atividade.getHorario_inicio_atv());
                        mIconUrls.add(atividade.getUrl_modalidade_atv());
                    }
                }
                initRecyclerView();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView(){
        Log.i(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView_buscar_atividade);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerViewAdapter2 adapter = new RecyclerViewAdapter2(this, mTitulos, mEnderecos, mDatas, mHorarios, mIconUrls);
        //onClickListener
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
            selectedItem = mCods.get(position);
            firebaseUser = firebaseAuth.getCurrentUser();

            callAtividade(selectedItem);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void callAtividade(final String atividadeClicada){
        participanteReference.orderByChild("cod_atv_parti").equalTo(atividadeClicada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                    for(DataSnapshot dados:dataSnapshot.getChildren()){
                        if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                            int status = dados.getValue(Participante.class).getStatus_parti();

                            if(status == 0){ //0-atv pública
                                Log.i(TAG,"onClickListener: participante: aprovado");
                                Intent intent = new Intent(BuscarAtividadeActivity.this, AtividadeMainActivity.class);
                                intent.putExtra("cod_atv", atividadeClicada);
                                startActivity(intent);
                            }else{
                                Log.i(TAG,"onClickListener: participante: aguardando");
                                Intent intent = new Intent(BuscarAtividadeActivity.this, PerfilAtividadeActivity.class);
                                intent.putExtra("cod_atv", atividadeClicada);
                                intent.putExtra("botao", "true");
                                startActivity(intent);
                            }
                        }else{
                            Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                            Intent intent = new Intent(BuscarAtividadeActivity.this, PerfilAtividadeActivity.class);
                            intent.putExtra("cod_atv", atividadeClicada);
                            startActivity(intent);
                        }
                    }
                }else{
                    Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                    Intent intent = new Intent(BuscarAtividadeActivity.this, PerfilAtividadeActivity.class);
                    intent.putExtra("cod_atv", atividadeClicada);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BuscarAtividadeActivity.this, AtividadeActivity.class));
    }
}
