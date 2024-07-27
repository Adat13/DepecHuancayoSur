package com.depec.depechuancayosur;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayQRDataActivity extends AppCompatActivity {

    private TextView textViewName, textViewAge, textViewDni, textViewChurch, textViewGender, textViewHasTent, textViewHasBus, textViewHasMeal, textViewBusNumber, textViewSeatNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr_data);

        textViewName = findViewById(R.id.text_view_name);
        textViewAge = findViewById(R.id.text_view_age);
        textViewDni = findViewById(R.id.text_view_dni);
        textViewChurch = findViewById(R.id.text_view_church);
        textViewGender = findViewById(R.id.text_view_gender);
        textViewHasTent = findViewById(R.id.text_view_has_tent);
        textViewHasBus = findViewById(R.id.text_view_has_bus);
        textViewHasMeal = findViewById(R.id.text_view_has_meal);
        textViewBusNumber = findViewById(R.id.text_view_bus_number);
        textViewSeatNumber = findViewById(R.id.text_view_seat_number);

        String qrData = getIntent().getStringExtra("qr_data");
        if (qrData != null) {
            try {
                JSONObject json = new JSONObject(qrData);
                textViewName.setText("Name: " + json.optString("name"));
                textViewAge.setText("Age: " + json.optString("age"));
                textViewDni.setText("DNI: " + json.optString("dni"));
                textViewChurch.setText("Church: " + json.optString("church"));
                textViewGender.setText("Gender: " + json.optString("gender"));
                textViewHasTent.setText("Has Tent: " + (json.optBoolean("carpa") ? "Sí" : "No"));
                textViewHasBus.setText("Has Bus: " + (json.optBoolean("hasBus") ? "Sí" : "No"));
                textViewHasMeal.setText("Has Meal: " + (json.optBoolean("hasMeal") ? "Sí" : "No"));

                textViewBusNumber.setText("Bus Number: " + json.optString("busNumber"));
                textViewSeatNumber.setText("Seat Number: " + json.optString("seatNumber"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
