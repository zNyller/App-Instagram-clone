package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editSenha;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);
        progressBarLogin = findViewById(R.id.progressBarLogin);

    }

    public void validarLogin(View view){
        String campoEmail = editEmail.getText().toString();
        String campoSenha = editSenha.getText().toString();

        if (!campoEmail.isEmpty()){
            if (!campoSenha.isEmpty()){
                usuario = new Usuario();
                usuario.setEmail(campoEmail);
                usuario.setSenha(campoSenha);
                logarUsuario();
            }else {
                Toast.makeText(LoginActivity.this, "Insira uma senha!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this, "Insira um e-mail!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(){
        progressBarLogin.setVisibility(View.VISIBLE);
        autenticacao = ConfigFirebase.getAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBarLogin.setVisibility(View.GONE);
                    abrirTelaPrincipal();
                    finish();
                }else {
                    progressBarLogin.setVisibility(View.GONE);
                    String excecao = "";
                    try {
                        throw (task.getException());
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem!";
                    }catch (Exception e){
                        excecao = "Erro ao logar usuário!" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao,
                            Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfigFirebase.getAutenticacao();
        if (autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
            finish();
        }
    }

    public void abrirCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

}