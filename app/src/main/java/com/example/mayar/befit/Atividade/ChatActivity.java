package com.example.mayar.befit.Atividade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mayar.befit.Adapter.MensagemAdapter;
import com.example.mayar.befit.Classes.Conversa;
import com.example.mayar.befit.Classes.Mensagem;
import com.example.mayar.befit.Configuracoes.ConfiguracaoFirebase;
import com.example.mayar.befit.Configuracoes.Preferencias;
import com.example.mayar.befit.R;
import com.example.mayar.befit.Usuario.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    //Tela
    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btMensagem;
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference mensagemReference = databaseReference.child("mensagens");
    private DatabaseReference conversaReference = databaseReference.child("conversas");
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    private ImageView botao_voltar;
    private ImageView marca;

    // dados do destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    // dados do rementente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);
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
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
            }
        });

        // dados do usuário logado
        Preferencias preferencias = new Preferencias(ChatActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if( extra != null ){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            //idUsuarioDestinatario = Base64Custom.codificarBase64( emailDestinatario );
        }


        // Monta listview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ChatActivity.this, mensagens);
        listView.setAdapter( adapter );

        // Recuperar mensagens do Firebase
        mensagemReference
                .child( "8359dcb0-e98c-4aee-9182-fbfd4f56bdf1" )
                .child( "8053f0e2-c7c0-4573-87d9-7f845f6407b4" );

        // Cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                mensagens.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Mensagem mensagem = dados.getValue( Mensagem.class );
                    mensagens.add( mensagem );
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener( valueEventListenerMensagem );


        // Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = editMensagem.getText().toString();

                if( textoMensagem.isEmpty() ){
                    Toast.makeText(ChatActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
                }else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    // salvamos mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario , mensagem );
                    if( !retornoMensagemRemetente ){
                        Toast.makeText(
                                ChatActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {

                        // salvamos mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente , mensagem );
                        if( !retornoMensagemDestinatario ){
                            Toast.makeText(
                                    ChatActivity.this,
                                    "Problema ao enviar mensagem para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    // salvamos Conversa para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario( idUsuarioDestinatario );
                    conversa.setNome( nomeUsuarioDestinatario );
                    conversa.setMensagem( textoMensagem );

                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if( !retornoConversaRemetente ){
                        Toast.makeText(
                                ChatActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        // salvamos Conversa para o Destinatario

                        conversa = new Conversa();
                        conversa.setIdUsuario( idUsuarioRemetente );
                        conversa.setNome( nomeUsuarioRemetente );
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa );
                        if( !retornoConversaDestinatario ){
                            Toast.makeText(
                                    ChatActivity.this,
                                    "Problema ao salvar conversa para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    editMensagem.setText("");


                }

            }
        });



    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {

            mensagemReference.child( idRemetente )
                    .child( idDestinatario )
                    .push()
                    .setValue( mensagem );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            conversaReference.child( idRemetente )
                    .child( idDestinatario )
                    .setValue( conversa );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerMensagem);
    }
}
