package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Przewodnik_MAIN extends AppCompatActivity {

    private String imie;
    private String nazwisko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przewodnik_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        Button wyloguj = findViewById(R.id.buttonPrzewodnikWyloguj);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imie = Objects.requireNonNull(snapshot.child("Imie").getValue()).toString();
                nazwisko = Objects.requireNonNull(snapshot.child("Nazwisko").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        listaWycieczek();

        wyloguj.setOnClickListener(c -> {
            firebaseAuth.signOut();
            startActivity(new Intent(Przewodnik_MAIN.this, MainActivity.class));
            finish();
        });
    }

    private void listaWycieczek() {
        ListView listView = findViewById(R.id.listViewListaWycieczekPrzewodnika);

        Query query = FirebaseDatabase.getInstance().getReference().child("Wycieczki");
        FirebaseListOptions<WycieczkaInfo> wycieczka = new FirebaseListOptions.Builder<WycieczkaInfo>()
                .setLayout(R.layout.wycieczka_element)
                .setLifecycleOwner(this)
                .setQuery(query, WycieczkaInfo.class)
                .build();

        FirebaseListAdapter adapter = new FirebaseListAdapter(wycieczka) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView miejsce = v.findViewById(R.id.textViewMiejsce);
                TextView cena = v.findViewById(R.id.textViewCena);
                TextView data = v.findViewById(R.id.textViewData);
                ImageView image = v.findViewById(R.id.imageMiejsce);

                WycieczkaInfo wycieczkaInfo = (WycieczkaInfo) model;
                if (wycieczkaInfo.getPrzewodnik().equals(imie + " " + nazwisko)) {
                    miejsce.setTextColor(Color.rgb(131, 200, 0));
                    cena.setTextColor(Color.rgb(131, 200, 0));
                    data.setTextColor(Color.rgb(131, 200, 0));

                    miejsce.setText("Miejsce: " + wycieczkaInfo.getMiejsce());
                    cena.setText("Cena: " + wycieczkaInfo.getCena() + " zl");
                    data.setText("Data wycieczki: " + wycieczkaInfo.getData());

                    Glide.with(getApplicationContext()).load(wycieczkaInfo.getURL()).into(image);
                } else {
                    miejsce.setText("Miejsce: " + wycieczkaInfo.getMiejsce());
                    cena.setText("Cena: " + wycieczkaInfo.getCena() + " zl");
                    data.setText("Data wycieczki: " + wycieczkaInfo.getData());

                    Glide.with(getApplicationContext()).load(wycieczkaInfo.getURL()).into(image);
                }
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Przewodnik_MAIN.this, Przewodnik_ListaUczestnikow.class);
            WycieczkaInfo wycieczkaInfo = (WycieczkaInfo) parent.getItemAtPosition(position);
            intent.putExtra("ID", wycieczkaInfo.getID());
            startActivity(intent);
            finish();
        });
    }
}