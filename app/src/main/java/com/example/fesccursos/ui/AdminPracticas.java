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
import com.example.fesccursos.model.Practica;
import com.example.fesccursos.viewmodel.CursoDataSource;
import com.example.fesccursos.viewmodel.Repository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminPracticas extends AppCompatActivity {

    private static final String TAG = "AdminPracticas";
    private Repository repository;

    private RecyclerView recyclerView;
    private PracticaAdapter practicaAdapterEditar;
    private List<Practica> listaPracticas;

    private List<Practica> listaCompletaPracticas;
    private List<Practica> listaBusquedaPracticas;

    private CursoDataSource practicaDataSource;

    SearchView searchView;

    private boolean actualizar = false;
    private String idCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_practicas);


        //Inicializar Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando sus Practicas");
        progressDialog.show();

        //Inicializar listaCompletaPracticas y listaBusquedaPracticas
        listaCompletaPracticas = new ArrayList<>();
        listaBusquedaPracticas = new ArrayList<>();

        // Inicializar PracticaDataSource
        practicaDataSource = new CursoDataSource(this);


        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPracticas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        practicaAdapterEditar = new PracticaAdapter(this, listaBusquedaPracticas);
        recyclerView.setAdapter(practicaAdapterEditar);

        //recuperar idCurso de Home y editarCurso y actualizar de editarCurso
        Bundle extras = getIntent().getExtras();
        idCurso = extras.getString("idCurso");
        actualizar = extras.getBoolean("actualizarPractica");

        //logica de actualizar = true para obtener datos de FireBase y false de SQLite
        Context context = this;
        if (actualizar) {
            // Inicializar Repository de FireBase
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            repository = new Repository(firestore);


            // Traer todos los cursos de Firestore
            listaPracticas = new ArrayList<>();
            repository.getPracticasByCurso(idCurso)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Practica practica = document.toObject(Practica.class);
                                listaPracticas.add(practica);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Log.d(TAG, "Practicas obtenidas correctamente en AgregarPractica: " + listaPracticas.size());
                            Log.d(TAG, "Practicas obtenidas correctamente en AgregarPractica" + listaPracticas);


                            //vaciar listaBusquedaPracticas y listaCompletaPracticas e igualar a listaPracticas para Filtrar en Busqueda
                            listaBusquedaPracticas.clear();
                            listaBusquedaPracticas.addAll(listaPracticas);
                            listaCompletaPracticas.clear();
                            listaCompletaPracticas.addAll(listaPracticas);
                            practicaAdapterEditar.notifyDataSetChanged();

                            if (listaBusquedaPracticas.isEmpty()) {
                                Toast.makeText(context, "Aun no tienes Practicas Agregadas", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);


                                practicaAdapterEditar.setOnItemClickListener(new PracticaAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //Abrir una nueva actividad con la practica y todas sus practicas

                                        String idPractica = listaBusquedaPracticas.get(position).getIdPractica();
                                        String idCurso = listaBusquedaPracticas.get(position).getIdCurso();
                                        int numPractica = (int) listaBusquedaPracticas.get(position).getNumPractica();
                                        String nombre = listaBusquedaPracticas.get(position).getNombre();
                                        String descripcion = listaBusquedaPracticas.get(position).getDescripcion();
                                        String video = listaBusquedaPracticas.get(position).getVideo();

                                        Intent intent = new Intent(AdminPracticas.this, EditarPractica.class);
                                        intent.putExtra("idPractica", idPractica);
                                        intent.putExtra("idCurso", idCurso);
                                        intent.putExtra("numPractica", numPractica);
                                        intent.putExtra("nombre", nombre);
                                        intent.putExtra("descripcion", descripcion);
                                        intent.putExtra("video", video);

                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                progressDialog.dismiss();
                            }

                        }
                    });


        } else {
            //Buscar todos los Cursos en base de datos SQLite
            listaCompletaPracticas = BuscarPracticasxIdCursoSQLite();

            listaBusquedaPracticas.addAll(listaCompletaPracticas);
            practicaAdapterEditar.notifyDataSetChanged();

            if (listaBusquedaPracticas.isEmpty()) {
                Toast.makeText(context, "Aun no tienes Practicas Agregadas", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                recyclerView.setVisibility(View.VISIBLE);

                practicaAdapterEditar.setOnItemClickListener(new PracticaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //Abrir una nueva actividad con la practica y todas sus practicas
                        String idPractica = listaBusquedaPracticas.get(position).getIdPractica();
                        String idCurso = listaBusquedaPracticas.get(position).getIdCurso();
                        int numPractica = (int) listaBusquedaPracticas.get(position).getNumPractica();
                        String nombre = listaBusquedaPracticas.get(position).getNombre();
                        String descripcion = listaBusquedaPracticas.get(position).getDescripcion();
                        String video = listaBusquedaPracticas.get(position).getVideo();

                        Intent intent = new Intent(AdminPracticas.this, EditarPractica.class);
                        intent.putExtra("idPractica", idPractica);
                        intent.putExtra("idCurso", idCurso);
                        intent.putExtra("numPractica", numPractica);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("video", video);

                        startActivity(intent);
                    }
                });
                progressDialog.dismiss();
            }

        }


        //inicializar searchView
        searchView = (SearchView) findViewById(R.id.searchViewAdmin);
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

    private List<Practica> BuscarPracticasxIdCursoSQLite() {
        List<Practica> Practicas = new ArrayList<>();
        try {
            practicaDataSource.open();
            Practicas = practicaDataSource.buscarPracticasXidCurso(idCurso);
        } catch (Exception e) {
            Log.e(TAG, "Error al Buscar las practicas en la base de datos SQLite: " + e.getMessage());
        } finally {
            practicaDataSource.close();
        }
        return Practicas;
    }


    private void filtrar(String query) {
        listaBusquedaPracticas.clear();
        for (Practica practica : listaCompletaPracticas) {
            try {
                if (practica.getNumPractica() == Integer.valueOf(query)) {
                    listaBusquedaPracticas.add(practica);
                }
            } catch (NumberFormatException e) {
                if (practica.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        practica.getDescripcion().toLowerCase().contains(query.toLowerCase())) {
                    listaBusquedaPracticas.add(practica);
                }
            }
        }
        practicaAdapterEditar.notifyDataSetChanged();
    }

    // Configurar botón para administrar cursos
    public void AgregarPractica(View view) {
        Intent intent = getIntent();
        Intent in = new Intent(this, AgregarPractica.class);
        in.putExtra("idCurso", intent.getStringExtra("idCurso"));
        startActivity(in);
        finish();
    }

}
