package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Uzytkownicy extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch przewodnik;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch blokada;

    private Button haslo;

    private String mail;
    private String przewodnik_status;
    private String blokada_status;
    private DatabaseReference databaseReference;
    private FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uzytkownicy);

        ListView listView = findViewById(R.id.listViewListaUzytkownikow);
        haslo = findViewById(R.id.buttonZmianaHasla);

        Query query = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");
        FirebaseListOptions<UzytkownikInfo> uzytkownik = new FirebaseListOptions.Builder<UzytkownikInfo>()
                .setLayout(R.layout.activity_uzytkownicy_element)
                .setLifecycleOwner(this)
                .setQuery(query, UzytkownikInfo.class)
                .build();

        adapter = new FirebaseListAdapter(uzytkownik) {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edycjaUzytkownika(parent, position);
            }
        });
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

                przewodnik = view.findViewById(R.id.switchStatusPrzewodnika);
                blokada = view.findViewById(R.id.switchStatusKonta);
                haslo = view.findViewById(R.id.buttonZmianaHasla);

                przewodnik_status = snapshot.child("Przewodnik").getValue().toString();
                blokada_status = snapshot.child("Blokada").getValue().toString();

                przewodnik.setChecked(snapshot.child("Przewodnik").getValue().toString().equals("TAK"));
                blokada.setChecked(snapshot.child("Blokada").getValue().toString().equals("TAK"));

                przewodnik.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            przewodnik_status = "TAK";
                        } else {
                            przewodnik_status = "NIE";
                        }
                    }
                });

                blokada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            przewodnik_status = "TAK";
                        } else {
                            blokada_status = "NIE";
                        }
                    }
                });

                builder.setView(view)
                        .setTitle("Edycja użytkownika")
                        .setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("ZATWIERDŹ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child("Przewodnik").setValue(przewodnik_status);
                                databaseReference.child("Blokada").setValue(blokada_status);
                                Toast.makeText(Uzytkownicy.this, "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();

                haslo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zmianaHasla(uzytkownik.getEmail());
                    }
                });
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void zmianaHasla(String email) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Uzytkownicy.this, "Link został wysłany!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}