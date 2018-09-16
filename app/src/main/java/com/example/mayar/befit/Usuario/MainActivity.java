package com.example.mayar.befit.Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.mayar.befit.Atividade.AtividadeActivity;
import com.example.mayar.befit.Atividade.AtividadeMainActivity;
import com.example.mayar.befit.Atividade.MinhasAtividadesActivity;
import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.Modalidade.ListarModalidadeActivity;
import com.example.mayar.befit.Modalidade.ModalidadeAdmActivity;
import com.example.mayar.befit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView botaoUsuario;
    private ImageView botaoAtividade;
    private ImageView botaoModalidade;
    private ImageView botaoMinhasAtividades;

    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ConfiguracaoFirebase.isUserSignedIn(firebaseUser) == false || firebaseUser.isEmailVerified()==false){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        botaoUsuario = findViewById(R.id.id_usuario);
        botaoAtividade = findViewById(R.id.id_atividade);
        botaoModalidade = findViewById(R.id.id_modalidade);
        botaoMinhasAtividades = findViewById(R.id.id_minhas_atividades);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseUser = ConfiguracaoFirebase.getFirebaseAuth().getCurrentUser();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        botaoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsuarioActivity.class));
            }
        });

        botaoAtividade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AtividadeActivity.class));
            }
        });

        botaoModalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Usuario usuario = new Usuario();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                databaseReference = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(firebaseUser.getUid());
                Log.i("databaseReference", databaseReference.toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tipo = dataSnapshot.child("tipo_usu").getValue().toString();
                        usuario.setTipo_usu(tipo);

                        Log.i("tipoUsuario", String.valueOf(usuario.getTipo_usu()));
                        if(usuario.getTipo_usu().equals("0")) { //usuário padrão;
                            startActivity(new Intent(MainActivity.this, ListarModalidadeActivity.class));
                        }else if(usuario.getTipo_usu().equals("1")){ //usuário adm;
                            startActivity(new Intent(MainActivity.this, ModalidadeAdmActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        botaoMinhasAtividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MinhasAtividadesActivity.class));
            }
        });
    }

    //Ação de voltar nativa;
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //Menu (três pontinhos) -> checa qual ação foi selecionada;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement -> recomendo mudar pra switch/case
        if (id == R.id.menu_sair) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Menu (drawer) -> checa qual ação foi selecionada;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_usuario:{
                startActivity(new Intent(MainActivity.this, UsuarioActivity.class));
                break;
            }
            case R.id.nav_atividade:{
                Intent intent = new Intent(MainActivity.this, AtividadeMainActivity.class);
                intent.putExtra("atividade2", "Yoga Fit");
                startActivity(intent);
                break;
            }
            case R.id.nav_minhas_atividades:{
                //startActivity(new Intent(MainActivity.this, PerfilAtividade2Activity.class));
                break;
            }
            case R.id.nav_modalidade:{
                startActivity(new Intent(MainActivity.this, ModalidadeAdmActivity.class));
                break;
            }
            case R.id.nav_logout:{
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
