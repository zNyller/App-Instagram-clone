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
import com.example.instagram.model.Feed;

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
