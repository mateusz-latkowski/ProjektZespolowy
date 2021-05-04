package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextHaslo;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextHaslo = findViewById(R.id.editTextPasswordLogin);
        Button buttonZaloguj = findViewById(R.id.buttonSignIn);
        TextView textViewRejestracja = findViewById(R.id.textViewSignIn);
        TextView textViewPrzypomnienieHasla = findViewById(R.id.textViewRecoveryPass);

        buttonZaloguj.setOnClickListener(v -> logowanie());

        textViewRejestracja.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Rejestracja.class)));

        textViewPrzypomnienieHasla.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PrzypomnienieHasla.class)));
    }


    private void logowanie() {
        String email = editTextEmail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Wpisz adres e-mail!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(haslo)) {
            Toast.makeText(this, "Wpisz haslo!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, haslo)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        rolaUzytkownika(email, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                    } else {
                        Toast.makeText(MainActivity.this, "Niepoprawny adres e-mail lub hasło, spróbuj ponownie!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void rolaUzytkownika(String email, String ID) {
        if (email.contains("admin@admin.com")) {
            startActivity(new Intent(this, Admin_MAIN.class));
            finish();
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (Objects.requireNonNull(snapshot.child(ID).child("Blokada").getValue()).toString().contains("TAK")) {
                        firebaseAuth.signOut();
                        Toast.makeText(MainActivity.this, "Twoje konto jest zablokowane!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Objects.requireNonNull(snapshot.child(ID).child("Przewodnik").getValue()).toString().contains("TAK")) {
                            startActivity(new Intent(MainActivity.this, Przewodnik_MAIN.class));
                            finish();
                        } else {
                            startActivity(new Intent(MainActivity.this, Uzytkownik_MAIN.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}