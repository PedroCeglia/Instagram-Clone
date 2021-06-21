package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.PerfilUsuariosAmigoActivity;
import com.example.instagramclone.adapter.AdapterFeed;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Feed;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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

    // RecyclerView
    private RecyclerView rv;
    private AdapterFeed adapterFeed;
    private List<Feed> listaDeFeed;
    private List<Usuario> listaDeUsuarios;

    //  Firebase
    private DatabaseReference database;
    private DatabaseReference feedRef;
    DatabaseReference usuariosRef;
    // Listener
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_feed, container, false);

        // Inicializa Componentes
        rv = view.findViewById(R.id.rvFeed);
        listaDeFeed = new ArrayList<>();
        listaDeUsuarios = new ArrayList<>();

        // Configurando Referencias do firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = database.child("usuarios");
        feedRef = database.child("feed")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        // Configura recyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        // Configurando Adapter
        adapterFeed = new AdapterFeed(listaDeFeed, listaDeUsuarios, getActivity());
        rv.setAdapter(adapterFeed);
        rv.setHasFixedSize(true);

        return view;
    }

    // Recupera Lista de feed
    public void recuperandoListadeFeed(){

        // Limpa Lista
        listaDeFeed.clear();

        // Cria Listener
        valueEventListener = feedRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Feed feed = ds.getValue(Feed.class);
                    recuperandoUsuarioDoFeed(feed);
                    listaDeFeed.add(feed);
                }
                // Reverte a exibição da Lista
                Collections.reverse(listaDeFeed);
                // Notifica adapter
                adapterFeed.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    public void recuperandoUsuarioDoFeed(Feed feed212){
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario usuarioDoFeed = ds.getValue(Usuario.class);
                    if (usuarioDoFeed.getNome().equals(feed212.getNomeUsuario())){
                        listaDeUsuarios.add(usuarioDoFeed);
                        Log.i("NomeUsuarioFeed", usuarioDoFeed.getNome());
                        adapterFeed.notifyDataSetChanged();
                    }
                }
                Log.i("NomeUsuarioFeed", String.valueOf(listaDeUsuarios.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Recupera os Feed do firebase
        recuperandoListadeFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove o listener
        feedRef.removeEventListener(valueEventListener);
    }
}