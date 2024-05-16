package com.example.fesccursos.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Practica;
import com.example.fesccursos.viewmodel.CursoDataSource;
import com.example.fesccursos.viewmodel.Repository;
import com.example.fesccursos.model.Curso;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    private Repository repository;

    private RecyclerView recyclerView;
    private CursoAdapter cursoAdapter;
    private List<Curso> listaCursos;

    private List<Practica> listaPracticas;

    private List<Curso> listaCompletaCursos;
    private List<Curso> listaBusquedaCursos;

    private CursoDataSource cursoDataSource;

    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Cursos");
        progressDialog.show();

        //Inicializar listaCompletaCursos y listaBusquedaCursos
        listaCompletaCursos = new ArrayList<>();
        listaBusquedaCursos = new ArrayList<>();

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cursoAdapter = new CursoAdapter(this, listaBusquedaCursos);
        recyclerView.setAdapter(cursoAdapter);

        // Inicializar Repository de FireBase
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        firestore.setFirestoreSettings(settings);
        repository = new Repository(firestore);


        // Inicializar CursoDataSource
        cursoDataSource = new CursoDataSource(this);

        // Traer todos los cursos de Firestore
        listaCursos = new ArrayList<>();
        listaPracticas = new ArrayList<>();
        Context context= this;
        repository.getAllCursos()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Curso curso = document.toObject(Curso.class);
                            listaCursos.add(curso);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        Log.d(TAG, "Cursos obtenidos correctamente: " + listaCursos.size());
                        Log.d(TAG, "Cursos obtenidos correctamente "+ listaCursos);
                        // Llamar al método getAllPracticas
                        repository.getAllPracticas()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            Practica practica = document.toObject(Practica.class);
                                            listaPracticas.add(practica);
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                        Log.d(TAG, "Prácticas obtenidas correctamente: " + listaPracticas.size());
                                        Log.d(TAG, "Prácticas obtenidos correctamente "+ listaPracticas.toString());

                                        // Cargar datos en la base de datos SQLite
                                        cargarDatosSQLite(listaCursos, listaPracticas);

                                        //Buscar todos los Cursos en base de datos SQLite
                                        listaCompletaCursos=BuscarCursosSQLite();

                                        //listaCompletaCursos=cursoDataSource.buscarTodosLosCursos();
                                        listaBusquedaCursos.addAll(listaCompletaCursos);
                                        cursoAdapter.notifyDataSetChanged();

                                        if (listaBusquedaCursos.isEmpty()) {
                                            Toast.makeText(context,"Aun no tienes Cursos Agregados", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        } else {
                                            recyclerView.setVisibility(View.VISIBLE);

                                            cursoAdapter.setOnItemClickListener(new CursoAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int position) {
                                                    //Abrir una nueva actividad con el curso y todas sus practicas
                                                    String idCurso = listaBusquedaCursos.get(position).getIdCurso();
                                                    Intent intent = new Intent(Home.this, VerCurso.class);
                                                    intent.putExtra("idCurso", idCurso);
                                                    startActivity(intent);
                                                }
                                            });
                                            progressDialog.dismiss();
                                        }

                                    }
                                });

                    }
                });


        //inicializar searchView
        searchView =(SearchView) findViewById(R.id.searchViewHome);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrar(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrar(newText);
                return false;
            }
        });




    }

    private void cargarDatosSQLite(List<Curso> listaCursos, List<Practica> listaPracticas) {
        // Cargar datos en la base de datos SQLite
        try {
            cursoDataSource.open();
            cursoDataSource.cargarCursos(listaCursos, listaPracticas);
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar los cursos en la base de datos SQLite: " + e.getMessage());
        }finally {
            cursoDataSource.close();
        }
    }

    private List<Curso> BuscarCursosSQLite(){
        List<Curso> Cursos=new ArrayList<>();
        try {
            cursoDataSource.open();
            Cursos=cursoDataSource.buscarTodosLosCursos();
        } catch (Exception e) {
            Log.e(TAG, "Error al Buscar los cursos en la base de datos SQLite: " + e.getMessage());
        }finally {
            cursoDataSource.close();
        }
        return Cursos;
    }


    private void filtrar(String query) {
        listaBusquedaCursos.clear();
        for (Curso curso : listaCompletaCursos) {
            if (curso.getNombre().toLowerCase().contains(query.toLowerCase())
                    || curso.getCategoria().toLowerCase().contains(query.toLowerCase())) {
                listaBusquedaCursos.add(curso);
            }
        }
        cursoAdapter.notifyDataSetChanged();
    }

    // Configurar botón para administrar cursos
    public void AdminActivity(View view) {
        Intent intent=getIntent();
        Intent in = new Intent(this, Admin.class);
        in.putExtra("idUsuario",intent.getStringExtra("idUsuario"));
        startActivity(in);
    }



}