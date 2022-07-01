package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.helper.Base64Custom;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private EditText editNome, editEmail, editSenha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editCadastroUsuario);
        editEmail = findViewById(R.id.editCadastroEmail);
        editSenha = findViewById(R.id.editCadastroSenha);
        progressBar = findViewById(R.id.progressBar);

    }

    public void validarCadastro(View view){
        String textoNome = editNome.getText().toString();
        String textoEmail = editEmail.getText().toString();
        String textoSenha = editSenha.getText().toString();
        if (!textoNome.isEmpty()) {
            if (!textoEmail.isEmpty()) {
                if (!textoSenha.isEmpty()) {
                    usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    cadastrarUsuario();
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Insira uma senha!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroActivity.this,
                        "Insira um e-mail!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o campo de usuário!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario() {
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfigFirebase.getAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    // Salvar dados no Firebase
                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();

                    // Salvar dados no profile do Firebase (Authentication)
                    UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                    finish();
                }else{
                    progressBar.setVisibility(View.GONE);

                    String excecao = "";
                    try {
                        throw task.getException(); // Lança a exceção
                    }catch ( FirebaseAuthWeakPasswordException e){
                        excecao = "Crie uma senha mais forte!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta já foi cadastrada!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário!" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
