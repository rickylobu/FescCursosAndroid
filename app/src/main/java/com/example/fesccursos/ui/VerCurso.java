package com.example.fesccursos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesccursos.R;
import com.example.fesccursos.model.Curso;
import com.example.fesccursos.model.Practica;
import com.example.fesccursos.viewmodel.CursoDataSource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VerCurso extends AppCompatActivity {

    private static final String TAG = "VerCurso";

    private String idCurso;

    private TextView textViewNombreVerCurso;
    private TextView textViewCategoriaVerCurso;
    private TextView textViewDescripcionVerCurso;
    private ImageView imagenVerCurso;
    private Button buttonRegresar;
    private Button buttonSwitch;
    private boolean Switch;

    private RecyclerView recyclerView;
    private PracticaAdapter practicaAdapterEditar;
    private List<Practica> listaPracticas;

    private List<Practica> listaCompletaPracticas;
    private List<Practica> listaBusquedaPracticas;

    private CursoDataSource cursoDataSource;


    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_curso);

        textViewNombreVerCurso = findViewById(R.id.TextViewNombreVerCurso);
        textViewCategoriaVerCurso = findViewById(R.id.TextViewCategoriaVerCurso);
        textViewDescripcionVerCurso = findViewById(R.id.TextViewDescripcionVerCurso);
        imagenVerCurso = findViewById(R.id.imagenVerCurso);
        buttonRegresar = findViewById(R.id.AgregarPracticaButton);
        buttonSwitch = findViewById(R.id.SwitchCursoPracticasVerCurso);


        cursoDataSource = new CursoDataSource(this);

        // Obtener el ID del curso de la actividad anterior
        idCurso = getIntent().getStringExtra("idCurso");

        // Buscar el curso en la base de datos SQLite
        Curso curso = BuscarCursoxIdCursoSQLite();

        // Mostrar los datos del curso en la interfaz
        textViewNombreVerCurso.setText(curso.getNombre());
        textViewCategoriaVerCurso.setText(curso.getCategoria());
        textViewDescripcionVerCurso.setText(curso.getDescripcion());
        cargarImagen(curso.getImagen());

        //Inicializar listaCompletaPracticas y listaBusquedaPracticas
        listaCompletaPracticas = new ArrayList<>();
        listaBusquedaPracticas = new ArrayList<>();

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPracticasVerCurso);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        practicaAdapterEditar = new PracticaAdapter(this, listaBusquedaPracticas);
        recyclerView.setAdapter(practicaAdapterEditar);

        // Obtener las prácticas del curso
        listaPracticas = new ArrayList<>();
        listaPracticas = BuscarPracticasxIdCursoSQLite();

        listaCompletaPracticas.addAll(listaPracticas);
        listaBusquedaPracticas.addAll(listaPracticas);
        practicaAdapterEditar.notifyDataSetChanged();

        if (listaBusquedaPracticas.isEmpty()) {
            Toast.makeText(this, "Aun no tiene Practicas este Curso", Toast.LENGTH_LONG).show();
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

                    Intent intent = new Intent(VerCurso.this, VerPractica.class);
                    intent.putExtra("idPractica", idPractica);
                    intent.putExtra("idCurso", idCurso);
                    intent.putExtra("numPractica", numPractica);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("descripcion", descripcion);
                    intent.putExtra("video", video);

                    startActivity(intent);
                }
            });
        }

        // Configurar el botón para regresar a la actividad anterior
        buttonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Switch = true;
        switchear();
        Switch =false;

        // Configurar el botón para cambiar entre curso y practicas
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Switch) {
                    switchear();
                    Switch = false;
                } else {
                    switchear();
                    Switch = true;
                }

            }
        });

        //inicializar searchView
        searchView = (SearchView) findViewById(R.id.searchViewVerCurso);
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

    private void cargarImagen(String urlImagen) {
        // Utilizar Picasso para cargar la imagen desde la URL en el ImageView
        if (!urlImagen.isEmpty()) {
            Picasso.get().load(urlImagen).into(imagenVerCurso);
        }
    }

    private Curso BuscarCursoxIdCursoSQLite() {
        Curso curso = new Curso();
        try {
            cursoDataSource.open();
            curso = cursoDataSource.buscarCursoXidCurso(idCurso);
        } catch (Exception e) {
            Log.e(TAG, "Error al Buscar las practicas en la base de datos SQLite: " + e.getMessage());
        } finally {
            cursoDataSource.close();
        }
        return curso;
    }

    private List<Practica> BuscarPracticasxIdCursoSQLite() {
        List<Practica> Practicas = new ArrayList<>();
        try {
            cursoDataSource.open();
            Practicas = cursoDataSource.buscarPracticasXidCurso(idCurso);
        } catch (Exception e) {
            Log.e(TAG, "Error al Buscar las practicas en la base de datos SQLite: " + e.getMessage());
        } finally {
            cursoDataSource.close();
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

    private void switchear (){
        ScrollView scrollCurso = findViewById(R.id.scrollCursoVerCurso);
        ScrollView scrollPracticas = findViewById(R.id.scrollPracticasVerCurso);
        SearchView searchView = findViewById(R.id.searchViewVerCurso);
        if (Switch) {
            scrollCurso.setVisibility(View.VISIBLE);
            scrollPracticas.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            buttonSwitch.setText("Practicas");
        } else {
            scrollCurso.setVisibility(View.GONE);
            scrollPracticas.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            buttonSwitch.setText("Curso");
        }
    }


}
