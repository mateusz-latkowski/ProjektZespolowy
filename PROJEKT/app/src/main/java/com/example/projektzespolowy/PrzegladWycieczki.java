package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class PrzegladWycieczki extends AppCompatActivity {

    private TextView miejsce;
    private TextView data;
    private TextView cena;
    private TextView przewodnik;
    private TextView opis;
    private ImageView zdjecie;

    private EditText edycjaCena;
    private EditText edycjaData;
    private EditText edycjaPrzewodnik;

    private String url;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przeglad_wycieczki);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String ID = getIntent().getExtras().get("ID").toString();

        miejsce = findViewById(R.id.textViewPrzegladMiejsce);
        data = findViewById(R.id.textViewPrzegladData);
        cena = findViewById(R.id.textViewPrzegladCena);
        przewodnik = findViewById(R.id.textViewPrzegladPrzewodnik);
        opis = findViewById(R.id.textViewPrzegladOpis);
        zdjecie = findViewById(R.id.imageViewPrzegladZdjecie);
        Button edycja = findViewById(R.id.buttonEdytuj);
        Button usuwanie = findViewById(R.id.buttonUsun);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wycieczki").child(ID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                miejsce.setText("Miejsce: " + Objects.requireNonNull(snapshot.child("Miejsce").getValue()).toString());
                data.setText("Data: " + Objects.requireNonNull(snapshot.child("Data").getValue()).toString());
                cena.setText("Cena: " + Objects.requireNonNull(snapshot.child("Cena").getValue()).toString() + " zl");
                przewodnik.setText("Przewodnik: " + Objects.requireNonNull(snapshot.child("Przewodnik").getValue()).toString());
                opis.setText("Opis: \n" + Objects.requireNonNull(snapshot.child("Opis").getValue()).toString());

                Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot.child("URL").getValue()).toString()).into(zdjecie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edycja.setOnClickListener(v -> edytujWycieczke(databaseReference));

        usuwanie.setOnClickListener(v -> usunWycieczke(databaseReference));
    }

    private void edytujWycieczke(final DatabaseReference reference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edycja_wycieczki, null);

        edycjaCena = view.findViewById(R.id.editTextEdycjaCena);
        edycjaData = view.findViewById(R.id.editTextEdycjaData);
        edycjaPrzewodnik = view.findViewById(R.id.editTextEdycjaPrzewodnik);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edycjaCena.setText(Objects.requireNonNull(snapshot.child("Cena").getValue()).toString());
                edycjaData.setText(Objects.requireNonNull(snapshot.child("Data").getValue()).toString());
                edycjaPrzewodnik.setText(Objects.requireNonNull(snapshot.child("Przewodnik").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        builder.setView(view)
                .setTitle("Edytuj wycieczkę")
                .setNegativeButton("ANULUJ", (dialog, which) -> dialog.cancel())
                .setPositiveButton("ZATWIERDŹ", (dialog, which) -> {
                    reference.child("Cena").setValue(edycjaCena.getText().toString().trim());
                    reference.child("Data").setValue(edycjaData.getText().toString().trim());
                    reference.child("Przewodnik").setValue(edycjaPrzewodnik.getText().toString().trim());
                    Toast.makeText(PrzegladWycieczki.this, "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
                });

        builder.create().show();
    }

    @SuppressLint("SetTextI18n")
    private void usunWycieczke(DatabaseReference reference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.usuwanie_wycieczki, null);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                url = Objects.requireNonNull(snapshot.child("URL").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        builder.setView(view);
        builder.setTitle("Usuń wycieczkę");
        builder.setNegativeButton("ANULUJ", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("ZATWIERDŹ", (dialog, which) -> {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(url);

            storageReference.delete().addOnSuccessListener(aVoid -> {
                finish();
                reference.removeValue();
                startActivity(new Intent(getApplicationContext(), Wycieczki.class));
            });
        });

        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(PrzegladWycieczki.this, Wycieczki.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}