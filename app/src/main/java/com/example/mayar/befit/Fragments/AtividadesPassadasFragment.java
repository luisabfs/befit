package com.example.mayar.befit.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayar.befit.Adapter.AtividadePassadaAdapter;
import com.example.mayar.befit.Adapter.ItemClickListener;
import com.example.mayar.befit.Atividade.AtividadeMainActivity;
import com.example.mayar.befit.Atividade.PerfilAtividadeActivity;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AtividadesPassadasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AtividadesPassadasFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private AtividadePassadaAdapter atividadeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //Atividade
    private ArrayList<String> mCods;
    private ArrayList<String> mTitulos;
    private ArrayList<String> mIconUrls;
    private ArrayList<String> mEnderecos;
    private ArrayList<String> mDatas;
    private ArrayList<String> mHorarios;
    private ArrayList<Float> mAvaliacoes;

    private String selectedItem;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private static final String TAG = "AtividadesPassadas";
    public AtividadesPassadasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_atividades_passadas, container, false);

        populateRecyclerView();
        recyclerView = rootView.findViewById(R.id.rv_atividades_passadas);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        atividadeAdapter = new AtividadePassadaAdapter(getActivity(),mTitulos, mEnderecos, mDatas, mHorarios, mAvaliacoes, mIconUrls);
        atividadeAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                selectedItem = mCods.get(position);
                Log.i(TAG, selectedItem);
                callAtividade(selectedItem);
            }
        });
        recyclerView.setAdapter(atividadeAdapter);

        return rootView;
    }

    private void populateRecyclerView(){
        mCods = new ArrayList<>();
        mTitulos = new ArrayList<>();
        mIconUrls = new ArrayList<>();
        mEnderecos = new ArrayList<>();
        mDatas = new ArrayList<>();
        mHorarios = new ArrayList<>();
        mAvaliacoes = new ArrayList<>();

        participanteReference.orderByChild("cod_usu_parti").equalTo(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot dados:dataSnapshot.getChildren()){
                        if(dados.getValue(Participante.class).getStatus_parti() == 0) {
                            atividadeReference.child(dados.getValue(Participante.class).getCod_atv_parti())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(Integer.parseInt(dataSnapshot.child("status_atv").getValue().toString()) == 1) {
                                        mCods.add(dataSnapshot.child("cod_atv").getValue().toString());
                                        mTitulos.add(dataSnapshot.child("titulo_atv").getValue().toString());
                                        mEnderecos.add(dataSnapshot.child("endereco_atv").getValue().toString());
                                        mDatas.add(dataSnapshot.child("data_atv").getValue().toString());
                                        mHorarios.add(dataSnapshot.child("horario_inicio_atv").getValue().toString());
                                        mAvaliacoes.add(Float.parseFloat(dataSnapshot.child("avaliacao_atv").getValue().toString()));
                                        mIconUrls.add(dataSnapshot.child("url_modalidade_atv").getValue().toString());

                                        atividadeAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void callAtividade(final String atividadeClicada){
        participanteReference.orderByChild("cod_atv_parti").equalTo(atividadeClicada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                    for(DataSnapshot dados:dataSnapshot.getChildren()){
                        if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                            int status = dados.getValue(Participante.class).getStatus_parti();

                            if(status == 0){ //0-atv pública
                                Log.i(TAG,"onClickListener: participante: aprovado");
                                Intent intent = new Intent(getContext(), AtividadeMainActivity.class);
                                intent.putExtra("cod_atv", atividadeClicada);
                                startActivity(intent);
                            }else{
                                Log.i(TAG,"onClickListener: participante: aguardando");
                                Intent intent = new Intent(getContext(), PerfilAtividadeActivity.class);
                                intent.putExtra("cod_atv", atividadeClicada);
                                intent.putExtra("botao", "true");
                                startActivity(intent);
                            }
                        }else{
                            Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                            Intent intent = new Intent(getContext(), PerfilAtividadeActivity.class);
                            intent.putExtra("cod_atv", atividadeClicada);
                            startActivity(intent);
                        }
                    }
                }else{
                    Log.i(TAG, "callAtividade: dataSnapshot: " + dataSnapshot.getValue());
                    Intent intent = new Intent(getContext(), PerfilAtividadeActivity.class);
                    intent.putExtra("cod_atv", atividadeClicada);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
