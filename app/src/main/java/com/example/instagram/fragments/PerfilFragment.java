package com.example.instagram.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private Button buttonEditarPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView imagePerfil;
    private GridView gridViewPerfil;

    private DatabaseReference databaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAtualRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfil;
    private Usuario usuarioAtual;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private AdapterGrid adapterGrid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configs iniciais
        usuarioAtual = UsuarioFirebase.getDadosUsuarioAtual();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        usuariosRef = databaseRef.child("usuarios");
        // Configura referencia postagens usuario
        postagensUsuarioRef = ConfigFirebase.getDatabase()
                .child("postagens")
                .child(usuarioAtual.getIdUsuario());

        buttonEditarPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        gridViewPerfil = view.findViewById(R.id.gridPerfil);

        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        inicializarImageLoader();

        // Carrega as fotos das postagens de um usuário
        carregarPostagens();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosPerfilUsuario();

        usuarioAtual = UsuarioFirebase.getDadosUsuarioAtual();

        // Recuperar foto do usuario
        String caminhoFoto = usuarioAtual.getCaminhoFoto();
        if (caminhoFoto != null) {
            Uri uri = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(uri).into(imagePerfil);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioAtualRef.removeEventListener(valueEventListenerPerfil);
    }

    public void inicializarImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        imageLoader.init(config);
    }

    private void recuperarDadosPerfilUsuario() {
        usuarioAtualRef = usuariosRef.child(usuarioAtual.getIdUsuario());
        valueEventListenerPerfil = usuarioAtualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Recupera dados do usuario logado
                usuarioAtual = snapshot.getValue(Usuario.class);

                // Configurar os valores
                String publicacoes = String.valueOf(usuarioAtual.getPublicacoes());
                String seguidores = String.valueOf(usuarioAtual.getSeguidores());
                String seguindo = String.valueOf(usuarioAtual.getSeguindo());

                textPublicacoes.setText(publicacoes);
                textSeguidores.setText(seguidores);
                textSeguindo.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void carregarPostagens(){
        // Recupera as fotos postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Postagem postagem = dataSnapshot.getValue(Postagem.class);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                // Configurar adapter GridView
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}