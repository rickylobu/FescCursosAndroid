package com.example.fesccursos.ui;

import static com.example.fesccursos.R.id.youtubePlayerViewVerPractica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fesccursos.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VerPractica extends AppCompatActivity {

    TextView titulo,nombreVerPractica, descripcionVerPractica;
    YouTubePlayerView videoVerPractica;

    private Button regresar, fullScreenButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_practica);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titulo = findViewById(R.id.titulo);
        nombreVerPractica = findViewById(R.id.nombreVerPractica);
        descripcionVerPractica = findViewById(R.id.descripcionVerPractica);
        regresar = findViewById(R.id.regresarVerPractica);

        videoVerPractica = new YouTubePlayerView(this);
        videoVerPractica = findViewById(R.id.youtubePlayerViewVerPractica);
        getLifecycle().addObserver(videoVerPractica);
        fullScreenButton = findViewById(R.id.full_screen_button);


        Bundle bundle = getIntent().getExtras();
        String nombre = bundle.getString("nombre");
        String descripcion = bundle.getString("descripcion");
        String video = bundle.getString("video");

        nombreVerPractica.setText(nombre);
        descripcionVerPractica.setText(descripcion);
        cargarVideo(video);


        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoVerPractica.isFullScreen()) {
                    videoVerPractica.exitFullScreen();
                    fullScreenButton.setText("Full Screen");

                    titulo.setVisibility(View.VISIBLE);
                    nombreVerPractica.setVisibility(View.VISIBLE);
                    descripcionVerPractica.setVisibility(View.VISIBLE);
                    regresar.setVisibility(View.VISIBLE);
                } else {
                    videoVerPractica.enterFullScreen();
                    fullScreenButton.setText("Exit Full Screen");

                    titulo.setVisibility(View.GONE);
                    nombreVerPractica.setVisibility(View.GONE);
                    descripcionVerPractica.setVisibility(View.GONE);
                    regresar.setVisibility(View.GONE);

                    
                }
            }
        });
    }

    private void cargarVideo(String urlVideo) {
        // Obtener la clave del video de YouTube
        String videoId = YouTubeThumbnailHelper.extractVideoId(urlVideo);

        if (!videoId.isEmpty()) {
            videoVerPractica.setVisibility(View.VISIBLE);
            videoVerPractica.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    // Cargar el video de YouTube
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        }
    }

    public void regresarVerPractica(View view) {
        finish();
    }
}