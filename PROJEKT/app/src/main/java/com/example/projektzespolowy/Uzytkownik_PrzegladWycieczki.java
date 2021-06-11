package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class Uzytkownik_PrzegladWycieczki extends AppCompatActivity {

    private TextView miejsce;
    private TextView data;
    private TextView cena;
    private TextView przewodnik;
    private TextView opis;
    private ImageView zdjecie;

    private String url;
    private String imie;
    private String nazwisko;
    private String telefon;
    private String konto;

    private String s_miejsce;
    private String s_data;
    int int_cena = 0;
    private String s_przewodnik;
    private String s_opis;

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private String ID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownik_przeglad_wycieczki);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userID = firebaseUser.getUid();

        ID = getIntent().getExtras().get("ID").toString();

        miejsce = findViewById(R.id.textViewPrzegladMiejsceUzytkownik);
        data = findViewById(R.id.textViewPrzegladDataUzytkownik);
        cena = findViewById(R.id.textViewPrzegladCenaUzytkownik);
        przewodnik = findViewById(R.id.textViewPrzegladPrzewodnikUzytkownik);
        opis = findViewById(R.id.textViewPrzegladOpisUzytkownik);
        zdjecie = findViewById(R.id.imageViewPrzegladZdjecieUzytkownik);
        Button rezerwacja = findViewById(R.id.buttonRezerwuj);

        databaseReference.child("Wycieczki").child(ID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                s_miejsce = Objects.requireNonNull(snapshot.child("Miejsce").getValue()).toString();
                s_data = Objects.requireNonNull(snapshot.child("Data").getValue()).toString();
                int_cena = Integer.parseInt(Objects.requireNonNull(snapshot.child("Cena").getValue()).toString());
                s_przewodnik = Objects.requireNonNull(snapshot.child("Przewodnik").getValue()).toString();
                s_opis = Objects.requireNonNull(snapshot.child("Opis").getValue()).toString();

                miejsce.setText("Miejsce: " + s_miejsce);
                data.setText("Data: " + s_data);
                cena.setText("Cena: " + Integer.toString(int_cena) + " zl");
                przewodnik.setText("Przewodnik: " + s_przewodnik);
                opis.setText("Opis: \n" + s_opis);

                Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot.child("URL").getValue()).toString()).into(zdjecie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        rezerwacja.setOnClickListener(v -> rezerwujWycieczke());
    }

    private void rezerwujWycieczke() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.wycieczka_rezerwacja, null);

        databaseReference.child("Wycieczki").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                url = Objects.requireNonNull(snapshot.child("URL").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        databaseReference.child("Uzytkownicy").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imie = Objects.requireNonNull(snapshot.child("Imie").getValue()).toString();
                nazwisko = Objects.requireNonNull(snapshot.child("Nazwisko").getValue()).toString();
                telefon = Objects.requireNonNull(snapshot.child("Telefon").getValue()).toString();
                konto = Objects.requireNonNull(snapshot.child("Stan konta").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        builder.setView(view)
            .setTitle("Zarezerwuj wycieczkę")
            .setNegativeButton("ANULUJ", (dialog, which) -> dialog.cancel())
            .setPositiveButton("ZATWIERDŹ", (dialog, which) -> {

                int stan_konta = Integer.parseInt(konto);

                String generator = generator();

                if (int_cena <= stan_konta) {
                    databaseReference.child("Wycieczki").child(ID).child("Uczestnicy").child(userID).child("Imie").setValue(imie);
                    databaseReference.child("Wycieczki").child(ID).child("Uczestnicy").child(userID).child("Nazwisko").setValue(nazwisko);
                    databaseReference.child("Wycieczki").child(ID).child("Uczestnicy").child(userID).child("Telefon").setValue(telefon);
                    databaseReference.child("Wycieczki").child(ID).child("Uczestnicy").child(userID).child("Email").setValue(firebaseUser.getEmail());

                    databaseReference.child("Uzytkownicy").child(userID).child("Stan konta").setValue(Integer.toString(stan_konta - int_cena));
                    databaseReference.child("Uzytkownicy").child(userID).child("Historia konta").child(generator).child("Opis").setValue("WYCIECZKA - " + s_miejsce + " | KWOTA = " + Integer.toString(int_cena) + " ZŁ");
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("Miejsce").setValue(s_miejsce);
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("Data").setValue(s_data);
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("ID").setValue(ID);
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("Cena").setValue(Integer.toString(int_cena));
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("Przewodnik").setValue(s_przewodnik);
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("Opis").setValue(s_opis);
                    databaseReference.child("Uzytkownicy").child(userID).child("Wycieczki").child(ID).child("URL").setValue(url);

                    Toast.makeText(Uzytkownik_PrzegladWycieczki.this, "Wycieczka została zarezerwowana!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Uzytkownik_PrzegladWycieczki.this, "Brak srodków na koncie!", Toast.LENGTH_SHORT).show();
                }
        });

        builder.create().show();
    }

    private String generator() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder numerTransakcji = new StringBuilder();

        int i = 0;
        while (i < 10) {
            Random random = new Random();
            numerTransakcji.append(characters.charAt(random.nextInt(characters.length())));
            i++;
        }

        return numerTransakcji.toString();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Uzytkownik_PrzegladWycieczki.this, Uzytkownik_ListaWycieczek.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}