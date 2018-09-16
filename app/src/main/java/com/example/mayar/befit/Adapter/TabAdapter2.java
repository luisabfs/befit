package com.example.mayar.befit.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mayar.befit.Fragments.AtividadesFuturasFragment;
import com.example.mayar.befit.Fragments.AtividadesPassadasFragment;
import com.example.mayar.befit.Fragments.AtividadesSolicitadasFragment;

/**
 * Created by lylly on 03/05/2018.
 */

public class TabAdapter2 extends FragmentPagerAdapter {
    private String[] tituloAbas = {"FUTURAS", "SOLICITADAS", "PASSADAS"};

    public TabAdapter2(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0: fragment = new AtividadesFuturasFragment();
                break;

            case 1: fragment = new AtividadesSolicitadasFragment();
                break;

            case 2: fragment = new AtividadesPassadasFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tituloAbas[position];
    }
}
