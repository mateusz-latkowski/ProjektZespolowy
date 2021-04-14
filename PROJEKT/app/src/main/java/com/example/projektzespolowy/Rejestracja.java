package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Rejestracja extends AppCompatActivity {

    private EditText editTextEmail, editTextHaslo, editTextImie, editTextNazwisko, editTextTelefon;
    private Button buttonZarejestruj;
    private TextView textviewLogowanie;

    private String email;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);

        editTextEmail = findViewById(R.id.editTextMailRegistration);
        editTextHaslo = findViewById(R.id.editTextPasswordRegistration);
        editTextImie = findViewById(R.id.editTextName);
        editTextNazwisko = findViewById(R.id.editTextSurname);
        editTextTelefon = findViewById(R.id.editTextPhoneNumber);

        buttonZarejestruj = findViewById(R.id.buttonRegistration);
        textviewLogowanie = findViewById(R.id.textViewSignUp);

        buttonZarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejestracja();
            }
        });

        textviewLogowanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Rejestracja.this, MainActivity.class));
            }
        });
    }

    private void rejestracja() {
        email = editTextEmail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Wpisz adres e-mail!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(haslo)) {
            Toast.makeText(this, "Wpisz hasło!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, haslo)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            zapiszDaneUzytkownika();
                            Toast.makeText(Rejestracja.this, "Użytkownik został zarejestrowany!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Rejestracja.this, "Błąd rejestracji, spróbuj ponownie!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void zapiszDaneUzytkownika() {

        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");

        String imie = editTextImie.getText().toString().trim();
        String nazwisko = editTextNazwisko.getText().toString().trim();
        String telefon = editTextTelefon.getText().toString().trim();

        databaseReference.child(userID).child("Imie").setValue(imie);
        databaseReference.child(userID).child("Nazwisko").setValue(nazwisko);
        databaseReference.child(userID).child("Telefon").setValue(telefon);
        databaseReference.child(userID).child("Email").setValue(email);
        databaseReference.child(userID).child("Przewodnik").setValue("NIE");
        databaseReference.child(userID).child("Blokada").setValue("NIE");
        databaseReference.child(userID).child("ID").setValue(userID);
        databaseReference.child(userID).child("Stan konta").setValue("0");
    }
}