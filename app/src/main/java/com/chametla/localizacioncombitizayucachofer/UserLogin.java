package com.chametla.localizacioncombitizayucachofer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class UserLogin extends AppCompatActivity {

    TextInputEditText TextInputEmail;
    TextInputEditText TextInputPassword;
    Button botonLogin;

    FirebaseAuth mAtuh;
    DatabaseReference mDatabase;

    AlertDialog load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        TextInputEmail = findViewById(R.id.textInputEmail);
        TextInputPassword = findViewById(R.id.textInputPassword);
        botonLogin = findViewById(R.id.bLogin);

        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        load = new SpotsDialog.Builder().setContext(UserLogin.this).setMessage("Cargando").build();

        botonLogin.setOnClickListener(view -> login());
    }

    private void login() {
        String email = TextInputEmail.getText().toString();
        String password = TextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            if (password.length() >= 6){
                load.show();
                mAtuh.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(UserLogin.this, "¡Hola de nuevo!, Bienvenido", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserLogin.this,UserMap.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            }
                        else {
                            Toast.makeText(UserLogin.this, "El correo o contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });
                load.dismiss();
            }
            else{
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese un correo y una contraseña", Toast.LENGTH_SHORT).show();
        }
    }
}