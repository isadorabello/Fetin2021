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
import com.example.appfetin.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PerfilEmpresaFragment extends Fragment {

    private TextView textNomeDono, textNome, textEmail, textEndereco, textTelefoneP, textTelefoneC,
            categoria, tempo, preco;
    private CircleImageView imagePerfil;
    private AlertDialog dialog;

    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    public PerfilEmpresaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_empresa, container, false);

        Empresa empresaLogada = UsuarioFirebase.getDadosEmpresaLogada();

        inicializarComponentes(view);
        recuperarDados();

        String caminhoFoto = empresaLogada.getUrlImagem();
        if(caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }

        return view;
    }

    private void inicializarComponentes(View v){
        textNome = v.findViewById(R.id.textNomeEmpresa);
        textNomeDono = v.findViewById(R.id.textNomeDonoEmpresa);
        textEmail = v.findViewById(R.id.textEmailEmpresa);
        textEndereco = v.findViewById(R.id.textEnderecoEmpresa);
        textTelefoneP = v.findViewById(R.id.textTelefonePEmpresa);
        textTelefoneC = v.findViewById(R.id.textTelefoneCEmpresa);
        imagePerfil = v.findViewById(R.id.imagePerfilEmpresa);
        categoria = v.findViewById(R.id.textCategoriaEmpresa);
        tempo = v.findViewById(R.id.textTempoEntrega);
        preco = v.findViewById(R.id.textPrecoEntrega);
    }


    private void recuperarDados(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity()).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("empresas").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                assert empresa != null;
                textNomeDono.setText(empresa.getNomeD());
                textNome.setText(empresa.getNomeE());
                textEndereco.setText(empresa.getEndereco());
                textEmail.setText(empresa.getEmail());
                textTelefoneC.setText(empresa.getTelefoneC());
                textTelefoneP.setText(empresa.getTelefoneP());
                categoria.setText(empresa.getCategoria());
                preco.setText(empresa.getPrecoEntrega());
                tempo.setText(empresa.getTempoEntrega());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}