package com.example.instagramclone.models;

import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;
    private int publicacoes = 0;
    private int seguidores = 0;
    private int seguindo = 0;

    public Usuario() {
    }

    // Salva Usuario no Firebase
    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child( getId() );

        usuario.setValue( this );

    }

    // Atualiza as Postagens do usuario no firebase
    public void atualizarPostagens(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child( getId() );

        Map<String, Object> valoresPostagem = converterParaMapPostagem();

        usuariosRef.updateChildren( valoresPostagem );
    }

    // Atualiza Nome e foto do usuario no firebase
    public void atualizar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child( getId() );

        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren( valoresUsuario );
    }

    // Usamos esse método para atualizar o banco de dados
    @Exclude
    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("foto", getFoto() );

        return usuarioMap;

    }
    // Usamos esse método para atualizar o banco de dados
    @Exclude
    public Map<String, Object> converterParaMapPostagem(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("publicacoes", getPublicacoes());
        return usuarioMap;

    }

    public int getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(int publicacoes) {
        this.publicacoes = publicacoes;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

