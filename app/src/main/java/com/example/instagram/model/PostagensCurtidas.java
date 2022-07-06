package com.example.instagram.model;

import com.example.instagram.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagensCurtidas {
    public Feed feed;
    public Usuario usuario;
    public int qtdCurtidas = 0;

    public PostagensCurtidas() {
    }

    public void salvar(){
        DatabaseReference databaseRef = ConfigFirebase.getDatabase();

        // Objeto usuario (para recuperar apenas nome e caminhoFoto)
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidasRef = databaseRef
                .child("postagens-curtidas")
                .child( "idPostagem"  ) //id_post
                .child( usuario.getIdUsuario() ); // id_usuario_logado
        pCurtidasRef.setValue( dadosUsuario ); // Salva no nó ID usuario apenas os dados do HashMap

        // Atualizar quantidade
        atualizarCurtidas(1);
    }

    public void atualizarCurtidas(int quantidade){
        DatabaseReference databaseRef = ConfigFirebase.getDatabase();

        DatabaseReference pCurtidasRef = databaseRef
                .child("postagens-curtidas")
                .child( "idPostagem"  ) //id_post
                .child("qtdCurtidas");
        setQtdCurtidas( getQtdCurtidas() + quantidade );
        pCurtidasRef.setValue( getQtdCurtidas() );
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }
}
