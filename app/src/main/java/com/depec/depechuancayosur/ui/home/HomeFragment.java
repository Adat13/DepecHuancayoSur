package com.depec.depechuancayosur.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.depec.depechuancayosur.BibliaActivity;
import com.depec.depechuancayosur.Deuda;
import com.depec.depechuancayosur.Galeria;
import com.depec.depechuancayosur.InscripcionClass;
import com.depec.depechuancayosur.MisInscritos;
import com.depec.depechuancayosur.Polos;
import com.depec.depechuancayosur.QRScannerActivity;
import com.depec.depechuancayosur.R;
import com.depec.depechuancayosur.VerInscritosCampamento;
import com.depec.depechuancayosur.adminMisInscritos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ImageView galeria;
    ImageView inscripción;
    ImageView biblia;
    ImageButton musica;

    ImageView pantallainscripcion, verInscritosCampamento;

    ImageView polos;

    ImageView deuda;
    ImageView verificarVauchers;







    private static final long COUNTDOWN_INTERVAL = 1000;

    private TextView tvDays;
    private FirebaseAuth mAuth;

    private TextView tvHours;
    private TextView tvMinutes;
    private TextView tvSeconds;
    private CountDownTimer countDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        galeria = view.findViewById(R.id.galeria);
        inscripción = view.findViewById(R.id.inscripcion);
        biblia = view.findViewById(R.id.biblia);
        mAuth = FirebaseAuth.getInstance();


        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(getContext(), Galeria.class);
                //startActivity(intent);
                Toast.makeText(getContext(), "No hay fotos disponibles", Toast.LENGTH_SHORT).show();
            }
        });

        inscripción.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRScannerActivity.class);
                startActivity(intent);
            }
        });
        biblia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), BibliaActivity.class);
                Toast.makeText(getContext(), "Disponible pronto", Toast.LENGTH_SHORT).show();
                //startActivity(intent);
            }
        });
        verificarVauchers = view.findViewById(R.id.adminMisInscritos);
        verificarVauchers.setOnClickListener(v -> {
            if (isUserAuthenticated()) {
                Intent intent = new Intent(getActivity(), adminMisInscritos.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Debes iniciar sesión para acceder a esta sección", Toast.LENGTH_SHORT).show();
            }
        });







        polos = view.findViewById(R.id.polos);
        polos.setOnClickListener(v -> {
            if (isUserAuthenticated()) {
                Intent intent = new Intent(getActivity(), InscripcionClass.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Debes iniciar sesión para acceder a esta sección", Toast.LENGTH_SHORT).show();
            }
        });
        verInscritosCampamento = view.findViewById(R.id.verInscritosCampamento);
        verInscritosCampamento.setOnClickListener(v -> {
            if (isUserAuthenticated()) {
                Intent intent = new Intent(getActivity(), VerInscritosCampamento.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Debes iniciar sesión para acceder a esta sección", Toast.LENGTH_SHORT).show();
            }
        });

        deuda = view.findViewById(R.id.completarpago);
        deuda.setOnClickListener(v -> {
            if (isUserAuthenticated()) {
                Intent intent = new Intent(getActivity(), MisInscritos.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Debes iniciar sesión para acceder a esta sección", Toast.LENGTH_SHORT).show();
            }
        });



        tvDays = view.findViewById(R.id.tvDays);
        tvHours = view.findViewById(R.id.tvHours);
        tvMinutes = view.findViewById(R.id.tvMinutes);
        tvSeconds = view.findViewById(R.id.tvSeconds);

        startCountdown();

        return view;


    }

    private void startCountdown() {
        long currentTime = System.currentTimeMillis();
        long targetTime = getTargetTime();

        countDownTimer = new CountDownTimer(targetTime - currentTime, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = millisUntilFinished / (24 * 60 * 60 * 1000);
                long hours = (millisUntilFinished % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                long minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;

                tvDays.setText(String.valueOf(days));
                tvHours.setText(String.format(Locale.getDefault(), "%02d", hours));
                tvMinutes.setText(String.format(Locale.getDefault(), "%02d", minutes));
                tvSeconds.setText(String.format(Locale.getDefault(), "%02d", seconds));
            }

            @Override
            public void onFinish() {
                tvDays.setText("0");
                tvHours.setText("00");
                tvMinutes.setText("00");
                tvSeconds.setText("00");
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private long getTargetTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date targetDate = sdf.parse("2024-08-02");
            return targetDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    private boolean isUserAuthenticated() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }
    //PANTALLA DE INSCRIPCIÓN


}
