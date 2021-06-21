package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.PerfilUsuariosAmigoActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Recycler View
    private RecyclerView rvSearch;
    private AdapterSearch adapterSearch;
    private List<Usuario> listaDeUsuarios = new ArrayList<>();
    List<Usuario> listaDeUsuariosBusca = new ArrayList<>();

    // Firebase
    private DatabaseReference database;
    private DatabaseReference usuariosRef;
    private FirebaseUser usuarioAtual;

    // Listener
    private ValueEventListener valueEventListenerSearchUsuarios;

    // Material SearchView
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Configura conversas ref
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = database.child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        // Configurando recyclerView
        rvSearch = view.findViewById(R.id.rvSearch);
        adapterSearch = new AdapterSearch( listaDeUsuarios, getActivity());
        adapterSearch.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvSearch.setAdapter(adapterSearch);
        rvSearch.setLayoutManager(layoutManager);

        //Configurar evento de clique
        rvSearch.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        rvSearch,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Log.i("Erro", "Não Abre");

                                List<Usuario> listaUsuariosAtualizada = adapterSearch.getListaDeUsuarios();
                                Usuario userSelecionado = listaUsuariosAtualizada.get(position);

                                Intent i = new Intent(getActivity(), PerfilUsuariosAmigoActivity.class);
                                i.putExtra("usuarioSelecionado", userSelecionado );
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
        searchView = view.findViewById(R.id.searchViewSearch);

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

        // Inflate the layout for this fragment
        return view ;
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
        adapterSearch = new AdapterSearch(listaDeUsuarios, getActivity());
        rvSearch.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }
    // Recarrega o Adapter após qualquer Texto for escrito
    public void recarregarUsuariosAdapterBusca(){
        adapterSearch = new AdapterSearch(listaDeUsuariosBusca, getActivity());
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