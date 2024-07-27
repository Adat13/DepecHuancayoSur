package com.depec.depechuancayosur;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class adminMisInscritos extends AppCompatActivity {

    private ListView listViewMisInscritos;
    private List<Inscrito> inscritosList = new ArrayList<>();
    private InscritoAdapter adapter;
    private TextView textViewTotalInReview;
    private TextView textViewTotalVerified;
    private Set<String> reviewedItems = new HashSet<>(); // Para controlar los elementos ya revisados

    // Variable para almacenar la URI de la imagen seleccionada
    private Uri selectedImageUri;
    private AlertDialog uploadDialog;

    private ImageView dialogImageViewPreview;

    private static final int PICK_IMAGE_REQUEST = 1;
    private double totalInReview = 0;
    private double totalVerified = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_mis_inscritos);
        this.setTitle("Mis Inscritos");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Establecer el color de fondo de la barra de acción
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorActionBar)));
        }

        listViewMisInscritos = findViewById(R.id.listViewMisInscritos);
        textViewTotalInReview = findViewById(R.id.textViewTotalInReview);
        textViewTotalVerified = findViewById(R.id.textViewTotalVerified);

        listViewMisInscritos = findViewById(R.id.listViewMisInscritos);
        adapter = new InscritoAdapter(this, inscritosList);
        listViewMisInscritos.setAdapter(adapter);

        Spinner spinnerFiltro = findViewById(R.id.spinnerFiltro);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.filtro_opciones, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(spinnerAdapter);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filtro = parent.getItemAtPosition(position).toString();
                applyFilter(filtro);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collectionGroup("Inscripciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Inscrito inscrito = document.toObject(Inscrito.class);
                            inscrito.setUserId(document.getReference().getParent().getParent().getId());
                            inscritosList.add(inscrito);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(adminMisInscritos.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void applyFilter(String filtro) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query inscripcionesRef = db.collectionGroup("Inscripciones");

        switch (filtro) {
            case "En orden alfabético":
                inscripcionesRef.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                Collections.sort(documents, (doc1, doc2) -> {
                                    String name1 = doc1.getString("name").toLowerCase();
                                    String name2 = doc2.getString("name").toLowerCase();
                                    return name1.compareTo(name2);
                                });

                                // Pasar la lista ordenada a una nueva versión de handleQueryResult
                                handleQueryResult(documents, filtro);
                            } else {
                                // Manejar error
                                Log.e("FirestoreError", "Error obteniendo documentos: ", task.getException());
                            }
                        });
                break;




            case "En revisión":
                inscripcionesRef
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                inscritosList.clear(); // Limpia la lista de inscritos antes de agregar nuevos resultados
                                List<Task<List<DocumentSnapshot>>> allTasks = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Inscrito inscrito = document.toObject(Inscrito.class);
                                    inscrito.setUserId(document.getReference().getParent().getParent().getId());

                                    // Siempre revisamos los vauchers, incluso si inscrito.verified es false
                                    String name = inscrito.getName();
                                    Task<DocumentSnapshot> task1 = db.collection("vauchers_cuotas").document(name + "_firstInstallment").get();
                                    Task<DocumentSnapshot> task2 = db.collection("vauchers_cuotas").document(name + "_secondInstallment").get();
                                    Task<DocumentSnapshot> task3 = db.collection("vauchers_cuotas").document(name + "_thirdInstallment").get();

                                    Task<List<DocumentSnapshot>> allVoucherTasks = Tasks.whenAllSuccess(task1, task2, task3);
                                    allTasks.add(allVoucherTasks);

                                    allVoucherTasks.addOnSuccessListener(vouchers -> {
                                        boolean hasUnverifiedVoucher = false;
                                        for (DocumentSnapshot voucher : vouchers) {
                                            if (voucher.exists() && voucher.getBoolean("verified") != null && !voucher.getBoolean("verified")) {
                                                hasUnverifiedVoucher = true;
                                                break;
                                            }
                                        }

                                        // Añadir a la lista si inscrito.verified es false o si hay un voucher no verificado
                                        if (!inscrito.isVerified() || hasUnverifiedVoucher) {
                                            inscritosList.add(inscrito);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(adminMisInscritos.this, "Error verificando vauchers.", Toast.LENGTH_SHORT).show();
                                    });
                                }

                                // Esperar a que todas las tareas de vauchers terminen
                                Tasks.whenAllComplete(allTasks).addOnCompleteListener(voucherTask -> {
                                    // Opcionalmente puedes hacer algo cuando todas las verificaciones de vauchers hayan terminado
                                });
                            } else {
                                Toast.makeText(adminMisInscritos.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;



            case "Verificados completamente":
                inscripcionesRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        inscritosList.clear(); // Limpia la lista de inscritos antes de agregar nuevos resultados

                        List<Task<?>> allTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Inscrito inscrito = document.toObject(Inscrito.class);
                            inscrito.setUserId(document.getReference().getParent().getParent().getId());

                            String name = inscrito.getName();
                            List<Task<DocumentSnapshot>> voucherTasks = new ArrayList<>();

                            if (!inscrito.getFirstInstallment().isEmpty()) {
                                voucherTasks.add(db.collection("vauchers_cuotas").document(name + "_firstInstallment").get());
                            }
                            if (!inscrito.getSecondInstallment().isEmpty()) {
                                voucherTasks.add(db.collection("vauchers_cuotas").document(name + "_secondInstallment").get());
                            }
                            if (!inscrito.getThirdInstallment().isEmpty()) {
                                voucherTasks.add(db.collection("vauchers_cuotas").document(name + "_thirdInstallment").get());
                            }

                            Task<List<DocumentSnapshot>> allVoucherTasks = Tasks.whenAllSuccess(voucherTasks);
                            allVoucherTasks.addOnSuccessListener(voucherDocuments -> {
                                boolean allVouchersVerified = true;
                                for (DocumentSnapshot voucherDoc : voucherDocuments) {
                                    if (voucherDoc.exists()) {
                                        Boolean verified = voucherDoc.getBoolean("verified");
                                        if (verified == null || !verified) {
                                            allVouchersVerified = false;
                                            break;
                                        }
                                    } else {
                                        allVouchersVerified = false;
                                        break;
                                    }
                                }

                                if (inscrito.isVerified() && allVouchersVerified) {
                                    inscritosList.add(inscrito);
                                }

                                // Notificar al adaptador después de procesar todos los documentos
                                if (allTasks.size() == task.getResult().size()) {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            allTasks.add(allVoucherTasks);
                        }

                        // Notificar al adaptador después de que todas las tareas hayan sido añadidas
                        if (allTasks.isEmpty()) {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(adminMisInscritos.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;




        }
    }
    private void handleQueryResultado(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            inscritosList.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Inscrito inscrito = document.toObject(Inscrito.class);
                inscrito.setUserId(document.getReference().getParent().getParent().getId());
                inscritosList.add(inscrito);
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(adminMisInscritos.this, "Error obteniendo documentos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleQueryResult(List<DocumentSnapshot> documents, String filtro) {
        inscritosList.clear();
        List<Task<Inscrito>> voucherTasks = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Inscrito inscrito = document.toObject(Inscrito.class);
            inscrito.setUserId(document.getReference().getParent().getParent().getId());

            voucherTasks.add(checkVoucher(inscrito, filtro));
        }

        Tasks.whenAllComplete(voucherTasks).addOnCompleteListener(voucherTask -> {
            for (Task<Inscrito> voucherTaskResult : voucherTasks) {
                if (voucherTaskResult.isSuccessful()) {
                    Inscrito inscrito = voucherTaskResult.getResult();
                    if (inscrito != null) {
                        inscritosList.add(inscrito);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }


    private Task<Inscrito> checkVoucher(Inscrito inscrito, String filtro) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String name = inscrito.getName();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        tasks.add(db.collection("vauchers_cuotas").document(name + "_firstInstallment").get());
        tasks.add(db.collection("vauchers_cuotas").document(name + "_secondInstallment").get());
        tasks.add(db.collection("vauchers_cuotas").document(name + "_thirdInstallment").get());

        return Tasks.whenAllComplete(tasks).continueWith(task -> {
            boolean allVerified = true;
            for (Task<DocumentSnapshot> voucherTask : tasks) {
                if (voucherTask.isSuccessful()) {
                    DocumentSnapshot voucherDocument = voucherTask.getResult();
                    if (!voucherDocument.exists() || !voucherDocument.getBoolean("verified")) {
                        allVerified = false;
                        break;
                    }
                } else {
                    allVerified = false;
                    break;
                }
            }

            if (filtro.equals("En revisión") && !allVerified) {
                return inscrito;
            } else if (filtro.equals("Verificados completamente") && allVerified) {
                return inscrito;
            } else if (filtro.equals("En orden alfabético")) {
                return inscrito;
            }
            return null;
        });
    }





    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if(dialogImageViewPreview != null){
                dialogImageViewPreview.setImageURI(selectedImageUri);
            }

            // Notificar al adaptador que la imagen ha sido seleccionada
            adapter.setSelectedImageUri(selectedImageUri);

        }
    }

    public class InscritoAdapter extends ArrayAdapter<Inscrito> {

        private Uri selectedImageUri;
        private String qrCodeUrl;

        public InscritoAdapter(Context context, List<Inscrito> inscritos) {
            super(context, 0, inscritos);
        }

        public void setSelectedImageUri(Uri uri) {
            this.selectedImageUri = uri;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_inscrito, parent, false);
            }

            Inscrito inscrito = getItem(position);

            TextView textViewNombre = convertView.findViewById(R.id.textViewNombre);

            TextView textViewMontoAbonado = convertView.findViewById(R.id.textViewMontoAbonado);
            TextView textViewPrimeraCuota = convertView.findViewById(R.id.textViewPrimeraCuota);
            TextView textViewSegundaCuota = convertView.findViewById(R.id.textViewSegundaCuota);
            TextView textViewTerceraCuota = convertView.findViewById(R.id.textViewTerceraCuota);
            Button buttonViewVoucher = convertView.findViewById(R.id.buttonViewVoucher);
            Button buttonUploadVoucher1 = convertView.findViewById(R.id.buttonUploadVoucher1);
            Button buttonUploadVoucher2 = convertView.findViewById(R.id.buttonUploadVoucher2);
            Button buttonUploadVoucher3 = convertView.findViewById(R.id.buttonUploadVoucher3);
            Button buttonQR = convertView.findViewById(R.id.buttonQR);
            LinearLayout firtsInstallmentLayout = convertView.findViewById(R.id.firtsInstallmentLayout);
            LinearLayout secondInstallmentLayout = convertView.findViewById(R.id.secondInstallmentLayout);
            LinearLayout thirdInstallmentLayout = convertView.findViewById(R.id.thirdInstallmentLayout);

            textViewNombre.setText(inscrito.getName());

            textViewMontoAbonado.setText(String.valueOf(inscrito.getAmountPaid()));
            textViewPrimeraCuota.setText(inscrito.getFirstInstallment());

            if (!TextUtils.isEmpty(inscrito.getFirstInstallment())) {
                textViewPrimeraCuota.setText(inscrito.getFirstInstallment());
                firtsInstallmentLayout.setVisibility(View.VISIBLE);
            } else {
                firtsInstallmentLayout.setVisibility(View.GONE);
            }

            // Configurar la visibilidad de la segunda cuota
            if (!TextUtils.isEmpty(inscrito.getSecondInstallment())) {
                textViewSegundaCuota.setText(inscrito.getSecondInstallment());
                secondInstallmentLayout.setVisibility(View.VISIBLE);
            } else {
                secondInstallmentLayout.setVisibility(View.GONE);
            }

            // Configurar la visibilidad de la tercera cuota
            if (!TextUtils.isEmpty(inscrito.getThirdInstallment())) {
                textViewTerceraCuota.setText(inscrito.getThirdInstallment());
                thirdInstallmentLayout.setVisibility(View.VISIBLE);
            } else {
                thirdInstallmentLayout.setVisibility(View.GONE);
            }

            // Verificar el estado de los vouchers y actualizar los botones
            checkVoucherState(inscrito.getName(), "firstInstallment", buttonUploadVoucher1, inscrito, buttonQR);
            checkVoucherState(inscrito.getName(), "secondInstallment", buttonUploadVoucher2, inscrito, buttonQR);
            checkVoucherState(inscrito.getName(), "thirdInstallment", buttonUploadVoucher3, inscrito, buttonQR);

            // Añadir click listener para mostrar el voucher
            configureViewVoucherButton(buttonViewVoucher, inscrito, buttonQR);

            // Configurar los botones de "Cancelar" para subir un voucher
            buttonUploadVoucher1.setOnClickListener(v -> handleVoucherButtonClick(inscrito, "firstInstallment", buttonUploadVoucher1));
            buttonUploadVoucher2.setOnClickListener(v -> handleVoucherButtonClick(inscrito, "secondInstallment", buttonUploadVoucher2));
            buttonUploadVoucher3.setOnClickListener(v -> handleVoucherButtonClick(inscrito, "thirdInstallment", buttonUploadVoucher3));

            buttonQR.setOnClickListener(v -> {
                String inscritoName = inscrito.getName();  // Obtener el nombre real del inscrito
                retrieveAndShowQRCodeFromQrs(inscritoName);  // Llamar a la función para recuperar y mostrar el QR
            });





            return convertView;
        }



        private void configureViewVoucherButton(Button button, Inscrito inscrito, Button buttonQR) {
            if (inscrito.isVerified()) {
                button.setBackgroundResource(R.drawable.boton_verificado);
                button.setText("Verificado");
                button.setTextColor(getResources().getColor(R.color.verde_verificado));
                String inscritoId = inscrito.getName();
                if (!reviewedItems.contains(inscritoId)) {
                    double amountInicialVerified = inscrito.getAmountPaid();
                    totalVerified += amountInicialVerified;
                    reviewedItems.add(inscritoId);
                    textViewTotalVerified.setText(String.format("Total Verificado: %.2f", totalVerified));
                }
            } else {
                button.setBackgroundResource(R.drawable.boton_en_revisiom);
                button.setText("En Revisión");
                button.setTextColor(getResources().getColor(R.color.amarillopastel));

                String inscritoId = inscrito.getName();
                if (!reviewedItems.contains(inscritoId)) {
                    double amountInicial = inscrito.getAmountPaid();
                    totalInReview += amountInicial;
                    reviewedItems.add(inscritoId);
                    textViewTotalInReview.setText(String.format("Total en revisión: %.2f", totalInReview));
                }
            }

            button.setOnClickListener(v -> {
                String paymentReceiptUrl = inscrito.getPaymentReceiptUrl();
                if (paymentReceiptUrl != null && !paymentReceiptUrl.isEmpty()) {
                    showVoucherDialog(paymentReceiptUrl, inscrito.getName(), "mainVoucher", inscrito);
                } else {
                    Toast.makeText(getContext(), "No hay voucher disponible", Toast.LENGTH_SHORT).show();
                }
            });

            // Verificar si todos los vouchers están verificados
            checkAllVouchersVerified(inscrito, buttonQR);
        }

        private void checkVoucherState(String name, String installment, Button button, Inscrito inscrito, Button buttonQR) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("vauchers_cuotas").document(name + "_" + installment)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            boolean verified = task.getResult().getBoolean("verified");
                            if (verified) {
                                button.setBackgroundResource(R.drawable.boton_verificado);
                                button.setText("Verificado");
                                button.setTextColor(getResources().getColor(R.color.verde_verificado));
                                updateVerifiedStatus(inscrito, installment, true);

                                String voucherId = name + "_" + installment; // Identificador único para cada voucher
                                if (!reviewedItems.contains(voucherId)) {
                                    double amount = task.getResult().getDouble("amount");
                                    totalVerified += amount;
                                    reviewedItems.add(voucherId);
                                    textViewTotalVerified.setText(String.format("Total Verficado: %.2f", totalVerified));
                                }


                            } else {
                                button.setBackgroundResource(R.drawable.boton_en_revisiom);
                                button.setText("En Revisión");
                                button.setTextColor(getResources().getColor(R.color.amarillopastel));

                                String voucherId = name + "_" + installment; // Identificador único para cada voucher
                                if (!reviewedItems.contains(voucherId)) {
                                    double amount = task.getResult().getDouble("amount");
                                    totalInReview += amount;
                                    reviewedItems.add(voucherId);
                                    textViewTotalInReview.setText(String.format("Total en revisión: %.2f", totalInReview));
                                }
                            }
                            String voucherUrl = task.getResult().getString("voucherUrl");
                            button.setTag(voucherUrl); // Guardar la URL del voucher en el botón
                            button.setOnClickListener(v -> {
                                showVoucherDialog(voucherUrl, name, installment, inscrito);
                            });
                        } else {
                            button.setBackgroundResource(R.drawable.boton_cancelar);
                            button.setText("Cancelar");
                            button.setTextColor(getResources().getColor(R.color.rojo));
                        }
                        checkAllVouchersVerified(inscrito, buttonQR);
                    })
                    .addOnFailureListener(e -> {
                        button.setBackgroundResource(R.drawable.boton_cancelar);
                        button.setText("Cancelar");
                        button.setTextColor(getResources().getColor(R.color.rojo));
                    });
        }




        private void handleVoucherButtonClick(Inscrito inscrito, String installment, Button button) {
            String voucherUrl = (String) button.getTag();
            if (voucherUrl != null) {
                // Mostrar el voucher si existe
                showVoucherDialog(voucherUrl, inscrito.getName(), installment, inscrito);
            } else {
                // Abrir el diálogo para subir un nuevo voucher
                openUploadVoucherDialog(inscrito, installment, button);
            }
        }



        private void showVoucherDialog(String voucherUrl, String name, String installment, Inscrito inscrito) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.admin_dialog_voucher, null);
            builder.setView(dialogView);

            ImageView imageViewVoucher = dialogView.findViewById(R.id.imageViewVoucher);
            Button buttonVerify = dialogView.findViewById(R.id.buttonVerify);

            Glide.with(getContext()).load(voucherUrl).into(imageViewVoucher);

            buttonVerify.setOnClickListener(v -> {
                // O el ID del usuario correcto

                verifyVoucher(name, installment, inscrito);
            });

            builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }



        private void verifyVoucher(String name, String installment, Inscrito inscrito) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if ("mainVoucher".equals(installment)) {
                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                // Buscar documentos en la subcolección "Inscripciones" de todos los usuarios
                db.collectionGroup("Inscripciones")
                        .whereEqualTo("name", name)
                        .get()
                        .addOnCompleteListener(task -> {


                            if (task.isSuccessful() && !task.getResult().isEmpty()) {

                                // Recuperar el primer documento que coincide con el filtro
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                // Verificar si el documento existe
                                if (document.exists()) {
                                    Toast.makeText(getContext(), document.getId(), Toast.LENGTH_SHORT).show();
                                    document.getReference().update("verified", true)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "Voucher principal verificado", Toast.LENGTH_SHORT).show();

                                                // Actualizar el estado en el objeto Inscrito si es necesario
                                                // notifyDataSetChanged();  // Esto solo es necesario si actualizas una lista de datos en la interfaz
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al verificar voucher principal", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Documento no encontrado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Documento no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al buscar documentos", Toast.LENGTH_SHORT).show();
                        });



            } else {
                db.collection("vauchers_cuotas").document(name + "_" + installment)
                        .update("verified", true)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Voucher verificado", Toast.LENGTH_SHORT).show();
                            updateVerifiedStatus(inscrito, installment, true);
                            notifyDataSetChanged();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al verificar voucher", Toast.LENGTH_SHORT).show();
                        });
            }
        }



        private void openUploadVoucherDialog(Inscrito inscrito, String installment, Button buttonUploadVoucher) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adminMisInscritos.this);
            builder.setTitle("Subir Voucher");

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.dialog_upload_voucher, null);
            builder.setView(dialogView);

            Button buttonUpload = dialogView.findViewById(R.id.buttonUpload);

            dialogImageViewPreview = dialogView.findViewById(R.id.imageViewPreview);

            dialogImageViewPreview.setOnClickListener(v -> openFileChooser());

            if (selectedImageUri != null) {
                dialogImageViewPreview.setImageURI(selectedImageUri); // Mostrar la imagen seleccionada en el ImageView
                buttonUpload.setEnabled(true);
            }

            buttonUpload.setOnClickListener(v -> {
                buttonUpload.setEnabled(false);
                if (selectedImageUri != null) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    uploadImageToStorage(selectedImageUri, "vauchers_cuotas", userId, uri -> {
                        // Crear un nuevo documento en la colección "vauchers_cuotas"
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> voucherData = new HashMap<>();
                        voucherData.put("name", inscrito.getName());
                        double amount = 0.0;
                        if (installment.equals("firstInstallment") && inscrito.getFirstInstallment() != null && !inscrito.isFirstInstallmentVerified()) {
                            try {
                                amount = Double.parseDouble(inscrito.getFirstInstallment());
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Error al convertir el monto de la primera cuota", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            voucherData.put("amount", amount); // Subir monto de la primera cuota
                            voucherData.put("installment", "firstInstallment");
                            inscrito.setFirstInstallmentVerified(false);
                        } else if (installment.equals("secondInstallment") && inscrito.getSecondInstallment() != null && !inscrito.isSecondInstallmentVerified()) {
                            try {
                                amount = Double.parseDouble(inscrito.getSecondInstallment());
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Error al convertir el monto de la segunda cuota", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            voucherData.put("amount", amount); // Subir monto de la segunda cuota
                            voucherData.put("installment", "secondInstallment");
                            inscrito.setSecondInstallmentVerified(false);
                        } else if (installment.equals("thirdInstallment") && inscrito.getThirdInstallment() != null && !inscrito.isThirdInstallmentVerified()) {
                            try {
                                amount = Double.parseDouble(inscrito.getThirdInstallment());
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Error al convertir el monto de la tercera cuota", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            voucherData.put("amount", amount); // Subir monto de la tercera cuota
                            voucherData.put("installment", "thirdInstallment");
                            inscrito.setThirdInstallmentVerified(false);
                        } else {
                            Toast.makeText(getContext(), "No se puede subir el voucher para esta cuota", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        voucherData.put("voucherUrl", uri.toString());
                        voucherData.put("verified", false);

                        db.collection("vauchers_cuotas").document(inscrito.getName() + "_" + installment).set(voucherData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Voucher subido", Toast.LENGTH_SHORT).show();
                                    buttonUpload.setEnabled(true);
                                    buttonUploadVoucher.setBackgroundResource(R.drawable.boton_en_revisiom);
                                    buttonUploadVoucher.setText("En Revisión");
                                    buttonUploadVoucher.setTextColor(getResources().getColor(R.color.amarillopastel));
                                    buttonUploadVoucher.setTag(uri.toString()); // Guardar la URL del voucher en el botón
                                    uploadDialog.dismiss(); // Cerrar el diálogo
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir voucher", Toast.LENGTH_SHORT).show());
                    });
                } else {
                    Toast.makeText(getContext(), "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                    buttonUpload.setEnabled(true);
                }
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            uploadDialog = builder.create();
            uploadDialog.show();
        }


        private void uploadImageToStorage(Uri imageUri, String folderName, String userId, OnSuccessListener<Uri> onSuccessListener) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(folderName + "/" + userId + "/" + UUID.randomUUID().toString());

            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    onSuccessListener.onSuccess(uri);
                }).addOnFailureListener(e -> {
                    // Maneja el error de obtener la URL de descarga
                    Log.e("Firebase", "Error al obtener la URL de descarga", e);
                });
            }).addOnFailureListener(e -> {
                // Maneja el error de la subida
                Log.e("Firebase", "Error al subir la imagen", e);

            });
        }

        private void updateVerifiedStatus(Inscrito inscrito, String installment, boolean status) {
            switch (installment) {
                case "firstInstallment":
                    inscrito.setFirstInstallmentVerified(status);
                    break;
                case "secondInstallment":
                    inscrito.setSecondInstallmentVerified(status);
                    break;
                case "thirdInstallment":
                    inscrito.setThirdInstallmentVerified(status);
                    break;
            }
        }



        private void checkAllVouchersVerified(Inscrito inscrito, Button buttonQR) {
            buttonQR.setVisibility(View.GONE);
            if ((TextUtils.isEmpty(inscrito.getFirstInstallment()) || inscrito.isFirstInstallmentVerified()) &&
                    (TextUtils.isEmpty(inscrito.getSecondInstallment()) || inscrito.isSecondInstallmentVerified()) &&
                    (TextUtils.isEmpty(inscrito.getThirdInstallment()) || inscrito.isThirdInstallmentVerified()) &&
                    inscrito.isVerified()) {
                buttonQR.setVisibility(View.VISIBLE);

                // Obtener el nombre (u otro campo único) del inscrito
                String inscritoName = inscrito.getName();

                // Obtener la referencia al Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Consultar y obtener el documento del inscrito en InscritosCampamento
                db.collection("InscritosCampamento")
                        .document(inscritoName)
                        .get()
                        .addOnSuccessListener(documentSnapshotCampamento -> {
                            if (documentSnapshotCampamento.exists() && documentSnapshotCampamento.contains("qrCodeUrl")) {

                            } else {
                                db.collection("MisInscritos")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Inscripciones")
                                        .whereEqualTo("name", inscritoName)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                QueryDocumentSnapshot documentSnapshotMisInscritos = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                                // Obtener los datos del inscrito
                                                Map<String, Object> inscritoData = documentSnapshotMisInscritos.getData();

                                                // Generar el QR y agregarlo a los datos del inscrito
                                                generateQRCodeAndSaveData(inscritoName, inscritoData, buttonQR);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Error al obtener datos del inscrito desde MisInscritos", Toast.LENGTH_SHORT).show();
                                            Log.e("Firebase", "Error al obtener datos del inscrito desde MisInscritos", e);
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al verificar la existencia del QR", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "Error al verificar la existencia del QR", e);
                        });
            }
        }

        private void generateQRCodeAndSaveData(String inscritoName, Map<String, Object> inscritoData, Button buttonQR) {
            // Crear un mapa para los datos específicos del QR
            Map<String, Object> qrData = new HashMap<>();
            qrData.put("name", inscritoData.get("name"));
            qrData.put("age", inscritoData.get("age"));
            qrData.put("dni", inscritoData.get("dni"));
            qrData.put("church", inscritoData.get("church"));
            qrData.put("gender", inscritoData.get("gender"));

            // Convertir los datos del mapa en formato JSON
            JSONObject json = new JSONObject(qrData);
            String jsonData = json.toString();

            // Generar el QR
            Bitmap bitmap = QRCode.from(jsonData).bitmap();

            // Convertir el Bitmap a un arreglo de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Subir el QR a Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference qrRef = storageRef.child("qrcodes/" + inscritoName + ".png");

            UploadTask uploadTask = qrRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Obtener la URL del QR subido
                qrRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String qrCodeUrl = uri.toString();

                    // Agregar la URL del QR a los datos del inscrito
                    inscritoData.put("qrCodeUrl", qrCodeUrl);

                    // Agregar los datos adicionales
                    inscritoData.put("hasBus", true);
                    inscritoData.put("hasMeal", true);
                    inscritoData.put("busNumber", "");
                    inscritoData.put("seatNumber", "");

                    // Guardar todos los datos juntos en InscritosCampamento
                    saveDataInInscritosCampamento(inscritoName, inscritoData, buttonQR);

                    // Guardar en la colección Qrs
                    saveDataInQrs(inscritoName, qrCodeUrl);

                }).addOnFailureListener(e -> {
                    Toast.makeText(adminMisInscritos.this, "Error al obtener la URL del QR", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error al obtener la URL del QR", e);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(adminMisInscritos.this, "Error al subir el QR", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error al subir el QR", e);
            });
        }

        private void saveDataInInscritosCampamento(String inscritoName, Map<String, Object> inscritoData, Button buttonQR) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("InscritosCampamento")
                    .document(inscritoName)
                    .set(inscritoData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(adminMisInscritos.this, "Datos y QR guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Habilitar el botón para ver el QR
                        buttonQR.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(adminMisInscritos.this, "Error al guardar datos y QR", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error al guardar datos y QR", e);
                    });
        }

        private void saveDataInQrs(String inscritoName, String qrCodeUrl) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> qrOnlyData = new HashMap<>();
            qrOnlyData.put("qrCodeUrl", qrCodeUrl);

            db.collection("Qrs")
                    .document(inscritoName)
                    .set(qrOnlyData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(adminMisInscritos.this, "QR Code guardado en colección Qrs", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(adminMisInscritos.this, "Error al guardar QR Code en colección Qrs", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error al guardar QR Code en colección Qrs", e);
                    });
        }



        private void retrieveAndShowQRCodeFromQrs(String inscritoName) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener la URL del QR desde la colección Qrs
            db.collection("Qrs").document(inscritoName).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("qrCodeUrl")) {
                            String qrCodeUrl = documentSnapshot.getString("qrCodeUrl");

                            // Verificar que la URL del QR no sea nula
                            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                                // Mostrar el QR usando la URL obtenida
                                showQRCodeDialog(qrCodeUrl);
                            } else {
                                Toast.makeText(adminMisInscritos.this, "No se encontró URL válida del QR", Toast.LENGTH_SHORT).show();
                                Log.e("QRCodeRetrieval", "No valid QR URL found for: " + inscritoName);
                            }
                        } else {
                            Toast.makeText(adminMisInscritos.this, "No se encontró información del inscrito", Toast.LENGTH_SHORT).show();
                            Log.e("QRCodeRetrieval", "No document found for: " + inscritoName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(adminMisInscritos.this, "Error al obtener información del inscrito", Toast.LENGTH_SHORT).show();
                        Log.e("QRCodeRetrieval", "Error retrieving document for: " + inscritoName, e);
                    });
        }

        private void showQRCodeDialog(String qrCodeUrl) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.dialog_qr_code, null);
            builder.setView(dialogView);

            ImageView imageViewQr = dialogView.findViewById(R.id.imageViewQRCode);

            // Cargar la imagen del QR usando Picasso
            Picasso.get()
                    .load(qrCodeUrl)
                    .into(imageViewQr, new Callback() {
                        @Override
                        public void onSuccess() {
                            // No se necesita realizar ninguna acción adicional
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", "Error loading image", e);
                        }
                    });

            builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }
}