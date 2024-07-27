package com.depec.depechuancayosur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Polos extends AppCompatActivity {
    private FirebaseFirestore mfirestore;
    private Spinner spinner;
    private StorageReference mStorageRef;

    Button enviardatos;
    TextView nombresins;
    TextView tallains;
    TextView otrasecins;

    private ImageView boleta;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private boolean botonHabilitado = true; // Variable para controlar si el botón está habilitado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polos);

        this.setTitle("PONLE ACTITUD - 2023");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));

        mfirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        spinner = findViewById(R.id.secins);
        nombresins = findViewById(R.id.nombresins);
        tallains = findViewById(R.id.talla);
        otrasecins = findViewById(R.id.otrasec);
        enviardatos = findViewById(R.id.enviardatos);
        boleta = findViewById(R.id.boleta);

        // Define las opciones del Spinner
        String[] opciones = {"Elije tu SEC", "Sec Huamanmarca", "Sec Puzo", "Sec Leoncio Prado",
                "Sec Viques", "Sec Auquimarca", "Sec La Punta","Sec Pichanaki", "Sec Huayucachi", "Sec Arterial","Otra"};

        // Crea el adaptador para las opciones
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spiner, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establece el adaptador en el Spinner
        spinner.setAdapter(adapter);

        boleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        enviardatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonHabilitado) { // Verifica si el botón está habilitado
                    botonHabilitado = false; // Deshabilita el botón

                    String nombres = nombresins.getText().toString().trim();
                    String opcion = spinner.getSelectedItem().toString();
                    String otrasec = otrasecins.getText().toString().trim();
                    String talla = tallains.getText().toString().trim();

                    if (nombres.isEmpty() || opcion.isEmpty() || talla.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingresar todos los datos", Toast.LENGTH_SHORT).show();
                        botonHabilitado = true; // Habilita el botón nuevamente
                    } else {
                        if (imageUri != null) {
                            uploadFile(nombres, opcion, otrasec, talla);
                        } else {
                            Toast.makeText(getApplicationContext(), "Seleccionar una imagen", Toast.LENGTH_SHORT).show();
                            botonHabilitado = true; // Habilita el botón nuevamente
                        }
                    }
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(boleta);
        }
    }

    private void uploadFile(String nombres, String opcion, String otrasec, String talla) {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    postUser(nombres, opcion, otrasec, talla, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                            botonHabilitado = true; // Habilita el botón nuevamente
                        }
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void postUser(String nombres, String opcion, String otrasec, String talla, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("Nombres", nombres);
        map.put("Sec", opcion);
        map.put("Otra SEC", otrasec);
        map.put("ImageUrl", imageUrl);
        map.put("Talla", talla);

        mfirestore.collection("Polos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Sus datos fueron enviados", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al inscribirse", Toast.LENGTH_SHORT).show();
                botonHabilitado = true; // Habilita el botón nuevamente
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
