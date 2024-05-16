package com.example.fesccursos.ui;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeThumbnailHelper {

    private static final String TAG = "YouTubeThumbnailHelper";

    // Método para obtener el ID del video de YouTube desde la URL
    public static String extractVideoId(String youtubeUrl) {
        String videoId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl); // Buscar el patrón en la URL

        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    // Método para construir la URL de la miniatura
    public static String getImageFromVideo(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId != null) {
            Log.d(TAG, "URL image From Video success");
            return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        } else {
            Log.e(TAG, "Error in get Image From Video YouTube");
            return null;// Si no se encuentra el ID del video, devuelve null
        }
    }

    public static String getYouTubeVideoUrl(String youtubeUrl) {
    String videoId = extractVideoId(youtubeUrl);
        if (videoId != null) {
            Log.d(TAG, "URL Video From youtubeUrl success");
            return "https://www.youtube.com/watch?v=" + videoId;
        } else {
            Log.e(TAG, "Error in get YouTube Video Url From youtubeUrl");
            return null;// Si no se encuentra el ID del video, devuelve null
        }
    }


}
