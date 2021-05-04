package com.example.projektzespolowy;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Admin_DodawanieWycieczek extends AppCompatActivity {

    private EditText Miejsce;
    private EditText Cena;
    private EditText Data;
    private EditText Opis;
    private EditText Przewodnik;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageURI;
    private static final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dodawanie_wycieczek);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Miejsce = findViewById(R.id.editTextMiejsce);
        Cena = findViewById(R.id.editTextCena);
        Data = findViewById(R.id.editTextDataWycieczki);
        Opis = findViewById(R.id.editTextOpisWycieczki);
        Przewodnik = findViewById(R.id.editTextNazwiskoPrzewodnika);
        Button DodawanieZdjecia = findViewById(R.id.buttonDodajZdjecie);
        Button Zatwierdz = findViewById(R.id.buttonZatwierdzWycieczke);

        storageReference = FirebaseStorage.getInstance().getReference("Images");

        DodawanieZdjecia.setOnClickListener(v -> dodajZdjecie());

        Zatwierdz.setOnClickListener(v -> {
            if (TextUtils.isEmpty(Miejsce.getText().toString().trim()) || TextUtils.isEmpty(Cena.getText().toString().trim()) || TextUtils.isEmpty(Data.getText().toString().trim()) || TextUtils.isEmpty(Opis.getText().toString().trim()) || TextUtils.isEmpty(Przewodnik.getText().toString().trim())) {
                Toast.makeText(Admin_DodawanieWycieczek.this, "Pola nie moga byc puste!", Toast.LENGTH_SHORT).show();
            } else {
                zapiszWyczieczke();
            }
        });
    }

    private void dodajZdjecie() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void zapiszWyczieczke() {
        String ID = generatorID();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wycieczki");
        databaseReference.child(ID).child("Miejsce").setValue(Miejsce.getText().toString().trim());
        databaseReference.child(ID).child("Cena").setValue(Cena.getText().toString().trim());
        databaseReference.child(ID).child("Data").setValue(Data.getText().toString().trim());
        databaseReference.child(ID).child("Opis").setValue(Opis.getText().toString().trim());
        databaseReference.child(ID).child("Przewodnik").setValue(Przewodnik.getText().toString().trim());
        databaseReference.child(ID).child("ID").setValue(ID);

        if (imageURI != null) {
            StorageReference ref = storageReference.child(ID + "." + getExtension(imageURI));
            ref.putFile(imageURI)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String download_url = uri.toString();
                        databaseReference.child(ID).child("URL").setValue(download_url);
                    }));
        }

        Toast.makeText(this, "Wycieczka zostala zapisana!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), Admin_MAIN.class));
        finish();
    }

    private Object getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private String generatorID() {
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
                startActivity(new Intent(Admin_DodawanieWycieczek.this, Admin_MAIN.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean OnCreateOptionsMenu(Menu menu) {
        return true;
    }
}