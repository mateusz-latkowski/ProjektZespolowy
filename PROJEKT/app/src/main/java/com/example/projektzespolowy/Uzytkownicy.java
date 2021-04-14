package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Uzytkownicy extends AppCompatActivity {

    private Button haslo;

    private String przewodnik_status;
    private String blokada_status;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownicy);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.listViewListaUzytkownikow);
        haslo = findViewById(R.id.buttonZmianaHasla);

        Query query = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");
        FirebaseListOptions<UzytkownikInfo> uzytkownik = new FirebaseListOptions.Builder<UzytkownikInfo>()
                .setLayout(R.layout.activity_uzytkownicy_element)
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

                if (uzytkownikInfo.getPrzewodnik().equals("TAK")) {
                    imie_nazwisko.setTextColor(Color.rgb(131, 200, 0));
                    email.setTextColor(Color.rgb(131, 200, 0));
                    telefon.setTextColor(Color.rgb(131, 200, 0));
                } else {
                    imie_nazwisko.setTextColor(Color.rgb(0, 0, 0));
                    email.setTextColor(Color.rgb(0, 0, 0));
                    telefon.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> edycjaUzytkownika(parent, position));
    }

    private void edycjaUzytkownika(AdapterView<?> parent, int position) {
        UzytkownikInfo uzytkownik = (UzytkownikInfo) parent.getItemAtPosition(position);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(uzytkownik.getID());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Uzytkownicy.this);
                LayoutInflater inflater = Uzytkownicy.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.edycja_uzytkownika, null);

                @SuppressLint("UseSwitchCompatOrMaterialCode") Switch przewodnik = view.findViewById(R.id.switchStatusPrzewodnika);
                @SuppressLint("UseSwitchCompatOrMaterialCode") Switch blokada = view.findViewById(R.id.switchStatusKonta);
                haslo = view.findViewById(R.id.buttonZmianaHasla);

                przewodnik_status = Objects.requireNonNull(snapshot.child("Przewodnik").getValue()).toString();
                blokada_status = Objects.requireNonNull(snapshot.child("Blokada").getValue()).toString();

                przewodnik.setChecked(Objects.requireNonNull(snapshot.child("Przewodnik").getValue()).toString().equals("TAK"));
                blokada.setChecked(Objects.requireNonNull(snapshot.child("Blokada").getValue()).toString().equals("TAK"));

                przewodnik.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        przewodnik_status = "TAK";
                    } else {
                        przewodnik_status = "NIE";
                    }
                });

                blokada.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        blokada_status = "TAK";
                    } else {
                        blokada_status = "NIE";
                    }
                });

                builder.setView(view)
                        .setTitle("Edycja użytkownika")
                        .setNegativeButton("ANULUJ", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("ZATWIERDŹ", (dialog, which) -> {
                            databaseReference.child("Przewodnik").setValue(przewodnik_status);
                            databaseReference.child("Blokada").setValue(blokada_status);
                            Toast.makeText(Uzytkownicy.this, "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        });

                final AlertDialog alert = builder.create();
                alert.show();

                haslo.setOnClickListener(v -> zmianaHasla(uzytkownik.getEmail()));
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void zmianaHasla(String email) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Uzytkownicy.this, "Link został wysłany!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(Uzytkownicy.this, AdminHome.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}