package com.example.mayar.befit.Modalidade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mayar.befit.Adapter.ItemClickListener;
import com.example.mayar.befit.Adapter.ModalidadeArrayAdapter;
import com.example.mayar.befit.Classes.Modalidade;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListarModalidadeActivity extends AppCompatActivity {
    /*
    ------------------------------------------ MÉTODOS ---------------------------------------------
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Tela
    private Toolbar toolbar;
    private ImageView botao_voltar;
    private ImageView marca;

    //Variáveis
    String selectedItem;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference modalidadeReference = databaseReference.child("modalidades");
    private FirebaseUser firebaseUser;

    //Modalidade
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mIconUrls = new ArrayList<>();
    private ArrayList<String> mDescricoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_modalidade);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Buscar Atividade");
        setSupportActionBar(toolbar);
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
                startActivity(new Intent(ListarModalidadeActivity.this, MainActivity.class));
            }
        });

        firebaseUser = firebaseAuth.getCurrentUser();

        //Recuperando os dados da modalidade e adicionando aos ArrayLists
        modalidadeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    final Modalidade modalidade = dados.getValue(Modalidade.class);
                    mIconUrls.add(modalidade.getUrl_icone_mod());
                    mNomes.add(modalidade.getNome_mod());
                    mDescricoes.add(modalidade.getDescricao_mod());
                }
                initRecyclerView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_listar_modalidade);
        recyclerView.setLayoutManager(layoutManager);
        final ModalidadeArrayAdapter adapter = new ModalidadeArrayAdapter(this, mNomes, mDescricoes, mIconUrls);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                selectedItem = mNomes.get(position);
                Log.i("selectedItem", selectedItem);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
