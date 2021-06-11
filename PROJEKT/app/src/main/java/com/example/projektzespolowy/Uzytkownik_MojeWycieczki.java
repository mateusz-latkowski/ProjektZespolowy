package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Uzytkownik_MojeWycieczki extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownik_moje_wycieczki);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!Objects.equals(snapshot.child("Wycieczki").getValue(), "0")) {
                    wyswietlWycieczki(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void wyswietlWycieczki(String userID) {
        ListView listView = findViewById(R.id.listViewListaWykupionychWycieczek);

        Query query = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID).child("Wycieczki");
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
                miejsce.setText("Miejsce: " + wycieczkaInfo.getMiejsce());
                cena.setText("Cena: " + wycieczkaInfo.getCena() + " zl");
                data.setText("Data wycieczki: " + wycieczkaInfo.getData());

                Glide.with(getApplicationContext()).load(wycieczkaInfo.getURL()).into(image);
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Uzytkownik_MojeWycieczki.this, Uzytkownik_PrzegladMojejWycieczki.class);
            WycieczkaInfo wycieczkaInfo = (WycieczkaInfo) parent.getItemAtPosition(position);
            intent.putExtra("ID", wycieczkaInfo.getID());
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Uzytkownik_MojeWycieczki.this, Uzytkownik_MAIN.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}