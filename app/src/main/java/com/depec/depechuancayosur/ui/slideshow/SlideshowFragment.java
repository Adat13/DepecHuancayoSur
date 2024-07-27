package com.depec.depechuancayosur.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.depec.depechuancayosur.R;
import com.google.firebase.auth.FirebaseAuth;

public class SlideshowFragment extends Fragment {

    private Button cerrarsesion;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Inicializar el botón de cerrar sesión
        cerrarsesion = view.findViewById(R.id.cerrarsesion);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Agregar un Listener al botón de cerrar sesión
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión en Firebase
                mAuth.signOut();






            }
        });

        return view;
    }
}