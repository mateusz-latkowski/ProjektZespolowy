package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminHome extends AppCompatActivity {

    private TextView DataGodzina;
    private Button DodajWycieczke;
    private Button ListaWwycieczek;
    private Button ListaUzytkownikow;
    private Button Wyloguj;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        firebaseAuth = FirebaseAuth.getInstance();

        DataGodzina = findViewById(R.id.textViewDataGodzina);
        DodajWycieczke = findViewById(R.id.buttonDodaj);
        ListaWwycieczek = findViewById(R.id.buttonListaWycieczek);
        ListaUzytkownikow = findViewById(R.id.buttonUzytkownicy);
        Wyloguj = findViewById(R.id.buttonWyloguj);

        String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        DataGodzina.setText(currentTime);

        DodajWycieczke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), DodawanieWycieczek.class));
            }
        });

        ListaWwycieczek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Wycieczki.class));
            }
        });

        ListaUzytkownikow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Uzytkownicy.class));
            }
        });

        Wyloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class))
                ;
            }
        });




    }
}