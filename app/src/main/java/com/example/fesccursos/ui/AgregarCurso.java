package com.example.fesccursos.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Curso;
import com.example.fesccursos.viewmodel.Repository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AgregarCurso extends AppCompatActivity {

    private static final String TAG = "AgregarCurso";

    private EditText editTextNombreAgregarCurso;
    private EditText editTextCategoriaAgregarCurso;
    private EditText editTextDescripcionAgregarCurso;
    private EditText editTextImagenAgregarCurso;
    private ImageView imagenAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_curso);

        editTextNombreAgregarCurso = findViewById(R.id.editTextNombreAgregarCurso);
        editTextCategoriaAgregarCurso = findViewById(R.id.editTextCategoriaAgregarCurso);
        editTextDescripcionAgregarCurso = findViewById(R.id.editTextDescripcionAgregarCurso);
        editTextImagenAgregarCurso = findViewById(R.id.editTextImagenAgregarCurso);
        imagenAgregar = findViewById(R.id.imagenAgregar);

        Button btnAgregarCurso = findViewById(R.id.btnAgregarCurso);
        btnAgregarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCurso();
            }
        });

        // Agregar listener al campo de la URL de la imagen
        editTextImagenAgregarCurso.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cargarImagen();
                }
            }
        });

    }

    private void agregarCurso() {

        String nombre = editTextNombreAgregarCurso.getText().toString().trim();
        String categoria = editTextCategoriaAgregarCurso.getText().toString().trim();
        String descripcion = editTextDescripcionAgregarCurso.getText().toString().trim();
        String imagen = editTextImagenAgregarCurso.getText().toString().trim();

        // Verificar que todos los campos estén completos
        if (nombre.isEmpty() || categoria.isEmpty() || descripcion.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Agregando Curso");
        progressDialog.show();

        // Crear una nueva instancia de Curso
        Curso curso = new Curso(null, getIntent().getStringExtra("idUsuario"), nombre, categoria, descripcion, imagen);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Agregar el curso a Firestore
        db.collection("Cursos")
                .add(curso)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Establecer el campo idCurso con el ID del documento generado automáticamente
                        documentReference.update("idCurso", documentReference.getId());

                        Bundle extras = getIntent().getExtras();
                        Intent in = new Intent(AgregarCurso.this, Admin.class);
                        in.putExtra("idUsuario", extras.getString("idUsuario"));
                        in.putExtra("actualizar", true);

                        startActivity(in);
                        progressDialog.dismiss();

                        Log.d(TAG, "Curso creado con ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al agregar el curso", e);
                        progressDialog.dismiss();
                        Toast.makeText(AgregarCurso.this,"Error al Agregar el nuevo Curso",Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void cargarImagen() {
        String urlImagen = editTextImagenAgregarCurso.getText().toString().trim();
        // Utilizar Picasso para cargar la imagen desde la URL en el ImageView
        if (urlImagen.isEmpty()){
            Toast.makeText(this, "No hay url de imagen", Toast.LENGTH_LONG).show();
        }else {
            Picasso.get().load(urlImagen).into(imagenAgregar);
            Toast.makeText(this, "SI NO puedes ver tu imágen\nSelecciona otra", Toast.LENGTH_LONG).show();
        }
    }
    public void regresarAgregarCurso(View view) {
        finish();
    }
}
