package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class Uzytkownik_PrzegladMojejWycieczki extends AppCompatActivity {

    private TextView miejsce;
    private TextView data;
    private TextView cena;
    private TextView przewodnik;
    private TextView opis;
    private ImageView zdjecie;

    private String s_miejsce;
    private String s_data;
    int int_cena = 0;
    private String s_przewodnik;
    private String s_opis;

    private String ID;
    private String userID;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownik_przeglad_mojej_wycieczki);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userID = firebaseUser.getUid();

        ID = getIntent().getExtras().get("ID").toString();

        miejsce = findViewById(R.id.textViewMojPrzegladMiejsceUzytkownik);
        data = findViewById(R.id.textViewMojPrzegladDataUzytkownik);
        cena = findViewById(R.id.textViewMojPrzegladCenaUzytkownik);
        przewodnik = findViewById(R.id.textViewMojPrzegladPrzewodnikUzytkownik);
        opis = findViewById(R.id.textViewMojPrzegladOpisUzytkownik);
        zdjecie = findViewById(R.id.imageViewMojPrzegladZdjecieUzytkownik);
        Button anuluj = findViewById(R.id.buttonAnuluj);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID).child("Wycieczki");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    s_miejsce = Objects.requireNonNull(snapshot.child(ID).child("Miejsce").getValue()).toString();
                    s_data = Objects.requireNonNull(snapshot.child(ID).child("Data").getValue()).toString();
                    int_cena = Integer.parseInt(Objects.requireNonNull(snapshot.child(ID).child("Cena").getValue()).toString());
                    s_przewodnik = Objects.requireNonNull(snapshot.child(ID).child("Przewodnik").getValue()).toString();
                    s_opis = Objects.requireNonNull(snapshot.child(ID).child("Opis").getValue()).toString();

                    miejsce.setText("Miejsce: " + s_miejsce);
                    data.setText("Data: " + s_data);
                    cena.setText("Cena: " + Integer.toString(int_cena) + " zl");
                    przewodnik.setText("Przewodnik: " + s_przewodnik);
                    opis.setText("Opis: \n" + s_opis);

                    Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot.child(ID).child("URL").getValue()).toString()).into(zdjecie);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        anuluj.setOnClickListener(v -> anulowanieRezerwacji());
    }

    private void anulowanieRezerwacji() {
        databaseReference.child(ID).removeValue();
        startActivity(new Intent(Uzytkownik_PrzegladMojejWycieczki.this, Uzytkownik_MAIN.class));
        finish();

        Toast.makeText(this, "Wycieczka została anulowana!", Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wycieczki").child(ID).child("Uczestnicy").child(userID);
        databaseReference.removeValue();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Uzytkownik_PrzegladMojejWycieczki.this, Uzytkownik_MojeWycieczki.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}