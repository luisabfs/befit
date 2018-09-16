package com.example.mayar.befit.Usuario;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Classes.Usuario;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class AlterarUsuarioActivity extends AppCompatActivity {
    //Database
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioReference = databaseReference.child("usuarios");
    private DatabaseReference estadoReference = databaseReference.child("estados");
    private DatabaseReference cidadeReference = databaseReference.child("cidades");

    //Tela
    private TextInputLayout layout_nome;
    private TextInputLayout layout_idade;
    private TextInputLayout layout_celular;

    private AppCompatEditText nome;
    private AppCompatEditText idade;
    private AppCompatEditText celular;
    private AppCompatSpinner sexo;
    private AppCompatSpinner cidade;
    private AppCompatSpinner estado;
    private CustomImageView foto;
    private TextView alterar_foto;
    private ImageView botao_voltar;
    private ImageView marca;

    private Button botao_alterar;
    //Aux
    ArrayList<String> estados = new ArrayList<>();
    ArrayList<String> cidades = new ArrayList<>();
    String[] sexos = new String[]{
            "Sexo",
            "Feminino",
            "Masculino",
            "Outro"
    };
    ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private FirebaseStorage storage;
    private FirebaseUser firebaseUser;
    StorageReference storageReference;
    private Uri filePath;
    private Uri downloadUrl;
    private final int PICK_IMAGE_REQUEST = 71;

    //Usuario
    Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_usuario);

        nome = findViewById(R.id.id_nome_alterar);
        idade = findViewById(R.id.id_idade_alterar);
        celular = findViewById(R.id.id_celular_alterar);
        foto = findViewById(R.id.id_foto_usuario_alterar);
        alterar_foto = findViewById(R.id.id_alterar_foto);
        estado = findViewById(R.id.id_estado_alterar);
        cidade = findViewById(R.id.id_cidade_alterar);
        sexo = findViewById(R.id.id_sexo_alterar);
        botao_alterar = findViewById(R.id.id_botao_alterar);
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                startActivity(new Intent(AlterarUsuarioActivity.this, MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AlterarUsuarioActivity.this, R.style.AlertDialog);
                builder.setMessage("Cancelar alteração?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        progressDialog = new ProgressDialog(AlterarUsuarioActivity.this, R.style.AlertDialog);
        progressDialog.setTitle("Carregando...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });
        alterar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        //SPINNERS
        //Spinner Sexo
        final ArrayAdapter<String> sexoAdapter = new ArrayAdapter<String>(AlterarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, sexos){
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
        final ArrayAdapter<String> estadoAdapter = new ArrayAdapter<String>(AlterarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, estados){
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
                                cidades.add(dados.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cidades.add(0, "Cidade");
        //Spinner Cidades
        final ArrayAdapter<String> cidadeAdapter = new ArrayAdapter<String>(AlterarUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item, cidades){
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //recuperando os dados de USUARIO
        firebaseUser = firebaseAuth.getCurrentUser();
        usuarioReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(dataSnapshot.child("url_foto_usu").getValue().toString())
                        .into(foto);
                filePath = Uri.parse(dataSnapshot.child("url_foto_usu").getValue().toString());
                nome.setText(dataSnapshot.child("nome_usu").getValue().toString());
                idade.setText(dataSnapshot.child("idade_usu").getValue().toString());
                celular.setText(dataSnapshot.child("celular_usu").getValue().toString());
                int sexo_position = sexoAdapter.getPosition(dataSnapshot.child("sexo_usu").getValue().toString());
                sexo.setSelection(sexo_position);
                int estado_position = estadoAdapter.getPosition(dataSnapshot.child("estado_usu").getValue().toString());
                estado.setSelection(estado_position);
                int cidade_position = cidadeAdapter.getPosition(dataSnapshot.child("cidade_usu").getValue().toString());
                cidade.setSelection(cidade_position);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        botao_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload foto
                    final ProgressDialog progressDialog = new ProgressDialog(AlterarUsuarioActivity.this, R.style.AlertDialog);
                    progressDialog.setTitle("Alterando...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("perfis_usu/" + UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(AlterarUsuarioActivity.this, "Alterado", Toast.LENGTH_SHORT).show();
                                downloadUrl = taskSnapshot.getDownloadUrl();

                                usuario.setNome_usu(nome.getText().toString());
                                usuario.setCelular_usu(celular.getText().toString());
                                usuario.setIdade_usu(idade.getText().toString());
                                usuario.setSexo_usu(sexo.getSelectedItem().toString());
                                usuario.setEstado_usu(estado.getSelectedItem().toString());
                                usuario.setCidade_usu(cidade.getSelectedItem().toString());
                                usuario.setUrl_foto_usu(downloadUrl.toString());

                                usuarioReference.child(firebaseUser.getUid()).child("nome_usu").setValue(usuario.getNome_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("celular_usu").setValue(usuario.getCelular_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("idade_usu").setValue(usuario.getIdade_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("sexo_usu").setValue(usuario.getSexo_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("estado_usu").setValue(usuario.getEstado_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("cidade_usu").setValue(usuario.getCidade_usu());
                                usuarioReference.child(firebaseUser.getUid()).child("url_foto_usu").setValue(usuario.getUrl_foto_usu());

                                startActivity(new Intent(AlterarUsuarioActivity.this, UsuarioActivity.class));
                            }
                            }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AlterarUsuarioActivity.this, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                foto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        AlterarUsuarioActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AlterarUsuarioActivity.this, R.style.AlertDialog);
        builder.setMessage("Cancelar alteração?").setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }
}
