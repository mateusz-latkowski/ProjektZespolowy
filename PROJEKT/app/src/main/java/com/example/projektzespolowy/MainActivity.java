package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextHaslo;
    private Button buttonZaloguj;
    private TextView textViewRejestracja, textViewPrzypomnienieHasla;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextHaslo = findViewById(R.id.editTextPasswordLogin);
        buttonZaloguj = findViewById(R.id.buttonSignIn);
        textViewRejestracja = findViewById(R.id.textViewSignIn);
        textViewPrzypomnienieHasla = findViewById(R.id.textViewRecoveryPass);

        buttonZaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logowanie();
            }
        });

        textViewRejestracja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rejestracja.class));
            }
        });

        textViewPrzypomnienieHasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PrzypomnienieHasla.class));
            }
        });

    }


    private void logowanie() {
        String email = editTextEmail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Wpisz adres e-mail!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(haslo)) {
            Toast.makeText(this, "Wpisz haslo!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, haslo)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
//                            startActivity(new Intent(getApplicationContext(), Home.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Niepoprawny adres e-mail lub hasło, spróbuj ponownie!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}