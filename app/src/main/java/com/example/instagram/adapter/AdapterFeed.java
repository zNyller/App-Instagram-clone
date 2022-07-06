package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfigFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.PostagensCurtidas;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {
    private List<Feed> listafeed;
    private Context context;

    public AdapterFeed(List<Feed> feed, Context context) {
        this.listafeed = feed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_feed, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Feed feed = listafeed.get(position);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioAtual();

        // Carrega dados do feed
        if ( feed.getFotoPost() != null){
            Uri uriFotoPostagem = Uri.parse( feed.getFotoPost() );
            Glide.with(context).load(uriFotoPostagem).into(holder.fotoPost);
        }else {
            holder.fotoPost.setImageResource(R.drawable.padrao);
        }

        Uri uriFotoPerfil = Uri.parse( feed.getFotoUsuario() );
        Glide.with(context).load(uriFotoPerfil).into(holder.fotoPerfil);
        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());

        // Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfigFirebase.getDatabase()
                .child("postagens-curtidas")
                .child( "idPostagem" ) ;
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int qtdCurtidas = 0;
                // Checar se o nó qtdCurtidas já existe
                if (snapshot.hasChild("qtdCurtidas")){
                    // Recuperar a quantidade de postagens
                    PostagensCurtidas postagensCurtidas = snapshot.getValue(PostagensCurtidas.class);
                    qtdCurtidas = postagensCurtidas.getQtdCurtidas();
                }

                // Verifica se já foi curtido
                if (snapshot.hasChild( usuarioLogado.getIdUsuario()) ){
                    holder.like.setClickable(false);
                }else {
                    holder.like.setClickable(true);
                }

                // Monta o objeto postagem curtida para o Firebase
                final PostagensCurtidas curtidas = new PostagensCurtidas();
                curtidas.setFeed( feed ); // Recuperar os dados das publicações
                curtidas.setUsuario( usuarioLogado ); // Recuperar os dados do usuario logado
                curtidas.setQtdCurtidas( qtdCurtidas );

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        curtidas.salvar();
                        holder.qtdCurtidas.setText( curtidas.getQtdCurtidas() + " curtidas ");
                    }
                });
                holder.qtdCurtidas.setText( curtidas.getQtdCurtidas() + " curtidas ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listafeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPost, comentarios, like;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imagePerfilPost);
            fotoPost = itemView.findViewById(R.id.imageVisualizarPost);
            nome = itemView.findViewById(R.id.textNomePost);
            descricao = itemView.findViewById(R.id.textDescricaoPost);
            qtdCurtidas = itemView.findViewById(R.id.textCurtidasPost);
            comentarios = itemView.findViewById(R.id.imageComentarioPost);
            like = itemView.findViewById(R.id.imageLikePost);
        }
    }

}
