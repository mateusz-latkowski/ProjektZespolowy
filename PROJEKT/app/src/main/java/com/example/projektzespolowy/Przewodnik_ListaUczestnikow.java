package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class Przewodnik_ListaUczestnikow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przewodnik_lista_uczestnikow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String ID = getIntent().getExtras().get("ID").toString();

        ListView listView = findViewById(R.id.listViewListaUczestnikow);

        Query query = FirebaseDatabase.getInstance().getReference().child("Wycieczki").child(ID).child("Uczestnicy");
        FirebaseListOptions<UzytkownikInfo> uzytkownik = new FirebaseListOptions.Builder<UzytkownikInfo>()
                .setLayout(R.layout.uzytkownik_element)
                .setLifecycleOwner(this)
                .setQuery(query, UzytkownikInfo.class)
                .build();

        FirebaseListAdapter adapter = new FirebaseListAdapter(uzytkownik) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView imie_nazwisko = v.findViewById(R.id.textViewImieNazwisko);
                TextView email = v.findViewById(R.id.textViewEmail);
                TextView telefon = v.findViewById(R.id.textViewTelefon);

                UzytkownikInfo uzytkownikInfo = (UzytkownikInfo) model;
                imie_nazwisko.setText("Imię i nazwisko: " + uzytkownikInfo.getImie() + " " + uzytkownikInfo.getNazwisko());
                email.setText("Email: " + uzytkownikInfo.getEmail());
                telefon.setText("Telefon: " + uzytkownikInfo.getTelefon());
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Przewodnik_ListaUczestnikow.this, Przewodnik_MAIN.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}