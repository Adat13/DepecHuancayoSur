package com.depec.depechuancayosur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public class MenoresActivity extends AppCompatActivity {
    private FirebaseFirestore mfirestore;
    private Spinner spinner;
    private StorageReference mStorageRef;

    Button enviardatos;
    TextView nombresins;
    TextView numerocelularins;
    TextView edadins;
    TextView numerodniins;
    TextView secopcionalins;
    TextView tutorins;
    TextView cantidadpagoins;



    private ImageView boleta;
    private ImageView autorizacion;
    private Uri imageUri;
    private Uri autorizacionUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_AUTORIZACION_REQUEST = 2;
    private boolean botonHabilitado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menores);

        this.setTitle("PONLE ACTITUD - 2023");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));

        mfirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        spinner = findViewById(R.id.secins);

        nombresins = findViewById(R.id.nombresins);
        tutorins = findViewById(R.id.apoderado);
        numerocelularins = findViewById(R.id.numerocelular);
        edadins = findViewById(R.id.edadins);
        numerodniins = findViewById(R.id.numerodniins);
        enviardatos = findViewById(R.id.enviardatos);
        secopcionalins = findViewById(R.id.secopcional);

        boleta = findViewById(R.id.boleta);
        cantidadpagoins = findViewById(R.id.cantidadpago);
        autorizacion = findViewById(R.id.autorizacion);



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

        autorizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutorizacionChooser();
            }
        });

        enviardatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonHabilitado) { // Verifica si el botón está habilitado
                    botonHabilitado = false; // Deshabilita el botón

                    String nombres = nombresins.getText().toString().trim();
                    String numerocelular = numerocelularins.getText().toString().trim();
                    String apoderado = tutorins.getText().toString().trim();
                    String edad = edadins.getText().toString().trim();
                    String DNI = numerodniins.getText().toString().trim();
                    String opcion = spinner.getSelectedItem().toString();
                    String secopcional = secopcionalins.getText().toString().trim();
                    String cantidadpago = cantidadpagoins.getText().toString().trim();

                    if (nombres.isEmpty() || numerocelular.isEmpty() || apoderado.isEmpty() || edad.isEmpty() || DNI.isEmpty() || opcion.isEmpty()  || cantidadpago.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingresar todos los datos", Toast.LENGTH_SHORT).show();
                        botonHabilitado = true; // Habilita el botón nuevamente
                    } else {
                        if (imageUri != null) {
                            uploadFile(nombres, numerocelular,apoderado, edad, DNI, opcion, secopcional, cantidadpago );
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

    private void openAutorizacionChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_AUTORIZACION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(boleta);
        } else if (requestCode == PICK_AUTORIZACION_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            autorizacionUri = data.getData();
            Glide.with(this).load(autorizacionUri).into(autorizacion);
        }
    }

    private void uploadFile(String nombres, String numerocelular, String apoderado, String edad, String dni, String opcion, String secopcional, String cantidadpago) {
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
                                    uploadAutorizacion(nombres, numerocelular, apoderado, edad,  dni, opcion, secopcional, cantidadpago, imageUrl);
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

    private void uploadAutorizacion(String nombres, String numerocelular, String apoderado,  String edad, String dni, String opcion,String secopcional, String cantidadpago, String imageUrl) {
        if (autorizacionUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "_autorizacion." + getFileExtension(autorizacionUri));

            fileReference.putFile(autorizacionUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String autorizacionUrl = uri.toString();
                                    postUser(nombres, numerocelular, apoderado, edad,  dni, opcion, secopcional, cantidadpago, imageUrl,  autorizacionUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error al subir la autorización", Toast.LENGTH_SHORT).show();
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

    private void postUser(String nombres, String numerocelular, String apoderado , String edad, String dni, String opcion, String secopcional, String cantidadpago, String imageUrl, String autorizacionUrl  ) {
        Map<String, Object> map = new HashMap<>();

        map.put("Autorización",autorizacionUrl);
        map.put("Nombres", nombres);
        map.put("Celular", numerocelular);
        map.put("TUTOR", apoderado);
        map.put("Edad",edad);
        map.put("DNI", dni);
        map.put("Sec", opcion);
        map.put("OTRA SEC", secopcional);
        map.put("Cantidad", cantidadpago);
        map.put("Boleta", imageUrl);




        mfirestore.collection("menores").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Felicidades sus datos fueron enviados", Toast.LENGTH_SHORT).show();
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