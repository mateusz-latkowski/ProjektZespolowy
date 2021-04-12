package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Wycieczki extends AppCompatActivity {

    private ListView listView;
    private FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wycieczki);

        listView = findViewById(R.id.listViewListaWycieczek);

        Query query = FirebaseDatabase.getInstance().getReference().child("Wycieczki");
        FirebaseListOptions<WycieczkaInfo> wycieczka = new FirebaseListOptions.Builder<WycieczkaInfo>()
                .setLayout(R.layout.activity_wycieczki_element)
                .setLifecycleOwner(this)
                .setQuery(query, WycieczkaInfo.class)
                .build();

        adapter = new FirebaseListAdapter(wycieczka) {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent wycieczka = new Intent(Wycieczki.this, PrzegladWycieczki.class);
                WycieczkaInfo wycieczkaInfo = (WycieczkaInfo) parent.getItemAtPosition(position);
                wycieczka.putExtra("ID", wycieczkaInfo.getID());
                startActivity(wycieczka);
            }
        });
    }
}