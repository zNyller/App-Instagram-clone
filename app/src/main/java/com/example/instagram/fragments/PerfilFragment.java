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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private Button buttonEditarPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView imagePerfil;

    private DatabaseReference databaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAtualRef;
    private ValueEventListener valueEventListenerPerfil;
    private Usuario usuarioAtual;

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

        buttonEditarPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        imagePerfil = view.findViewById(R.id.imagePerfil);

        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        // Recuperar foto do usuario
        String caminhoFoto = usuarioAtual.getCaminhoFoto();
        if (caminhoFoto != null) {
            Uri uri = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(uri).into(imagePerfil);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosPerfilUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioAtualRef.removeEventListener(valueEventListenerPerfil);
    }

    private void recuperarDadosPerfilUsuario() {
        usuarioAtualRef = usuariosRef.child(usuarioAtual.getIdUsuario());
        valueEventListenerPerfil = usuarioAtualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Recupera dados do usuario logado
                Usuario usuario = snapshot.getValue(Usuario.class);

                // Configurar os valores
                //String publicacoes = String.valueOf(usuario.getPublicacoes());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                //textPublicacoes.setText(publicacoes);
                textSeguidores.setText(seguidores);
                textSeguindo.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}