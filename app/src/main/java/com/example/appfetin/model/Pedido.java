package com.example.appfetin.model;

import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

public class Pedido {
    private String idUsuario;
    private String idEmpresa;
    private String idPedidoFeito;
    private String nome;
    private String endereco;
    private String telefone;
    private String observacao;
    private String status = "pendente";
    private Double total;
    private int metodoPagamento;
    private List<ItemPedido> itens;

    public Pedido() {
    }

    public Pedido(String idUsu, String idEmp) {
        setIdEmpresa(idEmp);
        setIdUsuario(idUsu);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(idEmp).child(idUsu);
        setIdPedidoFeito(pedidoRef.push().getKey());
    }

    public  void salvarPedido(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(getIdEmpresa()).child(getIdUsuario());
        pedidoRef.setValue(this);
    }

    public  void removerPedido(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(getIdEmpresa()).child(getIdUsuario());
        pedidoRef.removeValue();
    }

    public  void confirmarPedido(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedidoFeito());
        pedidoRef.setValue(this);
    }

    public void atualizarStatus() {
        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedidoFeito());
        pedidoRef.updateChildren(status);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedidoFeito() {
        return idPedidoFeito;
    }

    public void setIdPedidoFeito(String idPedidoFeito) {
        this.idPedidoFeito = idPedidoFeito;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}
