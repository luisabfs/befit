package com.example.mayar.befit.Usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Adapter.DadosUsuarioAdapter;
import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsuarioActivity extends AppCompatActivity {
    /*
    ------------------------------------------ MÉTODOS ---------------------------------------------
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(UsuarioActivity.this, MainActivity.class));
    }

    //Tela
    private Toolbar toolbar;
    private ImageView foto;
    private TextView endereco;
    private TextView nome;
    private CustomImageView configuracoes;
    private CustomImageView editar_perfil;
    private ImageView botao_voltar;
    private ImageView marca;
    private ProgressDialog progressDialog;

    //Dados do Usuário
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mValores = new ArrayList<>();

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        progressDialog = new ProgressDialog(UsuarioActivity.this, R.style.AlertDialog);
        progressDialog.setTitle("Carregando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Usuário");
        setSupportActionBar(toolbar);

        firebaseUser = firebaseAuth.getCurrentUser();

        foto = findViewById(R.id.id_foto_usuario);
        endereco = findViewById(R.id.id_endereco_usuario);
        nome = findViewById(R.id.id_nome_usuario);
        configuracoes = findViewById(R.id.id_configuracoes_usuario);
        editar_perfil = findViewById(R.id.id_editar_perfil_usuario);
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
                startActivity(new Intent(UsuarioActivity.this, MainActivity.class));
            }
        });

        usuarioReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario usuario = new Usuario();

                usuario.setUrl_foto_usu(dataSnapshot.child("url_foto_usu").getValue().toString());
                usuario.setNome_usu(dataSnapshot.child("nome_usu").getValue().toString());
                usuario.setEmail_usu(dataSnapshot.child("email_usu").getValue().toString());
                usuario.setIdade_usu((dataSnapshot.child("idade_usu").getValue()).toString());
                usuario.setSexo_usu(dataSnapshot.child("sexo_usu").getValue().toString());
                usuario.setEstado_usu(dataSnapshot.child("estado_usu").getValue().toString());
                usuario.setCidade_usu(dataSnapshot.child("cidade_usu").getValue().toString());
                usuario.setCelular_usu(dataSnapshot.child("celular_usu").getValue().toString());
                usuario.setTipo_usu(dataSnapshot.child("tipo_usu").getValue().toString());

                mNomes.add(0, "E-mail");
                mValores.add(0, usuario.getEmail_usu());
                mNomes.add(1, "Idade");
                mValores.add(1, usuario.getIdade_usu());
                mNomes.add(2, "Sexo");
                mValores.add(2, usuario.getSexo_usu());
                mNomes.add(3, "Celular");
                mValores.add(3, usuario.getCelular_usu());

                String tipoUsuario = usuario.getTipo_usu();
                if (tipoUsuario.equals("0")){
                    mNomes.add(4, "Tipo de Usuário");
                    mValores.add(4, "Usuário Padrão");
                }else if(tipoUsuario.equals("1")){
                    mNomes.add(4, "Tipo de Usuário");
                    mValores.add(4, "Usuário Administrador");
                }

                Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(usuario.getUrl_foto_usu())
                            .into(foto);
                String endereco_usu = usuario.getCidade_usu()+", "+usuario.getEstado_usu();
                endereco.setText(endereco_usu);
                nome.setText(usuario.getNome_usu());

                initRecyclerView();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        configuracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsuarioActivity.this, ConfiguracoesActivity.class));
            }
        });

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsuarioActivity.this, AlterarUsuarioActivity.class));
            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_dados_usuario);
        recyclerView.setLayoutManager(layoutManager);
        final DadosUsuarioAdapter adapter = new DadosUsuarioAdapter(this, mNomes, mValores);
        recyclerView.setAdapter(adapter);
    }
}
