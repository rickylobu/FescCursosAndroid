package com.example.fesccursos.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Practica;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PracticaAdapter extends RecyclerView.Adapter<PracticaAdapter.PracticaViewHolder> {
    private Context context;
    private List<Practica> listaPracticas;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PracticaAdapter(Context context, List<Practica> listaPracticas) {
        this.context = context;
        this.listaPracticas = listaPracticas;
    }

    @NonNull
    @Override
    public PracticaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_practica, parent, false);
        return new PracticaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticaViewHolder holder, int position) {
        Practica practica = listaPracticas.get(position);
        holder.numPracticaTextView.setText(String.valueOf(practica.getNumPractica()));
        holder.nombreTextView.setText(practica.getNombre());
        // Obtener la URL de la miniatura de YouTube
        String thumbnailUrl = YouTubeThumbnailHelper.getImageFromVideo(practica.getVideo());
        // Cargar la miniatura utilizando Picasso u otra biblioteca de carga de imágenes
        if (thumbnailUrl !=null){
            Picasso.get().load(thumbnailUrl).into(holder.imagenImageView);
        }else{

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPracticas.size();
    }

    public class PracticaViewHolder extends RecyclerView.ViewHolder {
        TextView numPracticaTextView;
        TextView nombreTextView;
        ImageView imagenImageView;

        public PracticaViewHolder(@NonNull View itemView) {
            super(itemView);
            numPracticaTextView = itemView.findViewById(R.id.numPracticaTextView);
            nombreTextView = itemView.findViewById(R.id.nombrePracticaTextView);
            imagenImageView = itemView.findViewById(R.id.imagenPracticaImageView);
        }
    }
}
