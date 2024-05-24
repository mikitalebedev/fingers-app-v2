package com.example.sign_lang_ml;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

public class RecognitionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

    private static final int BUTTON_SIZE = 80;
    private static final int CLASSIFY_INTERVAL = 20;
    private static final String CAPTURE_BUTTON = "captureButton.png";
    private static final String DEBUG_BUTTON = "debugButton.png";
    private static final String EDGE_BUTTON = "edgeButton.png";
    private static final String SAVE_BUTTON = "saveButton.png";
    private static final String TAG = "RecognitionActivity";

    private Classifier classifier;
    private Mat frame;
    private Mat mRGBA;
    private JavaCameraView openCvCameraView;

    private LinearLayout buttonLayout;
    private LinearLayout debugLayout;
    private TextView probTextView;
    private MaterialCardView probCardView;
    private TextView resultTextView;

    private Boolean isDebug = false;
    private Boolean isEdge = false;
    private Boolean isSave = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        // Инициализация камеры
        openCvCameraView = findViewById(R.id.my_camera_view);
        openCvCameraView.setCvCameraViewListener(RecognitionActivity.this);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);

        resultTextView = findViewById(R.id.result_text_view);
        probTextView = findViewById(R.id.probability_notification);
        probCardView = findViewById(R.id.probability_notification_card);

        // Прочие действия
        findViewById(R.id.exit_button).setOnClickListener(v -> onExitButtonClick());
        findViewById(R.id.recognition_mode_button).setOnClickListener(v -> onRecognitionModeButtonClick());
        findViewById(R.id.switch_camera_button).setOnClickListener(v -> onSwitchCameraButtonClick());
        findViewById(R.id.help_button).setOnClickListener(v -> onHelpButtonClick());
        findViewById(R.id.space_button).setOnClickListener(v -> onSpaceButtonClick());
        findViewById(R.id.delete_button).setOnClickListener(v -> onDeleteButtonClick());
        findViewById(R.id.delete_button).setOnLongClickListener(v -> onDeleteButtonLongClick());
        findViewById(R.id.propability_button).setOnClickListener(v -> showProbabilityNotification());
    }


    @Override
    public void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Подключилась камера.");
            baseloadercallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "Камера не подключилась.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseloadercallback);
        }

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            Log.e(TAG, "Не удалось инициировать классификатор", e);
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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();  // Получаем цветное изображение
        if (mCameraIndex == 1) {
            Core.flip(mRGBA, mRGBA, 1);
        }

        // Производим масштабирование и предобработку изображения согласно новому классификатору
        frame = classifier.processMat(mRGBA);

        if (counter == CLASSIFY_INTERVAL) {
            runInterpreter();  // Запускаем классификатор
            counter = 0;
        } else {
            counter++;
        }

        // Отображаем рамку вокруг области, которая классифицируется
        float boxSize = mRGBA.width() * 0.7f;
        Point rectStart = new Point((mRGBA.width() - boxSize) / 2, (mRGBA.height() - boxSize) / 2);
        Point rectEnd = new Point(rectStart.x + boxSize, rectStart.y + boxSize);

        // Draw white bars at the top and bottom
        Imgproc.rectangle(mRGBA, new Point(0, 0), new Point(mRGBA.width(), rectStart.y - 1), new Scalar(255, 255, 255), -1);
        Imgproc.rectangle(mRGBA, new Point(0, rectEnd.y + 1), new Point(mRGBA.width(), mRGBA.height()), new Scalar(255, 255, 255), -1);

        // Draw the purple rectangle with a 1-pixel gap
        Imgproc.rectangle(mRGBA, rectStart, rectEnd, new Scalar(206, 73, 239), 2); // Color CE49EF

        if (isEdge) {
            mRGBA = classifier.processMat(mRGBA);  // Если активирован режим отладки, отображаем края
        }

        return mRGBA;
    }

    @Override
    public void onClick(View view) {
        switch ((String) view.getTag()) {
            case SAVE_BUTTON:
                isSave = !isSave;
                setButton(SAVE_BUTTON, isSave);
                break;
            case EDGE_BUTTON:
                isEdge = !isEdge;
                setButton(EDGE_BUTTON, isEdge);
                break;
            case DEBUG_BUTTON:
                isDebug = !isDebug;
                if (isDebug) {
                    debugLayout.setVisibility(View.VISIBLE);
                    probTextView.setVisibility(View.VISIBLE);
                } else {
                    isSave = false;
                    setButton(SAVE_BUTTON, false);
                    isEdge = false;
                    setButton(EDGE_BUTTON, false);
                    debugLayout.setVisibility(View.INVISIBLE);
                    probTextView.setVisibility(View.INVISIBLE);
                }
                setButton(DEBUG_BUTTON, isDebug);
                break;
            case CAPTURE_BUTTON:
                try {
                    runInterpreter();
                    String t = "Вероятность: " + classifier.getProbability();
                    probTextView.setText(t);
                    if (isSave) {
                        Bitmap bmp = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(frame, bmp);
                        MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                                classifier.getResult(), "" + classifier.getProbability());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "" + e);
                }
                break;
        }
    }

    private ImageButton createButton(String tag) {
        ImageButton button = new ImageButton(this);
        button.setTag(tag);

        try {
            InputStream stream = getAssets().open(tag);
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            button.setImageBitmap(Bitmap.createScaledBitmap(bmp, BUTTON_SIZE, BUTTON_SIZE, false));
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        button.setPadding(25, 25, 25, 25);
        button.getBackground().setAlpha(0);
        button.setOnClickListener(this);
        return button;
    }

    private void setButton(String tag, Boolean isOn) {
        String path = tag;
        if (isOn) {
            path = path.substring(0, path.length() - 4) + "On.png";
        }
        try {
            InputStream stream = getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            ImageButton button = buttonLayout.findViewWithTag(tag);
            button.setImageBitmap(Bitmap.createScaledBitmap(bmp, 80, 80, false));
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void runInterpreter() {
        if (classifier != null) {
            classifier.classifyMat(frame);
            float probability = classifier.getProbability();
            String result = classifier.getResult();


            //if (probability > 0.7) {  // Ограничение вероятности 70%
                switch (result) {
                    case "Пробел":
                        if (text.length() < 22) {
                            text += " ";
                        }
                        break;
                    case "Удалить":
                        if (text.length() > 0) {
                            text = text.substring(0, text.length() - 1);
                        }
                        break;
                    case "Ничего":
                            text += "";
                        break;
                    default:
                        if (text.length() < 22) {  // Ограничение на 22 букв
                            text += result;
                        }
                }
            //}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText(text);
                    adjustTextSizeAndPosition();
                }
            });
            Log.d(TAG, "Предположение: " + result + " Вероятность: " + probability);
        }
    }

    private void showProbabilityNotification() {
        if (probTextView != null && probCardView != null) {
            String probabilityText = "Вероятность: " + classifier.getProbability();
            probTextView.setText(probabilityText);
            probCardView.setVisibility(View.VISIBLE);

            // Автоматическое скрытие уведомления через 3 секунды
            probCardView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    probCardView.setVisibility(View.GONE);
                }
            }, 3000);
        }
    }


    private void adjustTextSizeAndPosition() {
        int length = text.length();
        if (length <= 5) {
            resultTextView.setTextSize(38f);
        } else if (length <= 8) {
            resultTextView.setTextSize(36f);
        } else if (length <= 11) {
            resultTextView.setTextSize(24f);
        } else if (length <= 16) {
            resultTextView.setTextSize(20f);
        } else {
            resultTextView.setTextSize(16f);
        }

        // лимит
        MaterialCardView questionCard = findViewById(R.id.question_card);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) questionCard.getLayoutParams();
        if (length > 16) {
            params.horizontalBias = 0.3f;  // влево
        } else {
            params.horizontalBias = 0.5f;  // центр
        }
        questionCard.setLayoutParams(params);
    }

    public void onExitButtonClick() {
        Intent intent = new Intent(RecognitionActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRecognitionModeButtonClick() {
        Intent intent = new Intent(RecognitionActivity.this, TrainingActivity.class);
        startActivity(intent);
    }

    public void onSwitchCameraButtonClick() {
        mCameraIndex = (mCameraIndex == 0) ? 1 : 0; // Toggle between back (0) and front (1) camera
        openCvCameraView.disableView();
        openCvCameraView.setCameraIndex(mCameraIndex);
        openCvCameraView.enableView();
    }

    public void onHelpButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Помощь")
                .setMessage("Держите руку перед камерой так, чтобы она была видна полностью. Убедитесь, что освещение хорошее и нет теней на руке.")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSpaceButtonClick() {
        if (text.length() < 22) {
            text += " ";
            resultTextView.setText(text);
        }
    }

    public void onDeleteButtonClick() {
        if (text.length() > 0) {
            text = text.substring(0, text.length() - 1);
            resultTextView.setText(text);
        }
    }

    public boolean onDeleteButtonLongClick() {
        text = "";
        resultTextView.setText(text);
        return true;
    }
}
