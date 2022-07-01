package com.example.instagram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfigFirebase.getAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }

    public static void atualizarNomeUsuario(String nome){
        try {
            //Usuario logado no app
            FirebaseUser usuarioAtual = getUsuarioAtual();

            // Configurar objeto para alteração do perfil
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                .Builder()
                .setDisplayName( nome ) // Setta nome do usuário
                .build();

            usuarioAtual.updateProfile( profileChangeRequest ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar dados de perfil!");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUsuario(Uri url){
        try {
            //Usuario logado no app
            FirebaseUser usuarioAtual = getUsuarioAtual();

            // Configurar objeto para alteração do perfil
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url) // Setta foto do usuário
                    .build();

            usuarioAtual.updateProfile( profileChangeRequest ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil!");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioAtual(){
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setIdUsuario(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null){
            usuario.setCaminhoFoto("");
        }else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }
        return  usuario;
    }

}
