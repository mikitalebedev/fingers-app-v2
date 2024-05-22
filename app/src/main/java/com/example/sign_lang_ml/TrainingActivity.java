package com.example.sign_lang_ml;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class TrainingActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int CLASSIFY_INTERVAL = 20;
    private static final String TAG = "TrainingActivity";

    private Classifier classifier;
    private Mat frame;
    private Mat mRGBA;
    private JavaCameraView openCvCameraView;

    private TextView scoreTextView;
    private TextView resultTextView;

    private String text = "";
    private int counter = 0;
    private int mCameraIndex = 0;

    private BaseLoaderCallback baseloadercallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS) {
                openCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private int score = 0;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();


        if (mCameraIndex == 1) {
            Core.flip(mRGBA, mRGBA, 1);
        }

        frame = classifier.processMat(mRGBA);

        if (counter == CLASSIFY_INTERVAL) {
            runInterpreter();
            counter = 0;
        } else {
            counter++;
        }

        float boxSize = mRGBA.width() * 0.7f;
        Point rectStart = new Point((mRGBA.width() - boxSize) / 2, (mRGBA.height() - boxSize) / 2);
        Point rectEnd = new Point(rectStart.x + boxSize, rectStart.y + boxSize);

        // Draw white bars at the top and bottom
        Imgproc.rectangle(mRGBA, new Point(0, 0), new Point(mRGBA.width(), rectStart.y - 1), new Scalar(255, 255, 255), -1);
        Imgproc.rectangle(mRGBA, new Point(0, rectEnd.y + 1), new Point(mRGBA.width(), mRGBA.height()), new Scalar(255, 255, 255), -1);

        // Draw the purple rectangle with a 1-pixel gap
        Imgproc.rectangle(mRGBA, rectStart, rectEnd, new Scalar(206, 73, 239), 2); // Color CE49EF

        return mRGBA;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        openCvCameraView = findViewById(R.id.camera_view);
        openCvCameraView.setCvCameraViewListener(TrainingActivity.this);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);

        resultTextView = findViewById(R.id.result_text_view);
        scoreTextView = findViewById(R.id.score_text_view);
        Button nextButton = findViewById(R.id.next_button);

        findViewById(R.id.exit_button).setOnClickListener(v -> onExitButtonClick());
        findViewById(R.id.recognition_mode_button).setOnClickListener(v -> onRecognitionModeButtonClick());
        findViewById(R.id.switch_camera_button).setOnClickListener(v -> onSwitchCameraButtonClick());

        nextButton.setOnClickListener(v -> onNextButtonClick());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Подключение камеры.");
            baseloadercallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "Камера не подключена.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseloadercallback);
        }

        try {
            classifier = new Classifier(this);
            text = classifier.getRandomLabel();
            resultTextView.setText(text);
        } catch (IOException e) {
            Log.e(TAG, "Не удалось инициализировать классификатор", e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
        if (classifier != null) {
            classifier.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        if (mRGBA != null) mRGBA.release();
    }

    private void runInterpreter() {
        if (classifier != null) {
            classifier.classifyMat(frame);
            if (!classifier.getResult().equals("Ничего") && classifier.getResult().equals(text)) {
                score++;
                text = classifier.getRandomLabel();
                runOnUiThread(() -> {
                    String s = " " + score;
                    scoreTextView.setText(s);
                    resultTextView.setText(text);
                });
            }
            Log.d(TAG, "Предположение: " + classifier.getResult() + " Вероятность: " + classifier.getProbability());
        }
    }

    public void onExitButtonClick() {
        Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRecognitionModeButtonClick() {
        Intent intent = new Intent(TrainingActivity.this, RecognitionActivity.class);
        startActivity(intent);
    }

    public void onSwitchCameraButtonClick() {
        mCameraIndex = (mCameraIndex == 0) ? 1 : 0; // Toggle between back (0) and front (1) camera
        openCvCameraView.disableView();
        openCvCameraView.setCameraIndex(mCameraIndex);
        openCvCameraView.enableView();
    }

    public void onNextButtonClick() {
        if (classifier != null) {
            text = classifier.getRandomLabel();
            resultTextView.setText(text);
        }
    }
}
