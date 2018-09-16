package com.example.mayar.befit.Atividade;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Adapter.CustomInfoWindowAdapter;
import com.example.mayar.befit.Adapter.ParticipanteItemAdapter;
import com.example.mayar.befit.Classes.Atividade;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AtividadeMainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: mapa tá pronto");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            initAtividade();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BuscarAtividadeActivity.class));
    }

    //Tela
    private CustomImageView foto;
    private CustomImageView configuracoes;
    private CustomImageView chat;
    private CustomImageView participantes;
    private CustomImageView convidar;
    private AppCompatEditText descricao;
    private TextView titulo;
    private TextView ver_todos;
    private TextView q_participantes;
    private Toolbar toolbar;
    private ImageView botao_voltar;
    private ImageView marca;

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");

    //Instâncias
    Atividade atividade = new Atividade();

    //Globals
    private static final float DEFAULT_ZOOM = 15f;

    //Variáveis
    private Boolean mLocationPermissionsGranted = true;
    private GoogleMap mMap;
    private Marker mMarker;
    private String cod_atv;
    private int q_parti;

    //Participantes
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mFotosUrl = new ArrayList<>();

    private static final String TAG = "AtividadeMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade_main);

        toolbar = findViewById(R.id.toolbar);
        foto = findViewById(R.id.id_icone_modalidade_atividade);
        chat = findViewById(R.id.id_chat);
        descricao = findViewById(R.id.id_descricao_atividade);
        titulo = findViewById(R.id.id_titulo_atividade);
        ver_todos = findViewById(R.id.textView4);
        botao_voltar = findViewById(R.id.id_voltar);
        marca = findViewById(R.id.marca_toolbar);
        q_participantes = findViewById(R.id.tv_qparticipante);

        botao_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        marca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AtividadeMainActivity.this, MainActivity.class));
            }
        });

        ver_todos.setPaintFlags(ver_todos.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMap();
        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("cod_atv");
        Log.i(TAG, "Bundle getExtras(): " + cod_atv);

        atividadeReference.child(cod_atv).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                q_participantes.setText(dataSnapshot.child("qparticipante_atv").getValue().toString() + " participante(s)");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot dados: dataSnapshot.getChildren()){
                    usuarioReference.child(dados.getValue(Participante.class).getCod_usu_parti()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dados.getValue(Participante.class).getStatus_parti() == 0){
                                mNomes.add(dataSnapshot.child("nome_usu").getValue().toString());
                                mFotosUrl.add(dataSnapshot.child("url_foto_usu").getValue().toString());
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

        ver_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dados:dataSnapshot.getChildren()){
                                    if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                                        if(dados.getValue(Participante.class).getTipo_parti() == 1){
                                            Intent intent = new Intent(AtividadeMainActivity.this, ListarParticipantesAdmActivity.class);
                                            intent.putExtra("atividade_parti", cod_atv);
                                            startActivity(intent);
                                        }else{
                                           Intent intent = new Intent(AtividadeMainActivity.this, ListarParticipantesActivity.class);
                                            intent.putExtra("atividade_parti", cod_atv);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AtividadeMainActivity.this, ChatActivity.class));
            }
        });
    }

    private void initRecyclerView(){
        Log.i(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView recyclerView = findViewById(R.id.rv_participantes);
        recyclerView.setLayoutManager(layoutManager);
        final ParticipanteItemAdapter adapter = new ParticipanteItemAdapter(this, mNomes, mFotosUrl);
        recyclerView.setAdapter(adapter);
    }

    private void moveCamera(LatLng latLng, float zoom, Atividade atividade){
        Log.d(TAG, "moveCamera: moving the camera to: lat = " + latLng.latitude + ", lng = " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(AtividadeMainActivity.this));

        if(atividade != null){
            try{
                String snippet = "Endereço: " + atividade.getEndereco_atv() + "\n" +
                        "Data: " + atividade.getData_atv() + " - " +
                        "Horário: " + atividade.getHorario_inicio_atv() +
                        " até " + atividade.getHorario_fim_atv();

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(atividade.getLocal_atv())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);
                mMarker.showInfoWindow();
            }catch (NullPointerException e){
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }

        }else{
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    private void initMap(){
        Log.i(TAG, "initMap: inicializando mapa");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_main);

        mapFragment.getMapAsync(AtividadeMainActivity.this);
    }

    /*
    ---------------------------------------FIREBASE------------------------------------
     */

    private void initAtividade(){
        try{
            atividade = new Atividade();
            firebaseUser = firebaseAuth.getCurrentUser();

            Bundle extras = getIntent().getExtras();
            cod_atv = extras.getString("cod_atv");
            Log.i(TAG, "Bundle getExtras(): " + cod_atv);

            atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        titulo.setText(dataSnapshot.getValue(Atividade.class).getTitulo_atv());
                        descricao.setText(dataSnapshot.getValue(Atividade.class).getDescricao_atv());
                        atividade.setLocal_atv(dataSnapshot.getValue(Atividade.class).getLocal_atv());
                        atividade.setEndereco_atv(dataSnapshot.getValue(Atividade.class).getEndereco_atv());
                        atividade.setHorario_inicio_atv(dataSnapshot.getValue(Atividade.class).getHorario_inicio_atv());
                        atividade.setHorario_fim_atv(dataSnapshot.getValue(Atividade.class).getHorario_fim_atv());
                        atividade.setData_atv(dataSnapshot.getValue(Atividade.class).getData_atv());
                        atividade.setLatitude_atv(dataSnapshot.getValue(Atividade.class).getLatitude_atv());
                        atividade.setLongitude_atv(dataSnapshot.getValue(Atividade.class).getLongitude_atv());

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(dataSnapshot.getValue(Atividade.class).getUrl_modalidade_atv())
                                .into(foto);

                        moveCamera(new LatLng(atividade.getLatitude_atv(), atividade.getLongitude_atv()), DEFAULT_ZOOM, atividade);
                        Log.d(TAG, "initAtividade: atividade: " + dataSnapshot.getValue());
                    }else{
                        Log.d(TAG, "initAtividade: atividade: null");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.d(TAG, "onResult: NullPointerException " +e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        firebaseUser = firebaseAuth.getCurrentUser();
        // Inflate the menu; this adds items to the action bar if it is present.
        participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Log.i(TAG, dataSnapshot.getValue().toString());
                    if(dataSnapshot != null){
                        if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                            if(dados.getValue(Participante.class).getTipo_parti() == 1){
                                getMenuInflater().inflate(R.menu.atividade_main, menu);
                            }else{
                                getMenuInflater().inflate(R.menu.atividade, menu);
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return true;
    }

    //Menu (três pontinhos) -> checa qual ação foi selecionada;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_alterar_atividade) {
            Intent intent = new Intent(AtividadeMainActivity.this, AlterarAtividadeActivity.class);
            intent.putExtra("cod_atv", cod_atv);
            startActivity(intent);
            return true;
        }else if(id == R.id.menu_finalizar_atividade){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    atividadeReference.child(cod_atv).child("status_atv").setValue(1);
                                    Intent intent = new Intent(AtividadeMainActivity.this, AvaliarAtividadeActivity.class);
                                    intent.putExtra("cod_atv", cod_atv);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AtividadeMainActivity.this, R.style.AlertDialog);
            builder.setMessage("Finalizar atividade?").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
            builder.setCancelable(false);
            return true;
        }else if(id == R.id.menu_excluir_atividade){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot dados:dataSnapshot.getChildren()){
                                                participanteReference.child(dados.getValue(Participante.class).getCod_parti()).removeValue();
                                                atividadeReference.child(cod_atv).removeValue();
                                                startActivity(new Intent(AtividadeMainActivity.this, BuscarAtividadeActivity.class));
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AtividadeMainActivity.this, R.style.AlertDialog);
            builder.setMessage("Excluir atividade?").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
            builder.setCancelable(false);
            return true;
        }else if(id == R.id.menu_abandonar_atividade){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot dados:dataSnapshot.getChildren()){
                                                if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                                                    participanteReference.child(dados.getValue(Participante.class).getCod_parti()).removeValue();
                                                    atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            q_parti = Integer.parseInt(dataSnapshot.child("qparticipante_atv").getValue().toString())-1;
                                                            atividadeReference.child(cod_atv).child("qparticipante_atv").setValue(q_parti);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                    Intent intent = new Intent(AtividadeMainActivity.this, PerfilAtividadeActivity.class);
                                                    intent.putExtra("cod_atv", cod_atv);
                                                    startActivity(intent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AtividadeMainActivity.this, R.style.AlertDialog);
            builder.setMessage("Abandonar atividade?").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
            builder.setCancelable(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
