package com.example.instagram.model;

import com.example.instagram.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome, email, senha, idUsuario;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebase = ConfigFirebase.getDatabase();
        firebase.child("usuarios").child(this.idUsuario).setValue(this);
    }

    public String getNome() {
        return nome;
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
