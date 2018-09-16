package com.example.mayar.befit.Atividade;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mayar.befit.Adapter.Commons;
import com.example.mayar.befit.Adapter.OnItemClickListener;
import com.example.mayar.befit.Adapter.ParticipanteAdapter;
import com.example.mayar.befit.Adapter.ParticipanteAdmAdapter;
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

public class ListarParticipantesAdmActivity extends AppCompatActivity implements Commons.OnRecyclerItemClickedListener {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AtividadeMainActivity.class);
        intent.putExtra("cod_atv", cod_atv);
        startActivity(intent);
    }

    //Tela
    private ImageView botao_voltar;
    private ImageView marca;

    // /Variáveis
    private ProgressDialog progressDialog;
    private String cod_atv;
    private int q_parti;

    //Participante
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mCidades = new ArrayList<>();
    private ArrayList<String> mIdades = new ArrayList<>();
    private ArrayList<String> mTelefones = new ArrayList<>();
    private ArrayList<String> mFotosUrls = new ArrayList<>();

    private ArrayList<String> mIds2 = new ArrayList<>();
    private ArrayList<String> mNomes2 = new ArrayList<>();
    private ArrayList<String> mCidades2 = new ArrayList<>();
    private ArrayList<String> mIdades2 = new ArrayList<>();
    private ArrayList<String> mTelefones2 = new ArrayList<>();
    private ArrayList<String> mFotosUrls2 = new ArrayList<>();

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");
    private FirebaseUser firebaseUser;

    private static final String TAG = "ParticipantesAdm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_participantes_adm);

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
                startActivity(new Intent(ListarParticipantesAdmActivity.this, MainActivity.class));
            }
        });

        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("atividade_parti");

        progressDialog = new ProgressDialog(ListarParticipantesAdmActivity.this, R.style.AlertDialog);
        progressDialog.setTitle("Carregando...");
        progressDialog.setCancelable(false);

        participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "participanteReference: dataSnapshot: " + dataSnapshot.getValue());
                    for(final DataSnapshot dados:dataSnapshot.getChildren()){
                        Log.i(TAG, "participanteReference: dados: " + dados.getValue());
                        usuarioReference.child(dados.getValue(Participante.class).getCod_usu_parti()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dados.getValue(Participante.class).getStatus_parti() == 1){
                                    mIds.add(dados.getValue(Participante.class).getCod_parti());
                                    mNomes.add(dataSnapshot.child("nome_usu").getValue().toString());
                                    mCidades.add(dataSnapshot.child("cidade_usu").getValue().toString()+", ");
                                    mIdades.add(dataSnapshot.child("idade_usu").getValue().toString()+" anos");
                                    mTelefones.add(dataSnapshot.child("celular_usu").getValue().toString());
                                    mFotosUrls.add(dataSnapshot.child("url_foto_usu").getValue().toString());
                                }

                                if(dados.getValue(Participante.class).getStatus_parti() == 0){
                                    mIds2.add(dados.getValue(Participante.class).getCod_parti());
                                    mNomes2.add(dataSnapshot.child("nome_usu").getValue().toString());
                                    mCidades2.add(dataSnapshot.child("cidade_usu").getValue().toString()+", ");
                                    mIdades2.add(dataSnapshot.child("idade_usu").getValue().toString()+" anos");
                                    mTelefones2.add(dataSnapshot.child("celular_usu").getValue().toString());
                                    mFotosUrls2.add(dataSnapshot.child("url_foto_usu").getValue().toString());
                                }

                                initRecyclerViewAdm();
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

    private void initRecyclerViewAdm(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_listar_participantes);
        recyclerView.setLayoutManager(layoutManager);
        final ParticipanteAdmAdapter adapter = new ParticipanteAdmAdapter(this, mNomes, mCidades, mIdades, mTelefones, mFotosUrls, new OnItemClickListener() {
        @Override
        public void onClick(final int position, Long view_id) {
            Log.i(TAG, "onClick: view_id " + view_id.toString() + ", aprovar id: " + R.id.id_aprovar_parti + ", " + R.id.id_recusar_parti);
            if(view_id == R.id.id_aprovar_parti){
                Log.i("participanteActivity:", "onClick: aprovar");
                participanteReference.child(mIds.get(position)).child("status_parti").setValue(0);
                atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        q_parti = Integer.parseInt(dataSnapshot.child("qparticipante_atv").getValue().toString())+1;
                        atividadeReference.child(cod_atv).child("qparticipante_atv").setValue(q_parti);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if(view_id == R.id.id_recusar_parti){
                Log.i("participanteAcitivity:", "onClick: recusar");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                recyclerView.removeViewAt(position);
                                participanteReference.child(mIds.get(position)).removeValue();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(ListarParticipantesAdmActivity.this, "Solicitação não recusada!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ListarParticipantesAdmActivity.this, R.style.AlertDialog);
                builder.setMessage("Recusar solicitação?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
                builder.setCancelable(false);
            }
        }

        });
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_listar_participantes_aprovados);
        recyclerView.setLayoutManager(layoutManager);
        final ParticipanteAdapter adapter = new ParticipanteAdapter(this, mNomes2, mCidades2, mIdades2, mTelefones2, mFotosUrls2, mIds2);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecyclerItemClicked(String parameter) {
        Log.i(TAG, "onRecyclerItemClicked: clicked");
    }
}
