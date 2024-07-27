package com.depec.depechuancayosur;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class AuthManager {
    private CircleImageView dialogSelectedImageView;
    private static final String TAG = "AuthManager";
    private static final int RC_SIGN_IN = 9001;
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppCompatActivity activity;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView profileImageView;
    private TextView userNameTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Uri imageUri;
    private String currentUserEmail;

    public AuthManager(AppCompatActivity activity, ImageView profileImageView, TextView userNameTextView) {
        this.activity = activity;
        this.profileImageView = profileImageView;
        this.userNameTextView = userNameTextView;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        checkUserAuthentication();
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
            retrieveUserData(currentUserEmail);
        }
    }

    public void handleProfileImageClick() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signIn();
        } else {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users").document(userEmail).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    openOptionsDialog();
                                } else {
                                    openDialog();
                                }
                            } else {
                                Log.e(TAG, "Error al verificar el registro del usuario: ", task.getException());
                            }
                        });
            } else {
                Log.e(TAG, "El correo electrónico del usuario es nulo");
            }
        }
    }

    private void openOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Opciones de sesión");
        builder.setItems(new CharSequence[]{"Cerrar sesión"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN).revokeAccess()
                            .addOnCompleteListener(task -> {
                                mAuth.signOut();
                                currentUserEmail = null;
                                profileImageView.setImageResource(R.drawable.icon_person);
                                userNameTextView.setText("Iniciar Sesión");
                                Toast.makeText(activity, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                            });
                    break;
            }
        });
        builder.create().show();
    }

    private void openDialog() {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_user_info);

        EditText fullNameEditText = dialog.findViewById(R.id.fullNameEditText);
        EditText phoneNumberEditText = dialog.findViewById(R.id.phoneNumberEditText);
        EditText cityEditText = dialog.findViewById(R.id.cityEditText);
         TextInputEditText dateOfBirthEditText = dialog.findViewById(R.id.dateOfBirthEditText);
        dateOfBirthEditText.setOnClickListener(v -> showDatePickerDialog(dateOfBirthEditText));

        Spinner churchSpinner = dialog.findViewById(R.id.churchSpinner);
        EditText otherChurchEditText = dialog.findViewById(R.id.otherChurchEditText);
        dialogSelectedImageView = dialog.findViewById(R.id.selectedImageView);
        Button uploadImageButton = dialog.findViewById(R.id.uploadImageButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.churches_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        churchSpinner.setAdapter(adapter);

        churchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == adapter.getCount() - 1) {
                    otherChurchEditText.setVisibility(View.VISIBLE);
                } else {
                    otherChurchEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        uploadImageButton.setOnClickListener(view -> openFileChooser());

        saveButton.setOnClickListener(view -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String city = cityEditText.getText().toString().trim();
            String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
            String church = churchSpinner.getSelectedItem().toString();
            String otherChurch = otherChurchEditText.getText().toString().trim();

            if (validateInputs(fullName, phoneNumber, city, dateOfBirth, church, otherChurch)) {
                String finalChurch = church.equals("Otro") ? otherChurch : church;
                int age = calculateAge(dateOfBirth);

                if (imageUri != null) {
                    uploadImageToFirebaseStorage(fullName, fullNameEditText, phoneNumberEditText, cityEditText, dateOfBirthEditText, finalChurch, age, dialog);
                } else {
                    Toast.makeText(activity, "Por favor sube una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void uploadImageToFirebaseStorage(String fullName, EditText fullNameEditText, EditText phoneNumberEditText, EditText cityEditText, EditText dateOfBirthEditText, String finalChurch, int age, Dialog dialog) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images/" + currentUserEmail + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveUserDataToFirestore(fullName, fullNameEditText, phoneNumberEditText, cityEditText, dateOfBirthEditText, finalChurch, age, imageUrl, dialog);
                        }))
                .addOnFailureListener(e -> Toast.makeText(activity, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    private void saveUserDataToFirestore(String fullName, EditText fullNameEditText, EditText phoneNumberEditText, EditText cityEditText, EditText dateOfBirthEditText, String finalChurch, int age, String imageUrl, Dialog dialog) {
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("phoneNumber", phoneNumberEditText.getText().toString().trim());
        user.put("city", cityEditText.getText().toString().trim());
        user.put("dateOfBirth", dateOfBirthEditText.getText().toString().trim());
        user.put("church", finalChurch);
        user.put("age", age);
        user.put("imageUrl", imageUrl);

        db.collection("users").document(currentUserEmail)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                    userNameTextView.setText(fullName);
                    dialog.dismiss();
                    loadImageIntoView(imageUrl);
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(activity, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al guardar los datos", error);
                });
    }

    private void loadImageIntoView(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(activity).load(imageUrl).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.icon_person);
        }
    }

    private boolean validateInputs(String fullName, String phoneNumber, String city, String dateOfBirth, String church, String otherChurch) {
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(activity, "Por favor ingrese su nombre completo", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(activity, "Por favor ingrese su número de teléfono", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(activity, "Por favor ingrese su ciudad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(activity, "Por favor ingrese su fecha de nacimiento", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (church.equals("Otro") && TextUtils.isEmpty(otherChurch)) {
            Toast.makeText(activity, "Por favor especifique su iglesia", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int calculateAge(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar dob = Calendar.getInstance();
            dob.setTime(sdf.parse(dateOfBirth));
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            Log.e(TAG, "Error al calcular la edad: ", e);
            return 0;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                if (dialogSelectedImageView != null) {
                    dialogSelectedImageView.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            currentUserEmail = user.getEmail();
                            String displayName = user.getDisplayName();
                            if (displayName != null) {
                                userNameTextView.setText(displayName);
                            }
                            retrieveUserData(user.getEmail());
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        userNameTextView.setText("Iniciar Sesión");
                        profileImageView.setImageResource(R.drawable.danzandofondo);
                    }
                });
    }

    private void retrieveUserData(String email) {
        db.collection("users").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        userNameTextView.setText(fullName);

                        String profileImageUrl = documentSnapshot.getString("imageUrl");
                        loadImageIntoView(profileImageUrl);
                    } else {
                        Log.d(TAG, "No such document");
                        openDialog();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al recuperar los datos del usuario: ", e));
    }
    private void showDatePickerDialog(final TextInputEditText dateOfBirthEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
            dateOfBirthEditText.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}
