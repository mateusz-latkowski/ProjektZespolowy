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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Random;

public class DodawanieWycieczek extends AppCompatActivity {

    private TextView tytul;
    private EditText Miejsce;
    private EditText Cena;
    private EditText Data;
    private EditText Opis;
    private EditText Przewodnik;
    private Button DodawanieZdjecia;
    private Button Zatwierdz;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageURI;
    private static final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodawanie_wycieczek);

        tytul = findViewById(R.id.textViewTitle);
        Miejsce = findViewById(R.id.editTextMiejsce);
        Cena = findViewById(R.id.editTextCena);
        Data = findViewById(R.id.editTextDataWycieczki);
        Opis = findViewById(R.id.editTextOpisWycieczki);
        Przewodnik = findViewById(R.id.editTextNazwiskoPrzewodnika);
        DodawanieZdjecia = findViewById(R.id.buttonDodajZdjecie);
        Zatwierdz = findViewById(R.id.buttonZatwierdzWycieczke);

        storageReference = FirebaseStorage.getInstance().getReference("Images");

        DodawanieZdjecia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajZdjecie();
            }
        });

        Zatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Miejsce.getText().toString().trim()) || TextUtils.isEmpty(Cena.getText().toString().trim()) || TextUtils.isEmpty(Data.getText().toString().trim()) || TextUtils.isEmpty(Opis.getText().toString().trim()) || TextUtils.isEmpty(Przewodnik.getText().toString().trim())) {
                    Toast.makeText(DodawanieWycieczek.this, "Pola nie moga byc puste!", Toast.LENGTH_SHORT).show();
                } else {
                    ZapiszWyczieczke();
                }
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

    private void ZapiszWyczieczke() {
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
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_url = uri.toString();
                                    databaseReference.child(ID).child("URL").setValue(download_url);
                                }
                            });
                        }
                    });
        }

        Toast.makeText(this, "Wycieczka zostala zapisana!", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(getApplicationContext(), AdminHome.class));
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
}