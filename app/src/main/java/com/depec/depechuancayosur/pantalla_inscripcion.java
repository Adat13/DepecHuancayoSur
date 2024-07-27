package com.depec.depechuancayosur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class pantalla_inscripcion extends AppCompatActivity {

    ImageButton btn_ins;
    ImageButton btn_menores;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inscripcion);

        this.setTitle("INSCRIPCIÓN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));



        btn_ins = findViewById(R.id.btn_ins2);



        btn_menores = findViewById(R.id.btn_menores2);

        btn_menores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad deseada
                Intent intent = new Intent(pantalla_inscripcion.this, MenoresActivity.class);
                startActivity(intent);
            }

        });



    }
}