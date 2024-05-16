package com.example.fesccursos.viewmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fesccursos.viewmodel.AdminSQLiteOpenHelper;
import com.example.fesccursos.model.Curso;
import com.example.fesccursos.model.Practica;

import java.util.ArrayList;
import java.util.List;

public class CursoDataSource {
    private SQLiteDatabase database;
    private AdminSQLiteOpenHelper dbHelper;

    public CursoDataSource(Context context) {
        dbHelper = new AdminSQLiteOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Función para cargar datos en la base de datos SQLite
    // Función para cargar datos en la base de datos SQLite
    public void cargarCursos(List<Curso> listaCursos, List<Practica> listaPracticas) {
        try {
            database.beginTransaction();

            // Eliminar tablas existentes
            database.execSQL("DROP TABLE IF EXISTS Cursos");
            database.execSQL("DROP TABLE IF EXISTS Practicas");

            // Crear tablas
            database.execSQL("CREATE TABLE Cursos (idCurso TEXT PRIMARY KEY, idUsuario TEXT NOT NULL, nombre TEXT NOT NULL, categoria TEXT NOT NULL,descripcion TEXT NOT NULL,  imagen TEXT NOT NULL)");
            database.execSQL("CREATE TABLE Practicas (idPractica TEXT PRIMARY KEY, idCurso TEXT NOT NULL, numPractica INTEGER, nombre TEXT NOT NULL, descripcion TEXT NOT NULL, video TEXT, FOREIGN KEY (idCurso) REFERENCES Cursos(idCurso))");            for (Curso curso : listaCursos) {
                ContentValues values = new ContentValues();
                values.put("idCurso",curso.getIdCurso());
                values.put("idUsuario", curso.getIdUsuario());
                values.put("nombre", curso.getNombre());
                values.put("categoria", curso.getCategoria());
                values.put("descripcion", curso.getDescripcion());
                values.put("imagen", curso.getImagen());
                database.insert("Cursos", null, values);
            }

            for (Practica practica : listaPracticas) {
                ContentValues values2 = new ContentValues();
                values2.put("idPractica",practica.getIdPractica());
                values2.put("idCurso", practica.getIdCurso());
                values2.put("numPractica",(Integer) practica.getNumPractica());
                values2.put("nombre", practica.getNombre());
                values2.put("descripcion", practica.getDescripcion());
                values2.put("video", practica.getVideo());
                database.insert("Practicas", null, values2);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("CursoDataSource", "Error al cargar los cursos: " + e.getMessage());
        } finally {
            database.endTransaction();
        }
    }


    // Función para buscar todos los cursos
    public List<Curso> buscarTodosLosCursos() {
        List<Curso> listaCursos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Cursos", null);
        while (cursor.moveToNext()) {
            Curso curso = new Curso();
            curso.setIdCurso(cursor.getString(0));
            curso.setIdUsuario(cursor.getString(1));
            curso.setNombre(cursor.getString(2));
            curso.setCategoria(cursor.getString(3));
            curso.setDescripcion(cursor.getString(4));
            curso.setImagen(cursor.getString(5));
            listaCursos.add(curso);
        }
        cursor.close();
        return listaCursos;
    }

    // Función para buscar los cursos por ID de usuario
    public List<Curso> buscarCursosXIDUsuario(String idUsuario) {
        List<Curso> listaCursos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Cursos WHERE idUsuario = ?", new String[]{idUsuario});
        while (cursor.moveToNext()) {
            Curso curso = new Curso();
            curso.setIdCurso(cursor.getString(0));
            curso.setIdUsuario(cursor.getString(1));
            curso.setNombre(cursor.getString(2));
            curso.setCategoria(cursor.getString(3));
            curso.setDescripcion(cursor.getString(4));
            curso.setImagen(cursor.getString(5));
            listaCursos.add(curso);
        }
        cursor.close();
        return listaCursos;
    }

    // Función para buscar un curso por su ID
    public Curso buscarCursoXidCurso(String idCurso) {
        Curso curso = null;
        Cursor cursor = database.rawQuery("SELECT * FROM Cursos WHERE idCurso = ?", new String[]{idCurso});
        if (cursor.moveToFirst()) {
            curso = new Curso();
            curso.setIdCurso(cursor.getString(0));
            curso.setIdUsuario(cursor.getString(1));
            curso.setNombre(cursor.getString(2));
            curso.setCategoria(cursor.getString(3));
            curso.setDescripcion(cursor.getString(4));
            curso.setImagen(cursor.getString(5));
        }
        cursor.close();
        return curso;
    }

    // Función para buscar las prácticas de un curso por su ID
    public List<Practica> buscarPracticasXidCurso(String idCurso) {
        List<Practica> listaPracticas = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Practicas WHERE idCurso = ? ORDER BY numPractica", new String[]{idCurso});
        while (cursor.moveToNext()) {
            Practica practica = new Practica();
            practica.setIdPractica(cursor.getString(0));
            practica.setIdCurso(cursor.getString(1));
            practica.setNumPractica(cursor.getInt(2));
            practica.setNombre(cursor.getString(3));
            practica.setDescripcion(cursor.getString(4));
            practica.setVideo(cursor.getString(5));
            listaPracticas.add(practica);
        }
        cursor.close();
        return listaPracticas;
    }

    public int getNewNumPractica(String idCurso) {
        int numPractica = 1;
        Cursor cursor = database.rawQuery("SELECT MAX(numPractica) FROM Practicas WHERE idCurso = ?", new String[]{idCurso});
        if (cursor.moveToFirst()) {
            numPractica = cursor.getInt(0) + 1;
        }
        cursor.close();
        return numPractica;
    }



}
