package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {
    private CircleImageView imageEditarPerfil;
    private TextView alterarFoto;
    private EditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Configs iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioAtual();
        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Recuperar dados do usuário na activity
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText( usuarioPerfil.getDisplayName() );
        editEmailPerfil.setText( usuarioPerfil.getEmail() );

        // Salvar alterações do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Recuperar dados atualizados
                String nomeAtualizado = editNomePerfil.getText().toString();

                // Atualizar dados no perfil
                UsuarioFirebase.atualizarNomeUsuario( nomeAtualizado );

                // Atualizar dados no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void inicializarComponentes(){
        imageEditarPerfil = findViewById(R.id.imageFotoPerfil);
        alterarFoto = findViewById(R.id.textAlterarFoto);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        editEmailPerfil.setFocusable(false); // Não permitir usuario selecionar esse campo
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}