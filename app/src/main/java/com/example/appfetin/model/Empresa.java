package com.example.appfetin.model;

import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Empresa implements Serializable {
    private String idEmpresa;
    private String nomeE;
    private String nomeFiltro;
    private String nomeD;
    private String email;
    private String senha;
    private String endereco;
    private String telefoneC;
    private String telefoneP;
    private String categoria;
    private String tempoEntrega;
    private String precoEntrega;
    private String urlImagem;

    public Empresa() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ususariosRef = firebaseRef.child("empresas").child(getIdEmpresa());
        ususariosRef.setValue( this );
    }

    public void atualizarEmpresa(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(getIdEmpresa());

        Map<String, Object> valoresEmpresa = converterParaMap();
        empresaRef.updateChildren(valoresEmpresa);
    }

    public Map<String, Object> converterParaMap (){
        HashMap<String, Object> empresaMap = new HashMap<>();
        empresaMap.put("idEmpresa", getIdEmpresa());
        empresaMap.put("nomeE", getNomeE());
        empresaMap.put("nomeFiltro", getNomeFiltro());
        empresaMap.put("categoria", getCategoria());
        empresaMap.put("tempoEntrega", getTempoEntrega());
        empresaMap.put("precoEntrega", getPrecoEntrega());
        empresaMap.put("nomeD", getNomeD());
        empresaMap.put("email", getEmail());
        empresaMap.put("telefoneP", getTelefoneP());
        empresaMap.put("telefoneC", getTelefoneC());
        empresaMap.put("endereco", getEndereco());
        empresaMap.put("urlImagem", getUrlImagem());
        return empresaMap;
    }

    public void atualizarFotoEmpresa(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(getIdEmpresa());

        Map<String, Object> valoresUsuario = converterParaMapFoto();
        empresaRef.updateChildren(valoresUsuario);
    }

    public Map<String, Object> converterParaMapFoto(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nomeE", getNomeE());
        usuarioMap.put("idEmpresa", getIdEmpresa());
        usuarioMap.put("urlImagem", getUrlImagem());

        return usuarioMap;
    }

    public String getNomeFiltro() {
        return nomeFiltro;
    }

    public void setNomeFiltro(String nomeFiltro) {
        this.nomeFiltro = nomeFiltro.toUpperCase();
    }

    public String getNomeD() {
        return nomeD;
    }

    public void setNomeD(String nomeD) {
        this.nomeD = nomeD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    @Exclude
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefoneC() {
        return telefoneC;
    }

    public void setTelefoneC(String telefoneC) {
        this.telefoneC = telefoneC;
    }

    public String getTelefoneP() {
        return telefoneP;
    }

    public void setTelefoneP(String telefoneP) {
        this.telefoneP = telefoneP;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNomeE() {
        return nomeE;
    }

    public void setNomeE(String nomeE) {
        this.nomeE = nomeE;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTempoEntrega() {
        return tempoEntrega;
    }

    public void setTempoEntrega(String tempoEntrega) {
        this.tempoEntrega = tempoEntrega;
    }

    public String getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(String precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
