package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        TextView DataGodzina = findViewById(R.id.textViewDataGodzina);
        Button DodajWycieczke = findViewById(R.id.buttonDodaj);
        Button ListaWwycieczek = findViewById(R.id.buttonListaWycieczek);
        Button ListaUzytkownikow = findViewById(R.id.buttonUzytkownicy);
        Button Wyloguj = findViewById(R.id.buttonWyloguj);

        String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        DataGodzina.setText(currentTime);

        DodajWycieczke.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), DodawanieWycieczek.class));
        });

        ListaWwycieczek.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), Wycieczki.class));
        });

        ListaUzytkownikow.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), Uzytkownicy.class));
        });

        Wyloguj.setOnClickListener(v -> {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }
}