package com.example.instagram.model;

import com.example.instagram.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String nome, email, senha, idUsuario, caminhoFoto;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebase = ConfigFirebase.getDatabase();
        firebase.child("usuarios").child(getIdUsuario()).setValue(this);
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfigFirebase.getDatabase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( getIdUsuario() );

        Map<String, Object> dadosUsuario = converterParaMap();
        usuariosRef.updateChildren(dadosUsuario);

    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());

        return usuarioMap;
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
