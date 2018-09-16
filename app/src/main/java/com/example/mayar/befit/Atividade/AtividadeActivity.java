package com.example.mayar.befit.Atividade;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;

public class AtividadeActivity extends AppCompatActivity {
    /*
    -------------------------------------------- MÉTODOS -------------------------------------------
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AtividadeActivity.this, MainActivity.class));
    }

    //Tela
    private CustomImageView addAtividade;
    private CustomImageView buscarAtividade;
    private ImageView botao_voltar;
    private ImageView marca;

    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);

        addAtividade = findViewById(R.id.id_add_atividade);
        buscarAtividade = findViewById(R.id.id_buscar_atividade);
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
                startActivity(new Intent(AtividadeActivity.this, MainActivity.class));
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Atividade");
        setSupportActionBar(toolbar);

        addAtividade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(AtividadeActivity.this, CadastrarAtividadeActivity.class));
            }
        });

        buscarAtividade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(AtividadeActivity.this, BuscarAtividadeActivity.class));
            }
        });
    }
}
