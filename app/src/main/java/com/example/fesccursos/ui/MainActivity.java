package com.example.fesccursos.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fesccursos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button inicio;

    EditText email,pass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=(EditText) findViewById(R.id.emailLogin);
        pass=(EditText) findViewById(R.id.passLogin);

        inicio=(Button) findViewById(R.id.btnIniciar);

        mAuth=FirebaseAuth.getInstance();

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=email.getText().toString().trim();
                String password =pass.getText().toString().trim();

                //validar campos
                if (user.isEmpty() && password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Debes llenar los campos",Toast.LENGTH_LONG).show();
                }else{
                    //llenar método que recibe los parametros del usuario
                    loginU(user,password);
                }
            }
        });
    }

    public void loginU (String emailUser, String pass){
        mAuth.signInWithEmailAndPassword(emailUser,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userID = user.getUid();
                        Intent in = new Intent(MainActivity.this, Home.class);
                        in.putExtra("idUsuario", userID);
                        startActivity(in);
                    }else {
                        Toast.makeText(MainActivity.this, "Error al obtener el usuario", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Error de Autentificación",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error al Iniciar sesión",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void RegistrarActivity(View view) {
        Intent in = new Intent(this, RegistrarActivity.class);
        startActivity(in);
    }
}