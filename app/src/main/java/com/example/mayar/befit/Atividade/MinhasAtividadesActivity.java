package com.example.mayar.befit.Atividade;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.mayar.befit.Adapter.TabAdapter2;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.example.mayar.befit.Usuario.SlidingTabLayout;

import com.example.mayar.befit.Fragments.AtividadesFuturasFragment;
import com.example.mayar.befit.Fragments.AtividadesPassadasFragment;
import com.example.mayar.befit.Fragments.AtividadesSolicitadasFragment;

public class MinhasAtividadesActivity extends AppCompatActivity implements
        AtividadesFuturasFragment.OnFragmentInteractionListener,
        AtividadesPassadasFragment.OnFragmentInteractionListener,
        AtividadesSolicitadasFragment.OnFragmentInteractionListener{
    /*
    --------------------------------------- MÉTODOS --------------------------------------------
     */
    @Override
    public void onBackPressed() {

        startActivity(new Intent(MinhasAtividadesActivity.this, MainActivity.class));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*
    -------------------------------------------------------------------------------------------
     */

    //Tela
    private Toolbar toolbar;
    private ImageView botao_voltar;
    private ImageView marca;

    //TabLayout
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    //Variáveis
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_atividades);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Minhas Atividades");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_pagina);
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
                startActivity(new Intent(MinhasAtividadesActivity.this, MainActivity.class));
            }
        });

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        TabAdapter2 tabAdapter = new TabAdapter2(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(0);

        slidingTabLayout.setViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("TARGET_FRAGMENT_ID");

            viewPager.setCurrentItem(position);
        }
    }
}
