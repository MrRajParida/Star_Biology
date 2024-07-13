package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotePdfViewActivity extends AppCompatActivity {
    private PDFView notePdfView;
    private ProgressBar progressBar;
    private String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pdf_view);

        progressBar = findViewById(R.id.pdfLoadProgressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        notePdfView = findViewById(R.id.notePdfView);

        // Get PDF URL from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            pdfUrl = intent.getStringExtra("pdfUrl");
            if (pdfUrl != null) {
                progressBar.setVisibility(View.VISIBLE);
                new RetrievePDFStream().execute(pdfUrl);
            } else {
                Toast.makeText(this, "No PDF URL provided", Toast.LENGTH_SHORT).show();
                finish(); // Close activity if no URL is provided
            }
        } else {
            Toast.makeText(this, "No Intent received", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no Intent is received
        }
    }

    private class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                notePdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .scrollHandle(new DefaultScrollHandle(NotePdfViewActivity.this))
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progressBar.setVisibility(View.GONE); // Hide progress bar when PDF is fully loaded
                            }
                        })
                        .load();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotePdfViewActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}