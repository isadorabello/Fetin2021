package com.example.appfetin.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appfetin.R;
import com.example.appfetin.adapter.AdapterPedido;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.listener.RecyclerItemClickListener;
import com.example.appfetin.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PedidosEmpresaFragment extends Fragment {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private final List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;

    private DatabaseReference pedidosRef;

    public PedidosEmpresaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos_empresa, container, false);
        inicializarComponentes(view);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        String idEmpresaLogada = UsuarioFirebase.getIdentificadorUsuario();
        pedidosRef = firebaseRef.child("pedidos").child(idEmpresaLogada);

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPedidos.setLayoutManager(layoutManager);
        recyclerPedidos.setHasFixedSize(true);

        adapterPedido = new AdapterPedido(pedidos, getActivity());
        recyclerPedidos.setAdapter(adapterPedido);
        
        recuperarPedidos();

        //adicionar evento de clique no recyclerView
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pedido pedidoSelecionado = pedidos.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //Configurar o titulo e mensagem
                builder.setTitle("Confirmar pedido");
                builder.setMessage("Deseja confirmar a recepção do pedido?");

                builder.setPositiveButton("Sim", (dialog1, which) -> {
                    pedidoSelecionado.setStatus("Finalizado");
                    pedidoSelecionado.atualizarStatus();
                    Toast.makeText(getActivity(), "Produto confirmado!", Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("Não", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

        return view;
    }

    private void recuperarPedidos() {
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity()).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        Query pedidoPesquisa = pedidosRef.orderByChild("status").equalTo("Confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                pedidos.clear();

                if(snapshot.getValue()!=null){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(View v){
        recyclerPedidos = v.findViewById(R.id.recyclerPedidos);
    }
}