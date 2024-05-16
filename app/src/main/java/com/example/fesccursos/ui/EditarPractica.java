package com.example.fesccursos.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.fesccursos.R;
import com.example.fesccursos.model.Practica;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarPractica extends AppCompatActivity {

    private static final String TAG = "EditarPractica";

    private EditText editTextNombreEditarPractica;
    private EditText editTextVideoEditarPractica;
    private EditText editTextDescripcionEditarPractica;
    private WebView videoEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_practica);

        editTextNombreEditarPractica = findViewById(R.id.editTextNombreEditarPractica);
        editTextVideoEditarPractica = findViewById(R.id.editTextVideoEditarPractica);
        editTextDescripcionEditarPractica = findViewById(R.id.editTextDescripcionEditarPractica);
        videoEditar = findViewById(R.id.videoEditar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nombre = extras.getString("nombre");
            String video = extras.getString("video");
            String descripcion = extras.getString("descripcion");

            // Asigna los valores a los EditText
            editTextNombreEditarPractica.setText(nombre);
            editTextVideoEditarPractica.setText(video);
            editTextDescripcionEditarPractica.setText(descripcion);

            cargarVideo();
        }

        Button btnEditarPractica = findViewById(R.id.btnEditarPractica);
        btnEditarPractica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarPracticaenFireStore();
            }
        });

        // Editar listener al campo de la URL del video
        editTextVideoEditarPractica.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cargarVideo();
                }
            }
        });

    }

    private void EditarPracticaenFireStore() {

        String idPractica = getIntent().getStringExtra("idPractica");
        String idCurso = getIntent().getStringExtra("idCurso");
        int numPractica = getIntent().getIntExtra("numPractica",0);
        String nombre = editTextNombreEditarPractica.getText().toString().trim();
        String video = editTextVideoEditarPractica.getText().toString().trim();
        String descripcion = editTextDescripcionEditarPractica.getText().toString().trim();

        // Verificar que todos los campos estén completos
        if (nombre.isEmpty() || video.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando Práctica");
        progressDialog.show();

        // Crear una nueva instancia de Practica
        Practica practica = new Practica(idPractica, idCurso, numPractica, nombre, video, descripcion);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener la referencia al documento específico que se va a actualizar
        DocumentReference practicaRef = db.collection("Practicas").document(idPractica);

        // Actualizar la práctica en Firestore
        practicaRef
                .update("nombre", nombre, "video", video, "descripcion", descripcion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Bundle extras = getIntent().getExtras();
                        Intent in = new Intent(EditarPractica.this, AdminPracticas.class);
                        in.putExtra("idCurso", idCurso);
                        in.putExtra("actualizarPractica", true);

                        startActivity(in);
                        progressDialog.dismiss();

                        Log.d(TAG,"Práctica actualizada con ID: " + idPractica);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al Editar la práctica", e);
                        progressDialog.dismiss();
                        Toast.makeText(EditarPractica.this,"Error al Editar la nueva Práctica",Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void cargarVideo() {
        String urlVideo = editTextVideoEditarPractica.getText().toString().trim();

        // Obtener la clave del video de YouTube
        String YouTubeWatchUrl = YouTubeThumbnailHelper.getYouTubeVideoUrl(urlVideo);

        if (!YouTubeWatchUrl.isEmpty() && (YouTubeWatchUrl.startsWith("http://") || YouTubeWatchUrl.startsWith("https://"))) {
            videoEditar.setVisibility(View.VISIBLE);
            videoEditar.getSettings().setJavaScriptEnabled(true);
            videoEditar.setWebChromeClient(new WebChromeClient());
            videoEditar.setWebViewClient(new WebViewClient());
            videoEditar.loadUrl(YouTubeWatchUrl);
        }
    }

    public void regresarEditarPractica(View view) {
        finish();
    }

}
