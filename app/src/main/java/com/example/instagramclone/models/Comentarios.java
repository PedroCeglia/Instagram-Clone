package com.example.instagramclone.models;

import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Comentarios {

    private String fotoUsuario;
    private String comentario;
    private String nomeUsuario;
    private String idPostagem;
    private String idComentario;
    private String idUsuario;

    // Esse método salva uma instancia de comentarios no Firebase
    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference comentarioRef = database
                .child("comentarios")
                .child(getIdPostagem()); // idPostagem

        String chave = comentarioRef.push().getKey();
        setIdComentario(chave);

        comentarioRef.child(getIdComentario()) // idUsuarioLogad
         .setValue(this);
    }

    public Comentarios() {
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }


    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
