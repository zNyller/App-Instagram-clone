package com.example.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;

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

    // Retornar a instância do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        } return storage;
    }
}

