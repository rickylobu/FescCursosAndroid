package com.example.fesccursos.viewmodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cursos.db";
    private static final int DATABASE_VERSION = 1;


    public AdminSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Cursos (idCurso TEXT PRIMARY KEY, idUsuario TEXT NOT NULL, nombre TEXT NOT NULL, categoria TEXT NOT NULL,descripcion TEXT NOT NULL, imagen TEXT NOT NULL)");
        db.execSQL("CREATE TABLE Practicas (idPractica TEXT PRIMARY KEY, idCurso TEXT NOT NULL, numPractica INTEGER, nombre TEXT NOT NULL, descripcion TEXT NOT NULL, video TEXT, FOREIGN KEY (idCurso) REFERENCES Cursos(idCurso))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
