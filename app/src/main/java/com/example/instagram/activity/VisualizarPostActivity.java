package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostActivity extends AppCompatActivity {
    private CircleImageView imagePerfilPost;
    private TextView textNomePost, textDescricaoPost;
    private ImageView imageVisualizarPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_post);

        // Inicializar componentes
        imagePerfilPost = findViewById(R.id.imagePerfilPost);
        textNomePost = findViewById(R.id.textNomePost);
        textDescricaoPost = findViewById(R.id.textDescricaoPost);
        imageVisualizarPost = findViewById(R.id.imageVisualizarPost);

        // Recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            // Exibe dados do usuário
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPostActivity.this)
                    .load(uri)
                    .into( imagePerfilPost );
            textNomePost.setText( usuario.getNome() );

            // Exibe dados da postagem
            Uri uriPost = Uri.parse( postagem.getCaminhoFoto() );
            Glide.with(VisualizarPostActivity.this)
                    .load(uriPost)
                    .into(imageVisualizarPost);
            textDescricaoPost.setText( postagem.getDescricao() );
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Visualizar Postagem");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}