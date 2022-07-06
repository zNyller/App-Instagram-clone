package com.example.instagram.model;

import com.example.instagram.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String nome, email, senha, idUsuario, caminhoFoto;
    private int publicacoes = 0;
    private int seguidores = 0;
    private int seguindo = 0;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebase = ConfigFirebase.getDatabase();
        firebase.child("usuarios").child(getIdUsuario()).setValue(this);
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfigFirebase.getDatabase();
        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getIdUsuario() + "/nome", getNome());
        objeto.put("/usuarios/" + getIdUsuario() + "/caminhoFoto", getCaminhoFoto());

        firebaseRef.updateChildren( objeto );

    }

    public void atualizarQtdPosts(){
        DatabaseReference firebaseRef = ConfigFirebase.getDatabase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( getIdUsuario() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("publicacoes", getPublicacoes());
        usuariosRef.updateChildren(dados);

    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());
        usuarioMap.put("publicacoes", getPublicacoes());
        usuarioMap.put("seguidores", getSeguidores());
        usuarioMap.put("seguindo", getSeguindo());

        return usuarioMap;
    }

    public int getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(int publicacoes) {
        this.publicacoes = publicacoes;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public String getNome() {
        return nome;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
