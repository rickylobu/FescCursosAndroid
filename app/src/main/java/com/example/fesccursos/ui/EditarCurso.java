package com.example.fesccursos.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Curso;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditarCurso extends AppCompatActivity {

    private static final String TAG = "EditarCurso";

    private EditText editTextNombreEditarCurso;
    private EditText editTextCategoriaEditarCurso;
    private EditText editTextDescripcionEditarCurso;
    private EditText editTextImagenEditarCurso;
    private ImageView imagenEditar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_curso);

        editTextNombreEditarCurso = findViewById(R.id.editTextNombreEditarCurso);
        editTextCategoriaEditarCurso = findViewById(R.id.editTextCategoriaEditarCurso);
        editTextDescripcionEditarCurso = findViewById(R.id.editTextDescripcionEditarCurso);
        editTextImagenEditarCurso = findViewById(R.id.editTextImagenEditarCurso);
        imagenEditar = findViewById(R.id.imagenEditar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nombre = extras.getString("nombre");
            String categoria = extras.getString("categoria");
            String descripcion = extras.getString("descripcion");
            String imagen = extras.getString("imagen");

            // Asigna los valores a los EditText
            editTextNombreEditarCurso.setText(nombre);
            editTextCategoriaEditarCurso.setText(categoria);
            editTextDescripcionEditarCurso.setText(descripcion);
            editTextImagenEditarCurso.setText(imagen);

            cargarImagen(false);

        }

        Button btnEditarCurso = findViewById(R.id.btnEditarCurso);
        btnEditarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarCursoenFireStore();
            }
        });

        // Editar listener al campo de la URL de la imagen
        editTextImagenEditarCurso.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cargarImagen(true);
                }
            }
        });

    }

    private void EditarCursoenFireStore() {

        String idCurso = getIntent().getStringExtra("idCurso");
        String idUsuario = getIntent().getStringExtra("idUsuario");

        String nombre = editTextNombreEditarCurso.getText().toString().trim();
        String categoria = editTextCategoriaEditarCurso.getText().toString().trim();
        String descripcion = editTextDescripcionEditarCurso.getText().toString().trim();
        String imagen = editTextImagenEditarCurso.getText().toString().trim();

        // Verificar que todos los campos estén completos
        if (nombre.isEmpty() || categoria.isEmpty() || descripcion.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando Curso");
        progressDialog.show();

        // Crear una nueva instancia de Curso
        Curso curso = new Curso(idCurso, idUsuario, nombre, categoria, descripcion, imagen);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener la referencia al documento específico que se va a actualizar
        DocumentReference cursoRef = db.collection("Cursos").document(idCurso);

        // Actualizar el curso en Firestore
        cursoRef
                .update("nombre", nombre, "categoria", categoria, "descripcion", descripcion, "imagen", imagen)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Bundle extras = getIntent().getExtras();
                        Intent in = new Intent(EditarCurso.this, Admin.class);
                        in.putExtra("idUsuario", extras.getString("idUsuario"));
                        in.putExtra("actualizar", true);

                        startActivity(in);
                        progressDialog.dismiss();

                        Log.d(TAG, "Curso actualizado con ID: " + idCurso);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al Editar el curso", e);
                        progressDialog.dismiss();
                        Toast.makeText(EditarCurso.this,"Error al Editar el nuevo Curso",Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void cargarImagen(boolean nueva) {
        String urlImagen = editTextImagenEditarCurso.getText().toString().trim();
        // Utilizar Picasso para cargar la imagen desde la URL en el ImageView
        if (urlImagen.isEmpty()){
            Toast.makeText(this, "No hay url de imagen", Toast.LENGTH_LONG).show();
        }else {
            Picasso.get().load(urlImagen).into(imagenEditar);
            if (nueva) {
                Toast.makeText(this, "SI NO puedes ver tu imágen\nSelecciona otra", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void regresarEditarCurso(View view) {
        finish();
    }

    public void practicasActivity(View view) {
        Intent in = new Intent(this, AdminPracticas.class);
        in.putExtra("idCurso", getIntent().getStringExtra("idCurso"));
        startActivity(in);
    }
}

