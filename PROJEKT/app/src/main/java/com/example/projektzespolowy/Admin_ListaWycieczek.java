package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class Admin_ListaWycieczek extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lista_wycieczek);

        ListView listView = findViewById(R.id.listViewListaWycieczek);

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
                miejsce.setText("Miejsce: " + wycieczkaInfo.getMiejsce());
                cena.setText("Cena: " + wycieczkaInfo.getCena() + " zl");
                data.setText("Data wycieczki: " + wycieczkaInfo.getData());

                Glide.with(getApplicationContext()).load(wycieczkaInfo.getURL()).into(image);

            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent wycieczka1 = new Intent(Admin_ListaWycieczek.this, Admin_PrzegladWycieczki.class);
            WycieczkaInfo wycieczkaInfo = (WycieczkaInfo) parent.getItemAtPosition(position);
            wycieczka1.putExtra("ID", wycieczkaInfo.getID());
            startActivity(wycieczka1);
        });

        Button Wstecz = findViewById(R.id.buttonWsteczListaWycieczek);
        Wstecz.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Admin_MAIN.class));
            finish();
        });
    }
}