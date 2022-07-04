package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.Permissao;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {
    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private EditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int SELECAO_GALERIA = 20;
    private StorageReference storageReference;
    private String identificadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Configs iniciais
        inicializarComponentes();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioAtual();
        storageReference = ConfigFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Validar permissões
        Permissao.validarPermissoes(permissoes, this, 1);

        // Recuperar dados do usuário na activity
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText( usuarioPerfil.getDisplayName() );
        editEmailPerfil.setText( usuarioPerfil.getEmail() );

        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null){
            Glide.with(EditarPerfilActivity.this).load(url).into(imageEditarPerfil);
        }else {
            imageEditarPerfil.setImageResource(R.drawable.padrao);
        }

        // Salvar alterações do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Recuperar dados atualizados
                String nomeAtualizado = editNomePerfil.getText().toString();

                // Atualizar dados no perfil
                UsuarioFirebase.atualizarNomeUsuario( nomeAtualizado );

                // Atualizar dados no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                if (imagem != null){

                    // Configura imagem na tela
                    imageEditarPerfil.setImageBitmap(imagem);

                    // Recuperar dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Salvar imagem no Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens").child("perfil")
                            .child( identificadorUsuario + ".jpeg" );
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this, "" +
                                    "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditarPerfilActivity.this, "" +
                                    "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url){
        // Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        // Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();
        Toast.makeText(EditarPerfilActivity.this, "Sua foto foi alterada!"
                , Toast.LENGTH_SHORT).show();
        }


    public void inicializarComponentes(){
        imageEditarPerfil = findViewById(R.id.imageFotoPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        editEmailPerfil.setFocusable(false); // Não permitir usuario selecionar esse campo
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidarPermissao();
            }
        }
    }

    private void alertaValidarPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse recurso é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}