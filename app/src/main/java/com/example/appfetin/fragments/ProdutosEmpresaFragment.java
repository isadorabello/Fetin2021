package com.example.appfetin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appfetin.R;
import com.example.appfetin.adapter.AdapterProduto;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.listener.RecyclerItemClickListener;
import com.example.appfetin.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ProdutosEmpresaFragment extends Fragment {

    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private final List<Produto> produtos = new ArrayList<>();


    private ValueEventListener valueEventListenerProdutos;
    private DatabaseReference produtosRef;

    public ProdutosEmpresaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos_empresa, container, false);

        inicializarComponentes(view);
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerProdutos.setLayoutManager(layoutManager);
        recyclerProdutos.setHasFixedSize(true);

        adapterProduto = new AdapterProduto(produtos, getActivity());
        recyclerProdutos.setAdapter(adapterProduto);

        recuperarProdutos();

        //adicionar evento de clique no recyclerView
         recyclerProdutos.addOnItemTouchListener( new RecyclerItemClickListener(
                 getActivity(), recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
             @Override
             public void onItemClick(View view, int position) {

             }

             @Override
             public void onLongItemClick(View view, int position) {
                 Produto produtoSelecionado = produtos.get(position);

                 AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                 //Configurar o titulo e mensagem
                 dialog.setTitle("Confirmar exclusão");
                 dialog.setMessage("Deseja excluir o produto " + produtoSelecionado.getNome() + "?");

                 dialog.setPositiveButton("Sim", (dialog1, which) -> {
                     produtoSelecionado.deletar();
                     adapterProduto.notifyDataSetChanged();
                     Toast.makeText(getActivity(), "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                 });

                 dialog.setNegativeButton("Não", null);

                 AlertDialog alertDialog = dialog.create();
                 alertDialog.show();

             }

             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             }
         }
         ));

        return view;
    }

    private void inicializarComponentes(View v){
       recyclerProdutos = v.findViewById(R.id.recyclerProdutosEmpresa);
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
}