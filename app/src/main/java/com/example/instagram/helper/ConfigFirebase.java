package com.example.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth autenticacao;

    // Retornar a instância do FirebaseAuth
    public static FirebaseAuth getAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        } return  autenticacao;
    }

    // Retornar a instância do FirebaseDatabase
    public static DatabaseReference getDatabase(){
        if (database == null){
            database = FirebaseDatabase.getInstance().getReference();
        } return database;
    }

}

