package com.example.appfetin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appfetin.R;
import com.example.appfetin.adapter.AdapterProduto;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.listener.RecyclerItemClickListener;
import com.example.appfetin.model.Empresa;
import com.example.appfetin.model.ItemPedido;
import com.example.appfetin.model.Pedido;
import com.example.appfetin.model.Produto;
import com.example.appfetin.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerMenu;
    private ImageView imagemEmpresaMenu;
    private TextView textNomeEmpresaMenu, textCategoria, textTempo, textPreco, textQtd, textTotal;
    private AlertDialog dialog;
    private String idUsuarioLogado;
    private String idEmpresaSelecionada;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;

    private AdapterProduto adapterProduto;
    private final List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private ValueEventListener valueEventListenerProdutos;
    private DatabaseReference produtosRef;
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuario = new Usuario();
    private Pedido pedidoRecuperado;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        inicializarComponentes();

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //recuparar a empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Empresa empSelecionada = (Empresa) bundle.getSerializable("empresaSelecionada");

            textNomeEmpresaMenu.setText(empSelecionada.getNomeE());
            textTempo.setText(empSelecionada.getTempoEntrega());
            textPreco.setText("R$ " + empSelecionada.getPrecoEntrega());
            textCategoria.setText(empSelecionada.getCategoria());

            idEmpresaSelecionada = empSelecionada.getIdEmpresa();

            String url = empSelecionada.getUrlImagem();

            if(!url.isEmpty()){
                Picasso.get().load( url ).into( imagemEmpresaMenu );
            }else {
                Picasso.get().load( R.drawable.perfil ).into( imagemEmpresaMenu );
            }
        }

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Produtos");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        produtosRef = firebaseRef.child("produtos").child(idEmpresaSelecionada);

        //recyclerView
        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMenu.setLayoutManager(layoutManager);
        recyclerMenu.setHasFixedSize(true);

        adapterProduto = new AdapterProduto(produtos, getApplicationContext());
        recyclerMenu.setAdapter(adapterProduto);

        //configurar o evento de click
        recyclerMenu.addOnItemTouchListener(new RecyclerItemClickListener
                (this, recyclerMenu, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmarQuantidade(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        recuperarProdutos();
        recuperarDadosUsuario();

    }

    private void confirmarQuantidade(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String qtd = editQuantidade.getText().toString();

            Produto produtoSelecionado = produtos.get(position);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
            itemPedido.setNome(produtoSelecionado.getNome());
            itemPedido.setPreco(produtoSelecionado.getPreco());
            itemPedido.setQuantidade(Integer.parseInt(qtd));
            itensCarrinho.add(itemPedido);

            if(pedidoRecuperado==null){
                pedidoRecuperado=new Pedido(idUsuarioLogado, idEmpresaSelecionada);
            }

            pedidoRecuperado.setNome(usuario.getNome());
            pedidoRecuperado.setTelefone(usuario.getTelefonePessoal());
            pedidoRecuperado.setEndereco(usuario.getEndereco());
            pedidoRecuperado.setItens(itensCarrinho);
            pedidoRecuperado.salvarPedido();

        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            //
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void inicializarComponentes(){
        recyclerMenu = findViewById(R.id.recyclerMenu);
        imagemEmpresaMenu = findViewById(R.id.imageEmpresaMenu);
        textNomeEmpresaMenu = findViewById(R.id.textNomeEmpresaMenu);
        textCategoria = findViewById(R.id.textCategoriaEmpresaMenu);
        textPreco = findViewById(R.id.textEntregaEmpresaMenu);
        textTempo = findViewById(R.id.textTempoEmpresaMenu);
        textQtd = findViewById(R.id.textQuantidade);
        textTotal = findViewById(R.id.textValor);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosRef.removeEventListener(valueEventListenerProdutos);
    }

    private void recuperarDadosUsuario(){

        dialog = new SpotsDialog.Builder()
                .setContext(this).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuarioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    usuario = snapshot.getValue(Usuario.class);
                }
                recuperarPedidos();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void recuperarPedidos() {
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente")
                .child(idEmpresaSelecionada).child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(snapshot.getValue()!=null){
                    pedidoRecuperado = snapshot.getValue(Pedido.class);

                    assert pedidoRecuperado != null;
                    itensCarrinho = pedidoRecuperado.getItens();

                    for(ItemPedido itemPedido:itensCarrinho){
                        int qtde = itemPedido.getQuantidade();
                        double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde*preco);
                        qtdItensCarrinho+=qtde;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");
                textQtd.setText("Qtd. " + qtdItensCarrinho);
                textTotal.setText("R$ " + df.format(totalCarrinho));

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void recuperarProdutos(){

        valueEventListenerProdutos = produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                produtos.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Produto produto = ds.getValue(Produto.class);
                    produtos.add(produto);
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pedido, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_fazer_pedido:
                confirmarPedido();
                break;
            case R.id.menu_cancelar_pedido:
                cancelarPedido();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelamento");
        builder.setMessage("Deseja cancelar o pedido?");

        builder.setPositiveButton("Sim", (dialog, which) -> {
            pedidoRecuperado.removerPedido();
            pedidoRecuperado = null;
        });

        builder.setNegativeButton("Não", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

        CharSequence[] itens = new CharSequence[]{
          "Dinheiro", "Máquina de cartão"
        };
        builder.setSingleChoiceItems(itens, 0, (dialog, which) -> metodoPagamento = which);

        EditText editObservacao = new EditText(this);
        editObservacao.setHint("Digite uma observação");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String obs = editObservacao.getText().toString();
            pedidoRecuperado.setMetodoPagamento(metodoPagamento);
            pedidoRecuperado.setObservacao(obs);
            pedidoRecuperado.setStatus("Confirmado");
            pedidoRecuperado.confirmarPedido();
            pedidoRecuperado.removerPedido();
            pedidoRecuperado = null;
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}