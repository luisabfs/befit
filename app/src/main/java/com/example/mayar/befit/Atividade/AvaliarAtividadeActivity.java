package com.example.mayar.befit.Atividade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mayar.befit.Classes.Participante;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AvaliarAtividadeActivity extends AppCompatActivity {
    //Tela
    private RatingBar ratingBar;
    private TextView ratingScale;
    private Button botao_avaliar;

    //Variáveis
    private String cod_atv;
    private float soma = 0;
    private int cont = 0;

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference atividadeReference = databaseReference.child("atividades");
    private DatabaseReference participanteReference = databaseReference.child("participantes");

    private static final String TAG = "AvaliarAtividade";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliar_atividade);

        ratingBar = findViewById(R.id.ratingBar);
        ratingScale = findViewById(R.id.tvRatingScale);
        botao_avaliar = findViewById(R.id.id_botao_avaliar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        ratingScale.setText("Péssima!");
                        break;
                    case 2:
                        ratingScale.setText("Regular");
                        break;
                    case 3:
                        ratingScale.setText("Boa");
                        break;
                    case 4:
                        ratingScale.setText("Ótima!");
                        break;
                    case 5:
                        ratingScale.setText("Perfeita!");
                        break;
                    default:
                        ratingScale.setText("");
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        cod_atv = extras.getString("cod_atv");
        Log.i(TAG, "Bundle getExtras(): " + cod_atv);

        firebaseUser = firebaseAuth.getCurrentUser();

        botao_avaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participanteReference.orderByChild("cod_atv_parti").equalTo(cod_atv)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dados:dataSnapshot.getChildren()){
                                    if(dados.getValue(Participante.class).getCod_usu_parti().equals(firebaseUser.getUid())){
                                        participanteReference.child(dados.getValue(Participante.class).getCod_parti()).child("nota_parti").setValue(ratingBar.getRating());
                                    }

                                    if(dados.getValue(Participante.class).getNota_parti()!= 0){
                                        cont++;
                                        soma += dados.getValue(Participante.class).getNota_parti();
                                    }
                                }

                                atividadeReference.child(cod_atv).child("avaliacao_atv").setValue(soma/cont);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }
}
