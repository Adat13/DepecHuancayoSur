package com.depec.depechuancayosur;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerInscritosCampamento extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private InscritosAdapter adapter;
    private List<Inscrito> inscritosList;
    private TextView tvTotalInscritos, tvTotalResults;
    private Spinner spinnerFilter, spinnerChurchNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_inscritos_campamento);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        tvTotalInscritos = findViewById(R.id.tvTotalInscritos);
        tvTotalResults = findViewById(R.id.tvTotalResults);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        spinnerChurchNames = findViewById(R.id.spinnerChurchNames);

        inscritosList = new ArrayList<>();
        adapter = new InscritosAdapter(inscritosList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadTotalInscritos();

        // Configura el Spinner de nombres de iglesias
        ArrayAdapter<CharSequence> adapterChurchNames = ArrayAdapter.createFromResource(this,
                R.array.churches_array, android.R.layout.simple_spinner_item);
        adapterChurchNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChurchNames.setAdapter(adapterChurchNames);

        // Configura el listener para spinnerFilter
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Iglesia")) {
                    spinnerChurchNames.setVisibility(View.VISIBLE);
                } else {
                    spinnerChurchNames.setVisibility(View.GONE);
                    updateInscritosList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Configura el listener para spinnerChurchNames
        spinnerChurchNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerFilter.getSelectedItem().toString();
                if (selectedItem.equals("Iglesia")) {
                    updateInscritosList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadTotalInscritos() {
        db.collectionGroup("Inscripciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalInscritos = task.getResult().size();
                            tvTotalInscritos.setText("Total de Inscritos: " + totalInscritos);
                        } else {
                            Toast.makeText(VerInscritosCampamento.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateInscritosList() {
        String selectedItem = spinnerFilter.getSelectedItem().toString();

        if (selectedItem.equals("Todos")) {
            loadInscritos();
        } else if (selectedItem.equals("Iglesia")) {
            String churchName = spinnerChurchNames.getSelectedItem().toString();
            searchByChurch(churchName);
        } else if (selectedItem.equals("Mayores")) {
            searchByAge(true);
        } else if (selectedItem.equals("Menores")) {
            searchByAge(false);
        }
    }

    private void loadInscritos() {
        db.collectionGroup("Inscripciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            inscritosList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Inscrito inscrito = document.toObject(Inscrito.class);
                                inscrito.setUserId(document.getReference().getParent().getParent().getId());
                                inscritosList.add(inscrito);
                            }
                            adapter.notifyDataSetChanged();
                            tvTotalResults.setText("Total de Resultados: " + inscritosList.size());
                        } else {
                            Toast.makeText(VerInscritosCampamento.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchByChurch(String churchName) {
        db.collectionGroup("Inscripciones")
                .whereEqualTo("church", churchName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            inscritosList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Inscrito inscrito = document.toObject(Inscrito.class);
                                inscrito.setUserId(document.getReference().getParent().getParent().getId());
                                inscritosList.add(inscrito);
                            }
                            adapter.notifyDataSetChanged();
                            tvTotalResults.setText("Total de Resultados: " + inscritosList.size());
                        } else {
                            Toast.makeText(VerInscritosCampamento.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchByAge(boolean isAdult) {
        db.collectionGroup("Inscripciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            inscritosList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Inscrito inscrito = document.toObject(Inscrito.class);
                                inscrito.setUserId(document.getReference().getParent().getParent().getId());
                                if (isAdult && inscrito.getEdad() >= 18) {
                                    inscritosList.add(inscrito);
                                } else if (!isAdult && inscrito.getEdad() < 18) {
                                    inscritosList.add(inscrito);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            tvTotalResults.setText("Total de Resultados: " + inscritosList.size());
                        } else {
                            Toast.makeText(VerInscritosCampamento.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
