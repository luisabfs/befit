package com.example.mayar.befit.Usuario;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificaEmailActivity extends AppCompatActivity {
    //Tela
    private Button botao_reenviarEmail;
    private ImageView iconeVoltar;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_email);

        botao_reenviarEmail = findViewById(R.id.id_botao_reenviarEmail);
        iconeVoltar = findViewById(R.id.id_back);

        botao_reenviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();

                final FirebaseUser user = firebaseAuth.getCurrentUser();
                user.sendEmailVerification()
                        .addOnCompleteListener(VerificaEmailActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(VerificaEmailActivity.this,
                                            "Link de verificação enviado para " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("emailEnviado", "Falha no envio.", task.getException());
                                    Toast.makeText(VerificaEmailActivity.this,
                                            "Falha no envio. Tente novamente em alguns minutos.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        iconeVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(firebaseUser.isEmailVerified()==false){
                   startActivity(new Intent(VerificaEmailActivity.this, LoginActivity.class));
               }else {
                   startActivity(new Intent(VerificaEmailActivity.this, MainActivity.class));
               }
            }
        });

    }

    @Override
    public void onBackPressed(){
        if(firebaseUser.isEmailVerified()==false){
            startActivity(new Intent(VerificaEmailActivity.this, LoginActivity.class));
        }else {
            startActivity(new Intent(VerificaEmailActivity.this, MainActivity.class));
        }
    }
}
