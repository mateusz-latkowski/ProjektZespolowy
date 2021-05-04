package com.example.projektzespolowy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PrzypomnienieHasla extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przypomnienie_hasla);

        EditText editTextResetEmail = findViewById(R.id.editTextResetEmail);
        Button buttonResetHaslo = findViewById(R.id.buttonResetPassword);

        buttonResetHaslo.setOnClickListener(v -> {
            String userEmail = editTextResetEmail.getText().toString();

            if (TextUtils.isEmpty(userEmail))
            {
                Toast.makeText(PrzypomnienieHasla.this, "Podaj adres e-mail!", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PrzypomnienieHasla.this, "Link do zmiany hasła został wysłany!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PrzypomnienieHasla.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}