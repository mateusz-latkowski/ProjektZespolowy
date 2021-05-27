package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

public class Uzytkownik_Konto extends AppCompatActivity {

    private String stan_konta;
    private EditText kwota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownik_konto);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView konto = findViewById(R.id.textViewStanKonta);
        kwota = findViewById(R.id.editTextKwota);
        Button wplac = findViewById(R.id.buttonWplac);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stan_konta = Objects.requireNonNull(snapshot.child("Stan konta").getValue()).toString();
                konto.setText("Stan konta: " + stan_konta + " zł");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        historia(firebaseUser.getUid());

        wplac.setOnClickListener(v -> {
            if (TextUtils.isEmpty(kwota.getText().toString())) {
                Toast.makeText(Uzytkownik_Konto.this, "Podaj kwotę!", Toast.LENGTH_SHORT).show();
            } else {
                wplacKwote(databaseReference);
            }
        });
    }

    private void historia(String ID) {
        ListView listView = findViewById(R.id.listViewHistoria);

        Query query = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(ID).child("Historia konta");
        FirebaseListOptions<HistoriaKontaInfo> historia = new FirebaseListOptions.Builder<HistoriaKontaInfo>()
                .setLayout(R.layout.historia_element)
                .setLifecycleOwner(this)
                .setQuery(query, HistoriaKontaInfo.class)
                .build();

        FirebaseListAdapter adapter = new FirebaseListAdapter(historia) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView historia = v.findViewById(R.id.textViewHistoria);

                HistoriaKontaInfo historiaKontaInfo = (HistoriaKontaInfo) model;
                historia.setText(historiaKontaInfo.getOpis());
            }
        };

        listView.setAdapter(adapter);
    }

    private void wplacKwote(DatabaseReference databaseReference) {
        String ID_transakcji = generator();

        databaseReference.child("Stan konta").setValue(Integer.parseInt(kwota.getText().toString()) + Integer.parseInt(stan_konta));
        databaseReference.child("Historia konta").child(ID_transakcji).child("Opis").setValue("WPŁATA = " + kwota.getText().toString() + " ZŁ");
        Toast.makeText(Uzytkownik_Konto.this, "Kwota została wpłacona!", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(Uzytkownik_Konto.this, Uzytkownik_MAIN.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}