package com.example.appfetin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.appfetin.R;
import com.example.appfetin.activity.ProdutosActivity;
import com.example.appfetin.adapter.AdapterEmpresa;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.listener.RecyclerItemClickListener;
import com.example.appfetin.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ProdutosClienteFragment extends Fragment {

    private RecyclerView recyclerCliente;
    private SearchView searchEmpresas;
    private final List<Empresa> empresas = new ArrayList<>();
    private AdapterEmpresa adapterEmpresa;

    private ValueEventListener valueEventListeneEmpresas;
    private DatabaseReference empresasRef;

    public ProdutosClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos_cliente, container, false);

        inicializarComponentes(view);
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        empresasRef = firebaseRef.child("empresas");

        //configurar o searchView
        searchEmpresas.setQueryHint("Buscar estabelecimento");
        searchEmpresas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarEmpresas(textoDigitado);
                return true;
            }
        });

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerCliente.setLayoutManager(layoutManager);
        recyclerCliente.setHasFixedSize(true);

        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerCliente.setAdapter(adapterEmpresa);

        recuperarEmpresas();

        //adicionar evento de clique no recyclerView
        recyclerCliente.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerCliente, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Empresa empresaSelecionada = empresas.get(position);
                Intent i = new Intent(getActivity(), ProdutosActivity.class);
                i.putExtra("empresaSelecionada", empresaSelecionada);

                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        return view;
    }

    private void pesquisarEmpresas(String texto){

        //limpar lista
        empresas.clear();

        //pesquisar caso tenha texto na pesquisa
        if(texto.length()>=2){
            Query query = empresasRef.orderByChild("nomeFiltro").startAt(texto).endAt(texto+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    //limpar lista
                    empresas.clear();

                    for(DataSnapshot ds: snapshot.getChildren()){
                        Empresa empresa = ds.getValue(Empresa.class);
                        empresas.add(empresa);
                    }

                    adapterEmpresa.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void inicializarComponentes(View v){
       recyclerCliente = v.findViewById(R.id.recyclerClientes);
       searchEmpresas = v.findViewById(R.id.searchEmpresas);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarEmpresas();
    }

    @Override
    public void onStop() {
        super.onStop();
        empresasRef.removeEventListener(valueEventListeneEmpresas);
    }

    private void recuperarEmpresas(){
        valueEventListeneEmpresas = empresasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                empresas.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Empresa empresa = ds.getValue(Empresa.class);
                    empresas.add(empresa);
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}