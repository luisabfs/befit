package com.example.mayar.befit.Atividade;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mayar.befit.Adapter.ItemClickListener;
import com.example.mayar.befit.Adapter.RecyclerViewAdapter;
import com.example.mayar.befit.Classes.Atividade;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Classes.Modalidade;
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AlterarAtividadeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                local.setText(data.getStringExtra("local"));
                endereco.setText(data.getStringExtra("endereco"));
                latitude = data.getDoubleExtra("latitude", latitude);
                longitude = data.getDoubleExtra("longitude", longitude);
            }
        }
    }

    //Google Play services
    public static  final String TAG = "MainActivity";
    public static final int ERROR_DIALOG_REQUEST = 9001;

    //Tela
    private Toolbar toolbar;
    private AppCompatEditText titulo;
    private AppCompatEditText descricao;
    private AppCompatEditText data;
    private AppCompatEditText horario_inicio;
    private AppCompatEditText horario_fim;
    private AppCompatEditText local;
    private AppCompatEditText endereco;
    private TextInputLayout layout_titulo;
    private TextInputLayout layout_descricao;
    private TextInputLayout layout_data;
    private TextInputLayout layout_horario_inicio;
    private TextInputLayout layout_horario_fim;
    private TextInputLayout layout_local;
    private TextInputLayout layout_endereco;

    private CheckBox tipo_atividade;
    private Button botao_horario_inicio;
    private Button botao_horario_fim;
    private Button botao_data;
    private Button botao_mapa;
    private Button botao_alterar;
    private ImageView botao_voltar;
    private ImageView marca;

    //Variáveis
    private int dia, mes, ano, hora, minuto;
    private String selectedItem;
    private double latitude, longitude;
    private String cod_atv;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference modalidadeReference = databaseReference.child("modalidades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");

    private FirebaseUser firebaseUser;

    //Modalidade
    private ArrayList<String> mNomes = new ArrayList<>();
    private ArrayList<String> mIconUrls = new ArrayList<>();

    //Instâncias
    Atividade atividade = new Atividade();
    Participante participante = new Participante();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_atividade);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastrar Atividade");
        setSupportActionBar(toolbar);

        titulo = findViewById(R.id.id_titulo_atividade);
        layout_titulo = findViewById(R.id.id_til_titulo_atividade);
        descricao = findViewById(R.id.id_descricao_atividade);
        layout_descricao = findViewById(R.id.id_til_descricao_atividade);
        data = findViewById(R.id.id_data_atividade);
        layout_data = findViewById(R.id.id_til_data_atividade);
        botao_data = findViewById(R.id.id_botao_escolher_data);
        horario_inicio = findViewById(R.id.id_horario_inicio_atividade);
        layout_horario_inicio = findViewById(R.id.id_til_horario_inicio_atividade);
        botao_horario_inicio = findViewById(R.id.id_botao_escolher_horario_inicio);
        horario_fim = findViewById(R.id.id_horario_fim_atividade);
        layout_horario_fim = findViewById(R.id.id_til_horario_fim_atividade);
        botao_horario_fim = findViewById(R.id.id_botao_escolher_horario_fim);
        local = findViewById(R.id.id_local_atividade);
        layout_local = findViewById(R.id.id_til_local_atividade);
        tipo_atividade = findViewById(R.id.cb_tipo_atividade);
        endereco = findViewById(R.id.id_endereco_atividade);
        layout_endereco = findViewById(R.id.id_til_endereco_atividade);

        botao_alterar = findViewById(R.id.id_botao_alterar_atividade);
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Toast.makeText(AlterarAtividadeActivity.this, "Cadastro cancelado.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AlterarAtividadeActivity.this, MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AlterarAtividadeActivity.this, R.style.AlertDialog);
                builder.setMessage("Cancelar cadastro?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });

        //DATA
        final Calendar c = Calendar.getInstance();

        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);

        mes = mes+1;

        data.setText(dia+"/"+ mes +"/"+ano);

        botao_data.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AlterarAtividadeActivity.this,  R.style.AlertDialog, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        mes = selectedMonth+1;
                        data.setText(selectedDay + "/" + mes + "/" + selectedYear);
                    }
                },ano, mes, dia);

                datePickerDialog.show();
            }
        });

        hora = c.get(Calendar.HOUR_OF_DAY);
        minuto = c.get(Calendar.MINUTE);

        horario_inicio.setText(hora+":"+minuto);
        horario_fim.setText((hora+1)+":"+minuto);

        botao_horario_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AlterarAtividadeActivity.this,  R.style.AlertDialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horario_inicio.setText(hourOfDay+":"+minute);
                    }
                }, hora, minuto, DateFormat.is24HourFormat(AlterarAtividadeActivity.this));

                timePickerDialog.show();
            }

        });

        botao_horario_fim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AlterarAtividadeActivity.this,  R.style.AlertDialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horario_fim.setText(hourOfDay+":"+minute);
                    }
                }, hora+1, minuto, DateFormat.is24HourFormat(AlterarAtividadeActivity.this));

                timePickerDialog.show();
            }
        });

        if(isServicesAvailable()){
            botao_mapa = findViewById(R.id.id_botao_mapa);

            botao_mapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AlterarAtividadeActivity.this, MapActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        modalidadeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    final Modalidade modalidade = dados.getValue(Modalidade.class);
                    mNomes.add(modalidade.getNome_mod());
                    mIconUrls.add(modalidade.getUrl_icone_mod());
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("cod_atv");
        Log.i(TAG, "Bundle getExtras(): " + cod_atv);

        //recuperando os dados de atividade
        atividadeReference.child(cod_atv).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.setText(dataSnapshot.getValue(Atividade.class).getTitulo_atv());
                descricao.setText(dataSnapshot.getValue(Atividade.class).getDescricao_atv());
                data.setText(dataSnapshot.getValue(Atividade.class).getData_atv());
                horario_inicio.setText(dataSnapshot.getValue(Atividade.class).getHorario_inicio_atv());
                horario_fim.setText(dataSnapshot.getValue(Atividade.class).getHorario_fim_atv());
                local.setText(dataSnapshot.getValue(Atividade.class).getLocal_atv());
                endereco.setText(dataSnapshot.getValue(Atividade.class).getEndereco_atv());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ALTERANDO
        botao_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalidadeReference.orderByChild("nome_mod").equalTo(selectedItem)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dados:dataSnapshot.getChildren()){
                                    final Modalidade modalidade = dados.getValue(Modalidade.class);

                                    Log.i(TAG, "selectedItem: " + selectedItem);
                                    atividade.setCod_atv(cod_atv);
                                    atividade.setTitulo_atv(titulo.getText().toString());
                                    atividade.setDescricao_atv(descricao.getText().toString());
                                    atividade.setData_atv(data.getText().toString());
                                    atividade.setHorario_inicio_atv(horario_inicio.getText().toString());
                                    atividade.setHorario_fim_atv(horario_fim.getText().toString());
                                    atividade.setLocal_atv(local.getText().toString());
                                    atividade.setEndereco_atv(endereco.getText().toString());
                                    atividade.setLatitude_atv(latitude);
                                    atividade.setLongitude_atv(longitude);
                                    atividade.setQparticipante_atv(1);
                                    atividade.setStatus_atv(0); //0 - ativa; 1 - realizada;
                                    if(tipo_atividade.isChecked()){ //0 - pública; 1 - privada;
                                        atividade.setTipo_atv(1);
                                    }else {
                                        atividade.setTipo_atv(0);
                                    }
                                    atividade.setUrl_modalidade_atv(modalidade.getUrl_icone_mod());

                                    atividadeReference.child(String.valueOf(atividade.getCod_atv())).setValue(atividade);
                                    Toast.makeText(AlterarAtividadeActivity.this, "Alterada!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(AlterarAtividadeActivity.this, AtividadeMainActivity.class);
                                    intent.putExtra("cod_atv", atividade.getCod_atv());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "databaseError: " + databaseError.getMessage());
                            }
                        });
            }
        });
    }

    private void initRecyclerView(){
        Log.i(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNomes, mIconUrls);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                selectedItem = mNomes.get(position);
                Log.i(TAG, "selectedItem" + selectedItem);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(AlterarAtividadeActivity.this, "Alteração cancelada.", Toast.LENGTH_SHORT).show();
                        AlterarAtividadeActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AlterarAtividadeActivity.this, R.style.AlertDialog);
        builder.setMessage("Cancelar alteração?").setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }

    public boolean isServicesAvailable(){
        Log.d(TAG, "isServicesAvailable: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AlterarAtividadeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //tudo ok
            Log.i(TAG, "isServicesAvailable: tudo ok");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.i(TAG, "isServicesAvailable: erro, mas dá pra resolver");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AlterarAtividadeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(AlterarAtividadeActivity.this, "Não é possível realizar requisições no mapa.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
