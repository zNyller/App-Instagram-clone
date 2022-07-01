package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterPesquisa;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PesquisaFragment extends Fragment {
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;
    private AdapterPesquisa adapterPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference databaseRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);

        // Configs iniciais
        listaUsuarios = new ArrayList<>();
        databaseRef = ConfigFirebase.getDatabase()
                .child("usuarios");


        // Config SearchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // Captura o que foi pesquisado
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { // Captura o que está sendo digitado
                String textoDigitado = newText;
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        // Adapter
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());

        // Config RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerPesquisa.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));

        recyclerPesquisa.setAdapter(adapterPesquisa);

        return view;
    }

    private void pesquisarUsuarios(String texto){
        listaUsuarios.clear();

        // Pesquisa usuários caso tenha texto na pesquisa
        if (texto.length() >= 2){
            Query query = databaseRef.orderByChild( "nome" )
                    .startAt( texto.toUpperCase() ).endAt( texto.toLowerCase() + "\uf8ff" );
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) { // Percorrer a lista de itens
                    listaUsuarios.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        listaUsuarios.add( ds.getValue(Usuario.class) );
                    }
                    adapterPesquisa.notifyDataSetChanged(); // Notificar o adapter
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}