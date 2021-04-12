package com.example.projektzespolowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PrzypomnienieHasla extends AppCompatActivity {

    private EditText editTextResetEmail;
    private Button buttonResetHaslo;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przypomnienie_hasla);

        editTextResetEmail = findViewById(R.id.editTextResetEmail);
        buttonResetHaslo = findViewById(R.id.buttonResetPassword);

        buttonResetHaslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editTextResetEmail.getText().toString();

                if (TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(PrzypomnienieHasla.this, "Podaj adres e-mail!", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PrzypomnienieHasla.this, "Link do zmiany hasła został wysłany!", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(PrzypomnienieHasla.this, MainActivity.class));
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}