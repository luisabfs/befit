package com.example.mayar.befit.Modalidade;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Classes.Modalidade;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;

public class CadastrarModalidadeActivity extends AppCompatActivity {
    //Tela
    private Toolbar toolbar;
    private CustomImageView escolherFoto;
    private TextInputLayout layout_descricao;
    private TextInputLayout layout_nome_modalidade;

    private AppCompatEditText nome;
    private AppCompatEditText descricao;
    private Button botaoCadastro;

    //Database
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference modalidadeReference = databaseReference.child("modalidades");

    //Firebase
    private FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private Uri downloadUrl;
    private final int PICK_IMAGE_REQUEST = 71;

    //Modalidade
    Modalidade modalidade = new Modalidade();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_modalidade);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Modalidade");
        setSupportActionBar(toolbar);

        layout_nome_modalidade = findViewById(R.id.id_til_nome_modalidade);
        layout_descricao = findViewById(R.id.id_til_descricao_modalidade);
        nome = findViewById(R.id.id_nome_modalidade);
        descricao = findViewById(R.id.id_descricao_modalidade);
        botaoCadastro = findViewById(R.id.id_botao_cadastrar_modalidade);
        escolherFoto = findViewById(R.id.id_icone_modalidade);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        escolherFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfiguracaoFirebase.getFirebaseAuth();

                //Upload foto
                if(filePath != null && validarCampos()==false) {
                    final ProgressDialog progressDialog = new ProgressDialog(CadastrarModalidadeActivity.this, R.style.AlertDialog);
                    progressDialog.setTitle("Cadastrando...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("icones_mod/"+ UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CadastrarModalidadeActivity.this, "Cadastrado", Toast.LENGTH_SHORT).show();
                                    downloadUrl = taskSnapshot.getDownloadUrl();

                                    modalidade.setCod_mod(UUID.randomUUID().toString());
                                    modalidade.setNome_mod(nome.getText().toString());
                                    modalidade.setDescricao_mod(descricao.getText().toString());
                                    modalidade.setStatus_mod(true);
                                    modalidade.setUrl_icone_mod(downloadUrl.toString());

                                    modalidadeReference.child(modalidade.getCod_mod()).setValue(modalidade);
                                    startActivity(new Intent(CadastrarModalidadeActivity.this, MainActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CadastrarModalidadeActivity.this, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }else {
                    if (filePath == null) {
                        Toast.makeText(CadastrarModalidadeActivity.this, "Por favor, selecione um ícone para a modalidade.", Toast.LENGTH_SHORT).show();
                    }
                    if(validarCampos() == false){
                        Toast.makeText(CadastrarModalidadeActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
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
               /* Glide.with(this)
                        .load("http://via.placeholder.com/300.png")
                        .bitmapTransform(new CropCircleTransformation(CadastrarModalidadeActivity.this))
                        .into(escolherFoto);
               */
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap rBitmap = Bitmap.createScaledBitmap(bitmap, 140, 140, true);

                escolherFoto.setImageBitmap(rBitmap);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean validarCampos(){
        layout_nome_modalidade.setErrorEnabled(false);
        layout_descricao.setErrorEnabled(false);

        int nomeSize = nome.getText().toString().length();
        int descricaoSize = descricao.getText().toString().length();

        if (nomeSize == 0 || descricaoSize == 0) {
            if(nomeSize == 0){
                layout_nome_modalidade.setErrorEnabled(true);
                layout_nome_modalidade.setError("Preencha com o nome da modalidade.");
            }
            if(descricaoSize == 0){
                layout_descricao.setErrorEnabled(true);
                layout_descricao.setError("Preencha com a descrição da modalidade.");
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
                        CadastrarModalidadeActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CadastrarModalidadeActivity.this, R.style.AlertDialog);
        builder.setMessage("Cancelar cadastro?").setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }
}
