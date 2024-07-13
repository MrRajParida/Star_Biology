package com.kprandro.newshow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.ProcessCameraProvider;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class PostActivity extends AppCompatActivity {
    private ImageButton postBack, flashButton, recordButton, cameraSwitchButton;
    private TextView postTimeStamp;
    private PreviewView cameraPreviewView;
    private boolean isFlashOn = false;
    private boolean isRecording = false;
    private Camera camera;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private VideoCapture<Recorder> videoCapture;
    private Recording activeRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Initialize views
        postBack = findViewById(R.id.postBack);
        flashButton = findViewById(R.id.flash);
        recordButton = findViewById(R.id.record);
        cameraSwitchButton = findViewById(R.id.cameraSwitch);
        postTimeStamp = findViewById(R.id.postTimeStamp);
        cameraPreviewView = findViewById(R.id.cameraView);

        postBack.setOnClickListener(v -> finish());

        startCamera();

        flashButton.setOnClickListener(v -> toggleFlashlight());

        recordButton.setOnClickListener(v -> toggleRecording());

        cameraSwitchButton.setOnClickListener(v -> switchCamera());
    }

    private void startCamera() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 101);
        }
    }

    private void setupCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA; // Default to back camera
                bindCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("PostActivity", "Error setting up camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCamera(ProcessCameraProvider cameraProvider) {
        // Define the camera selector (back camera)
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Create a VideoCapture use case
        videoCapture = new VideoCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        // Check available camera info and supported qualities
        List<CameraInfo> cameraInfos = cameraProvider.getAvailableCameraInfos();
        boolean foundSupportedQuality = false;
        QualitySelector qualitySelector = null;

        for (CameraInfo cameraInfo : cameraInfos) {
            // Check for supported qualities
            for (Quality quality : Quality.values()) {
                if (cameraInfo.hasCapability(quality)) {
                    // If the quality is supported, set it in the quality selector
                    qualitySelector = QualitySelector.from(ResolutionStrategy.HIGHER_RESOLUTION);
                    foundSupportedQuality = true;
                    break;
                }
            }
            if (foundSupportedQuality) {
                break; // Exit loop if a supported quality is found
            }
        }

        // Handle the case where no supported quality is found
        if (!foundSupportedQuality) {
            Log.e("PostActivity", "No supported quality found for the selected camera");
            return; // Return or handle the error as needed
        }

        // Bind the camera use cases to the lifecycle
        cameraProvider.bindToLifecycle(this, cameraSelector, videoCapture);

        // Now you can set up the preview and start video recording...
    }


    private void toggleFlashlight() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn); // Toggle flashlight
            flashButton.setImageResource(isFlashOn ? R.drawable.flash : R.drawable.flash_off);
        }
    }

    private void switchCamera() {
        cameraSelector = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                ? CameraSelector.DEFAULT_FRONT_CAMERA
                : CameraSelector.DEFAULT_BACK_CAMERA;
        bindCamera(cameraProvider); // Rebind the camera to switch
    }

    private void toggleRecording() {
        if (isRecording) {
            // Stop recording logic here
            activeRecording.stop(); // Stop the active recording
            activeRecording = null;
            recordButton.setImageResource(R.drawable.record);  // Change icon back to 'record'
            isRecording = false;
        } else {
            // Check for permissions before starting recording
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                // Start recording logic here
                File videoFile = new File(getExternalFilesDir(null), "video_" + System.currentTimeMillis() + ".mp4");

                // Prepare MediaStoreOutputOptions for the video file
                MediaStoreOutputOptions outputOptions = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        .build();

                try {
                    // Prepare the recording
                    activeRecording = videoCapture.getOutput()
                            .prepareRecording(this, outputOptions)
                            .withAudioEnabled() // Enable audio recording
                            .start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                                if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                                    Log.d("PostActivity", "Recording started");
                                } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                                    Log.d("PostActivity", "Recording finished: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri());
                                }
                            });

                    recordButton.setImageResource(R.drawable.stop_circled);  // Change icon to 'stop' while recording
                    isRecording = true;
                } catch (SecurityException e) {
                    Log.e("PostActivity", "Permission not granted for recording", e);
                    // Handle the case where permissions are not granted
                    // Optionally, you can show a dialog to the user or request permissions
                }
            } else {
                // Permissions not granted, handle accordingly
                Log.e("PostActivity", "Camera or Audio permission not granted for recording");
                // Optionally, you can request permissions here if needed
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            // Handle the case where camera or audio permission is not granted
            Log.e("PostActivity", "Camera or Audio permission not granted");
        }
    }
}
