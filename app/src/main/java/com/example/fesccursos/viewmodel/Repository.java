package com.example.fesccursos.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fesccursos.model.Curso;
import com.example.fesccursos.model.Practica;
import com.example.fesccursos.ui.AgregarCurso;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static final String TAG = "FirestoreRepository";
    private static final String COLLECTION_CURSOS = "Cursos";
    private static final String COLLECTION_PRACTICAS = "Practicas";

    private final FirebaseFirestore db;
    private final CollectionReference cursosRef;
    private final CollectionReference practicasRef;

    public Repository(FirebaseFirestore db) {
        this.db = db;
        this.cursosRef = db.collection(COLLECTION_CURSOS);
        this.practicasRef = db.collection(COLLECTION_PRACTICAS);
    }

    // Funciones para la colección "Cursos"
    public Task<QuerySnapshot> getAllCursos() {
        return cursosRef.get()
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error al obtener todos los cursos", e));
    }

    public Task<QuerySnapshot> getCursosByIdUsuario(String idUsuario) {
        return cursosRef.whereEqualTo("idUsuario", idUsuario).get()
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error al obtener los cursos por ID de usuario", e));
    }

    // Funciones para la colección "Practicas"
    public boolean createPractica(Practica practica) {
        final boolean[] exito = {false};
        practicasRef.add(practica)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Práctica creada con ID: " + documentReference.getId());
                    exito[0] = true;
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al crear la práctica", e);
                    exito[0] = false;
                });
        return exito[0];
    }

    public boolean updatePractica(Practica practica) {
        final boolean[] exito = {false};
        DocumentReference practicaRef = practicasRef.document(practica.getIdPractica());
        practicaRef.set(practica)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Práctica actualizada correctamente");
                    exito[0] = true;
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al actualizar la práctica", e);
                    exito[0] = false;
                });
        return exito[0];
    }

    public boolean deletePractica(String idPractica) {
        final boolean[] exito = {false};
        practicasRef.document(idPractica).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Práctica eliminada correctamente");
                    exito[0] = true;
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al eliminar la práctica", e);
                    exito[0] = false;
                });
        return exito[0];
    }

    public Task<QuerySnapshot> getAllPracticas() {
        return practicasRef.orderBy("idCurso")
                .get()
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error al obtener todas las prácticas", e));
    }

    public Task<QuerySnapshot> getPracticasByCurso(String idCurso) {
        return practicasRef.whereEqualTo("idCurso", idCurso)
                .orderBy("numPractica")
                .get()
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error al obtener las prácticas por ID de curso", e));
    }

}
