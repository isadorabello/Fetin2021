package com.example.appfetin.fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appfetin.R;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PerfilClienteFragment extends Fragment {

    private TextView textNome, textEmail, textEndereco, textTelefone;
    private CircleImageView imagePerfil;
    private AlertDialog dialog;

    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    public PerfilClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes(view);
        recuperarDadosPerfilCliente();

        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if(caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }

        return view;
    }

    private void inicializarComponentes(View v){
        textNome = v.findViewById(R.id.textNomeCliente);
        textEmail = v.findViewById(R.id.textEmailCliente);
        textEndereco = v.findViewById(R.id.textEnderecoCliente);
        textTelefone = v.findViewById(R.id.textTelefoneCliente);
        imagePerfil = v.findViewById(R.id.imagePerfilCliente);
    }

    private void recuperarDadosPerfilCliente(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity()).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                assert usuario != null;
                textNome.setText(usuario.getNome());
                textEmail.setText(usuario.getEmail());
                textEndereco.setText(usuario.getEndereco());
                textTelefone.setText(usuario.getTelefonePessoal());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}