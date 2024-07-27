package com.depec.depechuancayosur.ui.devocional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.depec.depechuancayosur.databinding.FragmentDevocionalBinding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DevocionalFragment extends Fragment {

    private FragmentDevocionalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDevocionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        checkAndCreateDevocionalData();
        loadDevotionalData();

        return root;
    }

    private void checkAndCreateDevocionalData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("devocionales").document("defaultDevocional")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (!documentSnapshot.exists()) {
                            createDevocionalData();
                        }
                    }
                });
    }

    private void createDevocionalData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> devotional = new HashMap<>();
        devotional.put("versiculo", "");
        devotional.put("oracion", "");
        devotional.put("reflexion", "");
        devotional.put("imageUrl", ""); // Add imageUrl field

        db.collection("devocionales").document("defaultDevocional")
                .set(devotional)
                .addOnSuccessListener(aVoid -> {
                    // Document was successfully written
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    private void loadDevotionalData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("devocionales").document("defaultDevocional")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String versiculo = documentSnapshot.getString("versiculo");
                            String oracion = documentSnapshot.getString("oracion");
                            String reflexion = documentSnapshot.getString("reflexion");
                            String imageUrl = documentSnapshot.getString("imageUrl");

                            binding.versiculoEditText.setText(versiculo);
                            binding.oracionEditText.setText(oracion);
                            binding.reflexionEditText.setText(reflexion);

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Use Glide to load the image
                                Glide.with(this).load(imageUrl).into(binding.devotionalImageView);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
