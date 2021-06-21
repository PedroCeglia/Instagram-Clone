package com.example.instagramclone.activitys.loginecadastro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.Base64Custom;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btCadastro;
    private TextView tvAbrirLogin;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBarCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome  = findViewById(R.id.etCampoNomeCadastro);
        campoEmail = findViewById(R.id.etCampoEmailCadastro);
        campoSenha = findViewById(R.id.etCampoSenhaCadastro);
        progressBarCadastro =findViewById(R.id.progressBarCadastro);
        tvAbrirLogin = findViewById(R.id.tvAbrirLogin);
        btCadastro =findViewById(R.id.btCadastro);

    }
    public void cadastrarUsuario(final Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){


                    progressBarCadastro.setVisibility(View.GONE);
                    exibindoWidgetsCadastro();

                    try {

                        Toast.makeText(CadastroActivity.this,
                                "Sucesso ao cadastrar usuário!",
                                Toast.LENGTH_SHORT).show();

                        finish();

                        String identificadorUsuario = task.getResult().getUser().getUid();
                        usuario.setId( identificadorUsuario );
                        usuario.salvar();

                        // Salvar dados no Profile do Firebase
                        UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {

                    // Alterando a visibilidade dos widgets
                    progressBarCadastro.setVisibility(View.GONE);
                    exibindoWidgetsCadastro();

                    String excecao ;
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao= "Por favor, digite um e-mail válido";
                    }catch ( FirebaseAuthUserCollisionException e){
                        excecao = "Este conta já foi cadastrada";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void validarCadastroUsuario(View view){

        //Recuperar textos dos campos
        String textoNome  = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoNome.isEmpty() ){//verifica nome
            if( !textoEmail.isEmpty() ){//verifica e-mail
                if ( !textoSenha.isEmpty() ){

                    // Alterando a visibilidade dos widgets
                    progressBarCadastro.setVisibility(View.VISIBLE);
                    escondendoWidgetsCadastro();

                    // Cadastrando usuario
                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );
                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();

                }
            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o email!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void abrirLogin(View view){
        finish();
    }

    public void escondendoWidgetsCadastro(){

        // Alterando a visibilidade dos widgets
        campoNome.setVisibility(View.GONE);
        campoEmail.setVisibility(View.GONE);
        campoSenha.setVisibility(View.GONE);
        tvAbrirLogin.setVisibility(View.GONE);
        btCadastro.setVisibility(View.GONE);

    }


    public void exibindoWidgetsCadastro(){

        // Alterando a visibilidade dos widgets
        campoNome.setVisibility(View.VISIBLE);
        campoEmail.setVisibility(View.VISIBLE);
        campoSenha.setVisibility(View.VISIBLE);
        campoNome.setText("");
        campoEmail.setText("");
        campoSenha.setText("");
        tvAbrirLogin.setVisibility(View.VISIBLE);
        btCadastro.setVisibility(View.VISIBLE);

    }
}