package com.example.fesccursos.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Practica;
import com.example.fesccursos.viewmodel.CursoDataSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AgregarPractica extends AppCompatActivity {

    private static final String TAG = "AgregarPractica";

    private EditText editTextNombreAgregarPractica;
    private EditText editTextVideoAgregarPractica;
    private EditText editTextDescripcionAgregarPractica;
    private WebView videoAgregar;

    private CursoDataSource cursoDataSource;

    String idCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_practica);

        idCurso = getIntent().getStringExtra("idCurso");
        editTextNombreAgregarPractica = findViewById(R.id.editTextNombreAgregarPractica);
        editTextVideoAgregarPractica = findViewById(R.id.editTextVideoAgregarPractica);
        editTextDescripcionAgregarPractica = findViewById(R.id.editTextDescripcionAgregarPractica);
        videoAgregar = findViewById(R.id.videoAgregar);

        Button btnAgregarPractica = findViewById(R.id.btnAgregarPractica);
        btnAgregarPractica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPractica();
            }
        });

        // Agregar listener al campo de la URL del video
        editTextVideoAgregarPractica.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cargarVideo();
                }
            }
        });

    }

    private void agregarPractica() {

        String nombre = editTextNombreAgregarPractica.getText().toString().trim();
        String video = editTextVideoAgregarPractica.getText().toString().trim();
        String descripcion = editTextDescripcionAgregarPractica.getText().toString().trim();

        // Verificar que todos los campos estén completos
        if (nombre.isEmpty() || video.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Agregando Práctica");
        progressDialog.show();

        // Crear un nuevo número de Practica
        int numPractica = NewNumPractica(idCurso);
        Practica practica = new Practica(null, idCurso, numPractica, nombre, descripcion, video);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Agregar la práctica a Firestore
        db.collection("Practicas")
                .add(practica)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Establecer el campo idPractica con el ID del documento generado automáticamente
                        documentReference.update("idPractica", documentReference.getId());

                        Bundle extras = getIntent().getExtras();
                        Intent in = new Intent(AgregarPractica.this, AdminPracticas.class);
                        in.putExtra("idCurso", extras.getString("idCurso"));
                        in.putExtra("actualizarPractica", true);

                        startActivity(in);
                        progressDialog.dismiss();

                        Log.d(TAG, "Práctica creada con ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al agregar la práctica", e);
                        progressDialog.dismiss();
                        Toast.makeText(AgregarPractica.this, "Error al Agregar la nueva Práctica", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private int NewNumPractica(String idCurso) {
        cursoDataSource = new CursoDataSource(this);
        int newNumP = 1;
        try {
            cursoDataSource.open();
            newNumP = cursoDataSource.getNewNumPractica(idCurso);
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener Nuevo número para practica " + e.getMessage());
        } finally {
            cursoDataSource.close();
        }
        return newNumP;
    }

    private void cargarVideo() {
        String urlVideo = editTextVideoAgregarPractica.getText().toString().trim();

        // Obtener la clave del video de YouTube
        String YouTubeWatchUrl = YouTubeThumbnailHelper.getYouTubeVideoUrl(urlVideo);

        if (!YouTubeWatchUrl.isEmpty() && (YouTubeWatchUrl.startsWith("http://") || YouTubeWatchUrl.startsWith("https://"))) {
            videoAgregar.setVisibility(View.VISIBLE);
            videoAgregar.getSettings().setJavaScriptEnabled(true);
            videoAgregar.setWebChromeClient(new WebChromeClient());
            videoAgregar.setWebViewClient(new WebViewClient());
            videoAgregar.loadUrl(YouTubeWatchUrl);
        }
    }

}