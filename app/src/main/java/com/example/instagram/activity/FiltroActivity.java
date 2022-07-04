package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class FiltroActivity extends AppCompatActivity {
    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private String idUsuarioAtual;
    private EditText editDescricao;

    private RecyclerView recyclerFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        idUsuarioAtual = UsuarioFirebase.getIdentificadorUsuario();
        editDescricao = findViewById(R.id.editDescricao);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            byte[] dadosImagem = bundle.getByteArray("fotoSelecionada");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerFiltros.setLayoutManager(layoutManager);
        recyclerFiltros.setHasFixedSize(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void publicarPostagem(){
        Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioAtual);
        postagem.setDescricao(editDescricao.getText().toString());

        // Recuperar dados da imagem para o Firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        // Salvar imagem no Firebase Storage
        StorageReference storageReference = ConfigFirebase.getFirebaseStorage();
        StorageReference imagemRef = storageReference
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdPost() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this, "" +
                        "Erro ao salvar a imagem!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        postagem.setCaminhoFoto(url.toString());
                        if (postagem.salvar()){
                            Toast.makeText(FiltroActivity.this, "" +
                                    "Sucesso ao salvar a imagem!", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.publicar_imagem:
                publicarPostagem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}