package com.depec.depechuancayosur;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.DecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private static final String TAG = QRScannerActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private DecoratedBarcodeView barcodeView;
    private boolean isScanning = false; // Variable para evitar múltiples escaneos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<com.google.zxing.BarcodeFormat> formats = Arrays.asList(com.google.zxing.BarcodeFormat.QR_CODE);
        DecoderFactory decoderFactory = new DefaultDecoderFactory(formats);
        barcodeView.getBarcodeView().setDecoderFactory(decoderFactory);
        barcodeView.decodeContinuous(callback);

        barcodeView.setTorchListener(this); // Configurar el listener de la linterna
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isScanning) {
                isScanning = true; // Evitar múltiples escaneos
                Log.d(TAG, "Barcode result: " + result.getText());
                barcodeView.pause(); // Pausar el escáner
                new ProcessQRCodeTask().execute(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {
        }
    };

    private class ProcessQRCodeTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String qrData = params[0];
            try {
                return new JSONObject(qrData);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject qrData) {
            if (qrData != null) {
                retrieveAdditionalData(qrData);
            } else {
                isScanning = false; // Rehabilitar el escaneo si ocurre un error
                barcodeView.resume(); // Reanudar el escáner
            }
        }
    }

    private void retrieveAdditionalData(JSONObject qrData) {
        try {
            String name = qrData.getString("name");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("InscritosCampamento").document(name).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> inscritoData = document.getData();
                        if (inscritoData != null) {
                            for (Map.Entry<String, Object> entry : inscritoData.entrySet()) {
                                try {
                                    qrData.put(entry.getKey(), entry.getValue());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            showQRData(qrData.toString());
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting document: ", task.getException());
                    isScanning = false; // Rehabilitar el escaneo en caso de error
                    barcodeView.resume(); // Reanudar el escáner
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            isScanning = false; // Rehabilitar el escaneo en caso de excepción
            barcodeView.resume(); // Reanudar el escáner
        }
    }

    private void showQRData(String qrData) {
        Intent intent = new Intent(this, DisplayQRDataActivity.class);
        intent.putExtra("qr_data", qrData);
        startActivity(intent);
        finish(); // Terminar la actividad actual para evitar múltiples ejecuciones
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();
        barcodeView.resume();
        isScanning = false; // Rehabilitar el escaneo cuando la actividad se reanude
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeView.resume();
            } else {
                Toast.makeText(this, "Permiso de cámara requerido para escanear QR", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onTorchOn() {
        Log.d(TAG, "Torch is ON");
    }

    @Override
    public void onTorchOff() {
        Log.d(TAG, "Torch is OFF");
    }
}
