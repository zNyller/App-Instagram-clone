package com.example.instagram.model;

import com.example.instagram.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;

public class Postagem {
    private String idPost, idUsuario, descricao, caminhoFoto;

    public Postagem() {
        // Gerar o Id para a postagem
        DatabaseReference firebaseRef = ConfigFirebase.getDatabase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey(); // Push gera o id | getKey recupera
        setIdPost(idPostagem);
    }

    public boolean salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getDatabase();
        DatabaseReference postagensRef = firebaseRef
                .child("postagens")
                .child(getIdUsuario())
                .child(getIdPost());
        postagensRef.setValue(this);

        return true;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
