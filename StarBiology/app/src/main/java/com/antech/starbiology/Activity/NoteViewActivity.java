package com.antech.starbiology.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.antech.starbiology.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoteViewActivity extends AppCompatActivity {
    private PDFView notePdfView;
    private ProgressBar progressBar;
    private String pdfUrl, title, subject;
    private ImageView noPdfImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Set custom back icon if needed
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        noPdfImg = findViewById(R.id.noPdfImage);
        progressBar = findViewById(R.id.noteLoadProgressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        notePdfView = findViewById(R.id.noteView);
        TextView pdfTitle = findViewById(R.id.titlePdfText);
        pdfTitle.setSelected(true);

        Intent intent = getIntent();
        title = intent.getStringExtra("pdfTopic");
        subject = intent.getStringExtra("pdfSubject");
        if (intent != null) {
            pdfUrl = intent.getStringExtra("pdfUrl");
            pdfTitle.setText(title + ", " + subject);
            if (pdfUrl != null) {
                progressBar.setVisibility(View.VISIBLE);
                new RetrievePDFStream().execute(pdfUrl);
            } else {
                Toast.makeText(this, "No PDF URL provided", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No Intent received", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        .scrollHandle(new DefaultScrollHandle(NoteViewActivity.this))
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .load();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NoteViewActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
                noPdfImg.setVisibility(View.VISIBLE);
            }

        }

    }
}