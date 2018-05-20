package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model;

import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config.ConfigFirebase;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.Base64Custom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Movimentacao {

    private String data, categoria, descricao, tipo;
    private double valor;

    public Movimentacao() {
    }

    public void salvar(String dataEscolhida) {
        FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
        String idUsuario = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        String mesAno = DateCustom.mesAnoDataEscolhida(dataEscolhida);

        DatabaseReference mDatabase = ConfigFirebase.getFirebaseDatabase();
        mDatabase
                .child("movimentacao")
                .child(idUsuario)
                .child(mesAno)
                .push()
                .setValue(this);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
