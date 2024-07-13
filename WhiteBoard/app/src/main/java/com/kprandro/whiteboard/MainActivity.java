package com.kprandro.whiteboard;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private ImageButton btnClear, btnBrushSize, btnColor, viewBtn, btnEraser, btnShapeSelect;
    private Boolean isOpen = true;
    private LinearLayout linearLayout;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isEraserActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        hideSystemUI();


        btnShapeSelect = findViewById(R.id.btn_shape);
        btnEraser = findViewById(R.id.btn_eraser);
        linearLayout = findViewById(R.id.writingBoard);
        drawingView = findViewById(R.id.drawing_view);
        btnClear = findViewById(R.id.btn_clear);
        btnBrushSize = findViewById(R.id.btn_brush_size);
        btnColor = findViewById(R.id.btn_color);
        viewBtn = findViewById(R.id.viewBtn);

        btnShapeSelect.setOnClickListener(v -> showShapeSelectionDialog());

        viewBtn.setOnClickListener(v -> {
            if (isOpen) {
                isOpen = false;
                linearLayout.setVisibility(View.VISIBLE);
                viewBtn.setImageResource(R.drawable.down);
            } else {
                isOpen = true;
                linearLayout.setVisibility(View.GONE);
                viewBtn.setImageResource(R.drawable.up);
            }
        });

        btnEraser.setOnClickListener(v -> {
            isEraserActive = !isEraserActive;
            drawingView.setEraser(isEraserActive);
            if (isEraserActive) {
                btnEraser.setImageResource(R.drawable.pen); // Change to pen icon when erasing
            } else {
                btnEraser.setImageResource(R.drawable.eraser); // Change back to eraser icon
            }
        });

        // Clear board on button click
        btnClear.setOnClickListener(v -> drawingView.clearBoard());

        // Change brush size on button click
        btnBrushSize.setOnClickListener(v -> changeBrushSize());

        // Change brush color on button click
        btnColor.setOnClickListener(v -> changeColor());
    }


    private void showShapeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View shapeSelectionView = getLayoutInflater().inflate(R.layout.shape_selection, null);
        builder.setView(shapeSelectionView);

        AlertDialog dialog = builder.create();

        ImageButton btnRectangle = shapeSelectionView.findViewById(R.id.btn_rectangle);
        ImageButton btnCircle = shapeSelectionView.findViewById(R.id.btn_circle);
        ImageButton btnLine = shapeSelectionView.findViewById(R.id.btn_line);
        ImageButton btnFreehand = shapeSelectionView.findViewById(R.id.btn_freehand);

        btnRectangle.setOnClickListener(v -> {
            btnShapeSelect.setImageResource(R.drawable.square);
            drawingView.setShape("rectangle");
            dialog.dismiss();
        });

        btnCircle.setOnClickListener(v -> {
            btnShapeSelect.setImageResource(R.drawable.circle);
            drawingView.setShape("circle");
            dialog.dismiss();
        });

        btnLine.setOnClickListener(v -> {
            btnShapeSelect.setImageResource(R.drawable.line);
            drawingView.setShape("line");
            dialog.dismiss();
        });

        btnFreehand.setOnClickListener(v -> {
            btnShapeSelect.setImageResource(R.drawable.wavy_line);
            drawingView.setShape("freehand");
            dialog.dismiss();
        });

        dialog.show();
    }

    // Method to hide system UI (status bar, navigation bar)
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // Method to change brush size
    private void changeBrushSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View brushSizePickerLayout = getLayoutInflater().inflate(R.layout.brush_size_picker, null);
        builder.setView(brushSizePickerLayout);

        AlertDialog dialog = builder.create();

        TextView tvBrushSize = brushSizePickerLayout.findViewById(R.id.tv_brush_size);
        SeekBar seekBarBrushSize = brushSizePickerLayout.findViewById(R.id.seekBar_brush_size);

        // Set initial value and update as the user interacts with the slider
        seekBarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBrushSize.setText("Brush Size: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawingView.setBrushSize(seekBar.getProgress());
            }
        });

        dialog.show();
    }


    // Method to change brush color
    private void changeColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View colorPickerLayout = getLayoutInflater().inflate(R.layout.color_picker, null);
        builder.setView(colorPickerLayout);

        AlertDialog dialog = builder.create();

        ImageButton btnBlack = colorPickerLayout.findViewById(R.id.btn_black);
        ImageButton btnRed = colorPickerLayout.findViewById(R.id.btn_red);
        ImageButton btnBlue = colorPickerLayout.findViewById(R.id.btn_blue);
        ImageButton btnGreen = colorPickerLayout.findViewById(R.id.btn_green);

        btnBlack.setOnClickListener(v -> {
            drawingView.setColor(Color.BLACK);
            dialog.dismiss();
        });

        btnRed.setOnClickListener(v -> {
            drawingView.setColor(Color.RED);
            dialog.dismiss();
        });

        btnBlue.setOnClickListener(v -> {
            drawingView.setColor(Color.BLUE);
            dialog.dismiss();
        });

        btnGreen.setOnClickListener(v -> {
            drawingView.setColor(Color.GREEN);
            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1);
    }
}
