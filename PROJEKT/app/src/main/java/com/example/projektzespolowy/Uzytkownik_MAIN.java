package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Uzytkownik_MAIN extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownik_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Button buttonWycieczki = findViewById(R.id.buttonWycieczki);
        Button buttonMojeWycieczki = findViewById(R.id.buttonMojeWycieczki);
        Button buttonKonto = findViewById(R.id.buttonKonto);
        Button buttonWyloguj = findViewById(R.id.buttonWylogowanie);

        buttonWycieczki.setOnClickListener(v -> {
            startActivity(new Intent(Uzytkownik_MAIN.this, Uzytkownik_ListaWycieczek.class));
            finish();
        });

        buttonMojeWycieczki.setOnClickListener(v -> {
            startActivity(new Intent(Uzytkownik_MAIN.this, Uzytkownik_MojeWycieczki.class));
            finish();
        });

        buttonKonto.setOnClickListener(v -> {
            startActivity(new Intent(Uzytkownik_MAIN.this, Uzytkownik_Konto.class));
            finish();
        });

        buttonWyloguj.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(Uzytkownik_MAIN.this, MainActivity.class));
            finish();
        });
    }
}