package com.example.mayar.befit.Usuario;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesActivity extends AppCompatActivity {
    //Tela
    private Button excluir_conta;
    private AlertDialog alertDialog;
    private ImageView botao_voltar;
    private ImageView marca;

    //Database
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        excluir_conta = findViewById(R.id.id_botao_excluir_conta);
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
                startActivity(new Intent(ConfiguracoesActivity.this, MainActivity.class));
            }
        });

        excluir_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ConfiguracoesActivity.this);
                final View view = layoutInflater.inflate(R.layout.activity_excluir_usuario, null);

                final EditText senha = view.findViewById(R.id.id_senha_alert_dialog);

                AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracoesActivity.this, R.style.AlertDialog);
                builder.setTitle("Excluir conta.");
                builder.setMessage("Informe sua senha para continuar.");
                builder.setView(view);

                builder
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseUser = firebaseAuth.getCurrentUser();

                                usuarioReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dados: dataSnapshot.getChildren()){
                                            final Usuario usuario = dados.getValue(Usuario.class);

                                            if(firebaseUser.getEmail().toString().equals(usuario.getEmail_usu())) {
                                                if (usuario.getSenha_usu().equals(senha.getText().toString())) {
                                                    //Excluindo o usuário no Auth;
                                                    firebaseUser.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.i("usuarioExcluido", "true");

                                                                        //Excluindo o usuário no banco de dados;
                                                                        databaseReference.child("usuarios").child(usuario.getCod_usu()).removeValue();

                                                                        startActivity(new Intent(ConfiguracoesActivity.this, LoginActivity.class));
                                                                        finish();
                                                                    }else{
                                                                        Log.i("usuarioExcluido", "false", task.getException());
                                                                        Toast.makeText(ConfiguracoesActivity.this, "Não foi possível excluir a conta.", Toast.LENGTH_LONG).show();
                                                                        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail().toString(), usuario.getSenha_usu());
                                                                        firebaseUser.reauthenticate(credential);
                                                                    }
                                                                }
                                                            });
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Senha incorreta.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Operação cancelada.", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
