package com.example.instagram.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.activity.FiltroActivity;
import com.example.instagram.helper.Permissao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PostagemFragment extends Fragment {
    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 10;
    private static final int SELECAO_GALERIA = 20;
    private String[] permissoes = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        // Validar permissões
        Permissao.validarPermissoes(permissoes, getActivity(), 1);

        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);

        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK){
            Bitmap imagem = null;
            try {
                // Valida tipo de seleção da imagem
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                }
                // Valida imagem selecionada
                if (imagem != null) {
                    // Converte Imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Envia imagem escolhida para aplicação de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("fotoSelecionada", dadosImagem);
                    startActivity(i);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}