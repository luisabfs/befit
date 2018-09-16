package com.example.mayar.befit.Usuario;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.R;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;

public class CadastrarUsuarioActivity extends AppCompatActivity {
    //Tela
    private TextInputLayout layout_email;
    private TextInputLayout layout_senha;
    private TextInputLayout layout_nome;
    private TextInputLayout layout_idade;
    private TextInputLayout layout_sexo;
    private TextInputLayout layout_celular;
    private TextInputLayout layout_cidade;
    private TextInputLayout layout_estado;

    private AppCompatEditText email;
    private AppCompatEditText senha;
    private AppCompatEditText nome;
    private AppCompatEditText idade;
    private AppCompatSpinner sexo;
    private AppCompatEditText celular;
    private AppCompatSpinner cidade;
    private AppCompatSpinner estado;

    private Button botao_cadastro;
    private ImageView escolherFoto;

    //Database
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");
    private DatabaseReference estadoReference = databaseReference.child("estados");
    private DatabaseReference cidadeReference = databaseReference.child("cidades");

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private Uri downloadUrl;
    private final int PICK_IMAGE_REQUEST = 71;

    //Instâncias
    private final Usuario usuario = new Usuario();

    //Aux
    ArrayList<String> estados = new ArrayList<>();
    ArrayList<String> cidades = new ArrayList<>();
    String[] sexos = new String[]{
            "Sexo",
            "Feminino",
            "Masculino",
            "Outro"
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        nome = findViewById(R.id.id_nome_cadastro);
        email = findViewById(R.id.id_email_cadastro);
        senha = findViewById(R.id.id_senha_cadastro);
        idade = findViewById(R.id.id_idade_cadastro);
        sexo = findViewById(R.id.id_sexo_cadastro);
        celular = findViewById(R.id.id_celular_cadastro);
        cidade = findViewById(R.id.id_cidade_cadastro);
        estado = findViewById(R.id.id_estado_cadastro);

        layout_email = findViewById(R.id.id_til_email_cadastro);
        layout_senha = findViewById(R.id.id_til_senha_cadastro);
        layout_nome = findViewById(R.id.id_til_nome);
        layout_cidade = findViewById(R.id.id_til_cidade);
        layout_sexo = findViewById(R.id.id_til_sexo);
        layout_celular = findViewById(R.id.id_til_celular);
        layout_estado = findViewById(R.id.id_til_estado);
        layout_idade = findViewById(R.id.id_til_idade);

        botao_cadastro = findViewById(R.id.id_botao_cadastro);

        escolherFoto = findViewById(R.id.id_escolher_foto);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        SimpleMaskFormatter simpleMaskCelular = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        MaskTextWatcher maskCelular = new MaskTextWatcher(celular, simpleMaskCelular);

        celular.addTextChangedListener(maskCelular);

        //Spinner Sexo
        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<String>(CadastrarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, sexos){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        sexoAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sexo.setAdapter(sexoAdapter);

        sexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Recuperando os estados do bd
        estados.add(0, "Estado");
        estadoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    estados.add(dados.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Spinner Estados
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<String>(CadastrarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, estados){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        estadoAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        estado.setAdapter(estadoAdapter);

        estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text
                    //Recuperando as cidades do bd
                    cidadeReference.child(selectedItemText).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cidades.clear();
                            cidades.add(0, "Cidade");
                            for(DataSnapshot dados:dataSnapshot.getChildren()) {
                                Log.i("socorro3", dataSnapshot.getValue().toString());
                                cidades.add(dados.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cidades.add(0, "Cidade");
        //Spinner Cidades
        ArrayAdapter<String> cidadeAdapter = new ArrayAdapter<String>(CadastrarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, cidades){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        cidadeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        cidade.setAdapter(cidadeAdapter);

        cidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        escolherFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        botao_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();

                //Upload foto
                if(filePath != null && validarCampos()==false) {
                    final ProgressDialog progressDialog = new ProgressDialog(CadastrarUsuarioActivity.this, R.style.AlertDialog);
                    progressDialog.setTitle("Cadastrando...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("perfis_usu/"+ UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUrl = taskSnapshot.getDownloadUrl();
                                    usuario.setNome_usu(nome.getText().toString());
                                    usuario.setEmail_usu(email.getText().toString());
                                    usuario.setSenha_usu(senha.getText().toString());
                                    usuario.setIdade_usu(idade.getText().toString());
                                    usuario.setSexo_usu(sexo.getSelectedItem().toString());
                                    usuario.setTipo_usu("0"); //0 - usuário padrão; 1 - administrador
                                    usuario.setCelular_usu(celular.getText().toString());
                                    usuario.setCidade_usu(cidade.getSelectedItem().toString());
                                    usuario.setEstado_usu(estado.getSelectedItem().toString());
                                    usuario.setUrl_foto_usu(downloadUrl.toString());

                                    final Task<AuthResult> authResultTask = firebaseAuth.createUserWithEmailAndPassword(
                                            email.getText().toString(),
                                            senha.getText().toString())
                                            .addOnCompleteListener(CadastrarUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.i("createUser", "Usuario criado");
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CadastrarUsuarioActivity.this, "Autenticado",
                                                                Toast.LENGTH_SHORT).show();

                                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                        usuario.setCod_usu(firebaseUser.getUid());
                                                        usuarioReference.child(usuario.getCod_usu()).setValue(usuario);

                                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.i("emailEnviado", "true");
                                                                    Intent it = new Intent(CadastrarUsuarioActivity.this, VerificaEmailActivity.class);
                                                                    startActivity(it);
                                                                } else {
                                                                    try{
                                                                        throw task.getException();
                                                                    } catch (Exception e){
                                                                        Log.i("emailEnviado", "false", task.getException());
                                                                        Toast.makeText(
                                                                                CadastrarUsuarioActivity.this,
                                                                                "Erro ao enviar e=mail.",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        try {
                                                            throw task.getException();
                                                        } catch (FirebaseAuthUserCollisionException e) {
                                                            Toast.makeText(CadastrarUsuarioActivity.this, "Conta já existente.", Toast.LENGTH_SHORT).show();
                                                        } catch (Exception e) {
                                                            Log.i("createUser", "Erro ao criar", task.getException());
                                                            Toast.makeText(CadastrarUsuarioActivity.this, "Erro na autenticação", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CadastrarUsuarioActivity.this, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage((int)progress+"%");
                                }
                            });
                }else {
                    if (filePath == null) {
                        Toast.makeText(CadastrarUsuarioActivity.this, "Por favor, selecione uma foto de perfil.", Toast.LENGTH_SHORT).show();
                    }
                    if(validarCampos() == true){
                        Toast.makeText(CadastrarUsuarioActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void escolherFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolher foto de perfil"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                escolherFoto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean validarCampos(){
        layout_email.setErrorEnabled(false);
        layout_senha.setErrorEnabled(false);
        layout_nome.setErrorEnabled(false);
        layout_celular.setErrorEnabled(false);
        layout_idade.setErrorEnabled(false);
        layout_sexo.setErrorEnabled(false);
        layout_estado.setErrorEnabled(false);
        layout_cidade.setErrorEnabled(false);

        int emailSize = email.getText().toString().length();
        int senhaSize = senha.getText().toString().length();
        int nomeSize = nome.getText().toString().length();
        int celularSize = celular.getText().toString().length();
        int idadeSize = idade.getText().toString().length();

        if (emailSize == 0 || senhaSize == 0 || nomeSize == 0 || celularSize == 0 || idadeSize == 0 || sexo.getSelectedItem().equals("Sexo") || estado.getSelectedItem().equals("Estado") || cidade.getSelectedItem().equals("Cidade")) {
            if(emailSize == 0){
                layout_email.setErrorEnabled(true);
                layout_email.setError("Preencha com seu email.");
            }
            if(senhaSize == 0){
                layout_senha.setErrorEnabled(true);
                layout_senha.setError("Preencha com sua senha.");
            }
            if(nomeSize == 0){
                layout_nome.setErrorEnabled(true);
                layout_nome.setError("Preencha com seu nome.");
            }
            if(celularSize == 0){
                layout_celular.setErrorEnabled(true);
                layout_celular.setError("Preencha com seu celular.");
            }
            if(idadeSize == 0){
                layout_idade.setErrorEnabled(true);
                layout_idade.setError("Preencha com sua idade.");
            }
            if(sexo.getSelectedItem().equals("Sexo")){
                layout_sexo.setErrorEnabled(true);
                layout_sexo.setError("Preencha com seu sexo.");
            }
            if(estado.getSelectedItem().equals("Estado")){
                layout_estado.setErrorEnabled(true);
                layout_estado.setError("Preencha com seu estado.");
            }
            if(cidade.getSelectedItem().equals("Cidade")){
                layout_cidade.setErrorEnabled(true);
                layout_cidade.setError("Preencha com sua cidade.");
            }
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        CadastrarUsuarioActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CadastrarUsuarioActivity.this, R.style.AlertDialog);
        builder.setMessage("Cancelar cadastro?").setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }
}
