package com.depec.depechuancayosur;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BibliaActivity extends AppCompatActivity {

    private EditText bookEditText;
    private EditText chapterEditText;
    private EditText verseEditText;
    private TextView verseTextView;
    private Button getVerseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblia);

        bookEditText = findViewById(R.id.bookEditText);
        chapterEditText = findViewById(R.id.chapterEditText);
        verseEditText = findViewById(R.id.verseEditText);
        verseTextView = findViewById(R.id.verseTextView);
        getVerseButton = findViewById(R.id.getVerseButton);

        getVerseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBibleVerse();
            }
        });
    }

    private void getBibleVerse() {
        String book = bookEditText.getText().toString().trim();
        String chapterStr = chapterEditText.getText().toString().trim();
        String verseStr = verseEditText.getText().toString().trim();

        if (book.isEmpty() || chapterStr.isEmpty() || verseStr.isEmpty()) {
            Toast.makeText(this, "Please enter book, chapter, and verse", Toast.LENGTH_SHORT).show();
            return;
        }

        int chapter = Integer.parseInt(chapterStr);
        int verse = Integer.parseInt(verseStr);

        BibleApi bibleApi = BibleApiService.getBibleApi();
        Call<BibleVerse> call = bibleApi.getVerse("kjv", book, chapter, verse);

        call.enqueue(new Callback<BibleVerse>() {
            @Override
            public void onResponse(Call<BibleVerse> call, Response<BibleVerse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    verseTextView.setText(response.body().getText());
                } else {
                    Toast.makeText(BibliaActivity.this, "Verse not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BibleVerse> call, Throwable t) {
                Toast.makeText(BibliaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
