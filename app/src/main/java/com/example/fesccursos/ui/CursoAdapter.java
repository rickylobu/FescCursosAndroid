package com.example.fesccursos.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Curso;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {
    private Context context;
    private List<Curso> listaCursos;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CursoAdapter(Context context, List<Curso> listaCursos) {
        this.context = context;
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso Curso = listaCursos.get(position);
        holder.nombreTextView.setText(Curso.getNombre());
        holder.categoriaTextView.setText(Curso.getCategoria());
        holder.descripcionTextView.setText(Curso.getDescripcion());
        // Cargar imagen utilizando Picasso u otra biblioteca de carga de imágenes
        Picasso.get().load(Curso.getImagen()).into(holder.imagenImageView);

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
        return listaCursos.size();
    }

    public class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView categoriaTextView;
        TextView descripcionTextView;
        ImageView imagenImageView;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            categoriaTextView = itemView.findViewById(R.id.categoriaTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);
        }
    }
}

