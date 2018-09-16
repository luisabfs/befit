package com.example.mayar.befit.Usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HelpActivity extends AppCompatActivity {
    private AppCompatEditText email;
    private TextInputLayout layout_email;
    private Button botao_reenviarEmail;
    private Button botao_recuperarSenha;
    private ImageView iconeHelp;
    private ImageView iconeVoltar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        email = findViewById(R.id.id_email_help);
        layout_email = findViewById(R.id.idtextinputlayoutemail);
        botao_reenviarEmail = findViewById(R.id.id_botao_reenviarEmail);
        botao_recuperarSenha = findViewById(R.id.id_botao_recuperarSenha);
        iconeHelp = findViewById(R.id.id_help);
        iconeVoltar = findViewById(R.id.id_back);

        botao_reenviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampo() == false) {

                    Usuario usuario = new Usuario();
                    usuario.setEmail_usu(email.getText().toString());

                    if(isGooglePlayServicesAvailable(HelpActivity.this) == true) {
                        firebaseAuth = FirebaseAuth.getInstance();

                        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        try {
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(HelpActivity.this, new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(HelpActivity.this,
                                                        "Link de verificação enviado para " + firebaseUser.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                                startActivity(new Intent(HelpActivity.this, VerificaEmailActivity.class));
                                            } else {
                                                Log.i("emailEnviado", "Falha no envio.", task.getException());
                                                Toast.makeText(HelpActivity.this,
                                                        "Falha no envio. Tente novamente em alguns minutos.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }catch (Exception e){
                            Toast.makeText(HelpActivity.this,
                                    "Falha no envio. Tente novamente em alguns minutos.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(HelpActivity.this,
                                "Os serviços do Google Play não estão disponíveis.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        botao_recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampo() == false) {
                    final Usuario usuario = new Usuario();
                    usuario.setEmail_usu(email.getText().toString());

                    if (isGooglePlayServicesAvailable(HelpActivity.this) == true) {
                        firebaseAuth = FirebaseAuth.getInstance();

                        firebaseAuth.sendPasswordResetEmail(usuario.getEmail_usu().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.i("emailEnviado", "true");
                                            Toast.makeText(HelpActivity.this,
                                                    "Link de redefinição de senha enviado para " + usuario.getEmail_usu(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HelpActivity.this,
                                                    "Falha no envio. Tente novamente em alguns minutos.",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.i("emailEnviado", "false", task.getException());
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(HelpActivity.this,
                                "Os serviços do Google Play não estão disponíveis.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        iconeHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iconeVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    private boolean validarCampo(){
        layout_email.setErrorEnabled(false);

        int emailSize = email.getText().toString().length();

        if (emailSize == 0) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("Preencha com seu email.");
            return true;
        }

        return false;
    }
}
