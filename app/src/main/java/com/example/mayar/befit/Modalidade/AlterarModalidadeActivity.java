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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mayar.befit.Adapter.CustomImageView;
import com.example.mayar.befit.Classes.Modalidade;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AlterarModalidadeActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        AlterarModalidadeActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AlterarModalidadeActivity.this, R.style.AlertDialog);
        builder.setMessage("Cancelar alteração?").setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }

    //Tela
    private Toolbar toolbar;
    private CustomImageView escolherFoto;
    private TextInputLayout layout_descricao;
    private TextInputLayout layout_nome_modalidade;
    private ImageView botao_voltar;
    private ImageView marca;

    private AppCompatEditText nome;
    private AppCompatEditText descricao;
    private Button botaoAlterar;

    //Database
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference modalidadeReference = databaseReference.child("modalidades");

    //Firebase
    private FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private Uri downloadUrl;
    private final int PICK_IMAGE_REQUEST = 71;

    private String mod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_modalidade);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Modalidade");
        setSupportActionBar(toolbar);

        layout_nome_modalidade = findViewById(R.id.id_til_nome_modalidade);
        layout_descricao = findViewById(R.id.id_til_descricao_modalidade);
        nome = findViewById(R.id.id_nome_modalidade);
        descricao = findViewById(R.id.id_descricao_modalidade);
        botaoAlterar = findViewById(R.id.id_botao_alterar_modalidade);
        escolherFoto = findViewById(R.id.id_icone_modalidade);
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
                                Toast.makeText(AlterarModalidadeActivity.this, "Alteração cancelada.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AlterarModalidadeActivity.this, MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AlterarModalidadeActivity.this, R.style.AlertDialog);
                builder.setMessage("Cancelar alteração?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        escolherFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        Bundle extras = getIntent().getExtras();
        mod = extras.getString("modalidade");

        modalidadeReference.orderByChild("nome_mod").equalTo(mod)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(dados.getValue(Modalidade.class).getUrl_icone_mod())
                            .into(escolherFoto);
                    filePath = Uri.parse(dados.getValue(Modalidade.class).getUrl_icone_mod());
                    nome.setText(dados.getValue(Modalidade.class).getNome_mod());
                    descricao.setText(dados.getValue(Modalidade.class).getDescricao_mod());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        botaoAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AlterarModalidadeActivity.this, R.style.AlertDialog);
                progressDialog.setTitle("Alterando...");
                progressDialog.show();

                validarCampos();

                StorageReference ref = storageReference.child("icones_mod/" + UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(AlterarModalidadeActivity.this, "Alterado", Toast.LENGTH_SHORT).show();
                                downloadUrl = taskSnapshot.getDownloadUrl();

                                modalidadeReference.orderByChild("nome_mod").equalTo(mod)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dados:dataSnapshot.getChildren()){
                                            modalidadeReference.child(dados.getValue(Modalidade.class).getCod_mod()).child("nome_mod").setValue(nome.getText());
                                            modalidadeReference.child(dados.getValue(Modalidade.class).getCod_mod()).child("descricao_mod").setValue(descricao.getText());
                                            modalidadeReference.child(dados.getValue(Modalidade.class).getCod_mod()).child("url_icone_mod").setValue(downloadUrl.toString());

                                            startActivity(new Intent(AlterarModalidadeActivity.this, ModalidadeAdmActivity.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AlterarModalidadeActivity.this, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void escolherFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolher ícone da modalidade"), PICK_IMAGE_REQUEST);
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
}
