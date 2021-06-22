package com.example.instagramclone.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterSearch;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.RecyclerItemClickListener;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirectActivity extends AppCompatActivity {

    private SearchView searchView;

    // Recycler View
    private RecyclerView rvSearch;
    private AdapterSearch adapterSearch;
    private List<Usuario> listaDeUsuarios = new ArrayList<>();
    List<Usuario> listaDeUsuariosBusca = new ArrayList<>();

    // Firebase
    private DatabaseReference database;
    private DatabaseReference usuariosRef;
    private FirebaseUser usuarioAtual;

    private ValueEventListener valueEventListenerSearchUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct);

        configurandoToolbar();
        configuracoesIniciais();
    }

    private void configurandoToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Direct");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void configuracoesIniciais(){
        //Configura conversas ref
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = database.child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        // Configurando recyclerView
        rvSearch = findViewById(R.id.rvSearch);
        adapterSearch = new AdapterSearch( listaDeUsuarios, getApplicationContext());
        adapterSearch.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearch.setAdapter(adapterSearch);
        rvSearch.setLayoutManager(layoutManager);

        //Configurar evento de clique
        rvSearch.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        rvSearch,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Usuario> listaUsuariosAtualizada = adapterSearch.getListaDeUsuarios();
                                Usuario userSelecionado = listaUsuariosAtualizada.get(position);

                                Intent i = new Intent(DirectActivity.this, ChatActivity.class);
                                i.putExtra("chatContato", userSelecionado );
                                startActivity( i );

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //Configuração do search view
        searchView =findViewById(R.id.searchViewSearch);

        //Listener para caixa de texto
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d("evento", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento", "onQueryTextChange");

                if( newText != null && !newText.isEmpty() ){
                    pesquisarUsuario( newText.toLowerCase() );
                }else{
                    recarregarUsuariosAdapter();
                }
                return true;
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  false;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarUsuarios();
    }

    // Esse método pega os Usuarios que o o nome contem letras do texto da Caixa de Texto e adicionam a outra lista
    public void pesquisarUsuario(String texto){
        //Log.d("pesquisa",  texto );
        listaDeUsuariosBusca.clear();
        for ( Usuario usuario : listaDeUsuarios ){
            String nome = usuario.getNome().toLowerCase();
            if (nome.contains(texto)){
                listaDeUsuariosBusca.add(usuario);
            }
        }
        recarregarUsuariosAdapterBusca();

    }

    // Recarrega o Adapter Inicial( com todos os usuarios )
    public void recarregarUsuariosAdapter(){
        adapterSearch = new AdapterSearch(listaDeUsuarios, getApplicationContext());
        rvSearch.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }
    // Recarrega o Adapter após qualquer Texto for escrito
    public void recarregarUsuariosAdapterBusca(){
        adapterSearch = new AdapterSearch(listaDeUsuariosBusca, getApplicationContext());
        rvSearch.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }

    // Esse método recupera todos os usuarios do nosso banco de dados
    public void recuperarUsuarios(){
        // Limpa a Lista de usuarios
        listaDeUsuarios.clear();

        // criamos o listener para recuperarmos os usuarios do nosso banco de dados
        valueEventListenerSearchUsuarios = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){

                    //  Recuperamos o usuario caso não seje o usuario Logado
                    Usuario usuario = dados.getValue( Usuario.class );
                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if ( !emailUsuarioAtual.equals( usuario.getEmail() ) ){
                        listaDeUsuarios.add( usuario );
                    }
                }
                // Notifica o Adapter
                adapterSearch.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}