package com.example.instagram.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT>=23){
            List<String> listaPermissoes = new ArrayList<>();

            /* Percorre as permissões passadas, verificando
            uma a uma se já tem a permissão liberada */
            for (String permissao : permissoes){
                ContextCompat.checkSelfPermission(activity, permissao);
            }
            // Caso a lista esteja vazia, não é necessário solicitar permissão
            if (listaPermissoes.isEmpty()) return true;
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            // Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }
        return  true;
    }

}
