package com.example.mayar.befit.Atividade;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Adapter.CustomInfoWindowAdapter;
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

import java.util.UUID;

public class PerfilAtividadeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    /*
    ------------------------------------------ MÉTODOS ---------------------------------------------
     */
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

    /*
    ------------------------------------------------------------------------------------------------
     */

    //Tela
    private CustomImageView foto;
    private AppCompatEditText descricao;
    private TextView titulo;
    private ToggleButton botao;
    private ImageView botao_voltar;
    private ImageView marca;

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");

    //Globals
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "PerfilAtividadeActivity";

    //Variáveis
    private Boolean mLocationPermissionsGranted = true;
    private GoogleMap mMap;
    private Marker mMarker;

    private String cod_atv;
    private int q_parti;
    private String bt;

    //Instâncias
    Participante participante = new Participante();
    Atividade atividade = new Atividade();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_atividade);

        foto = findViewById(R.id.id_icone_modalidade_perfil);
        descricao = findViewById(R.id.id_descricao_atividade);
        titulo = findViewById(R.id.id_titulo_atividade_perfil1);
        botao = findViewById(R.id.botao_solicitar_part);
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
                startActivity(new Intent(PerfilAtividadeActivity.this, MainActivity.class));
            }
        });

        initMap();

        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("cod_atv");
        bt = extras.getString("botao");

        if(bt != null){
            botao.setChecked(true);
        }
        Log.i(TAG, "Bundle getExtras(): " + cod_atv + ", " + bt);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botao.isChecked()) {
                    Log.i(TAG, "onCheckerChanged: isChecked");

                    firebaseUser = firebaseAuth.getCurrentUser();
                    participante.setCod_parti(UUID.randomUUID().toString());
                    participante.setCod_usu_parti(firebaseUser.getUid());
                    participante.setId_parti(0);
                    participante.setTipo_parti(0); //0-padrão; 1-gerenciador;
                    participante.setNota_parti(0);

                    participanteReference.child(participante.getCod_parti()).setValue(participante);

                    if (participante.getStatus_parti() == 0) {
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


                        Intent intent= new Intent(PerfilAtividadeActivity.this, AtividadeMainActivity.class);
                        intent.putExtra("cod_atv", cod_atv);
                        startActivity(intent);
                    }
                }else{
                    botao.setChecked(true);
                    Log.i(TAG, "onCheckerChanged: notChecked");
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                   participanteReference.orderByChild("cod_usu_parti").equalTo(firebaseUser.getUid())
                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.i(TAG, "Query: " + dataSnapshot.getValue());
                                            for(DataSnapshot dados:dataSnapshot.getChildren()){
                                                String atv = dados.getValue(Participante.class).getCod_atv_parti();
                                                Log.i(TAG, "Query: atv: " + atv);

                                                if(atv != null){
                                                    if(atv.equals(cod_atv)){
                                                        participanteReference.child(participante.getCod_parti()).removeValue();
                                                        Toast.makeText(PerfilAtividadeActivity.this, "Solicitação cancelada", Toast.LENGTH_SHORT);
                                                        botao.setChecked(false);
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(PerfilAtividadeActivity.this, "Solicitação não cancelada!", Toast.LENGTH_SHORT);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(PerfilAtividadeActivity.this, R.style.AlertDialog);
                    builder.setMessage("Cancelar solicitação?").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();
                    builder.setCancelable(false);
                }
            }
        });

    }

    private void moveCamera(LatLng latLng, float zoom, Atividade atividade){
        Log.d(TAG, "moveCamera: moving the camera to: lat = " + latLng.latitude + ", lng = " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(PerfilAtividadeActivity.this));

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_perfil);

        mapFragment.getMapAsync(PerfilAtividadeActivity.this);
    }

    /*
    ---------------------------------------FIREBASE------------------------------------
     */

    private void initAtividade(){
        try{
            atividade = new Atividade();
            atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        participante.setCod_atv_parti(dataSnapshot.child("cod_atv").getValue().toString());
                        participante.setStatus_parti(Integer.parseInt(dataSnapshot.child("tipo_atv").getValue().toString())); //0 - aprovado; 1 - aguardando aprovação;
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
}
