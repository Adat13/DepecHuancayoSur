package com.depec.depechuancayosur;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.view.View;

public class InscripcionClass extends AppCompatActivity {
    private EditText editTextName, editTextDOB, editTextTutorName, editTextTutorNumber, editTextOtherChurch;

    private EditText editTextPhoneNumber, editTextDNI, editTextAlergia, editTextFirstInstallment, editTextFirstInstallmentDate;
    private EditText editTextSecondInstallment, editTextSecondInstallmentDate, editTextThirdInstallment, editTextThirdInstallmentDate;
    private EditText editTextAmountPaid, editTextCodigoCarpa;
    private Spinner spinnerGender, spinnerChurch, spinnerCarpaPersonas,spinnerPayment;
    private CheckBox checkBoxCarpa, checkBoxDNIYes, checkBoxDNINo;
    private TextView textViewAge, textViewCuposRestantes, textViewDNIQuestion;
    private Button buttonInscribirse;
    private MaterialButton buttonUploadPayment;


    private ImageView imageViewPaymentReceipt;
    private Uri paymentReceiptUri;
    private TableLayout tableLayoutInstallments;
    private ImageView imageViewAuthorization;
    private Uri authorizationUri;


    private TextView textViewCodigoCarpa;
    private int previousCarpaPosition = 0;




    private int cuposCarpaMujeres;
    private int cuposCarpaVarones;
    private boolean isCarpaAvailable = false;


    private static int PREINSCRIPTION_PRICE = 120;
    private static int INSCRIPTION_PRICE = 130;
    private static int LATE_INSCRIPTION_PRICE = 180;

    private static Calendar PREINSCRIPTION_END = Calendar.getInstance();
    private static Calendar INSCRIPTION_END = Calendar.getInstance();


    private int currentPrice;
    private Button buttonInscripcion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscripcionclass);
        this.setTitle("INSCRIPCIÓN");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        }

        fetchPricesFromFirebase();
        fetchCuposFromFirebase();
        fetchAndSetValuesFromFirebase();

        editTextName = findViewById(R.id.editTextName);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextTutorName = findViewById(R.id.editTextTutorName);
        editTextTutorNumber = findViewById(R.id.editTextTutorNumber);
        editTextOtherChurch = findViewById(R.id.editTextOtherChurch);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextDNI = findViewById(R.id.editTextDNI);
        editTextAlergia = findViewById(R.id.editTextAlergia);
        editTextFirstInstallment = findViewById(R.id.editTextFirstInstallment);
        editTextFirstInstallmentDate = findViewById(R.id.editTextFirstInstallmentDate);
        editTextSecondInstallment = findViewById(R.id.editTextSecondInstallment);
        editTextSecondInstallmentDate = findViewById(R.id.editTextSecondInstallmentDate);
        editTextThirdInstallment = findViewById(R.id.editTextThirdInstallment);
        editTextThirdInstallmentDate = findViewById(R.id.editTextThirdInstallmentDate);
        editTextAmountPaid = findViewById(R.id.editTextAmountPaid);
        editTextCodigoCarpa = findViewById(R.id.editTextCodigoCarpa);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerChurch = findViewById(R.id.spinnerChurch);
        spinnerCarpaPersonas = findViewById(R.id.spinnerCarpaPersonas);
        checkBoxCarpa = findViewById(R.id.checkBoxCarpa);
        checkBoxDNIYes = findViewById(R.id.checkBoxDNIYes);
        checkBoxDNINo = findViewById(R.id.checkBoxDNINo);
        textViewAge = findViewById(R.id.textViewAge);
        textViewCuposRestantes = findViewById(R.id.textViewCuposRestantes);
        textViewDNIQuestion = findViewById(R.id.textViewDNIQuestion);
        buttonInscribirse = findViewById(R.id.buttonInscribirse);


        textViewAge = findViewById(R.id.textViewAge);
        textViewDNIQuestion = findViewById(R.id.textViewDNIQuestion);
        checkBoxDNIYes = findViewById(R.id.checkBoxDNIYes);
        checkBoxDNINo = findViewById(R.id.checkBoxDNINo);
        editTextTutorName = findViewById(R.id.editTextTutorName);
        editTextTutorNumber = findViewById(R.id.editTextTutorNumber);
        imageViewAuthorization = findViewById(R.id.imageViewAuthorization);
        spinnerChurch = findViewById(R.id.spinnerChurch);
        editTextOtherChurch = findViewById(R.id.editTextOtherChurch);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        editTextFirstInstallment = findViewById(R.id.editTextFirstInstallment);
        editTextFirstInstallmentDate = findViewById(R.id.editTextFirstInstallmentDate);
        editTextSecondInstallment = findViewById(R.id.editTextSecondInstallment);
        editTextSecondInstallmentDate = findViewById(R.id.editTextSecondInstallmentDate);
        editTextThirdInstallment = findViewById(R.id.editTextThirdInstallment);
        editTextThirdInstallmentDate = findViewById(R.id.editTextThirdInstallmentDate);
        imageViewPaymentReceipt = findViewById(R.id.imageViewPaymentReceipt);
        editTextAmountPaid = findViewById(R.id.editTextAmountPaid);
        buttonUploadPayment = findViewById(R.id.buttonUploadPayment);

        textViewCodigoCarpa = findViewById(R.id.textViewCodigoCarpa);
        editTextCodigoCarpa = findViewById(R.id.editTextCodigoCarpa);
        textViewCuposRestantes = findViewById(R.id.textViewCuposRestantes);


        checkBoxCarpa = findViewById(R.id.checkBoxCarpa);


        buttonInscripcion = findViewById(R.id.buttonInscribirse);

        buttonInscripcion.setOnClickListener(view -> {
            buttonInscripcion.setEnabled(false); // Deshabilitar el botón
            registerInscription(); // Llamar a la función de registro

            // Puedes volver a habilitar el botón una vez que termine el registro
            // Si la función `registerInscription` es asincrónica, debes habilitar el botón dentro de su callback de éxito/error
        });



        checkBoxCarpa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (isCarpaAvailable) {
                    spinnerCarpaPersonas.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(this, "No hay carpas disponibles", Toast.LENGTH_SHORT).show();
                    checkBoxCarpa.setChecked(false);
                }
            } else {
                currentPrice += 25;

                spinnerCarpaPersonas.setVisibility(View.GONE);
                textViewCodigoCarpa.setVisibility(View.GONE);
                editTextCodigoCarpa.setVisibility(View.GONE);
            }
        });
        spinnerCarpaPersonas = findViewById(R.id.spinnerCarpaPersonas);
        spinnerCarpaPersonas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el precio actual antes de aplicar cambios
                int previousPrice = currentPrice;

                // Restablecer el precio si se cambió la selección del spinner
                if (position != previousCarpaPosition) {
                    switch (previousCarpaPosition) {
                        case 0: // "1 persona" option
                        case 1: // "2 personas" option
                            // Sumar el monto que se restó previamente
                            currentPrice += 25;
                            editTextCodigoCarpa.setVisibility(View.GONE);
                            break;
                        // No se necesita un caso para la opción "Código"
                    }
                }

                // Aplicar el cambio en el precio según la selección actual del spinner
                switch (position) {
                    case 0: // "1 persona" option
                        currentPrice -= 25;
                        textViewCodigoCarpa.setVisibility(View.GONE);
                        editTextCodigoCarpa.setVisibility(View.GONE);
                        break;
                    case 1: // "2 personas" option
                        currentPrice -= 25;
                        String gender = spinnerGender.getSelectedItem().toString();
                        String codigoCarpa = generarCodigoCarpa(gender);
                        textViewCodigoCarpa.setText("Código de carpa: " + codigoCarpa);
                        textViewCodigoCarpa.setVisibility(View.VISIBLE);
                        editTextCodigoCarpa.setVisibility(View.GONE);
                        break;
                    case 2: // "Codigo" option
                        textViewCodigoCarpa.setVisibility(View.GONE);
                        editTextCodigoCarpa.setVisibility(View.VISIBLE);
                        break;
                }

                // Actualizar el estado anterior del spinner
                previousCarpaPosition = position;

                // Mostrar el precio actualizado en la interfaz de usuario, si es necesario
                // Por ejemplo: textViewPrice.setText(String.valueOf(currentPrice));
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textViewCodigoCarpa.setVisibility(View.GONE);
                editTextCodigoCarpa.setVisibility(View.GONE);
            }
        });



        editTextCodigoCarpa.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String codigoCarpa = editTextCodigoCarpa.getText().toString().trim();
                if (!codigoCarpa.isEmpty()) {
                    String gender = spinnerGender.getSelectedItem().toString(); // Obtener género seleccionado
                    verificarCodigoCarpa(codigoCarpa, gender);
                }
            }
        });



        Button buttonUploadPayment = findViewById(R.id.buttonUploadPayment);

        setupSpinners();
        setPriceBasedOnDate();

        editTextDOB.setOnClickListener(view -> showDatePickerDialog());

        checkBoxDNIYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxDNINo.setChecked(false);
                editTextTutorName.setVisibility(View.GONE);
                editTextTutorNumber.setVisibility(View.GONE);
                imageViewAuthorization.setVisibility(View.GONE);
            }
        });

        checkBoxDNINo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxDNIYes.setChecked(false);
                editTextTutorName.setVisibility(View.VISIBLE);
                editTextTutorNumber.setVisibility(View.VISIBLE);
                imageViewAuthorization.setVisibility(View.VISIBLE);
            }
        });
        editTextCodigoCarpa.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndRemoveFocus(v);
                return true;
            }
            return false;
        });

        spinnerChurch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedChurch = parent.getItemAtPosition(position).toString();
                if (selectedChurch.equals("Otro")) {
                    editTextOtherChurch.setVisibility(View.VISIBLE);
                } else {
                    editTextOtherChurch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextOtherChurch.setVisibility(View.GONE);
            }
        });

        buttonUploadPayment.setVisibility(View.GONE);

        buttonUploadPayment.setOnClickListener(view -> {
            if (isAmountPaidValid()) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido antes de subir el voucher", Toast.LENGTH_SHORT).show();
            }
        });
        editTextAmountPaid.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndRemoveFocus(v);
                processAmountPaid();
                return true;
            }
            return false;
        });

        editTextAmountPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Procesar las validaciones y acciones necesarias
                if (isAmountPaidValid()) {
                    double amountPaid = Double.parseDouble(editTextAmountPaid.getText().toString());

                    if (amountPaid >= PREINSCRIPTION_PRICE || amountPaid >= INSCRIPTION_PRICE || amountPaid >= LATE_INSCRIPTION_PRICE|| amountPaid >= currentPrice ) {
                        // Si el monto abonado es igual o mayor que cualquiera de los costos, ocultar el spinner de cuotas
                        spinnerPayment.setVisibility(View.GONE);
                    } else {
                        // Si el monto abonado es menor que cualquiera de los costos, mostrar el spinner de cuotas
                        spinnerPayment.setVisibility(View.VISIBLE);
                    }
                    // Hacer visible el botón de subir el voucher
                    buttonUploadPayment.setVisibility(View.VISIBLE);

                    // Aquí puedes llamar a tu método calculateInstallments() si es necesario
                    calculateInstallments();
                } else {
                    // Si el monto abonado no es válido, ocultar el botón de subir el voucher
                    buttonUploadPayment.setVisibility(View.GONE);
                }
            }
        });


        editTextFirstInstallment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndRemoveFocus(v);
                calculateInstallments();
                return true;
            }
            return false;
        });

        editTextSecondInstallment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndRemoveFocus(v);
                calculateInstallments();
                return true;
            }
            return false;
        });


        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handlePaymentSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextFirstInstallment.setVisibility(View.GONE);
                editTextSecondInstallment.setVisibility(View.GONE);
                editTextThirdInstallment.setVisibility(View.GONE);
            }
        });



        editTextFirstInstallmentDate.setOnClickListener(v -> showDatePickerDialogForInstallment(editTextFirstInstallmentDate));
        editTextSecondInstallmentDate.setOnClickListener(v -> showDatePickerDialogForInstallment(editTextSecondInstallmentDate));
        editTextThirdInstallmentDate.setOnClickListener(v -> showDatePickerDialogForInstallment(editTextThirdInstallmentDate));

        imageViewAuthorization.setOnClickListener(view -> openAuthorizationFileChooser());

    }

    private void processAmountPaid() {
        if (isAmountPaidValid()) {
            double amountPaid = Double.parseDouble(editTextAmountPaid.getText().toString());

            if (amountPaid >= PREINSCRIPTION_PRICE || amountPaid >= INSCRIPTION_PRICE || amountPaid >= LATE_INSCRIPTION_PRICE) {
                // Si el monto abonado es igual o mayor que cualquiera de los costos, ocultar el spinner de cuotas
                spinnerPayment.setVisibility(View.GONE);
            } else {
                // Si el monto abonado es menor que cualquiera de los costos, mostrar el spinner de cuotas
                spinnerPayment.setVisibility(View.VISIBLE);
            }
            // Hacer visible el botón de subir el voucher
            buttonUploadPayment.setVisibility(View.VISIBLE);

            // Aquí puedes llamar a tu método calculateInstallments() si es necesario
            calculateInstallments();
        } else {
            // Si el monto abonado no es válido, ocultar el botón de subir el voucher
            buttonUploadPayment.setVisibility(View.GONE);
        }
    }

    private void fetchAndSetValuesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference fechasRef = db.collection("Fechas");

        fechasRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0); // Solo tomamos el primer documento
                Map<String, Object> fechaData = document.getData();
                setDatesFromFirebase(fechaData);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al obtener fechas de Firebase", e);
        });
    }

    private void setDatesFromFirebase(Map<String, Object> fechaData) {
        int preinscriptionYear = Integer.parseInt(fechaData.get("PREINSCRIPTION_END_year").toString());
        int preinscriptionMonth = Integer.parseInt(fechaData.get("PREINSCRIPTION_END_month").toString()) - 1; // Restar 1 al mes porque Calendar los cuenta desde 0 (enero = 0, febrero = 1, etc.)
        int preinscriptionDay = Integer.parseInt(fechaData.get("PREINSCRIPTION_END_day").toString());
        int inscriptionYear = Integer.parseInt(fechaData.get("INSCRIPTION_END_year").toString());
        int inscriptionMonth = Integer.parseInt(fechaData.get("INSCRIPTION_END_month").toString()) - 1; // Restar 1 al mes porque Calendar los cuenta desde 0 (enero = 0, febrero = 1, etc.)
        int inscriptionDay = Integer.parseInt(fechaData.get("INSCRIPTION_END_day").toString());

        // Configurar valores estáticos basados en los datos de Firebase
        PREINSCRIPTION_END.set(preinscriptionYear, preinscriptionMonth, preinscriptionDay);
        INSCRIPTION_END.set(inscriptionYear, inscriptionMonth, inscriptionDay);

        // Actualizar el precio basado en las fechas recuperadas
        setPriceBasedOnDate();
    }





    private void verificarCodigoCarpa(String codigoCarpa, String gender) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("CodigosCarpa").document(codigoCarpa);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Verificar que el código no esté obsoleto y que el género coincida
                boolean obsoleto = documentSnapshot.getBoolean("obsoleto");
                String generoCodigo = documentSnapshot.getString("genero");

                if (!obsoleto && generoCodigo != null && generoCodigo.equalsIgnoreCase(gender.substring(0, 1))) {
                    Toast.makeText(this, "Código válido, descuento aplicado", Toast.LENGTH_SHORT).show();
                    currentPrice -= 25;
                    checkBoxCarpa.setChecked(true);
                    checkBoxCarpa.setEnabled(false);
                    spinnerCarpaPersonas.setVisibility(View.GONE);
                    textViewCodigoCarpa.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "Código inválido para el género seleccionado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al verificar el código", Toast.LENGTH_SHORT).show());
    }



    private void fetchCuposFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("CuposCarpa").document("CantidadCupos");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                cuposCarpaMujeres = ((Long) documentSnapshot.get("CarpaMujeres")).intValue();
                cuposCarpaVarones = ((Long) documentSnapshot.get("CarpaVarones")).intValue();
                isCarpaAvailable = (cuposCarpaMujeres > 0 || cuposCarpaVarones > 0);
                actualizarCuposRestantes(); // Llamada al método para actualizar el TextView
            } else {
                Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al obtener los cupos", Toast.LENGTH_SHORT).show());
    }

    private String generarCodigoCarpa(String gender) {
        Random random = new Random();
        String genderInitial = gender.substring(0, 1).toUpperCase(); // Obtener la inicial del género (M o F)
        return genderInitial + String.format("%06d", random.nextInt(1000000));
    }


    private void guardarCodigoEnFirebase(String codigoCarpa, String gender) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> carpaData = new HashMap<>();
        carpaData.put("codigo", codigoCarpa);
        carpaData.put("genero", gender.substring(0, 1).toUpperCase()); // Guarda la inicial del género
        carpaData.put("obsoleto", false);

        db.collection("CodigosCarpa").document(codigoCarpa).set(carpaData)
                .addOnSuccessListener(aVoid -> {
                    // Código guardado exitosamente
                    Toast.makeText(this, "Código de carpa guardado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el código
                    Toast.makeText(this, "Error al guardar el código de carpa", Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchPricesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Precios").document("Costo");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> prices = documentSnapshot.getData();
                PREINSCRIPTION_PRICE = ((Long) prices.get("preinscripcion")).intValue();
                INSCRIPTION_PRICE = ((Long) prices.get("inscripcion")).intValue();
                LATE_INSCRIPTION_PRICE = ((Long) prices.get("rezagados")).intValue();
                setPriceBasedOnDate(); // Actualizar el precio actual según la fecha
            } else {
                Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al obtener los precios", Toast.LENGTH_SHORT).show());
    }
    private void hideKeyboardAndRemoveFocus(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    private void setPriceBasedOnDate() {
        Calendar today = Calendar.getInstance();
        if (today.before(PREINSCRIPTION_END)) {
            currentPrice = PREINSCRIPTION_PRICE;
        } else if (today.before(INSCRIPTION_END)) {
            currentPrice = INSCRIPTION_PRICE;
        } else {
            currentPrice = LATE_INSCRIPTION_PRICE;
        }
        // Mostrar precio actual en la UI, si es necesario
        // Por ejemplo: textViewPrice.setText(String.valueOf(currentPrice));
    }
    private boolean isAmountPaidValid() {
        String amountPaidText = editTextAmountPaid.getText().toString().trim();
        if (amountPaidText.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el monto abonado", Toast.LENGTH_SHORT).show();
            spinnerPayment.setVisibility(View.GONE);
            resetPaymentFields();
            buttonUploadPayment.setVisibility(View.GONE);

            return false;
        }
        try {
            double amountPaid = Double.parseDouble(amountPaidText);
            if (amountPaid <= 0) {
                Toast.makeText(this, "El monto abonado debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                spinnerPayment.setVisibility(View.GONE);
                resetPaymentFields();
                buttonUploadPayment.setVisibility(View.GONE);

                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show();
            spinnerPayment.setVisibility(View.GONE);
            resetPaymentFields();
            buttonUploadPayment.setVisibility(View.GONE);
            return false;
        }
        return true;
    }
    private void openAuthorizationFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (requestCode == 1) {
                paymentReceiptUri = selectedImageUri;
                Glide.with(this).load(paymentReceiptUri).into(imageViewPaymentReceipt);
                imageViewPaymentReceipt.setVisibility(View.VISIBLE);
            } else if (requestCode == 2) {
                authorizationUri = selectedImageUri;
                Glide.with(this).load(authorizationUri).into(imageViewAuthorization);
                imageViewAuthorization.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDatePickerDialogForInstallment(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth = selectedMonth + 1; // Month is 0-based
                    String date = selectedDay + "/" + selectedMonth + "/" + selectedYear;

                    if (isValidDate(selectedYear, selectedMonth, selectedDay)) {
                        editText.setText(date);
                    } else {
                        Toast.makeText(InscripcionClass.this, "Fecha fuera del rango permitido", Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    private boolean isValidDate(int year, int month, int day) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month - 1, day);

        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.JUNE, 28);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2024, Calendar.JULY, 27);

        return !selectedDate.before(startDate) && !selectedDate.after(endDate);
    }


    private void setupSpinners() {
        ArrayAdapter<CharSequence> churchAdapter = ArrayAdapter.createFromResource(this,
                R.array.churches_array, android.R.layout.simple_spinner_item);
        churchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChurch.setAdapter(churchAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                R.array.payment_options, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(paymentAdapter);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    selectedMonth = selectedMonth + 1; // Month is 0-based
                    String date = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    editTextDOB.setText(date);
                    calculateAge(selectedYear, selectedMonth, selectedDay);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void calculateAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        textViewAge.setText(String.valueOf(age));

        if (age < 17) {
            //Edad menor a 17 obligatorio la autorización
            textViewDNIQuestion.setVisibility(View.GONE);
            checkBoxDNIYes.setVisibility(View.GONE);
            checkBoxDNINo.setVisibility(View.GONE);
            editTextTutorName.setVisibility(View.VISIBLE);
            editTextTutorNumber.setVisibility(View.VISIBLE);
            imageViewAuthorization.setVisibility(View.VISIBLE);
        } else if (age == 17) {
            //Edad igual a 17
            textViewDNIQuestion.setVisibility(View.VISIBLE);
            checkBoxDNIYes.setVisibility(View.VISIBLE);
            checkBoxDNINo.setVisibility(View.VISIBLE);
            editTextTutorName.setVisibility(View.GONE);
            editTextTutorNumber.setVisibility(View.GONE);
            imageViewAuthorization.setVisibility(View.GONE);
        } else {
            textViewDNIQuestion.setVisibility(View.GONE);
            checkBoxDNIYes.setVisibility(View.GONE);
            checkBoxDNINo.setVisibility(View.GONE);
            editTextTutorName.setVisibility(View.GONE);
            editTextTutorNumber.setVisibility(View.GONE);
            imageViewAuthorization.setVisibility(View.GONE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void handlePaymentSelection(int position) {
        // Ocultar campos de cuotas por defecto
        editTextFirstInstallment.setVisibility(View.GONE);
        editTextSecondInstallment.setVisibility(View.GONE);
        editTextThirdInstallment.setVisibility(View.GONE);
        editTextFirstInstallmentDate.setVisibility(View.GONE);
        editTextSecondInstallmentDate.setVisibility(View.GONE);
        editTextThirdInstallmentDate.setVisibility(View.GONE);

        // Restablecer el estado de los campos de fecha
        editTextFirstInstallmentDate.setText("");
        editTextSecondInstallmentDate.setText("");
        editTextThirdInstallmentDate.setText("");

        // Obtener el monto abonado
        String amountPaidText = editTextAmountPaid.getText().toString();
        if (amountPaidText.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el monto abonado", Toast.LENGTH_SHORT).show();
            resetPaymentFields();
            return;
        }

        double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
            if (amountPaid <= 0) {
                Toast.makeText(this, "El monto abonado debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                resetPaymentFields();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show();
            resetPaymentFields();
            return;
        }

        // Calcular el monto pendiente según el costo actual
        double remainingAmount = currentPrice - amountPaid;

        // Verificar si el monto abonado cubre cualquiera de los tres costos
        if (amountPaid >= PREINSCRIPTION_PRICE || amountPaid >= INSCRIPTION_PRICE || amountPaid >= LATE_INSCRIPTION_PRICE) {
            // Si el monto abonado es igual o mayor que cualquiera de los costos, mostrar un mensaje indicando que no es necesario seleccionar cuotas
            Toast.makeText(this, "El monto abonado cubre el costo completo, no es necesario seleccionar cuotas", Toast.LENGTH_SHORT).show();
        } else {
            // Si aún hay monto pendiente después del pago, permitir la selección de cuotas según la opción elegida
            switch (position) {
                case 1: // Una cuota
                    editTextFirstInstallment.setVisibility(View.VISIBLE);
                    editTextFirstInstallment.setText(String.valueOf(remainingAmount));
                    editTextFirstInstallment.setEnabled(false);
                    editTextFirstInstallmentDate.setVisibility(View.VISIBLE);
                    break;
                case 2: // Dos cuotas
                    editTextFirstInstallment.setVisibility(View.VISIBLE);
                    editTextSecondInstallment.setVisibility(View.VISIBLE);
                    editTextFirstInstallment.setEnabled(true);
                    editTextSecondInstallment.setEnabled(false);
                    editTextFirstInstallmentDate.setVisibility(View.VISIBLE);
                    editTextSecondInstallmentDate.setVisibility(View.VISIBLE);
                    break;
                case 3: // Tres cuotas
                    editTextFirstInstallment.setVisibility(View.VISIBLE);
                    editTextSecondInstallment.setVisibility(View.VISIBLE);
                    editTextThirdInstallment.setVisibility(View.VISIBLE);
                    editTextFirstInstallment.setEnabled(true);
                    editTextSecondInstallment.setEnabled(true);
                    editTextThirdInstallment.setEnabled(false);
                    editTextFirstInstallmentDate.setVisibility(View.VISIBLE);
                    editTextSecondInstallmentDate.setVisibility(View.VISIBLE);
                    editTextThirdInstallmentDate.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void resetPaymentFields() {
        editTextFirstInstallment.setVisibility(View.GONE);
        editTextSecondInstallment.setVisibility(View.GONE);
        editTextThirdInstallment.setVisibility(View.GONE);
        editTextFirstInstallmentDate.setVisibility(View.GONE);
        editTextSecondInstallmentDate.setVisibility(View.GONE);
        editTextThirdInstallmentDate.setVisibility(View.GONE);
        editTextFirstInstallmentDate.setText("");
        editTextSecondInstallmentDate.setText("");
        editTextThirdInstallmentDate.setText("");
        editTextFirstInstallment.setText("");
        editTextSecondInstallment.setText("");
        editTextThirdInstallment.setText("");
        spinnerPayment.setSelection(0); // Restablecer el Spinner al valor predeterminado
    }

    private void descontarCupos(String gender, int cantidad) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("CuposCarpa").document("CantidadCupos");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int cuposMujeres = ((Long) documentSnapshot.get("CarpaMujeres")).intValue();
                int cuposVarones = ((Long) documentSnapshot.get("CarpaVarones")).intValue();

                if (gender.equalsIgnoreCase("Femenino") && cuposMujeres >= cantidad) {
                    cuposMujeres -= cantidad;
                } else if (gender.equalsIgnoreCase("Masculino") && cuposVarones >= cantidad) {
                    cuposVarones -= cantidad;
                }

                Map<String, Object> cuposActualizados = new HashMap<>();
                cuposActualizados.put("CarpaMujeres", cuposMujeres);
                cuposActualizados.put("CarpaVarones", cuposVarones);

                docRef.set(cuposActualizados)
                        .addOnSuccessListener(aVoid -> {
                            // Cupos actualizados correctamente
                            fetchCuposFromFirebase(); // Actualiza la UI con los nuevos valores de cupos
                        })
                        .addOnFailureListener(e -> {
                            // Error al actualizar los cupos
                            Toast.makeText(this, "Error al actualizar los cupos", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al obtener los cupos", Toast.LENGTH_SHORT).show());
    }


    private void registerInscription() {
        if (!areRequiredFieldsFilled()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            buttonInscripcion.setEnabled(true);
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener los datos del formulario
        String name = editTextName.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        int age = Integer.parseInt(textViewAge.getText().toString().trim());
        String gender = spinnerGender.getSelectedItem().toString();
        String church = spinnerChurch.getSelectedItem().toString();
        String otherChurch = editTextOtherChurch.getVisibility() == View.VISIBLE ? editTextOtherChurch.getText().toString().trim() : "";
        String tutorName = editTextTutorName.getVisibility() == View.VISIBLE ? editTextTutorName.getText().toString().trim() : "";
        String tutorNumber = editTextTutorNumber.getVisibility() == View.VISIBLE ? editTextTutorNumber.getText().toString().trim() : "";
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String dni = editTextDNI.getText().toString().trim();
        String alergia = editTextAlergia.getText().toString().trim();
        String firstInstallment = editTextFirstInstallment.getVisibility() == View.VISIBLE ? editTextFirstInstallment.getText().toString().trim() : "";
        String firstInstallmentDate = editTextFirstInstallmentDate.getVisibility() == View.VISIBLE ? editTextFirstInstallmentDate.getText().toString().trim() : "";
        String secondInstallment = editTextSecondInstallment.getVisibility() == View.VISIBLE ? editTextSecondInstallment.getText().toString().trim() : "";
        String secondInstallmentDate = editTextSecondInstallmentDate.getVisibility() == View.VISIBLE ? editTextSecondInstallmentDate.getText().toString().trim() : "";
        String thirdInstallment = editTextThirdInstallment.getVisibility() == View.VISIBLE ? editTextThirdInstallment.getText().toString().trim() : "";
        String thirdInstallmentDate = editTextThirdInstallmentDate.getVisibility() == View.VISIBLE ? editTextThirdInstallmentDate.getText().toString().trim() : "";
        double amountPaid = Double.parseDouble(editTextAmountPaid.getText().toString().trim());
        boolean carpa = checkBoxCarpa.isChecked();
        String carpaPersonas = carpa ? spinnerCarpaPersonas.getSelectedItem().toString() : "";
        final String codigoCarpa;

        // Usar la clase contenedora para la variable
        MutableBoolean isTextViewCodigo = new MutableBoolean(false);

        // Obtener el código de carpa según el tipo de componente utilizado
        if (carpa) {
            if (textViewCodigoCarpa.getVisibility() == View.VISIBLE) {
                // Obtener el código de carpa desde el TextView (visualización)
                String textViewText = textViewCodigoCarpa.getText().toString().trim();
                if (textViewText.startsWith("Código de carpa: ")) {
                    codigoCarpa = textViewText.substring("Código de carpa: ".length());
                    isTextViewCodigo.value = true;
                } else {
                    codigoCarpa = ""; // Código no válido, guardar como vacío
                }
            } else {
                // Obtener el código de carpa desde el EditText (entrada) solo si está visible
                if (editTextCodigoCarpa.getVisibility() == View.VISIBLE) {
                    codigoCarpa = editTextCodigoCarpa.getText().toString().trim();
                } else {
                    codigoCarpa = ""; // No es carpa, guardar como vacío
                }
            }
        } else {
            codigoCarpa = ""; // No es carpa, guardar como vacío
        }

        // Crear un mapa para los datos
        Map<String, Object> inscriptionData = new HashMap<>();
        inscriptionData.put("name", name);
        inscriptionData.put("dob", dob);
        inscriptionData.put("age", age);
        inscriptionData.put("gender", gender);
        inscriptionData.put("church", church);
        inscriptionData.put("otherChurch", otherChurch);
        inscriptionData.put("tutorName", tutorName);
        inscriptionData.put("tutorNumber", tutorNumber);
        inscriptionData.put("phoneNumber", phoneNumber);
        inscriptionData.put("dni", dni);
        inscriptionData.put("alergia", alergia);
        inscriptionData.put("firstInstallment", firstInstallment);
        inscriptionData.put("firstInstallmentDate", firstInstallmentDate);
        inscriptionData.put("secondInstallment", secondInstallment);
        inscriptionData.put("secondInstallmentDate", secondInstallmentDate);
        inscriptionData.put("thirdInstallment", thirdInstallment);
        inscriptionData.put("thirdInstallmentDate", thirdInstallmentDate);
        inscriptionData.put("amountPaid", amountPaid);
        inscriptionData.put("carpa", carpa);
        inscriptionData.put("carpaPersonas", carpaPersonas);
        inscriptionData.put("codigoCarpa", codigoCarpa.isEmpty() ? "" : codigoCarpa); // Guardar código de carpa o cadena vacía
        inscriptionData.put("verified", false);
        // Obtener el ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Subir la imagen del voucher de pago y la autorización si existen
        if (paymentReceiptUri != null) {
            uploadImageToStorage(paymentReceiptUri, "paymentReceipts", userId, uri -> {
                inscriptionData.put("paymentReceiptUrl", uri.toString());
                if (authorizationUri != null) {
                    uploadImageToStorage(authorizationUri, "authorizations", userId, uri2 -> {
                        inscriptionData.put("authorizationUrl", uri2.toString());
                        saveInscriptionData(db, userId, inscriptionData, carpa, codigoCarpa, isTextViewCodigo, carpaPersonas, gender, age);
                    });
                } else {
                    saveInscriptionData(db, userId, inscriptionData, carpa, codigoCarpa, isTextViewCodigo, carpaPersonas, gender, age);
                }
            });
        } else if (authorizationUri != null) {
            uploadImageToStorage(authorizationUri, "authorizations", userId, uri -> {
                inscriptionData.put("authorizationUrl", uri.toString());
                saveInscriptionData(db, userId, inscriptionData, carpa, codigoCarpa, isTextViewCodigo, carpaPersonas, gender, age);
            });
        } else {
            saveInscriptionData(db, userId, inscriptionData, carpa, codigoCarpa, isTextViewCodigo, carpaPersonas, gender, age);
        }
    }

    private void uploadImageToStorage(Uri imageUri, String folder, String userId, OnSuccessListener<Uri> onSuccessListener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child(folder + "/" + userId + "/" + imageUri.getLastPathSegment());
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    private void saveInscriptionData(FirebaseFirestore db, String userId, Map<String, Object> inscriptionData, boolean carpa, String codigoCarpa, MutableBoolean isTextViewCodigo, String carpaPersonas, String gender, int age) {
        db.collection("MisInscritos").document(userId).collection("Inscripciones").add(inscriptionData)
                .addOnSuccessListener(documentReference -> {
                    // Datos guardados correctamente
                    if (Double.parseDouble(inscriptionData.get("amountPaid").toString()) >= currentPrice) {
                        db.collection("InscritosCampamento").add(inscriptionData);
                    } else {
                        db.collection("InscritoDeudor").add(inscriptionData);
                    }

                    if (age >= 18 && (authorizationUri == null || (inscriptionData.get("tutorName").toString().isEmpty() && inscriptionData.get("tutorNumber").toString().isEmpty()))) {
                        db.collection("InscritosMayores").add(inscriptionData);
                    } else {
                        db.collection("InscritosMenores").add(inscriptionData);
                    }

                    // Manejo del código de carpa y actualización de cupos
                    if (carpa && !codigoCarpa.isEmpty()) {
                        db.collection("CodigosCarpa").document(codigoCarpa).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!documentSnapshot.exists()) {
                                        db.collection("CodigosCarpa").document(codigoCarpa).set(new HashMap<String, Object>() {{
                                            put("genero", gender.substring(0, 1).toUpperCase());
                                            put("obsoleto", false);
                                        }});
                                    } else {
                                        // Marcar el código de carpa como obsoleto después de ser utilizado
                                        db.collection("CodigosCarpa").document(codigoCarpa).update("obsoleto", true);
                                    }
                                });

                        // Descontar cupos dependiendo del valor de "carpaPersonas" y si el código se obtuvo del TextView
                        if (isTextViewCodigo.value) {
                            if (carpaPersonas.equals("2 personas")) {
                                descontarCupos(gender, 2);
                            } else if (carpaPersonas.equals("1 persona")) {
                                descontarCupos(gender, 1);
                            }
                        }
                    } else if (carpaPersonas.equals("2 personas")) {
                        descontarCupos(gender, 2);
                    } else if (carpaPersonas.equals("1 persona")) {
                        descontarCupos(gender, 1);
                    }

                    Toast.makeText(this, "Inscripción registrada con éxito", Toast.LENGTH_SHORT).show();
                    buttonInscripcion.setEnabled(true);
                    finish(); // Cierra la actividad después de la inscripción exitosa
                })
                .addOnFailureListener(e -> {
                    // Error al guardar los datos
                    Toast.makeText(this, "Error al registrar la inscripción", Toast.LENGTH_SHORT).show();
                    buttonInscripcion.setEnabled(false);
                });
    }


    private void actualizarCuposRestantes() {
        runOnUiThread(() -> {
            String cuposRestantes = "Cupos Restantes:\nMujeres: " + cuposCarpaMujeres + "\nVarones: " + cuposCarpaVarones;
            textViewCuposRestantes.setText(cuposRestantes);
        });
    }
    private boolean areRequiredFieldsFilled() {
        if (editTextDOB.getText().toString().trim().isEmpty()) return false;
        if (textViewAge.getText().toString().trim().isEmpty()) return false;
        if (spinnerGender.getSelectedItemPosition() == 0) return false;
        if (spinnerChurch.getSelectedItemPosition() == 0) return false;

        if (spinnerPayment.getVisibility() == View.VISIBLE && spinnerPayment.getSelectedItemPosition() == 0) return false;

        if (editTextAmountPaid.getText().toString().trim().isEmpty()) return false;
        if (imageViewPaymentReceipt.getVisibility() == View.VISIBLE && imageViewPaymentReceipt.getDrawable() == null) return false;


        if (editTextTutorName.getVisibility() == View.VISIBLE && editTextTutorName.getText().toString().trim().isEmpty()) return false;
        if (editTextTutorNumber.getVisibility() == View.VISIBLE && editTextTutorNumber.getText().toString().trim().isEmpty()) return false;


        if (editTextFirstInstallment.getVisibility() == View.VISIBLE && editTextFirstInstallment.getText().toString().trim().isEmpty()) return false;
        if (editTextSecondInstallment.getVisibility() == View.VISIBLE && editTextSecondInstallment.getText().toString().trim().isEmpty()) return false;
        if (editTextThirdInstallment.getVisibility() == View.VISIBLE && editTextThirdInstallment.getText().toString().trim().isEmpty()) return false;

        if (editTextThirdInstallmentDate.getVisibility() == View.VISIBLE && editTextFirstInstallment.getText().toString().trim().isEmpty()) return false;
        if (editTextSecondInstallmentDate.getVisibility() == View.VISIBLE && editTextSecondInstallment.getText().toString().trim().isEmpty()) return false;
        if (editTextThirdInstallmentDate.getVisibility() == View.VISIBLE && editTextThirdInstallment.getText().toString().trim().isEmpty()) return false;

        return true;
    }




    private void calculateInstallments() {
        String firstInstallmentText = editTextFirstInstallment.getText().toString();
        String secondInstallmentText = editTextSecondInstallment.getText().toString();
        String amountPaidText = editTextAmountPaid.getText().toString();

        // Validar que se ingrese el monto abonado
        if (amountPaidText.isEmpty()) {
            editTextAmountPaid.setError("Por favor, ingrese el monto abonado");
            spinnerPayment.setVisibility(View.GONE);
            editTextFirstInstallment.setVisibility(View.GONE);
            editTextSecondInstallment.setVisibility(View.GONE);
            editTextThirdInstallment.setVisibility(View.GONE);
            editTextFirstInstallmentDate.setVisibility(View.GONE);
            editTextSecondInstallmentDate.setVisibility(View.GONE);
            editTextThirdInstallmentDate.setVisibility(View.GONE);
            editTextFirstInstallmentDate.setText("");
            editTextSecondInstallmentDate.setText("");
            editTextThirdInstallmentDate.setText("");


            return;
        }

        double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
            if (amountPaid < 30) { // Validar que el monto abonado sea mayor o igual a 30
                editTextAmountPaid.setError("El monto abonado debe ser mayor o igual a 30");
                spinnerPayment.setVisibility(View.GONE);
                editTextFirstInstallment.setVisibility(View.GONE);
                editTextSecondInstallment.setVisibility(View.GONE);
                editTextThirdInstallment.setVisibility(View.GONE);
                editTextFirstInstallmentDate.setVisibility(View.GONE);
                editTextSecondInstallmentDate.setVisibility(View.GONE);
                editTextThirdInstallmentDate.setVisibility(View.GONE);
                editTextFirstInstallmentDate.setText("");
                editTextSecondInstallmentDate.setText("");
                editTextThirdInstallmentDate.setText("");

                return;
            }
        } catch (NumberFormatException e) {
            editTextAmountPaid.setError("Por favor, ingrese un monto abonado válido");
            return;
        }

        // Resto del método sigue igual
        double remainingAmount = currentPrice - amountPaid;

        if (!firstInstallmentText.isEmpty()) {
            double firstInstallment;
            try {
                firstInstallment = Double.parseDouble(firstInstallmentText);
                if (firstInstallment < 30) {  // Validación para la primera cuota
                    editTextFirstInstallment.setError("La cantidad mínima es 30");
                    return;
                }
            } catch (NumberFormatException e) {
                editTextFirstInstallment.setError("Por favor, ingrese un monto válido");
                return;
            }

            remainingAmount -= firstInstallment;

            // Validaciones y cálculos adicionales según la selección del método de pago
            switch (spinnerPayment.getSelectedItemPosition()) {
                case 2:  // Caso de dos cuotas
                    if (remainingAmount < 30) {
                        editTextSecondInstallment.setError("La cantidad mínima es 30");
                    } else {
                        editTextSecondInstallment.setText(String.valueOf(remainingAmount));
                    }
                    break;
                case 3:  // Caso de tres cuotas
                    if (!secondInstallmentText.isEmpty()) {
                        double secondInstallment;
                        try {
                            secondInstallment = Double.parseDouble(secondInstallmentText);
                            if (secondInstallment < 30) {  // Validación para la segunda cuota
                                editTextSecondInstallment.setError("La cantidad mínima es 30");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            editTextSecondInstallment.setError("Por favor, ingrese un monto válido");
                            return;
                        }
                        double finalAmount = remainingAmount - secondInstallment;
                        editTextThirdInstallment.setText(String.valueOf(finalAmount));
                    } else {
                        double installment = remainingAmount / 2;
                        if (installment < 30) {
                            editTextSecondInstallment.setError("La cantidad mínima es 30");
                        } else {
                            editTextSecondInstallment.setText(String.valueOf(installment));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }



}

