package com.example.instagramclone.activitys.loginecadastro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.MainActivity;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBarLogin;
    private Button btLogin;
    private TextView tvAbrirCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        campoEmail = findViewById(R.id.etCampoEmailLogin);
        campoSenha = findViewById(R.id.etCampoSenhaLogin);
        progressBarLogin =findViewById(R.id.progressBarLogin);
        btLogin = findViewById(R.id.btLogin);
        tvAbrirCadastro = findViewById(R.id.tvAbrirCadastro);

    }

    public void logarUsuario(Usuario usuario){

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful() ){
                    progressBarLogin.setVisibility(View.GONE);
                    exibindoWidgetsLogin();
                    abrirTelaPrincipal();
                }else {

                    // Alterando a visibilidade dos widgets
                    progressBarLogin.setVisibility(View.GONE);
                    exibindoWidgetsLogin();

                    String excecao;
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void validarAutenticacaoUsuario(View view){


        //Recuperar textos dos campos
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        //Validar se e-mail e senha foram digitados
        if( !textoEmail.isEmpty() ){//verifica e-mail
            if( !textoSenha.isEmpty() ){//verifica senha

                // Definindo Visibilidade dos widget
                progressBarLogin.setVisibility(View.VISIBLE);
                escondendoWidgetsLogin();

                Usuario usuario = new Usuario();
                usuario.setEmail( textoEmail );
                usuario.setSenha( textoSenha );

                logarUsuario( usuario );

            }else {
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( intent );

    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( intent );
    }

    public void escondendoWidgetsLogin(){

        // Alterando a visibilidade dos widgets
        campoEmail.setVisibility(View.GONE);
        campoSenha.setVisibility(View.GONE);
        tvAbrirCadastro.setVisibility(View.GONE);
        btLogin.setVisibility(View.GONE);

    }


    public void exibindoWidgetsLogin(){

        // Alterando a visibilidade dos widgets
        campoEmail.setVisibility(View.VISIBLE);
        campoSenha.setVisibility(View.VISIBLE);
        campoEmail.setText("");
        campoSenha.setText("");
        tvAbrirCadastro.setVisibility(View.VISIBLE);
        btLogin.setVisibility(View.VISIBLE);

    }

}