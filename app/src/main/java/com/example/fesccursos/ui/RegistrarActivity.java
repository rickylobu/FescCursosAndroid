package com.example.fesccursos.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fesccursos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrarActivity extends AppCompatActivity {

    EditText email, pass;

    FirebaseAuth mAuth;

    Button  registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);


        email =(EditText) findViewById(R.id.UsuarioRegistrar);
        pass = (EditText) findViewById(R.id.passRegistrar);

        registrar =(Button) findViewById(R.id.RegistrarUsuario);

        mAuth=FirebaseAuth.getInstance();
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = pass.getText().toString().trim();

                // Validar campos
                if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(RegistrarActivity.this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    // Verificar si el correo electrónico tiene un formato válido
                    Toast.makeText(RegistrarActivity.this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
                } else if (userPassword.length() < 6) {
                    // Verificar si la contraseña tiene al menos 6 caracteres
                    Toast.makeText(RegistrarActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    // Crear usuario con Firebase
                    mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // Redirigir a la pantalla de inicio o bienvenida
                                        Intent i = new Intent(RegistrarActivity.this, MainActivity.class);
                                        startActivity(i);
                                        Toast.makeText(RegistrarActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        // Manejar errores de autenticación
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(RegistrarActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        Log.e("RegistroError", "Error al registrar: " + errorMessage);
                                    }
                                }
                            });
                }
            }
        });

    }
    public void regresarRegistrar(View view) {
        onBackPressed();
        finish();
    }
}