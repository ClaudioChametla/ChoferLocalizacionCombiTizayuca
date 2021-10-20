package com.chametla.localizacioncombitizayucachofer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {

    FirebaseAuth authum;
    DatabaseReference database;
    
    TextInputEditText nombre;
    TextInputEditText correo;
    TextInputEditText contrasena;
    Button botonRegistrar;

    String name;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        authum = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        nombre = findViewById(R.id.textInputName);
        correo = findViewById(R.id.textInputEmail);
        contrasena = findViewById(R.id.textInputPassword);
        botonRegistrar = findViewById(R.id.bRegistrarseya);

        
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registarUsuario();
            }
        });
        
    }

    private void registarUsuario() {
        name = nombre.getText().toString();
        email = correo.getText().toString();
        password = contrasena.getText().toString();
        
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if(password.length() > 5){

                newUser();

            }
            else{
                Toast.makeText(this, "La contraseña de tener por lo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese sus datos", Toast.LENGTH_SHORT).show();
        }
    }

    void newUser() {
        authum.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("name",name);
                    map.put("email", email);
                    map.put("password", password);

                    String id = authum.getCurrentUser().getUid();

                    database.child("Users").child("Chofer").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                Toast.makeText(UserRegister.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserRegister.this,UserMap.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(UserRegister.this, "No se pudo registrar el usuario.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                else {
                    Toast.makeText(UserRegister.this, "Error: No ha sido posible registrarse", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}