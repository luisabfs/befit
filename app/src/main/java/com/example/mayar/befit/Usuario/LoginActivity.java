package com.example.mayar.befit.Usuario;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText email;
    private AppCompatEditText senha;
    private TextInputLayout layout_email;
    private TextInputLayout layout_senha;
    private TextView recuperaSenha;
    private Button botao_login;
    private Button botao_cadastro;
    private ImageView help;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AlertDialog);
        progressDialog.setTitle("Logando...");
        progressDialog.setCancelable(false);

        if(ConfiguracaoFirebase.isUserSignedIn(firebaseUser) == true && firebaseUser.isEmailVerified()==true){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }else {
            email = findViewById(R.id.id_email_login);
            senha = findViewById(R.id.id_senha_login);
            layout_email = findViewById(R.id.id_til_email);
            layout_senha = findViewById(R.id.id_til_senha);
            recuperaSenha = findViewById(R.id.id_recuperar_senha);
            botao_login = findViewById(R.id.id_botao_login);
            botao_cadastro = findViewById(R.id.id_botao_cadastro_login);
            help = findViewById(R.id.id_help);

            botao_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validarCampos() == false) {
                        final Usuario usuario = new Usuario();
                        firebaseUser = firebaseAuth.getCurrentUser();

                        usuario.setEmail_usu(email.getText().toString());
                        usuario.setSenha_usu(senha.getText().toString());

                        progressDialog.show();

                        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail_usu().toString().trim(), usuario.getSenha_usu().toString().trim())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            Log.i("signIn", "Sucesso");

                                            if (firebaseUser.isEmailVerified() == true) {
                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(it);
                                                finish();
                                            } else {
                                                firebaseUser.reload();
                                                Log.i("emailVerificado", "false");
                                                Toast.makeText(LoginActivity.this, "E-mail não verificado. Por favor, cheque sua conta ou reenvie acessando a tela de Ajuda (?).", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            Log.i("aqui", "AAAAAAAAA");
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseNetworkException e) {
                                                firebaseAuth.signOut();
                                                Log.i("ERRO1", "VTMN");
                                                Toast.makeText(getApplicationContext(), "Por favor, conecte-se para realizar o login.", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Log.i("ERRO2", "VTMN");
                                                firebaseAuth.signOut();
                                                Toast.makeText(getApplicationContext(), "E-mail incorreto.", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                firebaseAuth.signOut();
                                                Toast.makeText(getApplicationContext(), "Senha incorreta.", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Log.i("signIn", "Erro", task.getException());
                                                firebaseAuth.signOut();
                                                Toast.makeText(getApplicationContext(), "Erro ao realizar login.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });

                    }
                }
            });

            botao_cadastro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, CadastrarUsuarioActivity.class));
                }
            });

            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, HelpActivity.class));
                }
            });

            recuperaSenha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, HelpActivity.class));
                }
            });
        }
    }

    private boolean validarCampos(){
        layout_email.setErrorEnabled(false);
        layout_senha.setErrorEnabled(false);

        int emailSize = email.getText().toString().length();
        int senhaSize = senha.getText().toString().length();

        if (emailSize == 0 || senhaSize == 0) {
            if(emailSize == 0){
                layout_email.setErrorEnabled(true);
                layout_email.setError("Preencha com seu email.");
            }
            if(senhaSize == 0){
                layout_senha.setErrorEnabled(true);
                layout_senha.setError("Preencha com sua senha.");
            }
            return true;
        }

        return false;
    }
}
