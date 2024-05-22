package com.example.sign_lang_ml;
//lkz cnfhnf
import android.app.Activity;
import android.util.Log;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Random;

class Classifier {
    private static final String TAG = "Tflite";
    private static final String MODEL = "model_unquant.tflite";
    private static final String LABEL = "labels.txt";
    private static final int DIM_HEIGHT = 224;
    private static final int DIM_WIDTH = 224;
    private static final int BYTES_PER_CHANNEL = 4; // Floating point
    private static final int CHANNELS = 3; // RGB
    private Interpreter tflite;
    private List<String> labels;
    private ByteBuffer imgData;
    private float[][] probArray;
    private Random random = new Random();

    Classifier(Activity activity) throws IOException {
        MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(activity, MODEL);
        Interpreter.Options tfliteOptions = new Interpreter.Options();
        tfliteOptions.setNumThreads(4);
        tflite = new Interpreter(tfliteModel, tfliteOptions);
        labels = FileUtil.loadLabels(activity, LABEL);
        imgData = ByteBuffer.allocateDirect(DIM_HEIGHT * DIM_WIDTH * BYTES_PER_CHANNEL * CHANNELS);
        imgData.order(ByteOrder.nativeOrder());
        probArray = new float[1][labels.size()];
    }

    void classifyMat(Mat mat) {
        if (tflite != null) {
            convertMatToByteBuffer(mat);
            runInterface();
        }
    }

    private void convertMatToByteBuffer(Mat mat) {
        imgData.rewind();
        for (int i = 0; i < DIM_HEIGHT; ++i) {
            for (int j = 0; j < DIM_WIDTH; ++j) {
                double[] pixelValue = mat.get(i, j);
                imgData.putFloat((float) pixelValue[0] / 255.0f); // Red
                imgData.putFloat((float) pixelValue[1] / 255.0f); // Green
                imgData.putFloat((float) pixelValue[2] / 255.0f); // Blue
            }
        }
    }

    private void runInterface() {
        if (imgData != null) {
            tflite.run(imgData, probArray);
        }
    }

    Mat processMat(Mat mat) {
        // Determine the size of the square ROI
        int size = Math.min(mat.cols(), mat.rows()); // Use the smaller dimension to ensure the square fits
        int squareSize = (int)(size * 0.7f); // Reduce the size to 70% of the smaller dimension to crop the central part

        // Calculate the top-left corner of the ROI to center it
        int x = (mat.cols() - squareSize) / 2;
        int y = (mat.rows() - squareSize) / 2;

        // Define the ROI and crop it
        Rect roi = new Rect(x, y, squareSize, squareSize);
        Mat cropped = new Mat(mat, roi);

        // Resize the cropped image to 224x224 for the classifier
        Mat resized = new Mat();
        Imgproc.resize(cropped, resized, new Size(224, 224));

        return resized; // Return the processed image ready for classification
    }

    void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }

    String getResult() {
        return labels.get(maxIndex(probArray[0]));
    }

    float getProbability() {
        return probArray[0][maxIndex(probArray[0])];
    }

    private int maxIndex(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    String getRandomLabel() {
        return labels.get(random.nextInt(labels.size()));
    }
}