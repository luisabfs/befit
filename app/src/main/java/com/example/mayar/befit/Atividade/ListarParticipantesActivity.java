package com.example.mayar.befit.Atividade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mayar.befit.Adapter.ParticipantePadraoAdapter;
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListarParticipantesActivity extends AppCompatActivity {
    //Tela
    private ImageView botao_voltar;
    private ImageView marca;

    // /Variáveis
    private String cod_atv;

    //Participante
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mCidades = new ArrayList<>();
    private ArrayList<String> mIdades = new ArrayList<>();
    private ArrayList<String> mTelefones = new ArrayList<>();
    private ArrayList<String> mFotosUrls = new ArrayList<>();

    //Firebase
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");

    private static final String TAG = "ListarParticipantes";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_participantes);

        botao_voltar = findViewById(R.id.id_voltar);
        marca = findViewById(R.id.marca_toolbar);

        botao_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        marca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListarParticipantesActivity.this, MainActivity.class));
            }
        });

        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("atividade_parti");

        participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "participanteReference: dataSnapshot: " + dataSnapshot.getValue());
                for(final DataSnapshot dados:dataSnapshot.getChildren()){
                    Log.i(TAG, "participanteReference: dados: " + dados.getValue());
                    usuarioReference.child(dados.getValue(Participante.class).getCod_usu_parti()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dados.getValue(Participante.class).getStatus_parti() == 0){
                                mIds.add(dados.getValue(Participante.class).getCod_parti());
                                mNomes.add(dataSnapshot.child("nome_usu").getValue().toString());
                                mCidades.add(dataSnapshot.child("cidade_usu").getValue().toString()+", ");
                                mIdades.add(dataSnapshot.child("idade_usu").getValue().toString()+" anos");
                                mTelefones.add(dataSnapshot.child("celular_usu").getValue().toString());
                                mFotosUrls.add(dataSnapshot.child("url_foto_usu").getValue().toString());
                            }

                            initRecyclerView();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_participantes);
        recyclerView.setLayoutManager(layoutManager);
        final ParticipantePadraoAdapter adapter = new ParticipantePadraoAdapter(this, mNomes, mCidades, mIdades, mTelefones, mFotosUrls);

        recyclerView.setAdapter(adapter);
    }
}
