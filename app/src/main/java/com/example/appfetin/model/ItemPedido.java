package com.example.appfetin.model;

public class ItemPedido {
    private String nome;
    private int quantidade;
    private String IdProduto;
    private double preco;

    public ItemPedido() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getIdProduto() {
        return IdProduto;
    }

    public void setIdProduto(String idProduto) {
        IdProduto = idProduto;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
