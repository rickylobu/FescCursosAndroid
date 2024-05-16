package com.example.fesccursos.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Curso;
import com.example.fesccursos.viewmodel.CursoDataSource;
import com.example.fesccursos.viewmodel.Repository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private static final String TAG = "Admin";
    private Repository repository;

    private RecyclerView recyclerView;
    private CursoAdapter cursoAdapterEditar;
    private List<Curso> listaCursos;

    private List<Curso> listaCompletaCursos;
    private List<Curso> listaBusquedaCursos;

    private CursoDataSource cursoDataSource;

    SearchView searchView;

    private boolean actualizar = false;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando sus Cursos");
        progressDialog.show();

        //Inicializar listaCompletaCursos y listaBusquedaCursos
        listaCompletaCursos = new ArrayList<>();
        listaBusquedaCursos = new ArrayList<>();

        // Inicializar CursoDataSource
        cursoDataSource = new CursoDataSource(this);


        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cursoAdapterEditar = new CursoAdapter(this, listaBusquedaCursos);
        recyclerView.setAdapter(cursoAdapterEditar);

        //recuperar idUsuario de Home y editarCurso y actualizar de editarCurso
        Bundle extras = getIntent().getExtras();
        idUsuario =extras.getString("idUsuario");
        actualizar=extras.getBoolean("actualizar");

        //logica de actualizar = true para obtener datos de FireBase y false de SQLite
        Context context= this;
        if (actualizar){
            // Inicializar Repository de FireBase
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();

            firestore.setFirestoreSettings(settings);
            repository = new Repository(firestore);


            // Traer todos los cursos de Firestore
            listaCursos = new ArrayList<>();
            repository.getCursosByIdUsuario(idUsuario)
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


                            //vaciar listaBusquedaCursos y listaCompletaCursos e igualar a listaCursos para Filtrar en Busqueda
                            listaBusquedaCursos.clear();
                            listaBusquedaCursos.addAll(listaCursos);
                            listaCompletaCursos.clear();
                            listaCompletaCursos.addAll(listaCursos);
                            cursoAdapterEditar.notifyDataSetChanged();

                            if (listaBusquedaCursos.isEmpty()) {
                                Toast.makeText(context,"Aun no tienes Cursos Agregados", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);

                                cursoAdapterEditar.setOnItemClickListener(new CursoAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //Abrir una nueva actividad con el curso y todas sus practicas

                                        String idCurso = listaBusquedaCursos.get(position).getIdCurso();
                                        String idUsuario = listaBusquedaCursos.get(position).getIdUsuario();
                                        String nombre = listaBusquedaCursos.get(position).getNombre();
                                        String categoria = listaBusquedaCursos.get(position).getCategoria();
                                        String descripcion = listaBusquedaCursos.get(position).getDescripcion();
                                        String imagen = listaBusquedaCursos.get(position).getImagen();

                                        Intent intent = new Intent(Admin.this, EditarCurso.class);
                                        intent.putExtra("idCurso", idCurso);
                                        intent.putExtra("idUsuario", idUsuario);
                                        intent.putExtra("nombre", nombre);
                                        intent.putExtra("categoria", categoria);
                                        intent.putExtra("descripcion", descripcion);
                                        intent.putExtra("imagen", imagen);

                                        startActivity(intent);
                                    }
                                });
                                progressDialog.dismiss();
                            }

                        }
                    });



        }else{
            //Buscar todos los Cursos en base de datos SQLite
            listaCompletaCursos=BuscarCursosxIdUsuarioSQLite();

            listaBusquedaCursos.addAll(listaCompletaCursos);
            cursoAdapterEditar.notifyDataSetChanged();

            if (listaBusquedaCursos.isEmpty()) {
                Toast.makeText(context,"Aun no tienes Cursos Agregados", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                recyclerView.setVisibility(View.VISIBLE);

                cursoAdapterEditar.setOnItemClickListener(new CursoAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //Abrir una nueva actividad con el curso y todas sus practicas
                        String idCurso = listaBusquedaCursos.get(position).getIdCurso();
                        String idUsuario = listaBusquedaCursos.get(position).getIdUsuario();
                        String nombre = listaBusquedaCursos.get(position).getNombre();
                        String categoria = listaBusquedaCursos.get(position).getCategoria();
                        String descripcion = listaBusquedaCursos.get(position).getDescripcion();
                        String imagen = listaBusquedaCursos.get(position).getImagen();

                        Intent intent = new Intent(Admin.this, EditarCurso.class);
                        intent.putExtra("idCurso", idCurso);
                        intent.putExtra("idUsuario", idUsuario);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("categoria", categoria);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("imagen", imagen);

                        startActivity(intent);
                    }
                });
                progressDialog.dismiss();
            }

        }


        //inicializar searchView
        searchView =(SearchView) findViewById(R.id.searchViewAdmin);
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

    private List<Curso> BuscarCursosxIdUsuarioSQLite(){
        List<Curso> Cursos=new ArrayList<>();
        try {
            cursoDataSource.open();
            Cursos=cursoDataSource.buscarCursosXIDUsuario(idUsuario);
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
        cursoAdapterEditar.notifyDataSetChanged();
    }

    // Configurar botón para administrar cursos
    public void AgregarCurso(View view) {
        Intent intent=getIntent();
        Intent in = new Intent(this, AgregarCurso.class);
        in.putExtra("idUsuario",intent.getStringExtra("idUsuario"));
        startActivity(in);
    }

}