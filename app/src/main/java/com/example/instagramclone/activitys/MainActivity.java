package com.example.instagramclone.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.loginecadastro.LoginActivity;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.fragments.FeedFragment;
import com.example.instagramclone.fragments.PerfilFragment;
import com.example.instagramclone.fragments.PostFragment;
import com.example.instagramclone.fragments.SearchFragment;
import com.example.instagramclone.helper.Permissao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        Toolbar toolbarPrincipal = findViewById(R.id.toolbar_principal);
        toolbarPrincipal.setTitle("Instagram");
        setSupportActionBar(toolbarPrincipal);

        // Configurando Button Navigation
        configuraNavigationButton();
        // Definindo qual fragment sera definido como padrao
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair :
                deslogarUsuario();
                break;
            case R.id.menu_direct :
                abrirDirect();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void abrirDirect(){
        Intent i = new Intent(MainActivity.this, DirectActivity.class);
        startActivity(i);
    }

    public void deslogarUsuario(){
        try{
            autenticacao.signOut();
            finish();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }

    public void configuraNavigationButton(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.buttonNavigation);

        // Configurações iniciais
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(false);

        // Habilitar a navegação
        habilitarNavegacao(bottomNavigationViewEx);
    }

    // Habilita a navegação dos fragments
    private  void habilitarNavegacao(BottomNavigationViewEx bNEX){

        bNEX.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                switch (item.getItemId()){

                    case R.id.menu_home:
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        break;

                    case R.id.menu_search:
                        fragmentTransaction.replace(R.id.viewPager, new SearchFragment()).commit();
                        break;

                    case R.id.menu_postagem:
                        fragmentTransaction.replace(R.id.viewPager, new PostFragment()).commit();
                        break;

                    case R.id.menu_perfil:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        break;

                }
                return true;
            }
        });
    }

    // Perguntando se temos Permissão de Acesso
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado : grantResults ){
            if ( permissaoResultado == PackageManager.PERMISSION_DENIED ){
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}