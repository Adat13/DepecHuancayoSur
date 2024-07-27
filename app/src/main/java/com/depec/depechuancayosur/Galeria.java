package com.depec.depechuancayosur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Galeria extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        this.setTitle("Galeria");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Azulfav)));

        // Obtenemos una instancia de Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Carga las imágenes desde Firestore en los ImageViews correspondientes
        cargarImagenesDesdeFirestore();
    }

    private void cargarImagenesDesdeFirestore() {
        for (int i = 1; i <= 10; i++) {
            String imageViewId = "foto" + i;
            int resId = getResources().getIdentifier(imageViewId, "id", getPackageName());
            ImageView imageView = findViewById(resId);

            final int finalI = i;
            db.collection("propaganda")
                    .document("imagen" + i)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String imageUrl = documentSnapshot.getString("url_imagen");
                                Picasso.get().load(imageUrl).into(imageView);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Maneja el error según sea necesario
                            Toast.makeText(Galeria.this, "Error al cargar la imagen " + finalI, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
