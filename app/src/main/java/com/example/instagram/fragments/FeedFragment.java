package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterFeed;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {
    private RecyclerView recyclerFeed;
    private AdapterFeed adapterFeed;
    private List<Feed> listaFeed = new ArrayList<>();
    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference feedRef;
    private String idUsuarioLogado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        // Inicializar componentes
        recyclerFeed = view.findViewById(R.id.recyclerFeed);

        // Configs iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        feedRef = ConfigFirebase.getDatabase()
                .child("feed")
                .child(idUsuarioLogado);

        // Config adapter
        adapterFeed = new AdapterFeed(listaFeed, getActivity());

        // Config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerFeed.setLayoutManager(layoutManager);
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setAdapter(adapterFeed);

        return view;

    }

    private void listarFeed(){
        // Listener no child Id do usuário atual do nó feed
        valueEventListenerFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    listaFeed.add( ds.getValue(Feed.class) );
                }
                Collections.reverse( listaFeed );
                adapterFeed.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListenerFeed);
    }
}