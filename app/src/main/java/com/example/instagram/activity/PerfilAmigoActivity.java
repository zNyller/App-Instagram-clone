package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {
    private Usuario usuarioSelecionado;
    private Usuario usuarioAtual;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioAtualRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configs iniciais
        firebaseRef = ConfigFirebase.getDatabase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Inicializar componentes
        imagePerfil = findViewById(R.id.imagePerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Carregando");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configura nome do usuario na Toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome() );

            // Recuperar foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri uri = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(uri).into(imagePerfil);

            }
        }
    }

    private void verificaSeguindo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child( idUsuarioLogado ).child( usuarioSelecionado.getIdUsuario());

        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ( snapshot.exists() ){
                    // Se já está seguindo
                    habilitarBotaoSeguir(true);
                }else {
                    // Se ainda não segue
                    habilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if ( segueUsuario ){
            buttonAcaoPerfil.setText("Seguindo");
        } else{
            buttonAcaoPerfil.setText("Seguir");

            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    salvarSeguidor(usuarioAtual, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario userAtual, Usuario userSelecionado){
        /* seguidores
            id_usuarioAtual
                id_usuarioSelecionado
                    dados_usuarioselecionado */

        HashMap<String, Object> dadosUsuarioSelecionado = new HashMap<>();
        dadosUsuarioSelecionado.put("nome", userSelecionado.getNome());
        dadosUsuarioSelecionado.put("caminhoFoto", userSelecionado.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(userAtual.getIdUsuario()).child(userSelecionado.getIdUsuario());
        seguidorRef.setValue( dadosUsuarioSelecionado );

        // Alterar botão ação para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        // Atualizar numero "Seguindo" do usuario atual
        int seguindo = userAtual.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuariosRef
                .child( userAtual.getIdUsuario() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        // Atualizar numero "Seguidores" do usuario selecionado
        int seguidores = userSelecionado.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        DatabaseReference usuarioSeguidores = usuariosRef
                .child( userSelecionado.getIdUsuario() );
        usuarioSeguidores.updateChildren( dadosSeguidores );
    }

    private void recuperarDadosUsuarioAtual(){
        usuarioAtualRef = usuariosRef.child(idUsuarioLogado);
        usuarioAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Recupera dados do usuario logado
                usuarioAtual = snapshot.getValue(Usuario.class);

                // Verifica se ja esta seguindo o usuario selecionado
                verificaSeguindo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioAtual();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){
        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getIdUsuario() );
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                // Configurar os valores
                String publicacoes = String.valueOf(usuario.getPublicacoes());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                textPublicacoes.setText(publicacoes);
                textSeguidores.setText(seguidores);
                textSeguindo.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}